package tech.cassandre.trading.bot.service;

import org.knowm.xchange.Exchange;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.util.Set;

/**
 * Service getting information from the exchange.
 */
public interface ExchangeService {

    /**
     * Get the list of available currency pairs for trading.
     *
     * @return list of currency pairs
     */
    Set<CurrencyPairDTO> getAvailableCurrencyPairs();

    Exchange getExchange();
}
