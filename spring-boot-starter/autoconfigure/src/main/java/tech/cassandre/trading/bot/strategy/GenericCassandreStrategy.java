package tech.cassandre.trading.bot.strategy;

import lombok.NonNull;
import org.knowm.xchange.ExchangeSpecification;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionTypeDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.repository.ImportedTickersRepository;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.mapper.CurrencyMapper;
import tech.cassandre.trading.bot.util.mapper.OrderMapper;
import tech.cassandre.trading.bot.util.mapper.PositionMapper;
import tech.cassandre.trading.bot.util.mapper.TickerMapper;
import tech.cassandre.trading.bot.util.mapper.TradeMapper;

import org.knowm.xchange.dto.Order.IOrderFlags;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Optional;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;

/**
 * Generic Cassandre strategy implementation.
 */
@SuppressWarnings("checkstyle:DesignForExtension")
public abstract class GenericCassandreStrategy implements CassandreStrategyInterface {

    /** Big integer scale. */
    private static final int BIGINTEGER_SCALE = 8;

    /** Currency mapper. */
    protected final CurrencyMapper currencyMapper = Mappers.getMapper(CurrencyMapper.class);

    /** Order mapper. */
    protected final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    /** Trade mapper. */
    protected final TradeMapper tradeMapper = Mappers.getMapper(TradeMapper.class);

    /** Position mapper. */
    protected final PositionMapper positionMapper = Mappers.getMapper(PositionMapper.class);

    /** Ticker mapper. */
    protected final TickerMapper tickerMapper = Mappers.getMapper(TickerMapper.class);

    /** Logger. */
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    /** Strategy. */
    protected StrategyDTO strategy;

    /** Order repository. */
    protected OrderRepository orderRepository;

    /** Trade repository. */
    protected TradeRepository tradeRepository;

    /** Position repository. */
    protected PositionRepository positionRepository;

    /** "Imported tickers" repository. */
    protected ImportedTickersRepository importedTickersRepository;

    /** Exchange service. */
    private ExchangeService exchangeService;

    /** Market service. */
    protected MarketService marketService;

    /** Trade service. */
    protected TradeService tradeService;

    /** Position service. */
    private PositionService positionService;

    /** Ticker flux. */
    protected TickerFlux tickerFlux;

    /** Order flux. */
    protected OrderFlux orderFlux;

    /** Trade flux. */
    protected TradeFlux tradeFlux;

    /** Position flux. */
    private PositionFlux positionFlux;

    /** The accounts owned by the user. */
    private final Map<String, AccountDTO> userAccounts = new LinkedHashMap<>();

    /** Last tickers received. */
    private final Map<CurrencyPairDTO, TickerDTO> lastTickers = new LinkedHashMap<>();

    /** Positions previous status - used for onPositionsStatusUpdates() - Internal use only. */
    private final Map<Long, PositionStatusDTO> previousPositionsStatus = new LinkedHashMap<>();

    /** Dry mode indicator. */
    private boolean dryModeIndicator = false;

    // =================================================================================================================
    // Internal methods to setup dependencies.

    /**
     * getTradeService.
     * @return the trade service
     */
    public TradeService getTradeService() {
        return tradeService;
    }

    /**
     * Getter strategyDTO.
     *
     * @return strategyDTO
     */
    public final StrategyDTO getStrategyDTO() {
        return strategy;
    }
    /**
     * Getter exchangeService.
     *
     * @return exchangeService
     */
    public final ExchangeService getExchangeService() {
        return exchangeService;
    }

    @Override
    public final void setStrategy(final StrategyDTO newStrategyDTO) {
        this.strategy = newStrategyDTO;
    }

    @Override
    public void setDryModeIndicator(final boolean newDryModeIndicator) {
        this.dryModeIndicator = newDryModeIndicator;
    }

    @Override
    public boolean isRunningInDryMode() {
        return this.dryModeIndicator;
    }

    @Override
    public void setPositionFlux(final PositionFlux newPositionFlux) {
        positionFlux = newPositionFlux;
    }

    @Override
    public final void setPositionRepository(final PositionRepository newPositionRepository) {
        this.positionRepository = newPositionRepository;
        // To manage onPositionsStatusUpdates, we retrieve the positions' status from our database.
        this.positionRepository.findByStatusNot(CLOSED).forEach(position -> previousPositionsStatus.put(position.getId(), position.getStatus()));
    }

    @Override
    public final void setOrderRepository(final OrderRepository newOrderRepository) {
        this.orderRepository = newOrderRepository;
    }

    @Override
    public final void setTradeRepository(final TradeRepository newTradeRepository) {
        this.tradeRepository = newTradeRepository;
    }

    @Override
    public void setImportedTickersRepository(final ImportedTickersRepository newImportedTickersRepository) {
        this.importedTickersRepository = newImportedTickersRepository;
    }

    @Override
    public void setExchangeService(final ExchangeService newExchangeService) {
        this.exchangeService = newExchangeService;
    }

    @Override
    public final void setMarketService(final MarketService newMarketService) {
        this.marketService = newMarketService;
    }

    @Override
    public final void setTradeService(final TradeService newTradeService) {
        this.tradeService = newTradeService;
    }

    @Override
    public final void setPositionService(final PositionService newPositionService) {
        this.positionService = newPositionService;
    }

    @Override
    public void initializeAccounts(final Map<String, AccountDTO> newAccounts) {
        userAccounts.putAll(newAccounts);
    }

    // =================================================================================================================
    // Internal methods for event management.
    @Override
    public void accountsUpdates(final Set<AccountDTO> accounts) {
        // We notify the strategy.
        final Map<String, AccountDTO> accountsUpdates = accounts.stream()
                // We store the account values in the strategy.
                .peek(accountDTO -> userAccounts.put(accountDTO.getAccountId(), accountDTO))
                .collect(Collectors.toMap(AccountDTO::getAccountId, Function.identity(), (id, value) -> id, LinkedHashMap::new));

        // We notify the strategy.
        onAccountsUpdates(accountsUpdates);
    }

    @Override
    public void tickersUpdates(final Set<TickerDTO> tickers) {
        // We only retrieve the tickers requested by the strategy.
        final Set<CurrencyPairDTO> desiredPairs = getRequestedCurrencyPairs();
        final Map<CurrencyPairDTO, TickerDTO> tickersUpdates = tickers.stream()
                .filter(tickerDTO -> desiredPairs.contains(tickerDTO.getCurrencyPair()))
                // We update the values of the last tickers that can be found in the strategy.
                .peek(tickerDTO -> lastTickers.put(tickerDTO.getCurrencyPair(), tickerDTO))
                .collect(Collectors.toMap(TickerDTO::getCurrencyPair, Function.identity(), (id, value) -> id, LinkedHashMap::new));

        // We update the positions with tickers.
        updatePositionsWithTickersUpdates(tickersUpdates);

        // We notify the strategy.
        onTickersUpdates(tickersUpdates);
    }

    @Override
    public void ordersUpdates(final Set<OrderDTO> orders) {
        // We only retrieve the orders created by this strategy.
        final Map<String, OrderDTO> ordersUpdates = orders.stream()
                .filter(orderDTO -> orderDTO.getStrategy() == null || orderDTO.getStrategy().getId().equals(strategy.getId()))
                .collect(Collectors.toMap(OrderDTO::getOrderId, Function.identity(), (id, value) -> id, LinkedHashMap::new));

        // We update the positions with orders.
        updatePositionsWithOrdersUpdates(ordersUpdates);

        // We notify the strategy.
        onOrdersUpdates(ordersUpdates);
    }

    @Override
    public void tradesUpdates(final Set<TradeDTO> trades) {
        // We only retrieve the trades created by this strategy.
        final Map<String, TradeDTO> tradesUpdates = trades.stream()
                .filter(tradeDTO -> tradeDTO.getOrder().getStrategy().getId().equals(strategy.getId()))
                .collect(Collectors.toMap(TradeDTO::getTradeId, Function.identity(), (id, value) -> id, LinkedHashMap::new));

        // We update the positions with trades.
        updatePositionsWithTradesUpdates(tradesUpdates);

        // We notify the strategy.
        onTradesUpdates(tradesUpdates);
    }

    @Override
    public void positionsUpdates(final Set<PositionDTO> positions) {
        final Map<Long, PositionDTO> positionsUpdates = new HashMap<>();
        final Map<Long, PositionDTO> positionsStatusUpdates = new HashMap<>();

        positions.stream()
                .filter(positionDTO -> positionDTO.getStrategy().getId().equals(strategy.getId()))
                .forEach(positionDTO -> {
                    positionsUpdates.put(positionDTO.getPositionId(), positionDTO);

                    // From positionUpdate(), we see if it's also a onPositionStatusUpdate().
                    if (previousPositionsStatus.get(positionDTO.getId()) != positionDTO.getStatus()) {
                        if (positionDTO.getStatus() == CLOSED) {
                            // As CLOSED positions cannot change anymore, we don't need to store their previous positions.
                            previousPositionsStatus.remove(positionDTO.getId());
                        } else {
                            // As we have a new status for this position, we update it in previousPositionsStatus.
                            previousPositionsStatus.put(positionDTO.getId(), positionDTO.getStatus());
                        }
                        positionsStatusUpdates.put(positionDTO.getPositionId(), positionDTO);
                    }
                });

        // We notify the strategy.
        onPositionsUpdates(positionsUpdates);
        onPositionsStatusUpdates(positionsStatusUpdates);
    }

    // =================================================================================================================
    // Related to accounts.

    /**
     * Returns list of accounts.
     *
     * @return accounts
     */
    public final Map<String, AccountDTO> getAccounts() {
        return userAccounts;
    }

    /**
     * Search and return and account by its id.
     *
     * @param accountId account id
     * @return account
     */
    public final Optional<AccountDTO> getAccountByAccountId(final String accountId) {
        if (userAccounts.containsKey(accountId)) {
            return Optional.of(userAccounts.get(accountId));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public final Optional<AccountDTO> getTradeAccount() {
        return getTradeAccount(new LinkedHashSet<>(getAccounts().values()));
    }

    /**
     * Getter amountsLockedByPosition.
     *
     * @return amountsLockedByPosition
     */
    public final Map<Long, CurrencyAmountDTO> getAmountsLockedByPosition() {
        return positionService.getAmountsLockedByPosition();
    }

    /**
     * Returns the amounts locked for a specific currency.
     *
     * @param currency currency
     * @return amount
     */
    public final BigDecimal getAmountsLockedByCurrency(final CurrencyDTO currency) {
        return getAmountsLockedByPosition()
                .values()
                .stream()
                .filter(currencyAmount -> currencyAmount.getCurrency().equals(currency))
                .map(CurrencyAmountDTO::getValue)
                .reduce(ZERO, BigDecimal::add);
    }

    // =================================================================================================================
    // Related to tickers.

    /**
     * Return last received tickers.
     *
     * @return ticker
     */
    public final Map<CurrencyPairDTO, TickerDTO> getLastTickers() {
        return lastTickers;
    }

    /**
     * Return the last ticker for a currency pair.
     *
     * @param currencyPair currency pair
     * @return last ticker received
     */
    public final Optional<TickerDTO> getLastTickerByCurrencyPair(@NonNull final CurrencyPairDTO currencyPair) {
        return Optional.ofNullable(lastTickers.get(currencyPair));
    }

    /**
     * Returns the last price received for a currency pair.
     *
     * @param currencyPair currency pair
     * @return last price
     */
    public final BigDecimal getLastPriceForCurrencyPair(final CurrencyPairDTO currencyPair) {
        return getLastTickerByCurrencyPair(currencyPair).map(TickerDTO::getLast).orElse(null);
    }

    /**
     * Return the list of imported tickers (ordered by timestamp).
     *
     * @return imported tickers
     */
    public final List<TickerDTO> getImportedTickers() {
        return importedTickersRepository.findByOrderByTimestampAsc()
                .stream()
                .map(tickerMapper::mapToTickerDTO)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Return the list of imported tickers for a specific currency pair (ordered by timestamp).
     *
     * @param currencyPair currency pair
     * @return imported tickers
     */
    public final List<TickerDTO> getImportedTickers(final CurrencyPairDTO currencyPair) {
        return importedTickersRepository.findByCurrencyPairOrderByTimestampAsc(currencyPair.toString())
                .stream()
                .map(tickerMapper::mapToTickerDTO)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    // =================================================================================================================
    // Related to orders.

    /**
     * Returns list of orders.
     *
     * @return orders
     */
    public final Map<String, OrderDTO> getOrders() {
        return orderRepository.findByOrderByTimestampAsc()
                .stream()
                .filter(order -> order.getStrategy().getStrategyId().equals(strategy.getStrategyId()))
                .map(orderMapper::mapToOrderDTO)
                .collect(Collectors.toMap(OrderDTO::getOrderId, orderDTO -> orderDTO));
    }

    /**
     * Get an order by its id.
     *
     * @param orderId order id
     * @return order
     */
    public final Optional<OrderDTO> getOrderByOrderId(final String orderId) {
        return getOrders().values()
                .stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst();
    }

    // =================================================================================================================
    // Related to trades.

    /**
     * Returns list of trades.
     *
     * @return trades
     */
    public final Map<String, TradeDTO> getTrades() {
        return tradeRepository.findByOrderByTimestampAsc()
                .stream()
                .filter(trade -> trade.getOrder().getStrategy().getStrategyId().equals(strategy.getStrategyId()))
                .map(tradeMapper::mapToTradeDTO)
                .collect(Collectors.toMap(TradeDTO::getTradeId, tradeDTO -> tradeDTO));
    }

    /**
     * Get a trade by its id.
     *
     * @param tradeId trade id
     * @return trade
     */
    public final Optional<TradeDTO> getTradeByTradeId(final String tradeId) {
        return getTrades().values()
                .stream()
                .filter(trade -> trade.getTradeId().equals(tradeId))
                .findFirst();
    }

    // =================================================================================================================
    // Related to positions.

    /**
     * Update strategy positions with tickers updates.
     *
     * @param tickers tickers updates
     */
    void updatePositionsWithTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        // We check if any ticker updates a position, and we close if it's time.
        positionRepository.findByStatusNot(CLOSED)
                .stream()
                .map(positionMapper::mapToPositionDTO)
                // Only the positions of this strategy.
                .filter(positionDTO -> positionDTO.getStrategy().getId().equals(strategy.getId()))
                // Only if we received the ticker requested by the position.
                .filter(positionDTO -> tickers.get(positionDTO.getCurrencyPair()) != null)
                // We send the ticker corresponding to the currency pair of the position. If it returns true, we emit because price changed.
                .filter(positionDTO -> positionDTO.tickerUpdate(tickers.get(positionDTO.getCurrencyPair())))
                .peek(positionDTO -> logger.debug("Position {} updated with ticker {}", positionDTO.getPositionId(), tickers.get(positionDTO.getCurrencyPair())))
                .peek(positionDTO -> positionFlux.emitValue(positionDTO))
                // We only use tickers updates to close a position if position is set to autoclose.
                .filter(PositionDTO::isAutoClose)
                // We check if the position should be closed, if true, we closed and position service will emit the position.
                .filter(PositionDTO::shouldBeClosed)
                .peek(positionDTO -> logger.debug("Closing position {}", positionDTO.getPositionId()))
                .forEach(positionDTO -> positionService.closePosition(this, positionDTO.getId(), tickers.get(positionDTO.getCurrencyPair())));
    }

    /**
     * Update strategy positions with orders updates.
     *
     * @param orders orders updates
     */
    void updatePositionsWithOrdersUpdates(final Map<String, OrderDTO> orders) {
        // We check if any order updates a position.
        orders.values().forEach(orderDTO -> {
            logger.debug("Updating positions with order {}", orderDTO);
            positionRepository.findByStatusNot(CLOSED).stream()
                    .map(positionMapper::mapToPositionDTO)
                    .filter(positionDTO -> positionDTO.getStrategy().getId().equals(strategy.getId()))
                    .filter(positionDTO -> positionDTO.orderUpdate(orderDTO))
                    .peek(positionDTO -> logger.debug("Position {} updated with order {}", positionDTO.getPositionId(), orderDTO))
                    .forEach(positionFlux::emitValue);
        });
    }

    /**
     * Update strategy positions with trades updates.
     *
     * @param trades trades updates
     */
    void updatePositionsWithTradesUpdates(final Map<String, TradeDTO> trades) {
        // We check if any trade updates a position.
        trades.values().forEach(tradeDTO -> {
            logger.debug("Updating positions with trade {}", tradeDTO);
            positionRepository.findByStatusNot(CLOSED).stream()
                    .map(positionMapper::mapToPositionDTO)
                    .filter(positionDTO -> positionDTO.getStrategy().getId().equals(strategy.getId()))
                    .filter(positionDTO -> positionDTO.tradeUpdate(tradeDTO))
                    .peek(positionDTO -> logger.debug("Position {} updated with trade {}", positionDTO.getPositionId(), tradeDTO))
                    .forEach(positionFlux::emitValue);
        });
    }

    /**
     * Returns list of positions.
     *
     * @return positions
     */
    public final Map<Long, PositionDTO> getPositions() {
        return positionRepository.findByOrderById()
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .collect(Collectors.toMap(PositionDTO::getId, positionDTO -> positionDTO));
    }

    /**
     * Returns list of open positions.
     *
     * @return positions
     */
    public final Map<Long, PositionDTO> getOpenPositions() {
        return positionRepository.findByStatusIn(new HashSet<>(Arrays.asList(OPENING, OPENED)))
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .collect(Collectors.toMap(PositionDTO::getId, positionDTO -> positionDTO));
    }
    /**
     * Get a position by its id.
     *
     * @param positionId position id
     * @return position
     */
    public final Optional<PositionDTO> getPositionByPositionId(final long positionId) {
        return positionRepository.findByPositionId(positionId).map(positionMapper::mapToPositionDTO);
    }

    /**
     * Returns gains of all positions.
     *
     * @return total gains
     */
    public final Map<CurrencyDTO, GainDTO> getGains() {
        return positionService.getGains();
    }

    // =================================================================================================================
    // Methods related to creating of orders & positions.

    /**
     * Creates a buy market order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param flags        exchange-specific flags
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createBuyMarketOrder(final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount,
                                                       final Set<IOrderFlags> flags) {

        return tradeService.createBuyMarketOrder(this, currencyPair, amount, flags);
    }

    /**
     * Creates a buy market order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createBuyMarketOrder(final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount) {

        return createBuyMarketOrder(currencyPair, amount, null);
    }

    /**
     * Creates a sell market order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param flags        exchange-specific flags
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createSellMarketOrder(final CurrencyPairDTO currencyPair,
                                                        final BigDecimal amount,
                                                        final Set<IOrderFlags> flags) {

        return tradeService.createSellMarketOrder(this, currencyPair, amount, flags);
    }

    /**
     * Creates a sell market order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createSellMarketOrder(final CurrencyPairDTO currencyPair,
                                                        final BigDecimal amount) {

        return createSellMarketOrder(currencyPair, amount, null);
    }

    /**
     * Creates a buy limit order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the highest acceptable price
     * @param flags        exchange-specific flags
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createBuyLimitOrder(final CurrencyPairDTO currencyPair,
                                                      final BigDecimal amount,
                                                      final BigDecimal limitPrice,
                                                      final Set<IOrderFlags> flags) {

        return tradeService.createBuyLimitOrder(this, currencyPair, amount, limitPrice, flags);
    }

    /**
     * Creates a buy limit order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the highest acceptable price
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createBuyLimitOrder(final CurrencyPairDTO currencyPair,
                                                      final BigDecimal amount,
                                                      final BigDecimal limitPrice) {

        return createBuyLimitOrder(currencyPair, amount, limitPrice, null);
    }

    /**
     * Creates a sell limit order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the lowest acceptable price
     * @param flags        exchange-specific flags
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createSellLimitOrder(final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount,
                                                       final BigDecimal limitPrice,
                                                       final Set<IOrderFlags> flags) {

        return tradeService.createSellLimitOrder(this, currencyPair, amount, limitPrice, flags);
    }

    /**
     * Creates a sell limit order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the lowest acceptable price
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createSellLimitOrder(final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount,
                                                       final BigDecimal limitPrice) {

        return createSellLimitOrder(currencyPair, amount, limitPrice, null);
    }

    /**
     * Cancel order.
     *
     * @param id id
     * @return true if cancelled
     */
    boolean cancelOrder(final long id) {
        final Optional<Order> order = orderRepository.findById(id);
        return order.filter(value -> cancelOrder(value.getOrderId())).isPresent();
    }

    /**
     * Cancel order.
     *
     * @param orderId order id
     * @return true if cancelled
     */
    boolean cancelOrder(final String orderId) {
        return tradeService.cancelOrder(orderId);
    }

    public PositionDTO openPositionWithOrder(final PositionDTO position,
                                             final OrderDTO openingOrder) {
        PositionDTO newPosition = new PositionDTO(position, openingOrder);
        positionFlux.emitValue(newPosition);
        return newPosition;
    }

    /**
     * Creates a long position with its associated rules.
     * Long position is nothing but buying share.
     * If you are bullish (means you think that price of X share will rise) at that time you buy some amount of Share is called taking Long Position in share.
     *
     * @param type      the position type
     * @param order     the opening order
     * @param rules     rules
     * @return position creation result
     */
    public PositionCreationResultDTO createPositionWithOrder(final PositionTypeDTO type,
                                                            final OrderDTO order,
                                                            final PositionRulesDTO rules) {
        return positionService.createPositionWithOrder(this, type, order, rules);
    }

    /**
     * Creates a long position with its associated rules.
     * Long position is nothing but buying share.
     * If you are bullish (means you think that price of X share will rise) at that time you buy some amount of Share is called taking Long Position in share.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param rules        rules
     * @return position creation result
     */
    public PositionCreationResultDTO createLongPosition(final CurrencyPairDTO currencyPair,
                                                        final BigDecimal amount,
                                                        final PositionRulesDTO rules) {
        return positionService.createLongPosition(this, currencyPair, amount, rules);
    }

    /**
     * Creates a short position with its associated rules.
     * Short position is nothing but selling share.
     * If you are bearish (means you think that price of xyz share are going to fall) at that time you sell some amount of share is called taking Short Position in share.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param rules        rules
     * @return position creation result
     */
    public PositionCreationResultDTO createShortPosition(final CurrencyPairDTO currencyPair,
                                                         final BigDecimal amount,
                                                         final PositionRulesDTO rules) {
        return positionService.createShortPosition(this, currencyPair, amount, rules);
    }

    /**
     * Update position rules.
     *
     * @param id       position id
     * @param newRules new rules
     */
    public void updatePositionRules(final long id, final PositionRulesDTO newRules) {
        positionService.updatePositionRules(id, newRules);
    }

    /**
     * Set auto close value on a specific position.
     * If true, Cassandre will close the position according to rules.
     * if false, Cassandre will never close the position.
     *
     * @param id    position technical id
     * @param value auto close value
     */
    public void setAutoClose(final long id, final boolean value) {
        positionService.setAutoClose(id, value);
    }

    /**
     * Close position (no matter the rules).
     * The closing will happen when the next ticker arrives.
     *
     * @param id position id
     */
    public void closePosition(final long id) {
        positionService.forcePositionClosing(id);
    }

    /**
     * Close position (no matter the rules) using the provided order.
     * The closing will happen when the next ticker arrives.
     *
     * @param position the position
     * @param order    the order
     * @return the closed position
     */
    public PositionDTO closePositionWithOrder(final PositionDTO position, final OrderDTO order) {
        position.closePositionWithOrder(order);
        positionFlux.emitValue(position);
        return position;
    }

    /**
     * Close position (no matter the rules).
     * The closing will happen when the next ticker arrives.
     *
     * @param position the position
     */
    public void closePosition(final PositionDTO position) {
        closePosition(position.getId());
        OrderDTO order = position.getOpeningOrder();
        if (order.getStatus().isOpen()) {
            cancelOrder(order.getId());
        }
    }

    // =================================================================================================================
    // Methods that can be implemented by strategies.

    protected void initialize(final ApplicationContext context,
                              final ExchangeSpecification specification,
                              final CurrencyPairDTO currencyPair) {

        tickerFlux = new TickerFlux(context, marketService);
        tickerFlux.subscribe(specification, currencyPair,
                this::tickersUpdates, throwable -> logger.error("TickersUpdates failing: {}.", throwable.getMessage()));

        tradeFlux = new TradeFlux(orderRepository, tradeRepository, tradeService);
        tradeFlux.subscribe(specification, currencyPair,
                this::tradesUpdates, throwable -> logger.error("TradesUpdates failing: {}.", throwable.getMessage()));

        orderFlux = new OrderFlux(tradeService);
        orderFlux.subscribe(specification, currencyPair,
                this::ordersUpdates, throwable -> logger.error("OrdersUpdates failing: {}.", throwable.getMessage()));
    }

    @Override
    public void initialize(final ApplicationContext context,
                           final ExchangeSpecification specification) {

        initialize(context, specification, null);
    }

    @Override
    public void onAccountsUpdates(final Map<String, AccountDTO> accounts) {
    }

    @Override
    public void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
    }

    @Override
    public void onOrdersUpdates(final Map<String, OrderDTO> orders) {
    }

    @Override
    public void onTradesUpdates(final Map<String, TradeDTO> trades) {
    }

    @Override
    public void onPositionsUpdates(final Map<Long, PositionDTO> positions) {
    }

    @Override
    public void onPositionsStatusUpdates(final Map<Long, PositionDTO> positions) {
    }

    // =================================================================================================================
    // Related to canBuy & canSell methods.

    /**
     * Returns the amount of a currency I can buy with a certain amount of another currency.
     *
     * @param amountToUse    amount you want to use buy the currency you want
     * @param currencyWanted the currency you want to buy
     * @return amount of currencyWanted you can buy with amountToUse
     */
    public final Optional<BigDecimal> getEstimatedBuyableAmount(final CurrencyAmountDTO amountToUse, final CurrencyDTO currencyWanted) {
        /*
            symbol=BTC-USDT
            {
              "time": 1637270267065,
              "sequence": "1622704211505",
              "price": "58098.3",
              "size": "0.00001747",
              "bestBid": "58098.2",
              "bestBidSize": "0.038",
              "bestAsk": "60000",
              "bestAskSize": "0.27476785"
            }
            This means 1 Bitcoin can be bought with 60000 USDT.
         */
        final TickerDTO ticker = lastTickers.get(CurrencyPairDTO.getInstance(currencyWanted, amountToUse.getCurrency()));
        if (ticker == null) {
            // No ticker for this currency pair.
            return Optional.empty();
        } else {
            // Make the calculation.
            // amountToUse: 150 000 USDT.
            // CurrencyWanted: BTC.
            // How much BTC I can buy ? amountToUse / last
            return Optional.of(amountToUse.getValue().divide(ticker.getLast(), BIGINTEGER_SCALE, HALF_EVEN));
        }
    }

    /**
     * Returns the cost of buying an amount of a currency pair.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return cost
     */
    public final Optional<CurrencyAmountDTO> getEstimatedBuyingCost(final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        /*
            symbol=ETH-BTC
            {
              "time": 1598626640265,
              "sequence": "1594421123246",
              "price": "0.034227",
              "size": "0.0200088",
              "bestBid": "0.034226",
              "bestBidSize": "6.3384368",
              "bestAsk": "0.034227",
              "bestAskSize": "18.6378851"
            }
            This means 1 Ether can be bought with 0.034227 Bitcoin.
         */

        // We get the last ticker from the last values received.
        final TickerDTO ticker = lastTickers.get(currencyPair);
        if (ticker == null) {
            // No ticker for this currency pair.
            return Optional.empty();
        } else {
            // Make the calculation.
            return Optional.of(CurrencyAmountDTO.builder()
                    .value(ticker.getLast().multiply(amount))
                    .currency(currencyPair.getQuoteCurrency())
                    .build());
        }
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return true if we have enough assets to buy
     */
    public final boolean canBuy(final CurrencyPairDTO currencyPair,
                                final BigDecimal amount) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canBuy(account, currencyPair, amount)).isPresent();
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param currencyPair        currency pair
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after buying
     * @return true if we have enough assets to buy
     */
    public final boolean canBuy(final CurrencyPairDTO currencyPair,
                                final BigDecimal amount,
                                final BigDecimal minimumBalanceAfter) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canBuy(account, currencyPair, amount, minimumBalanceAfter)).isPresent();
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param account      account
     * @param currencyPair currency pair
     * @param amount       amount
     * @return true if we have enough assets to buy
     */
    public final boolean canBuy(final AccountDTO account,
                                final CurrencyPairDTO currencyPair,
                                final BigDecimal amount) {
        return canBuy(account, currencyPair, amount, ZERO);
    }

    /**
     * Returns true if we have enough assets to buy and if minimumBalanceAfter is left on the account after.
     *
     * @param account             account
     * @param currencyPair        currency pair
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after buying
     * @return true if we have enough assets to buy
     */
    public final boolean canBuy(final AccountDTO account,
                                final CurrencyPairDTO currencyPair,
                                final BigDecimal amount,
                                final BigDecimal minimumBalanceAfter) {
        // We get the amount.
        final Optional<BalanceDTO> balance = account.getBalance(currencyPair.getQuoteCurrency());
        if (balance.isPresent()) {
            // We get the estimated cost of buying.
            final Optional<CurrencyAmountDTO> estimatedBuyingCost = getEstimatedBuyingCost(currencyPair, amount);

            // We calculate.
            // Balance in the account
            // Minus
            // Estimated cost
            // Must be superior to zero
            // If there is no way to calculate the price for the moment (no ticker).
            return estimatedBuyingCost.filter(currencyAmountDTO -> balance.get().getAvailable()
                    .subtract(currencyAmountDTO.getValue().add(minimumBalanceAfter).add(getAmountsLockedByCurrency(currencyAmountDTO.getCurrency())))
                    .compareTo(ZERO) > 0).isPresent() || estimatedBuyingCost.filter(currencyAmountDTO -> balance.get().getAvailable()
                    .subtract(currencyAmountDTO.getValue().add(minimumBalanceAfter).add(getAmountsLockedByCurrency(currencyAmountDTO.getCurrency())))
                    .compareTo(ZERO) == 0).isPresent();
        } else {
            // If the is no balance in this currency, we can't buy.
            return false;
        }
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param currency currency
     * @param amount   amount
     * @return true if we have enough assets to sell
     */
    public final boolean canSell(final CurrencyDTO currency,
                                 final BigDecimal amount) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canSell(account, currency, amount)).isPresent();
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param currency            currency
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after selling
     * @return true if we have enough assets to sell
     */
    public final boolean canSell(final CurrencyDTO currency,
                                 final BigDecimal amount,
                                 final BigDecimal minimumBalanceAfter) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canSell(account, currency, amount, minimumBalanceAfter)).isPresent();
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param account  account
     * @param currency currency pair
     * @param amount   amount
     * @return true if we have enough assets to sell
     */
    public final boolean canSell(final AccountDTO account,
                                 final CurrencyDTO currency,
                                 final BigDecimal amount) {
        return canSell(account, currency, amount, ZERO);
    }

    /**
     * Returns true if we have enough assets to sell and if minimumBalanceAfter is left on the account after.
     *
     * @param account             account
     * @param currency            currency
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after selling
     * @return true if we have enough assets to sell
     */
    public final boolean canSell(final AccountDTO account,
                                 final CurrencyDTO currency,
                                 final BigDecimal amount,
                                 final BigDecimal minimumBalanceAfter) {
        // We get the amount.
        final Optional<BalanceDTO> balance = account.getBalance(currency);
        // public int compareTo(BigDecimal bg) returns
        // 1: if value of this BigDecimal is greater than that of BigDecimal object passed as parameter.
        // If the is no balance in this currency, we can't buy.
        return balance.filter(balanceDTO -> balanceDTO.getAvailable().subtract(amount).subtract(minimumBalanceAfter).subtract(getAmountsLockedByCurrency(currency)).compareTo(ZERO) > 0
                || balanceDTO.getAvailable().subtract(amount).subtract(minimumBalanceAfter).subtract(getAmountsLockedByCurrency(currency)).compareTo(ZERO) == 0).isPresent();
    }

}
