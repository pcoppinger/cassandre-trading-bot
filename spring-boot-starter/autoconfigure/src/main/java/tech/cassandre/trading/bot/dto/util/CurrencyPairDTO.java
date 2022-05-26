package tech.cassandre.trading.bot.dto.util;

import lombok.Builder;
import lombok.Value;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Objects;

/**
 * Currency pair for trading.
 * The base currency represents how much of the quote currency to get one unit of the base currency.
 * For example, if you were looking at the CAD/USD currency pair, the Canadian dollar would be the base currency, and the U.S. dollar would be the quote currency.
 */
@Value
@Builder
@SuppressWarnings("checkstyle:VisibilityModifier")
public final class CurrencyPairDTO {

    /** Currency pair separator. */
    public static final String CURRENCY_PAIR_SEPARATOR = "/";

    /** Currency pair default precision. */
    private static final Integer DEFAULT_CURRENCY_SCALE = 8;

    /** Currency pair default precision. */
    private static final BigDecimal DEFAULT_LOT_MULTIPLIER = BigDecimal.ONE;

    /** Set of all defined currency pairs. */
    private static final HashMap<String, CurrencyPairDTO> CURRENCY_PAIRS = new HashMap<>();

    /** The base currency is the first currency appearing in a currency pair quotation. */
    CurrencyDTO baseCurrency;

    /** The quote currency is the second currency appearing in a currency pair quotation. */
    CurrencyDTO quoteCurrency;

    /** The base currency scale. */
    int baseScale;

    /** The quote currency scale. */
    int quoteScale;

    /**
     * Static singleton builder.
     *
     * @param currencyPair currency pair descriptor
     * @return the matched currency pair
     */
    public static CurrencyPairDTO getInstance(final String currencyPair) {
        CurrencyPairDTO dto = CURRENCY_PAIRS.get(currencyPair);
        if (dto == null) {
            dto = new CurrencyPairDTO(currencyPair);
            CURRENCY_PAIRS.put(currencyPair, dto);
        }
        return dto;
    }

    /**
     * Static singleton builder.
     *
     * @param base the base currency code
     * @param quote the quote currency code
     * @return the matched currency pair
     */
    public static CurrencyPairDTO getInstance(final String base, final String quote) {
        return getInstance(base + CURRENCY_PAIR_SEPARATOR + quote);
    }

    /**
     * Static singleton builder.
     *
     * @param base the base currency code
     * @param quote the quote currency code
     * @param baseScale the base currency scale
     * @param quoteScale the quote currency scale
     * @return the matched currency pair
     */
    public static CurrencyPairDTO getInstance(final String base,
                                              final String quote,
                                              final int baseScale,
                                              final int quoteScale) {
        final String key = base + CURRENCY_PAIR_SEPARATOR + quote;
        CurrencyPairDTO dto = CURRENCY_PAIRS.get(key);
        if (dto == null || dto.baseScale == baseScale || dto.quoteScale != quoteScale) {
            CURRENCY_PAIRS.remove(key);
            dto = new CurrencyPairDTO(base, quote, baseScale, quoteScale);
            CURRENCY_PAIRS.put(key, dto);
        }
        return dto;
    }

    /**
     * Static singleton builder.
     *
     * @param base  The base currency
     * @param quote The quote currency
     * @return the matched currency pair
     */
    public static CurrencyPairDTO getInstance(final CurrencyDTO base, final CurrencyDTO quote) {
        return getInstance(base.getCurrencyCode(), quote.getCurrencyCode());
    }

    /**
     * Static singleton builder.
     *
     * @param instrument currency pair descriptor
     * @return the matched currency pair
     */
    public static CurrencyPairDTO getInstance(final Instrument instrument) {
        final CurrencyPair cp = (CurrencyPair) instrument;
        return getInstance(cp.base.getCurrencyCode() + CURRENCY_PAIR_SEPARATOR + cp.counter.getCurrencyCode());
    }

    /**
     * Constructor.
     *
     * @param currencyPair currency pair
     */
    private CurrencyPairDTO(final String currencyPair) {
        this(currencyPair.split(CURRENCY_PAIR_SEPARATOR)[0], currencyPair.split(CURRENCY_PAIR_SEPARATOR)[1]);
    }

    /**
     * Constructor.
     *
     * @param currencyPair currency pair
     */
    private CurrencyPairDTO(final CurrencyPair currencyPair) {
        this(currencyPair.base.toString(), currencyPair.counter.toString());
    }

    /**
     * Constructor with {@link CurrencyDTO}.
     *
     * @param newBaseCurrency  The base currency
     * @param newQuoteCurrency The quote currency
     */
    private CurrencyPairDTO(final String newBaseCurrency, final String newQuoteCurrency) {
        this(CurrencyDTO.getInstance(newBaseCurrency), CurrencyDTO.getInstance(newQuoteCurrency),
                DEFAULT_CURRENCY_SCALE, DEFAULT_CURRENCY_SCALE);
    }

    /**
     * Constructor with {@link CurrencyDTO}.
     *
     * @param baseCode the base currency
     * @param quoteCode the quote currency
     * @param baseScale  the base currency scale
     * @param quoteScale the quote currency scale
     */
    @SuppressWarnings("checkstyle:HiddenField")
    private CurrencyPairDTO(final String baseCode,
                           final String quoteCode,
                           final int baseScale,
                           final int quoteScale) {
        this(CurrencyDTO.getInstance(baseCode), CurrencyDTO.getInstance(quoteCode), baseScale, quoteScale);
    }

    /**
     * Constructor with String.
     *
     * @param newBaseCurrency  The base currency
     * @param newQuoteCurrency The quote currency
     */
    private CurrencyPairDTO(final CurrencyDTO newBaseCurrency, final CurrencyDTO newQuoteCurrency) {
        this(newBaseCurrency, newQuoteCurrency, DEFAULT_CURRENCY_SCALE, DEFAULT_CURRENCY_SCALE);
    }

    /**
     * Constructor with String.
     *
     * @param baseCurrency   The base currency
     * @param quoteCurrency  The quote currency
     * @param baseScale      The base currency scale
     * @param quoteScale     The quote currency precision
     */
    @SuppressWarnings("checkstyle:HiddenField")
    private CurrencyPairDTO(final CurrencyDTO baseCurrency,
                           final CurrencyDTO quoteCurrency,
                           final int baseScale,
                           final int quoteScale) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.baseScale = baseScale;
        this.quoteScale = quoteScale;
    }

    /**
     * Constructor from XChange instrument.
     *
     * @param instrument instrument
     */
    private CurrencyPairDTO(final Instrument instrument) {
        this(((CurrencyPair) instrument).base.getCurrencyCode(),
             ((CurrencyPair) instrument).counter.getCurrencyCode(),
             DEFAULT_CURRENCY_SCALE,
             DEFAULT_CURRENCY_SCALE);
    }
    /**
     * Constructor from XChange instrument.
     *
     * @param instrument    instrument
     * @param baseScale     the base currency scale
     * @param quoteScale    the quote currency scale
     */
    @SuppressWarnings("checkstyle:HiddenField")
    private CurrencyPairDTO(final Instrument instrument,
                            final int baseScale,
                            final int quoteScale) {
        this(((CurrencyPair) instrument).base.getCurrencyCode(),
             ((CurrencyPair) instrument).counter.getCurrencyCode(),
             baseScale, quoteScale);
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CurrencyPairDTO that = (CurrencyPairDTO) o;
        return getBaseCurrency().getCode().equalsIgnoreCase(that.getBaseCurrency().getCode())
                && getQuoteCurrency().getCode().equalsIgnoreCase(that.getQuoteCurrency().getCode());
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public int hashCode() {
        return Objects.hash(getBaseCurrency().getCode(), getQuoteCurrency().getCode());
    }

    @Override
    public String toString() {
        return baseCurrency + CURRENCY_PAIR_SEPARATOR + quoteCurrency;
    }

}
