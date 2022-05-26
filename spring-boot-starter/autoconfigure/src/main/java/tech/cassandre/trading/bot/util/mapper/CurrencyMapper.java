package tech.cassandre.trading.bot.util.mapper;

import liquibase.pro.packaged.M;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.jpa.CurrencyAmount;

/**
 * Currency mapper.
 */
@Mapper
public interface CurrencyMapper {

    // =================================================================================================================
    // XChange to DTO.

    default String mapToCurrencyString(CurrencyDTO source) {
        if (source != null) {
            return source.toString();
        } else {
            return null;
        }
    }

    default CurrencyDTO mapToCurrencyDTO(String value) {
        return new CurrencyDTO(value);
    }

    @Mapping(source = "currencyCode", target = "code")
    CurrencyDTO mapToCurrencyDTO(Currency source);

    default String mapToCurrencyPairString(CurrencyPairDTO source) {
        return source.toString();
    }

    default CurrencyPairDTO mapToCurrencyPairDTO(Instrument source) {
        final CurrencyPair cp = (CurrencyPair) source;
        CurrencyDTO base = new CurrencyDTO(cp.base.getCurrencyCode());
        CurrencyDTO quote = new CurrencyDTO(cp.counter.getCurrencyCode());
        return CurrencyPairDTO.builder()
                .baseCurrency(base)
                .quoteCurrency(quote)
                .build();
    }

    default CurrencyPairDTO mapToCurrencyPairDTO(String source) {
        return CurrencyPairDTO.getInstance(source);
    }

    @Mapping(source = "base", target = "baseCurrency")
    @Mapping(source = "counter", target = "quoteCurrency")
    @Mapping(target = "baseScale", ignore = true)
    @Mapping(target = "quoteScale", ignore = true)
    CurrencyPairDTO mapToCurrencyPairDTO(CurrencyPair source);

    @Mapping(source = "value", target = "value")
    @Mapping(source = "currency", target = "currency")
    CurrencyAmountDTO mapToCurrencyAmountDTO(CurrencyAmount source);

    // =================================================================================================================
    // XChange to DTO.

    default Currency mapToCurrency(CurrencyDTO source) {
        if (source != null) {
            return new Currency(source.getCode());
        } else {
            return null;
        }
    }

    default CurrencyPair mapToCurrencyPair(CurrencyPairDTO source) {
        return new CurrencyPair(source.getBaseCurrency().getCode(), source.getQuoteCurrency().getCode());
    }

    default Instrument mapToInstrument(CurrencyPairDTO source) {
        return mapToCurrencyPair(source);
    }

    default Instrument mapToInstrument(CurrencyPair source) {
        return mapToCurrencyPair(mapToCurrencyPairDTO(source));
    }

}
