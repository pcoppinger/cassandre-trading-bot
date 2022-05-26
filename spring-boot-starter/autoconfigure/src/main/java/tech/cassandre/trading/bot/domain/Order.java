package tech.cassandre.trading.bot.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Hibernate;
import tech.cassandre.trading.bot.dto.trade.OrderIntentionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.util.base.domain.BaseDomain;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.jpa.CurrencyAmount;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Order.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "ORDERS", indexes = {
        @Index(columnList = "ORDER_ID"),
        @Index(columnList = "STATUS")
})
public class Order extends BaseDomain {

    /** Technical ID. */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /** An identifier set by the exchange that uniquely identifies the order. */
    @NotNull
    @Column(name = "ORDER_ID", unique = true)
    private String orderId;

    /** Order type i.e. bid (buy) or ask (sell). */
    @Enumerated(STRING)
    @Column(name = "TYPE")
    private OrderTypeDTO type;

    /** The strategy that created the order. */
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "FK_STRATEGY_ID", updatable = false)
    private Strategy strategy;

    /** Currency pair. */
    @Column(name = "CURRENCY_PAIR")
    private String currencyPair;

    /** Amount that was ordered. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "AMOUNT_VALUE", precision = PRECISION, scale = SCALE)),
            @AttributeOverride(name = "currency", column = @Column(name = "AMOUNT_CURRENCY"))
    })
    private CurrencyAmount amount;

    /** Weighted Average price of the fills in the order. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "AVERAGE_PRICE_VALUE", precision = PRECISION, scale = SCALE)),
            @AttributeOverride(name = "currency", column = @Column(name = "AVERAGE_PRICE_CURRENCY"))
    })
    private CurrencyAmount averagePrice;

    /** Stop price. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "STOP_PRICE_VALUE", precision = PRECISION, scale = SCALE)),
            @AttributeOverride(name = "currency", column = @Column(name = "STOP_PRICE_CURRENCY"))
    })
    private CurrencyAmount stopPrice;

    /** Limit price. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "LIMIT_PRICE_VALUE", precision = PRECISION, scale = SCALE)),
            @AttributeOverride(name = "currency", column = @Column(name = "LIMIT_PRICE_CURRENCY"))
    })
    private CurrencyAmount limitPrice;

    /** Market price - The price Cassandre had when the order was created. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "MARKET_PRICE_VALUE", precision = PRECISION, scale = SCALE)),
            @AttributeOverride(name = "currency", column = @Column(name = "MARKET_PRICE_CURRENCY"))
    })
    private CurrencyAmount marketPrice;

    /** The leverage to use for margin related to this order. */
    @Column(name = "LEVERAGE")
    private String leverage;

    /** Order status. */
    @Enumerated(STRING)
    @Column(name = "STATUS")
    private OrderStatusDTO status;

    /** Amount to be ordered / amount that has been matched against order on the order book/filled. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "CUMULATIVE_AMOUNT_VALUE", precision = PRECISION, scale = SCALE)),
            @AttributeOverride(name = "currency", column = @Column(name = "CUMULATIVE_AMOUNT_CURRENCY"))
    })
    private CurrencyAmount cumulativeAmount;

    /** An identifier provided by the user on placement that uniquely identifies the order. */
    @Column(name = "USER_REFERENCE")
    private String userReference;

    /** The timestamp of the order. */
    @Column(name = "TIMESTAMP")
    private ZonedDateTime timestamp;

    /** The timestamp when the order was last updated. */
    @Column(name = "XCHANGE_UPDATED_AT")
    private ZonedDateTime updatedAt;

    /** The timestamp when the order entered a final state (filled or canceled). */
    @Column(name = "XCHANGE_END_AT")
    private ZonedDateTime endAt;

    /** Order intention. */
    @Enumerated(STRING)
    @Column(name = "INTENTION")
    private OrderIntentionDTO intention;

    /** Indicates that the stop price was triggered and the order became active. */
    @Column(name = "STOP_TRIGGERED")
    private Boolean stopTriggered;

    /** All trades related to order. */
    @OneToMany(mappedBy = "order", fetch = EAGER)
    @OrderBy("timestamp")
    @ToString.Exclude
    private Set<Trade> trades = new LinkedHashSet<>();

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        final Order that = (Order) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.orderId, that.orderId)
                .append(this.type, that.type)
                .append(this.currencyPair, that.currencyPair)
                .append(this.amount, that.amount)
                .append(this.averagePrice, that.averagePrice)
                .append(this.limitPrice, that.limitPrice)
                .append(this.marketPrice, that.marketPrice)
                .append(this.leverage, that.leverage)
                .append(this.status, that.status)
                .append(this.cumulativeAmount, that.cumulativeAmount)
                .append(this.userReference, that.userReference)
                .append(this.timestamp, that.timestamp)
                .append(this.updatedAt, that.updatedAt)
                .append(this.endAt, that.endAt)
                .append(this.intention, that.intention)
                .append(this.stopPrice, that.stopPrice)
                .append(this.stopTriggered, that.stopTriggered)
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(orderId)
                .toHashCode();
    }

}
