package tech.cassandre.trading.bot.service;

import lombok.RequiredArgsConstructor;
import org.knowm.xchange.Exchange;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Exchange service - XChange implementation.
 */
@RequiredArgsConstructor
public class ExchangeServiceXChangeImplementation extends BaseService implements ExchangeService {

    /** XChange service. */
    private final Exchange exchange;

    /** Set of available currency pairs. */
    private final LinkedHashSet<CurrencyPairDTO> currencyPairs = new LinkedHashSet<>();

    /**
     * getExchange.
     * @return the exchange
     */
    @Override
    public Exchange getExchange() {
        return exchange;
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public Set<CurrencyPairDTO> getAvailableCurrencyPairs() {
        if (currencyPairs.isEmpty()) {
            logger.debug("Retrieving available currency pairs");
            exchange.getExchangeMetaData()
                    .getCurrencyPairs()
                    .forEach((pair, meta) -> {
                        currencyPairs.add(
                            CurrencyPairDTO.getInstance(
                                pair.base.getCurrencyCode(),
                                pair.counter.getCurrencyCode(),
                                meta.getAmountStepSize().scale(),
                                meta.getPriceScale()
                            )
                        );
                    });
        }
        return currencyPairs;
    }

}
