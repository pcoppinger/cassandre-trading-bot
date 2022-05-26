package tech.cassandre.trading.bot.service;

import org.knowm.xchange.dto.Order;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderIntentionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Service getting information about orders and allowing you to create new ones.
 */
public interface TradeService {

    /**
     * Creates a buy market order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param flags        Exchange-specific flags
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createBuyMarketOrder(GenericCassandreStrategy strategy,
                                                CurrencyPairDTO currencyPair,
                                                BigDecimal amount,
                                                Set<Order.IOrderFlags> flags);

    /**
     * Creates a buy market order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createBuyMarketOrder(GenericCassandreStrategy strategy,
                                                CurrencyPairDTO currencyPair,
                                                BigDecimal amount);

    /**
     * Creates a sell market order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param flags        Exchange-specific flags
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createSellMarketOrder(GenericCassandreStrategy strategy,
                                                 CurrencyPairDTO currencyPair,
                                                 BigDecimal amount,
                                                 Set<Order.IOrderFlags> flags);

    /**
    /**
     * Creates a sell market order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createSellMarketOrder(GenericCassandreStrategy strategy,
                                                 CurrencyPairDTO currencyPair,
                                                 BigDecimal amount);

    /**
     * Creates a buy limit order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the highest acceptable price
     * @param flags        Exchange-specific flags
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createBuyLimitOrder(GenericCassandreStrategy strategy,
                                               CurrencyPairDTO currencyPair,
                                               BigDecimal amount,
                                               BigDecimal limitPrice,
                                               Set<Order.IOrderFlags> flags);

    /**
     * Creates a buy limit order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the highest acceptable price
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createBuyLimitOrder(GenericCassandreStrategy strategy,
                                               CurrencyPairDTO currencyPair,
                                               BigDecimal amount,
                                               BigDecimal limitPrice);

    /**
     * Creates a sell limit order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the lowest acceptable price
     * @param flags        Exchange-specific flags
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createSellLimitOrder(GenericCassandreStrategy strategy,
                                                CurrencyPairDTO currencyPair,
                                                BigDecimal amount,
                                                BigDecimal limitPrice,
                                                Set<Order.IOrderFlags> flags);

    /**
     * Creates a sell limit order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the lowest acceptable price
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createSellLimitOrder(GenericCassandreStrategy strategy,
                                                CurrencyPairDTO currencyPair,
                                                BigDecimal amount,
                                                BigDecimal limitPrice);


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
    OrderDTO buildStopOrder(GenericCassandreStrategy strategy,
                            OrderTypeDTO type,
                            CurrencyPairDTO currencyPair,
                            OrderIntentionDTO intention,
                            BigDecimal amount,
                            BigDecimal stopPrice,
                            BigDecimal limitPrice);

    /**
     * Creates submit the stop order to the exchange.
     *
     * @param order        the order
     * @param flags        exchange-specific flags
     * @return order creation result
     */
    CompletableFuture<OrderDTO> submitStopOrder(OrderDTO order, Set<Order.IOrderFlags> flags);

    /**
     * Cancel order.
     *
     * @param orderId order id
     * @return true if cancelled
     */
    Boolean cancelOrder(String orderId);

    /**
     * Get orders from exchange.
     *
     * @return list of orders
     */
    Set<OrderDTO> getOrders();

    /**
     * Get trades from exchange.
     *
     * @return list of orders
     */
    Set<TradeDTO> getTrades();

    /**
     * Set leverage value.
     *
     * @param leverage the default leverage
     */
    void setLeverage(BigDecimal leverage);

    /**
     * Get leverage value.
     *
     * @return the default leverage
     */
    BigDecimal getLeverage();
}
