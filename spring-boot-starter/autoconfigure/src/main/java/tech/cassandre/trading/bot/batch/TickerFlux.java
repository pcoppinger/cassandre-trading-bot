package tech.cassandre.trading.bot.batch;

import info.bitrich.xchangestream.core.StreamingExchange;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.ConnectableFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.Base;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Ticker flux - push {@link TickerDTO}.
 */
@RequiredArgsConstructor
public class TickerFlux extends BaseFlux<TickerDTO> implements Observer<Ticker> {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Market service. */
    private final MarketService marketService;

    /** Exchange. */
    private StreamingExchange exchange;

    /**
     * subscribe.
     * @param specification the specification
     * @param currencyPair the currency pair
     * @param consumer the consumer lambda
     * @param errorConsumer the error consumer Lambda
     */
    public void subscribe(final ExchangeSpecification specification,
                          final CurrencyPairDTO currencyPair,
                          final Consumer<? super Set<TickerDTO>> consumer,
                          final Consumer<? super Throwable> errorConsumer) {

        final ConnectableFlux<Set<TickerDTO>> connectableFlux = getFlux().publish();

        if (StreamingExchange.class.isAssignableFrom(specification.getExchangeClass())) {
            if (exchange == null) {
                exchange = (StreamingExchange) ExchangeFactory.INSTANCE.createExchange(specification);
                exchange.connect().blockingAwait();
            }

            exchange.getStreamingMarketDataService()
                    .getTicker(CURRENCY_MAPPER.mapToInstrument(currencyPair))
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
        logger.info("TickerFlux subscription successful");
    }

    /**
     * onNext.
     * @param ticker the ticker from xchange
     */
    public void onNext(@NonNull final Ticker ticker) {
        fluxSink.next(Set.of(Base.TICKER_MAPPER.mapToTickerDTO(ticker)));
    }

    /**
     * onError.
     * @param throwable the throwable
     */
    public void onError(@NonNull final Throwable throwable) {
        logger.error("TickerFlux error: " + throwable.getMessage());
    }

    /**
     * onComplete.
     */
    public void onComplete() {
        logger.info("TickerFlux complete");
    }

    @Override
    protected final Set<TickerDTO> getNewValues() {
        logger.debug("Retrieving tickers from exchange");
        Set<TickerDTO> newValues = new LinkedHashSet<>();

        // We retrieve the list of currency pairs asked by all strategies.
        final LinkedHashSet<CurrencyPairDTO> requestedCurrencyPairs = applicationContext
                .getBeansWithAnnotation(CassandreStrategy.class)
                .values()
                .stream()
                .map(o -> (CassandreStrategyInterface) o)
                .map(CassandreStrategyInterface::getRequestedCurrencyPairs)
                .flatMap(Set::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        try {
            // Get all tickers at once from market service if the method is implemented.
            marketService.getTickers(requestedCurrencyPairs).stream()
                    .filter(Objects::nonNull)
                    .peek(tickerDTO -> logger.debug("New ticker received: {}", tickerDTO))
                    .forEach(newValues::add);
        } catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException e) {
            // If getAllTickers is not available, we retrieve tickers one bye one.
            requestedCurrencyPairs.stream()
                    .filter(Objects::nonNull)
                    .map(marketService::getTicker)
                    .filter(Optional::isPresent)
                    .peek(tickerDTO -> logger.debug("New ticker received: {}", tickerDTO))
                    .map(Optional::get)
                    .forEach(newValues::add);
        }

        return newValues;
    }

    @Override
    protected final Set<TickerDTO> saveValues(final Set<TickerDTO> newValues) {
        // We don't save tickers in database.
        return newValues;
    }

}
