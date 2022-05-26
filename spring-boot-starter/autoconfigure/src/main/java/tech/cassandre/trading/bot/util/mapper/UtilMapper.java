package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.StopOrder;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import tech.cassandre.trading.bot.dto.trade.OrderIntentionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;

/**
 * Util mapper.
 */
@Mapper(uses = {CurrencyMapper.class})
public interface UtilMapper {

    // =================================================================================================================
    // XChange to DTO.

    @ValueMappings({
            @ValueMapping(source = "BID", target = "BID"),
            @ValueMapping(source = "ASK", target = "ASK"),
            @ValueMapping(source = "EXIT_BID", target = "BID"),
            @ValueMapping(source = "EXIT_ASK", target = "ASK")
    })
    OrderTypeDTO mapToOrderTypeDTO(Order.OrderType source);

    // =================================================================================================================
    // DTO to XChange.

    @ValueMappings({
            @ValueMapping(source = "BID", target = "BID"),
            @ValueMapping(source = "ASK", target = "ASK")
    })
    Order.OrderType mapToOrderType(OrderTypeDTO source);

    // =================================================================================================================
    // XChange to DTO.

    @ValueMappings({
            @ValueMapping(source = "PENDING_NEW", target = "PENDING_NEW"),
            @ValueMapping(source = "NEW", target = "NEW"),
            @ValueMapping(source = "PARTIALLY_FILLED", target = "PARTIALLY_FILLED"),
            @ValueMapping(source = "FILLED", target = "FILLED"),
            @ValueMapping(source = "PENDING_CANCEL", target = "PENDING_CANCEL"),
            @ValueMapping(source = "PARTIALLY_CANCELED", target = "PARTIALLY_CANCELED"),
            @ValueMapping(source = "CANCELED", target = "CANCELED"),
            @ValueMapping(source = "PENDING_REPLACE", target = "PENDING_REPLACE"),
            @ValueMapping(source = "STOPPED", target = "STOPPED"),
            @ValueMapping(source = "REJECTED", target = "REJECTED"),
            @ValueMapping(source = "EXPIRED", target = "EXPIRED"),
            @ValueMapping(source = "OPEN", target = "OPEN"),
            @ValueMapping(source = "CLOSED", target = "CLOSED"),
            @ValueMapping(source = "UNKNOWN", target = "UNKNOWN")
    })
    OrderStatusDTO mapToOrderStatusDTO(Order.OrderStatus source);

    // =================================================================================================================

    // =================================================================================================================
    // XChange to DTO.

    @ValueMappings({
            @ValueMapping(source = "STOP_LOSS", target = "STOP_LOSS"),
            @ValueMapping(source = "TAKE_PROFIT", target = "STOP_ENTRY"),
    })
    OrderIntentionDTO mapToOrderIntentionDTO(StopOrder.Intention source);

    // =================================================================================================================
    // DTO to XChange.

    @ValueMappings({
            @ValueMapping(source = "STOP_LOSS", target = "STOP_LOSS"),
            @ValueMapping(source = "STOP_ENTRY", target = "TAKE_PROFIT")
    })
    StopOrder.Intention mapToOrderIntention(OrderIntentionDTO source);

}
