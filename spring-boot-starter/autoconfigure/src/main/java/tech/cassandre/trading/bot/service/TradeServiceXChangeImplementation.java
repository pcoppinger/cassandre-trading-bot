package tech.cassandre.trading.bot.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.StopOrder;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.Order.OrderStatus;
import org.knowm.xchange.exceptions.CurrencyPairNotValidException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.DefaultQueryOrderParamCurrencyPair;
import org.knowm.xchange.dto.Order.IOrderFlags;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.OrderIntentionDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;
import tech.cassandre.trading.bot.util.base.Base;
import tech.cassandre.trading.bot.util.base.service.BaseService;
import tech.cassandre.trading.bot.util.xchange.CancelOrderParams;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Comparator;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.math.RoundingMode.HALF_EVEN;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

/**
 * Trade service - XChange implementation.
 */
@Configurable(autowire = Autowire.BY_TYPE, dependencyCheck = true)
@Service
public class TradeServiceXChangeImplementation extends BaseService implements TradeService {

    /** Size of the random string generated. */
    private static final int GENERATED_ORDER_SIZE = 32;

    /** OKEX broker id. */
    private static final String OKEX_BROKER_ID = "3fba96c2a09c42BC";

    /** Driver class name. */
    @Value("${cassandre.trading.bot.exchange.driver-class-name}")
    private String driverClassName;

    /** XChange trade service. */
    private final org.knowm.xchange.service.trade.TradeService tradeService;

    /** Leverage. */
    private BigDecimal leverage = BigDecimal.ONE;

    /** Executor for creating orders. */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Constructor.
     *
     * @param rate               rate in ms
     * @param newOrderRepository order repository
     * @param newTradeService    market data service
     */
    public TradeServiceXChangeImplementation(final long rate,
                                             final OrderRepository newOrderRepository,
                                             final org.knowm.xchange.service.trade.TradeService newTradeService) {
        super(rate);
        this.tradeService = newTradeService;
    }

    /**
     * Creates market order.
     *
     * @param strategy     strategy
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @param flags        Exchange-specific flags
     * @return order creation result
     */
    private OrderCreationResultDTO createMarketOrder(final GenericCassandreStrategy strategy,
                                                     final OrderTypeDTO orderTypeDTO,
                                                     final CurrencyPairDTO currencyPair,
                                                     final BigDecimal amount,
                                                     final Set<IOrderFlags> flags) {
        try {
            // Making the order.
            MarketOrder m = new MarketOrder(UTIL_MAPPER.mapToOrderType(orderTypeDTO),
                    amount.setScale(currencyPair.getBaseScale(), HALF_EVEN),
                    CURRENCY_MAPPER.mapToCurrencyPair(currencyPair),
                    getGeneratedClientOid(),
                    null);
            m.setLeverage(getLeverage().toString());
            if (flags != null) {
                flags.forEach(m::addOrderFlag);
            }

            logger.debug("Sending market order: {} - {} - {}",
                    orderTypeDTO,
                    currencyPair,
                    amount.setScale(currencyPair.getBaseScale(), HALF_EVEN));

            final String orderId = tradeService.placeMarketOrder(m);

            // Sending the order.
            OrderDTO order = OrderDTO.builder()
                    .type(orderTypeDTO)
                    .orderId(orderId)
                    .strategy(strategy.getStrategyDTO())
                    .currencyPair(currencyPair)
                    .amount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .leverage(getLeverage().toString())
                    .cumulativeAmount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .averagePrice(CurrencyAmountDTO.builder()
                            .value(strategy.getLastPriceForCurrencyPair(currencyPair))
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .marketPrice(CurrencyAmountDTO.builder()
                            .value(strategy.getLastPriceForCurrencyPair(currencyPair))
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .status(PENDING_NEW)
                    .trades(new HashSet<>())
                    .timestamp(ZonedDateTime.now())
                    .build();

            // We save the order.
            final OrderDTO saved = ActiveOrderController.create(orderId, order);
            final OrderCreationResultDTO result = new OrderCreationResultDTO(saved);
            logger.debug("Order creation result: {}", result);
            return result;
        } catch (Exception e) {
            final String error = "Error submitting market order for " + amount + " " + currencyPair + ": " + e.getMessage();
            logger.error(error);
            return new OrderCreationResultDTO(e.getMessage(), e);
        }
    }

    /**
     * Creates limit order.
     *
     * @param strategy     strategy
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   In a BID this is the highest acceptable price, in an ASK this is the lowest acceptable price
     * @param flags        Exchange-specific flags
     * @return order creation result
     */
    private OrderCreationResultDTO createLimitOrder(final GenericCassandreStrategy strategy,
                                                    final OrderTypeDTO orderTypeDTO,
                                                    final CurrencyPairDTO currencyPair,
                                                    final BigDecimal amount,
                                                    final BigDecimal limitPrice,
                                                    final Set<IOrderFlags> flags) {
        try {
            // Making the order.
            LimitOrder l = new LimitOrder(UTIL_MAPPER.mapToOrderType(orderTypeDTO),
                    amount.setScale(currencyPair.getBaseScale(), HALF_EVEN),
                    CURRENCY_MAPPER.mapToCurrencyPair(currencyPair),
                    getGeneratedClientOid(),
                    null,
                    limitPrice);
            l.setLeverage(getLeverage().toString());
            if (flags != null) {
                flags.forEach(l::addOrderFlag);
            }

            logger.debug("Sending limit order: {} - {} - {}",
                    orderTypeDTO,
                    currencyPair,
                    amount.setScale(currencyPair.getBaseScale(), HALF_EVEN));

            final String orderId = tradeService.placeLimitOrder(l);

            // Sending & creating the order.
            OrderDTO order = OrderDTO.builder()
                    .type(orderTypeDTO)
                    .orderId(orderId)
                    .strategy(strategy.getStrategyDTO())
                    .currencyPair(currencyPair)
                    .amount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .leverage(getLeverage().toString())
                    .cumulativeAmount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .averagePrice(CurrencyAmountDTO.builder()
                            .value(strategy.getLastPriceForCurrencyPair(currencyPair))
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .limitPrice(CurrencyAmountDTO.builder()
                            .value(limitPrice)
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .status(PENDING_NEW)
                    .trades(new HashSet<>())
                    .timestamp(ZonedDateTime.now())
                    .build();

            // We save the order.
            final OrderDTO saved = ActiveOrderController.create(orderId, order);
            final OrderCreationResultDTO result = new OrderCreationResultDTO(saved);
            logger.debug("Order creation result: {}", result);
            return result;
        } catch (Exception e) {
            final String error = "Error submitting limit order for " + amount + " " + currencyPair + ": " + e.getMessage();
            logger.error(error);
            return new OrderCreationResultDTO(e.getMessage(), e);
        }
    }

    /**
     * Builds a stop order with the given parameters.
     *
     * @param strategy     the strategy
     * @param type         the order type (ASK or BID)
     * @param currencyPair currency pair
     * @param intention    intention (stop entry or stop loss)
     * @param amount       amount
     * @param stopPrice    in a STOP_LOSS trigger is at or better than this price
     *                     in a TAKE_PROFIT trigger is at or better than this price.
     * @param limitPrice   in a BID this is the highest acceptable price, in an ASK
     *                     this is the lowest acceptable price
     * @return the created order
     */
    public OrderDTO buildStopOrder(final GenericCassandreStrategy strategy,
                                   final OrderTypeDTO type,
                                   final CurrencyPairDTO currencyPair,
                                   final OrderIntentionDTO intention,
                                   final BigDecimal amount,
                                   final BigDecimal stopPrice,
                                   final BigDecimal limitPrice) {

        int baseScale = currencyPair.getBaseScale();
        int quoteScale = currencyPair.getQuoteScale();

        // Sending & creating the order.
        return OrderDTO.builder()
            .type(type)
            .strategy(strategy.getStrategyDTO())
            .currencyPair(currencyPair)
            .amount(CurrencyAmountDTO.builder()
                    .value(amount.setScale(baseScale, HALF_EVEN))
                    .currency(currencyPair.getBaseCurrency())
                    .build())
            .leverage(getLeverage().toString())
            .cumulativeAmount(CurrencyAmountDTO.builder()
                    .value(amount.setScale(baseScale, HALF_EVEN))
                    .currency(currencyPair.getBaseCurrency())
                    .build())
            .averagePrice(CurrencyAmountDTO.builder()
                    .value(strategy.getLastPriceForCurrencyPair(currencyPair).setScale(quoteScale, HALF_EVEN))
                    .currency(currencyPair.getQuoteCurrency())
                    .build())
            .intention(intention)
            .stopPrice(CurrencyAmountDTO.builder()
                    .value(stopPrice.setScale(quoteScale, HALF_EVEN))
                    .currency(currencyPair.getQuoteCurrency())
                    .build())
            .limitPrice(CurrencyAmountDTO.builder()
                    .value(limitPrice.setScale(quoteScale, HALF_EVEN))
                    .currency(currencyPair.getQuoteCurrency())
                    .build())
            .status(PENDING_NEW)
            .trades(new HashSet<>())
            .timestamp(ZonedDateTime.now())
            .build();
    }

    private void submitStopOrder(final OrderDTO source,
                                 final Set<IOrderFlags> flags,
                                 final CompletableFuture<OrderDTO> futureOrder) {

        try {
            // Making the order.
            StopOrder order
                = new StopOrder(UTIL_MAPPER.mapToOrderType(source.getType()),
                    source.getAmountValue(),
                    CURRENCY_MAPPER.mapToCurrencyPair(source.getCurrencyPair()),
                    getGeneratedClientOid(),
                    null,
                    source.getStopPriceValue(),
                    source.getLimitPriceValue(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    OrderStatus.PENDING_NEW,
                    null,
                    UTIL_MAPPER.mapToOrderIntention(source.getIntention()));
            order.setLeverage(getLeverage().toString());
            if (flags != null) {
                flags.forEach(order::addOrderFlag);
            }

            logger.debug("Sent stop order {} {} @ {}",
                    source.getType(), source.getAmountValue(), order.getLimitPrice());

            final String orderId = tradeService.placeStopOrder(order);

            final OrderDTO result = ActiveOrderController.create(orderId, source);
            futureOrder.complete(result);

            logger.debug("Order creation result: {}", result);

        } catch (Exception e) {
            futureOrder.completeExceptionally(e);

            logger.error("Could not create stop order {} {} @ {}: {}",
                    source.getType(), source.getAmount(), source.getLimitPrice(), e.getMessage());
        }
    }

    /**
     * Creates buy stop order.
     *
     * @param order        strategy
     * @param flags        Exchange-specific flags
     * @return order creation result
     */
    public CompletableFuture<OrderDTO> submitStopOrder(final OrderDTO order,
                                                       final Set<IOrderFlags> flags) {

        CompletableFuture<OrderDTO> futureOrder = new CompletableFuture<>();
        executor.submit(() -> submitStopOrder(order, flags, futureOrder));
        return futureOrder;
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createBuyMarketOrder(final GenericCassandreStrategy strategy,
                                                       final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount,
                                                       final Set<IOrderFlags> flags) {
        return createMarketOrder(strategy, BID, currencyPair, amount, flags);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createBuyMarketOrder(final GenericCassandreStrategy strategy,
                                                       final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount) {

        return createMarketOrder(strategy, BID, currencyPair, amount, null);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createSellMarketOrder(final GenericCassandreStrategy strategy,
                                                        final CurrencyPairDTO currencyPair,
                                                        final BigDecimal amount,
                                                        final Set<IOrderFlags> flags) {

        return createMarketOrder(strategy, ASK, currencyPair, amount, flags);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createSellMarketOrder(final GenericCassandreStrategy strategy,
                                                        final CurrencyPairDTO currencyPair,
                                                        final BigDecimal amount) {

        return createMarketOrder(strategy, ASK, currencyPair, amount, null);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createBuyLimitOrder(final GenericCassandreStrategy strategy,
                                                      final CurrencyPairDTO currencyPair,
                                                      final BigDecimal amount,
                                                      final BigDecimal limitPrice,
                                                      final Set<IOrderFlags> flags) {

        return createLimitOrder(strategy, BID, currencyPair, amount, limitPrice, flags);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createBuyLimitOrder(final GenericCassandreStrategy strategy,
                                                      final CurrencyPairDTO currencyPair,
                                                      final BigDecimal amount,
                                                      final BigDecimal limitPrice) {

        return createLimitOrder(strategy, BID, currencyPair, amount, limitPrice, null);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createSellLimitOrder(final GenericCassandreStrategy strategy,
                                                       final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount,
                                                       final BigDecimal limitPrice,
                                                       final Set<IOrderFlags> flags) {

        return createLimitOrder(strategy, ASK, currencyPair, amount, limitPrice, flags);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createSellLimitOrder(final GenericCassandreStrategy strategy,
                                                       final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount,
                                                       final BigDecimal limitPrice) {

        return createLimitOrder(strategy, ASK, currencyPair, amount, limitPrice, null);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public Boolean cancelOrder(final String orderId) {
        logger.debug("Canceling order {}", orderId);
        if (orderId != null) {
            try {
                // We retrieve the order information.
                final Optional<Order> order = ActiveOrderController.findByOrderId(orderId);
                if (order.isPresent()) {
                    OrderDTO orderDTO = ORDER_MAPPER.mapToOrderDTO(order.get());

                    // Using a special object to specify which order to cancel.
                    final CancelOrderParams cancelOrderParams = new CancelOrderParams(
                            orderDTO.getOrderId(),
                            CURRENCY_MAPPER.mapToCurrencyPair(orderDTO.getCurrencyPair()),
                            UTIL_MAPPER.mapToOrderType(orderDTO.getType()));
                    logger.debug("Canceling order {}", orderId);
                    return tradeService.cancelOrder(cancelOrderParams);
                } else {
                    logger.debug(" Error canceling order {}: order not found in database", orderId);
                    return false;
                }
            } catch (Exception e) {
                logger.debug("Error canceling order {}: {}", orderId, e.getMessage());
                return false;
            }
        } else {
            logger.error("Error canceling order, order id provided is null");
            return false;
        }
    }

    // Get repo orders
    // Get active orders from remote
    // For each active order,
    //   If active order is in repo and is different, then collect it
    // For any order in the repo for which we have not received an update
    //   Poll the status of that order (maybe it closed)
    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public Set<OrderDTO> getOrders() {
        logger.debug("Getting orders from exchange");
        try {
            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            bucket.asBlocking().consume(1);

            final List<Order> orders = ActiveOrderController.findByStatus(NEW);
            if (!orders.isEmpty()) {
                Map<String, OrderDTO> repoOrder = new HashMap<>();
                Map<String, OrderDTO> xchangeOrder = new HashMap<>();

                //Exchange query conditions
                List<DefaultQueryOrderParamCurrencyPair> orderQueryParamCurrencyPairList = orders
                        .stream()
                        .peek(order -> repoOrder.put(order.getOrderId(), ORDER_MAPPER.mapToOrderDTO(order)))
                        .map(order -> new DefaultQueryOrderParamCurrencyPair(new CurrencyPair(order.getCurrencyPair()), order.getOrderId()))
                        .collect(Collectors.toList());

                //Query the status of the order in the exchange
                tradeService.getOrder(orderQueryParamCurrencyPairList.toArray(new DefaultQueryOrderParamCurrencyPair[]{}))
                        .stream()
                        .filter(order -> repoOrder.containsKey(order.getId()))
                        .map(order -> {
                            if (order instanceof StopOrder) {
                                return ORDER_MAPPER.mapToOrderDTO((StopOrder) order);
                            } else if (order instanceof LimitOrder) {
                                return ORDER_MAPPER.mapToOrderDTO((LimitOrder) order);
                            } else {
                                return ORDER_MAPPER.mapToOrderDTO((MarketOrder) order);
                            }
                        })
                        .filter(orderDTO -> !repoOrder.get(orderDTO.getOrderId()).equals(orderDTO))
                        .forEach(orderDTO -> xchangeOrder.put(orderDTO.getOrderId(), orderDTO));

                if (!xchangeOrder.isEmpty()) {
                    return xchangeOrder.values().stream()
                            .sorted(Comparator.comparing(OrderDTO::getTimestamp))
                            .peek(orderDTO -> logger.debug("Local order retrieved: {}", orderDTO))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                }
            }

            return tradeService.getOpenOrders()
                    .getOpenOrders()
                    .stream()
                    .map(order -> {
                        if (order instanceof StopOrder) {
                            return ORDER_MAPPER.mapToOrderDTO((StopOrder) order);
                        } else if (order instanceof LimitOrder) {
                            return ORDER_MAPPER.mapToOrderDTO((LimitOrder) order);
                        } else {
                            return ORDER_MAPPER.mapToOrderDTO((MarketOrder) order);
                        }
                    })
//                  .sorted(Comparator.comparing(OrderDTO::getTimestamp))
                    .peek(orderDTO -> logger.debug("Remote order retrieved: {}", orderDTO))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (NotAvailableFromExchangeException e) {
            // If the classical call to getOpenOrders() is not implemented, we use the specific parameters that asks for currency pair.
            Set<OrderDTO> orders = new LinkedHashSet<>();
            ActiveOrderController.findAll()
                    .stream()
                    .map(Base.ORDER_MAPPER::mapToOrderDTO)
                    // We only ask for currency pairs of the non fulfilled orders.
                    .filter(orderDTO -> !orderDTO.isFulfilled())
                    .map(OrderDTO::getCurrencyPair)
                    .distinct()
                    .forEach(currencyPairDTO -> {
                        try {
                            // Consume a token from the token bucket.
                            // If a token is not available this method will block until the refill adds one to the bucket.
                            bucket.asBlocking().consume(1);
                            orders.addAll(tradeService.getOpenOrders(new DefaultOpenOrdersParamCurrencyPair(Base.CURRENCY_MAPPER.mapToCurrencyPair(currencyPairDTO)))
                                    .getOpenOrders()
                                    .stream()
                                    .map(order -> {
                                        if (order instanceof StopOrder) {
                                            return ORDER_MAPPER.mapToOrderDTO((StopOrder) order);
                                        } else if (order instanceof LimitOrder) {
                                            return ORDER_MAPPER.mapToOrderDTO((LimitOrder) order);
                                        } else {
                                            return ORDER_MAPPER.mapToOrderDTO((MarketOrder) order);
                                        }
                                    })
//                                  .sorted(Comparator.comparing(OrderDTO::getTimestamp))
                                    .peek(orderDTO -> logger.debug("Remote order retrieved: {}", orderDTO))
                                    .collect(Collectors.toCollection(LinkedHashSet::new)));
                        } catch (IOException | InterruptedException specificOrderException) {
                            logger.error("Error retrieving orders: {}", specificOrderException.getMessage());
                        }
                    });
            return orders;
        } catch (CurrencyPairNotValidException e) {
            logger.error("Error retrieving orders(If it is a simulated environment, please configure simulated.json): {}", e.getMessage());
            return Collections.emptySet();
        } catch (IOException e) {
            logger.error("Error retrieving orders: {}", e.getMessage());
            return Collections.emptySet();
        } catch (InterruptedException e) {
            return Collections.emptySet();
        }
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public Set<TradeDTO> getTrades() {
        logger.debug("Getting trades from exchange");
        // Query trades from the last 24 jours (24 hours is the maximum because of Binance limitations).
        TradeHistoryParamsAll params = new TradeHistoryParamsAll();
        Date now = new Date();
        Date startDate = DateUtils.addDays(now, -1);
        params.setStartTime(startDate);
        params.setEndTime(now);

        // We only ask for trades with currency pairs that was used in the previous orders we made.
        // And we only choose the orders that are not fulfilled.
        final LinkedHashSet<CurrencyPairDTO> currencyPairs = ActiveOrderController.findByOrderByTimestampAsc()
                .stream()
                .map(ORDER_MAPPER::mapToOrderDTO)
                .filter(orderDTO -> !orderDTO.isFulfilled())
                .map(OrderDTO::getCurrencyPair)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<TradeDTO> results = new LinkedHashSet<>();
        // We set currency pairs on each param (required for exchanges like Gemini or Binance).
        if (!currencyPairs.isEmpty()) {
            currencyPairs.forEach(pair -> {
                params.setCurrencyPair(CURRENCY_MAPPER.mapToCurrencyPair(pair));
                try {
                    // Consume a token from the token bucket.
                    // If a token is not available this method will block until the refill adds one to the bucket.
                    bucket.asBlocking().consume(1);
                    results.addAll(
                            tradeService.getTradeHistory(params)
                                    .getUserTrades()
                                    .stream()
                                    .map(TRADE_MAPPER::mapToTradeDTO)
                                    .sorted(Comparator.comparing(TradeDTO::getTimestamp))
                                    .collect(Collectors.toCollection(LinkedHashSet::new))
                    );
                } catch (IOException e) {
                    logger.error("Error retrieving trades: {}", e.getMessage());
                } catch (InterruptedException e) {
                    logger.error("InterruptedException: {}", e.getMessage());
                }
            });
        }
        logger.debug("{} trade(s) found", results.size());
        return results;
    }

    /**
     * Returns a local generated order id.
     *
     * @return generated order id
     */
    private String getGeneratedClientOid() {
        if (driverClassName.toLowerCase(Locale.ROOT).contains("okex")) {
            // If we are on Okex, we use Cassandre broker id to get a reward.
            return OKEX_BROKER_ID + RandomStringUtils.random(GENERATED_ORDER_SIZE - OKEX_BROKER_ID.length(), true, true);
        } else {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    /**
     * Set leverage value used for future orders.
     *
     * @param value the leverage value
     */
    public void setLeverage(final BigDecimal value) {
        this.leverage = value;
    }

    /**
     * Get leverage value used for future orders.
     *
     * @return the leverage value
     */
    public BigDecimal getLeverage() {
        return leverage;
    }

}
