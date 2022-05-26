package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.StopOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * Order mapper.
 */
@Mapper(uses = {UtilMapper.class, CurrencyMapper.class, TradeMapper.class, StrategyMapper.class}, nullValuePropertyMappingStrategy = IGNORE)
public interface OrderMapper {

    default OrderDTO mapToOrderDTO(org.knowm.xchange.dto.Order source) {
        if (source instanceof StopOrder) {
            return mapToOrderDTO((StopOrder) source);
        } else if (source instanceof LimitOrder) {
            return mapToOrderDTO((LimitOrder) source);
        } else if (source instanceof MarketOrder) {
            return mapToOrderDTO((MarketOrder) source);
        } else {
            return null;
        }
    }

    // =================================================================================================================
    // XChange to DTO.

    @Mapping(source = "id", target = "orderId")
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapMarketOrderToOrderDTOAmount")
    @Mapping(source = "source", target = "cumulativeAmount", qualifiedByName = "mapMarketOrderToOrderDTOCumulativeAmount")
    @Mapping(source = "source", target = "averagePrice", qualifiedByName = "mapMarketOrderToOrderDTOAveragePrice")
    @Mapping(target = "marketPrice", ignore = true)
    @Mapping(target = "stopPrice", ignore = true)
    @Mapping(target = "limitPrice", ignore = true)
    @Mapping(source = "instrument", target = "currencyPair")
    @Mapping(target = "strategy", ignore = true)
    @Mapping(target = "trades", ignore = true)
    @Mapping(target = "stopTriggered", ignore = true)
    @Mapping(target = "intention", ignore = true)
//  @Mapping(target = "trade", ignore = true)
    OrderDTO mapToOrderDTO(MarketOrder source);

    @Named("mapMarketOrderToOrderDTOAmount")
    default CurrencyAmountDTO mapMarketOrderToOrderDTOAmount(MarketOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getOriginalAmount() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getOriginalAmount())
                    .currency(cp.getBaseCurrency())
                    .build();
        } else {
            return null;
        }
    }

    @Named("mapMarketOrderToOrderDTOCumulativeAmount")
    default CurrencyAmountDTO mapMarketOrderToOrderDTOCumulativeAmount(MarketOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getCumulativeAmount() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getCumulativeAmount())
                    .currency(cp.getBaseCurrency())
                    .build();
        } else {
            return null;
        }
    }

    @Named("mapMarketOrderToOrderDTOAveragePrice")
    default CurrencyAmountDTO mapMarketOrderToOrderDTOAveragePrice(MarketOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getAveragePrice() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getAveragePrice())
                    .currency(cp.getQuoteCurrency())
                    .build();
        } else {
            return null;
        }
    }

    // =================================================================================================================
    // XChange to DTO.

    @Mapping(source = "id", target = "orderId")
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapLimitOrderToOrderDTOAmount")
    @Mapping(source = "source", target = "cumulativeAmount", qualifiedByName = "mapLimitOrderToOrderDTOCumulativeAmount")
    @Mapping(source = "source", target = "averagePrice", qualifiedByName = "mapLimitOrderToOrderDTOAveragePrice")
    @Mapping(source = "source", target = "limitPrice", qualifiedByName = "mapLimitOrderToOrderDTOLimitPrice")
    @Mapping(target = "stopPrice", ignore = true)
    @Mapping(target = "marketPrice", ignore = true)
    @Mapping(source = "instrument", target = "currencyPair")
    @Mapping(target = "strategy", ignore = true)
    @Mapping(target = "trades", ignore = true)
    @Mapping(target = "stopTriggered", ignore = true)
    @Mapping(target = "intention", ignore = true)
//  @Mapping(target = "trade", ignore = true)
    OrderDTO mapToOrderDTO(LimitOrder source);

    @Named("mapLimitOrderToOrderDTOAmount")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOAmount(LimitOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getOriginalAmount() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getOriginalAmount())
                    .currency(cp.getBaseCurrency())
                    .build();
        } else {
            return null;
        }
    }

    @Named("mapLimitOrderToOrderDTOCumulativeAmount")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOCumulativeAmount(LimitOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getCumulativeAmount() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getCumulativeAmount())
                    .currency(cp.getBaseCurrency())
                    .build();
        } else {
            return null;
        }
    }

    @Named("mapLimitOrderToOrderDTOAveragePrice")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOAveragePrice(LimitOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getAveragePrice() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getAveragePrice())
                    .currency(cp.getQuoteCurrency())
                    .build();
        } else {
            return null;
        }
    }

    @Named("mapLimitOrderToOrderDTOLimitPrice")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOLimitPrice(LimitOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getLimitPrice() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getLimitPrice())
                    .currency(cp.getQuoteCurrency())
                    .build();
        } else {
            return null;
        }
    }

    // =================================================================================================================
    // XChange to DTO.

    @Mapping(source = "id", target = "orderId")
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapStopOrderToOrderDTOAmount")
    @Mapping(source = "source", target = "cumulativeAmount", qualifiedByName = "mapStopOrderToOrderDTOCumulativeAmount")
    @Mapping(source = "source", target = "averagePrice", qualifiedByName = "mapStopOrderToOrderDTOAveragePrice")
    @Mapping(source = "source", target = "stopPrice", qualifiedByName = "mapStopOrderToOrderDTOStopPrice")
    @Mapping(source = "source", target = "limitPrice", qualifiedByName = "mapStopOrderToOrderDTOLimitPrice")
    @Mapping(target = "marketPrice", ignore = true)
    @Mapping(source = "instrument", target = "currencyPair")
    @Mapping(target = "strategy", ignore = true)
    @Mapping(target = "trades", ignore = true)
//  @Mapping(target = "trade", ignore = true)
    OrderDTO mapToOrderDTO(StopOrder source);

    @Named("mapStopOrderToOrderDTOAmount")
    default CurrencyAmountDTO mapStopOrderToOrderDTOAmount(StopOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getOriginalAmount() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getOriginalAmount())
                    .currency(cp.getBaseCurrency())
                    .build();
        } else {
            return null;
        }
    }

    @Named("mapStopOrderToOrderDTOCumulativeAmount")
    default CurrencyAmountDTO mapStopOrderToOrderDTOCumulativeAmount(StopOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getCumulativeAmount() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getCumulativeAmount())
                    .currency(cp.getBaseCurrency())
                    .build();
        } else {
            return null;
        }
    }

    @Named("mapStopOrderToOrderDTOAveragePrice")
    default CurrencyAmountDTO mapStopOrderToOrderDTOAveragePrice(StopOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getAveragePrice() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getAveragePrice())
                    .currency(cp.getQuoteCurrency())
                    .build();
        } else {
            return null;
        }
    }

    @Named("mapStopOrderToOrderDTOLimitPrice")
    default CurrencyAmountDTO mapStopOrderToOrderDTOLimitPrice(StopOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getLimitPrice() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getLimitPrice())
                    .currency(cp.getQuoteCurrency())
                    .build();
        } else {
            return null;
        }
    }

    @Named("mapStopOrderToOrderDTOStopPrice")
    default CurrencyAmountDTO mapStopOrderToOrderDTOStopPrice(StopOrder source) {
        CurrencyPairDTO cp = CurrencyPairDTO.getInstance(source.getInstrument());
        if (source.getStopPrice() != null && source.getInstrument() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getStopPrice())
                    .currency(cp.getQuoteCurrency())
                    .build();
        } else {
            return null;
        }
    }

    // =================================================================================================================
    // DTO to domain.

    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(source = "trades", target = "trades", qualifiedByName = "tradeDTOSetToTradeSet")
    tech.cassandre.trading.bot.domain.Order mapToOrder(OrderDTO source);

    @Named("tradeDTOSetToTradeSet")
    default Set<Trade> tradeDTOSetToTradeSet(Set<TradeDTO> source) {
        if (source == null || source.size() == 0) {
            return new HashSet<>();
        } else {
            return source.stream().map(tradeMapper::mapToTrade).collect(Collectors.toSet());
        }
    }

    TradeMapper tradeMapper = Mappers.getMapper( TradeMapper.class );

    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "trades", expression = "java(mapOrderDTOTradesToOrderTrades(source.getTrades(),target.getTrades()))")
    @Mapping(target = "status", expression = "java(mapOrderStatusToOrderDTOStatus(source.getStatus(),target.getStatus()))")
    void updateOrder(OrderDTO source, @MappingTarget tech.cassandre.trading.bot.domain.Order target);

    default Set<Trade> mapOrderDTOTradesToOrderTrades(Set<TradeDTO> source, Set<Trade> target) {
        if (source == null) {
            return target != null ? target : new HashSet<>();
        }

        if (target != null && target.size() > 0) {
            HashMap<String, Trade> map = target.stream()
                    .collect(Collectors.toMap(Trade::getTradeId, Function.identity(), (k1, k2) -> k1, HashMap::new));

            source.forEach(tradeDTO -> {
                map.compute(tradeDTO.getTradeId(), (key, value) -> {
                    if (value == null) {
                        return tradeMapper.mapToTrade(tradeDTO);
                    } else {
                        tradeMapper.updateTrade(tradeDTO, value);
                        return value;
                    }
                });
            });

            return new HashSet<>(map.values());
        } else {
            return source.stream().map(tradeMapper::mapToTrade).collect(Collectors.toSet());
        }
    }

    default OrderStatusDTO mapOrderStatusToOrderDTOStatus(OrderStatusDTO source, OrderStatusDTO target) {
        if (target == null) {
            return source;
        } else if (source == null) {
            return target;
        } else if (source.getPriority() > target.getPriority()) {
            return source;
        } else {
            return target;
        }
    }

    // =================================================================================================================
    // Domain to DTO.

    /**
     * Map Order to OrderDTO.
     *
     * @param source order
     * @return OrderDTO
     */
    @Mapping(source = "trades", target = "trades", qualifiedByName = "tradeSetToTradeDTOSet")
//  @Mapping(target = "trade", ignore = true)
    OrderDTO mapToOrderDTO(tech.cassandre.trading.bot.domain.Order source);

    @Named("tradeSetToTradeDTOSet")
    default Set<TradeDTO> tradeSetToTradeDTOSet(Set<Trade> source) {
        if (source == null || source.size() == 0) {
            return new HashSet<>();
        } else {
            return source.stream().map(tradeMapper::mapToTradeDTO).collect(Collectors.toSet());
        }
    }
}
