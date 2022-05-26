package tech.cassandre.trading.bot.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
//import lombok.Singular;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing an account owned by a {@link UserDTO}.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class AccountDTO {

    /** A unique identifier for this account. */
    String accountId;

    /** A descriptive name for this account. Defaults to {@link #accountId}. */
    String name;

    /** Account features. */
//  @Singular
    Set<AccountFeatureDTO> features;

    /** Represents the different balances for each currency owned by the account. */
//  @Singular
    Set<BalanceDTO> balances;

    /**
     * Returns balance of a currency.
     *
     * @param currencyCode currency code
     * @return balance
     */
    public Optional<BalanceDTO> getBalance(final String currencyCode) {
        CurrencyDTO currency = CurrencyDTO.getInstanceNoCreate(currencyCode);
        if (currency == null) {
            return Optional.empty();
        } else {
            return getBalance(CurrencyDTO.getInstanceNoCreate(currencyCode));
        }
    }

    /**
     * Returns balance of a currency.
     *
     * @param currency currency
     * @return balance
     */
    public Optional<BalanceDTO> getBalance(final CurrencyDTO currency) {
        return balances.stream()
                .filter(balanceDTO -> balanceDTO.getCurrency() != null)
                .filter(balanceDTO -> balanceDTO.getCurrency().equals(currency))
                .findFirst();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AccountDTO that = (AccountDTO) o;
        // Check that data in account are the same (take time to also check
        boolean equals = new EqualsBuilder()
                .append(this.accountId, that.accountId)
                .append(this.name, that.name)
                .append(this.balances.size(), that.balances.size())
                .isEquals();
        // Test balances.
        if (equals) {
            // We search to see if there is at least one different balance.
            Optional<BalanceDTO> differentBalance = balances.stream()
                    .filter(balanceDTO -> !that.balances.contains(balanceDTO))
                    .findAny();
            if (differentBalance.isPresent()) {
                return false;
            }
        }
        return equals;
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(accountId)
                .toHashCode();
    }

}
