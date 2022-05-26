package tech.cassandre.trading.bot.dto.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static java.math.BigDecimal.ZERO;
import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing an order.
 * An order is a request by an investor to buy or sell.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class OrderDTO {

    /** Technical id. */
    Long id;

    /** An identifier set by the exchange that uniquely identifies the order. */
    String orderId;

    /** Order type i.e. bid (buy) or ask (sell). */
    OrderTypeDTO type;

    /** The strategy that created the order. */
    StrategyDTO strategy;

    /** Currency pair. */
    CurrencyPairDTO currencyPair;

    /** Amount to be ordered / amount that was ordered. */
    CurrencyAmountDTO amount;

    /** Weighted Average price of the fills in the order. */
    CurrencyAmountDTO averagePrice;

    /** Limit price. */
    CurrencyAmountDTO limitPrice;

    /** Market price - The price Cassandre had when the order was created. */
    CurrencyAmountDTO marketPrice;

    /** Stop price - The trigger price to of a stop order. */
    CurrencyAmountDTO stopPrice;

    /** Is Stop Triggered - TRUE is the stop price was hit and the order was activated. */
    Boolean stopTriggered;

    /** The intention of the order (stop-loss or take-profit). */
    OrderIntentionDTO intention;

    /** The leverage to use for margin related to this order. */
    String leverage;

    /** Order status. */
    OrderStatusDTO status;

    /** Amount to be ordered / amount that has been matched against order on the order book/filled. */
    CurrencyAmountDTO cumulativeAmount;

    /** An identifier provided by the user on placement that uniquely identifies the order. */
    String userReference;

    /** The timestamp from the xchange when the order was created. */
    ZonedDateTime timestamp;

    /** The timestamp from xchange when the order was last updated. */
    ZonedDateTime updatedAt;

    /** The timestamp from xchange when the order entered a final state (filled or canceled). */
    ZonedDateTime endAt;

    /** All trades related to the order. */
    Set<TradeDTO> trades;

    /**
     * Returns trade from its id.
     *
     * @param tradeId trade id
     * @return trade
     */
    public Optional<TradeDTO> getTrade(final String tradeId) {
        return trades.stream()
                .filter(t -> t.getTradeId().equals(tradeId))
                .findFirst();
    }

    /**
     * Returns amount value.
     *
     * @return amount value
     */
    public BigDecimal getAmountValue() {
        if (amount == null) {
            return null;
        } else {
            return amount.getValue();
        }
    }

    /**
     * Returns average price value.
     *
     * @return average price value.
     */
    public BigDecimal getAveragePriceValue() {
        if (averagePrice == null) {
            return null;
        } else {
            return averagePrice.getValue();
        }
    }

    /**
     * Returns stop price value.
     *
     * @return stop price value
     */
    public BigDecimal getStopPriceValue() {
        if (stopPrice == null) {
            return null;
        } else {
            return stopPrice.getValue();
        }
    }

    /**
     * Returns limit price value.
     *
     * @return limit price value
     */
    public BigDecimal getLimitPriceValue() {
        if (limitPrice == null) {
            return null;
        } else {
            return limitPrice.getValue();
        }
    }

    /**
     * Returns market price.
     *
     * @return market price value
     */
    public BigDecimal getMarketPriceValue() {
        if (marketPrice == null) {
            return null;
        } else {
            return marketPrice.getValue();
        }
    }

    /**
     * Returns cumulative amount.
     *
     * @return cumulative amount.
     */
    public BigDecimal getCumulativeAmountValue() {
        if (cumulativeAmount == null) {
            return null;
        } else {
            return cumulativeAmount.getValue();
        }
    }

    /**
     * Returns true if the order has been fulfilled with trades.
     *
     * @return true if order completed
     */
    public boolean isFulfilled() {
        return status == OrderStatusDTO.FILLED
                || (getTrades() != null && getTrades()
                        .stream()
                        .map(TradeDTO::getAmountValue)
                        .reduce(ZERO, BigDecimal::add)
                        .compareTo(getAmountValue()) == 0);
    }

    /**
     * equals comparison.
     * @param o the other object
     * @return true or false
     */
    @Override
    @ExcludeFromCoverageGeneratedReport
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrderDTO that = (OrderDTO) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.orderId, that.orderId)
                .append(this.type, that.type)
                .append(this.currencyPair, that.currencyPair)
                .append(this.amount, that.amount)
                .append(this.averagePrice, that.averagePrice)
                .append(this.limitPrice, that.limitPrice)
                .append(this.marketPrice, that.marketPrice)
                .append(this.stopPrice, that.stopPrice)
                .append(this.leverage, that.leverage)
                .append(this.status, that.status)
                .append(this.cumulativeAmount, that.cumulativeAmount)
                .append(this.userReference, that.userReference)
                .append(this.intention, that.intention)
                .append(this.stopTriggered, that.stopTriggered)
                .append(this.updatedAt, that.updatedAt)
                .append(this.endAt, that.endAt)
                .isEquals();
    }

    /**
     * hashCode.
     * @return the hash code
     */
    @Override
    @ExcludeFromCoverageGeneratedReport
    public int hashCode() {
        return new HashCodeBuilder()
                .append(orderId)
                .toHashCode();
    }

}
