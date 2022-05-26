package tech.cassandre.trading.bot.batch;

import info.bitrich.xchangestream.core.StreamingExchange;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.trade.UserTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.ConnectableFlux;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Trade flux - push {@link TradeDTO}.
 */
@RequiredArgsConstructor
public class TradeFlux extends BaseFlux<TradeDTO>  implements Observer<UserTrade> {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

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
                          final Consumer<? super Set<TradeDTO>> consumer,
                          final Consumer<? super Throwable> errorConsumer) {

        final ConnectableFlux<Set<TradeDTO>> connectableFlux = getFlux().publish();

        if (StreamingExchange.class.isAssignableFrom(specification.getExchangeClass())) {
            if (exchange == null) {
                exchange = (StreamingExchange) ExchangeFactory.INSTANCE.createExchange(specification);
                exchange.connect().blockingAwait();
            }

            exchange.getStreamingTradeService()
                    .getUserTrades(CURRENCY_MAPPER.mapToInstrument(currencyPair))
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
        logger.info("TradeFlux subscription successful");
    }

    /**
     * onNext.
     * @param trade the user trade from xchange
     */
    public void onNext(@NonNull final UserTrade trade) {

        Optional<Order> order = orderRepository.findByOrderId(trade.getOrderId());
        if (!order.isPresent()) {
            logger.debug("No order found in database for order: {}", trade.getOrderId());
            return;
        }

        TradeDTO tradeDTO = TRADE_MAPPER.mapToTradeDTO(trade);

        tradeRepository.findByTradeId(tradeDTO.getTradeId())
            .ifPresentOrElse(existingTrade -> {
                logger.debug("Updating trade in database: {}", existingTrade);
                TRADE_MAPPER.updateTrade(tradeDTO, existingTrade);
                fluxSink.next(Set.of(TRADE_MAPPER.mapToTradeDTO(tradeRepository.save(existingTrade))));
            }, () -> {
                final Trade newTrade = TRADE_MAPPER.mapToTrade(tradeDTO);
                logger.debug("Creating trade in database: {}", newTrade);
                newTrade.setOrder(order.get());
                fluxSink.next(Set.of(TRADE_MAPPER.mapToTradeDTO(tradeRepository.save(newTrade))));
            });
    }

    /**
     * onError.
     * @param throwable the throwable
     */
    public void onError(@NonNull final Throwable throwable) {
        logger.error("TradeFlux error: " + throwable.getMessage());
    }

    /**
     * onComplete.
     */
    public void onComplete() {
        logger.info("TradeFlux complete");
    }

    @Override
    protected final Set<TradeDTO> getNewValues() {
        logger.debug("Retrieving trades from exchange");
        Set<TradeDTO> newValues = new LinkedHashSet<>();

        // Finding which trades have been updated.
        tradeService.getTrades()
                .stream()
                // Note: we only save trades when the order present in database.
                .filter(t -> orderRepository.findByOrderId(t.getOrderId()).isPresent())
                .forEach(trade -> {
                    logger.debug("Checking trade: {}", trade.getTradeId());
                    final Optional<Trade> tradeInDatabase = tradeRepository.findByTradeId(trade.getTradeId());

                    // The trade is not in database.
                    if (tradeInDatabase.isEmpty()) {
                        logger.debug("New trade from exchange: {}", trade);
                        newValues.add(trade);
                    }

                    // The trade is in database but the trade values from the server changed.
                    if (tradeInDatabase.isPresent() && !TRADE_MAPPER.mapToTradeDTO(tradeInDatabase.get()).equals(trade)) {
                        logger.debug("Updated trade from exchange: {}", trade);
                        newValues.add(trade);
                    }
                });

        return newValues;
    }

    @Override
    public final Set<TradeDTO> saveValues(final Set<TradeDTO> newValues) {
        Set<Trade> trades = new LinkedHashSet<>();

        // We create or update every trade retrieved by the exchange.
        newValues.forEach(newValue -> tradeRepository.findByTradeId(newValue.getTradeId())
                .ifPresentOrElse(trade -> {
                    // Update trade.
                    TRADE_MAPPER.updateTrade(newValue, trade);
                    trades.add(tradeRepository.save(trade));
                    logger.debug("Updating trade in database: {}", trade);
                }, () -> {
                    // Create trade.
                    final Trade newTrade = TRADE_MAPPER.mapToTrade(newValue);
                    // Order is always present as we check it in getNewValues().
                    orderRepository.findByOrderId(newValue.getOrderId()).ifPresent(newTrade::setOrder);
                    trades.add(tradeRepository.save(newTrade));
                    logger.debug("Creating trade in database: {}", newTrade);
                }));

        return trades.stream()
                .map(TRADE_MAPPER::mapToTradeDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
