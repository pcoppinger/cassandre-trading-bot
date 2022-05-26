package tech.cassandre.trading.bot.test.core.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.KCS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@DisplayName("DTO - GainDTO")
public class GainDTOTest {

    @Test
    @DisplayName("Check toString() method")
    public void checkToString() {
        final GainDTO gain1 = GainDTO.ZERO;
        assertEquals("No gain", gain1.toString());
        assertEquals(0, gain1.getOrdersFees().size());

        final GainDTO gain2 = GainDTO.builder()
                .percentage(1)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                // Opening order fees.
                // 4.6 BTC
                // 0.5 ETH
                // 3 KCS
                .openingOrderFees(List.of(new CurrencyAmountDTO(new BigDecimal("1.5"), BTC)))
                .openingOrderFees(List.of(new CurrencyAmountDTO(new BigDecimal("0.5"), ETH)))
                .openingOrderFees(List.of(new CurrencyAmountDTO(new BigDecimal("3.1"), BTC)))
                .openingOrderFees(List.of(new CurrencyAmountDTO(new BigDecimal("3"), KCS)))
                // Closing order fees.
                // 0.8 ETH
                // 0.1 BTC
                // 0.9 USDT
                .closingOrderFees(List.of(new CurrencyAmountDTO(new BigDecimal("0.8"), ETH)))
                .closingOrderFees(List.of(new CurrencyAmountDTO(new BigDecimal("0.1"), BTC)))
                .closingOrderFees(List.of(new CurrencyAmountDTO(new BigDecimal("0.9"), USDT)))
                .build();

        // Global gain.
        assertEquals("Gains: 2 BTC (1.0 %)", gain2.toString());
        // Opening order fees list.
        assertEquals(4, gain2.getOpeningOrderFees().size());
        // Closing order fees list.
        assertEquals(3, gain2.getClosingOrderFees().size());
        // Global fees.
        final Map<CurrencyDTO, CurrencyAmountDTO> ordersFees = gain2.getOrdersFees();
        assertEquals(4, ordersFees.size());
        assertEquals(0, new BigDecimal("4.7").compareTo(ordersFees.get(BTC).getValue()));
        assertEquals(0, new BigDecimal("1.3").compareTo(ordersFees.get(ETH).getValue()));
        assertEquals(0, new BigDecimal("3").compareTo(ordersFees.get(KCS).getValue()));
        assertEquals(0, new BigDecimal("0.9").compareTo(ordersFees.get(USDT).getValue()));

    }

    @Test
    @DisplayName("Check isInferiorTo() method")
    public void checkIsInferiorTO() {
        GainDTO gain = GainDTO.builder()
                .percentage(2)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        GainDTO inferiorGain = GainDTO.builder()
                .percentage(1)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        GainDTO superiorGain = GainDTO.builder()
                .percentage(3)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        assertFalse(gain.isInferiorTo(inferiorGain));
        assertTrue(gain.isInferiorTo(superiorGain));
    }

    @Test
    @DisplayName("Check isSuperiorTo() method")
    public void isSuperiorTo() {
        GainDTO gain = GainDTO.builder()
                .percentage(2)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        GainDTO inferiorGain = GainDTO.builder()
                .percentage(1)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        GainDTO superiorGain = GainDTO.builder()
                .percentage(3)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        assertTrue(gain.isSuperiorTo(inferiorGain));
        assertFalse(gain.isSuperiorTo(superiorGain));
    }

}
