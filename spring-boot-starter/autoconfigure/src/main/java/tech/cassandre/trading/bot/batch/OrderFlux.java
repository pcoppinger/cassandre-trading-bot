package tech.cassandre.trading.bot.batch;

import info.bitrich.xchangestream.core.StreamingExchange;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.ConnectableFlux;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.service.ActiveOrderController;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Order flux - push {@link OrderDTO}.
 */
@RequiredArgsConstructor
public class OrderFlux extends BaseFlux<OrderDTO> implements Observer<org.knowm.xchange.dto.Order> {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Trade service. */
    private final TradeService tradeService;

    /** Exchange. */
    private StreamingExchange exchange;

    /**
     * subscribe.
     * @param specification the specification
     * @param currencyPair the currency pair
     * @param consumer the consumer lambda
     * @param errorConsumer the error consumer lambda
     */
    public void subscribe(final ExchangeSpecification specification,
                          final CurrencyPairDTO currencyPair,
                          final Consumer<? super Set<OrderDTO>> consumer,
                          final Consumer<? super Throwable> errorConsumer) {

        final ConnectableFlux<Set<OrderDTO>> connectableFlux = getFlux().publish();

        if (StreamingExchange.class.isAssignableFrom(specification.getExchangeClass())) {
            if (exchange == null) {
                exchange = (StreamingExchange) ExchangeFactory.INSTANCE.createExchange(specification);
                exchange.connect().blockingAwait();
            }

            exchange.getStreamingTradeService()
                    .getOrderChanges(CURRENCY_MAPPER.mapToInstrument(currencyPair))
                    .subscribe(this);

            exchange.getStreamingTradeService()
                    .getStopOrderChanges(CURRENCY_MAPPER.mapToInstrument(currencyPair))
                    .subscribe(this);
        }

        connectableFlux.subscribe(consumer, errorConsumer);
        connectableFlux.connect();
    }

    /**
     * onSubscribe.
     * @param disposable the disposable
     */
    public void onSubscribe(@NonNull final Disposable disposable) {
        logger.info("OrderFlux subscription successful");
    }

    /**
     * onNext.
     * @param order the order from xchange
     */
    public void onNext(@NonNull final org.knowm.xchange.dto.Order order) {
        fluxSink.next(Set.of(ActiveOrderController.update(order)));
    }

    /**
     * onError.
     * @param throwable the throwable
     */
    public void onError(@NonNull final Throwable throwable) {
        logger.error("OrderFlux error: " + throwable.getMessage());
    }

    /**
     * onComplete.
     */
    public void onComplete() {
        logger.info("OrderFlux complete");
    }

    @Override
    protected final Set<OrderDTO> getNewValues() {
        logger.debug("Retrieving orders from exchange");
        Set<OrderDTO> newValues = new LinkedHashSet<>();

        // Getting all the orders from the exchange.
        tradeService.getOrders()
                .forEach(order -> {
                    logger.debug("Checking order: {}", order.getOrderId());
                    final Optional<Order> orderInDatabase = ActiveOrderController.findByOrderId(order.getOrderId());

                    // If the order is not in database, we insert it only if strategy is set on that order.
                    // If strategy is not set, it means that Cassandre did not yet save its locally created order.
                    if (orderInDatabase.isEmpty() && order.getStrategy() != null) {
                        logger.debug("New order from exchange: {}", order);
                        newValues.add(order);
                    }

                    // If the local order is already saved in database and the order retrieved from the exchange
                    // is different, then, we update the order in database.
                    if (orderInDatabase.isPresent() && !ORDER_MAPPER.mapToOrderDTO(orderInDatabase.get()).equals(order)) {
                        logger.debug("Updated order from exchange: {}", order);
                        newValues.add(order);
                    }
                });

        return newValues;
    }

    @Override
    protected final Set<OrderDTO> saveValues(final Set<OrderDTO> newValues) {
        Set<Order> orders = new LinkedHashSet<>();

        // We create or update every order retrieved by the exchange.
        newValues.forEach(newValue -> ActiveOrderController.findByOrderId(newValue.getOrderId())
            .ifPresentOrElse(existing -> {
                // Update order.
                ORDER_MAPPER.updateOrder(newValue, existing);
                ActiveOrderController.save(existing).ifPresent(orders::add);
                logger.debug("Updating order in database: {}", existing);
            }, () -> {
                // Create order.
                final Order order = ORDER_MAPPER.mapToOrder(newValue);
                ActiveOrderController.save(order).ifPresent(orders::add);
                logger.debug("Creating order in database: {}", order);
            }));

        return orders.stream()
                .map(ORDER_MAPPER::mapToOrderDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
