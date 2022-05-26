package tech.cassandre.trading.bot.service;

import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.util.mapper.OrderMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ActiveOrderController {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveOrderController.class);

    /** Timeout for get(). */
    private static final long WAIT_TIMEOUT = 1000L;

    /** Order mapper. */
    private static final OrderMapper ORDER_MAPPER = Mappers.getMapper(OrderMapper.class);

    /** Collection of active orders. */
    private static final ConcurrentHashMap<String, CompletableFuture<OrderDTO>> ORDERS = new ConcurrentHashMap<>();

    /** Executor for creating orders. */
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    /** The order repository. */
    private static OrderRepository repository = null;

    private ActiveOrderController() {

    }

    public static OrderDTO create(final String orderId, final OrderDTO source) {
        CompletableFuture<OrderDTO> future =
            ORDERS.compute(orderId, (key, value) ->
                CompletableFuture.supplyAsync(() -> {
                    if (value == null) {
                        final Order order = ORDER_MAPPER.mapToOrder(source);
                        ORDER_MAPPER.updateOrder(
                                OrderDTO.builder().orderId(key).trades(new HashSet<>()).build(), order);
                        return ORDER_MAPPER.mapToOrderDTO(repository.save(order));
                    }
                    try {
                        if (!value.get().equals(source)) {
                            final Order order = ORDER_MAPPER.mapToOrder(value.get());
                            ORDER_MAPPER.updateOrder(source, order);
                            return ORDER_MAPPER.mapToOrderDTO(repository.save(order));
                        }
                    } catch (InterruptedException | ExecutionException ignored) {
                    }
                    return null;
                }, EXECUTOR));

        try {
            final OrderDTO order = future.get();
            if (order.getStatus().isFinal()) {
                ORDERS.remove(order.getOrderId());
            }
            return order;
        } catch (InterruptedException | ExecutionException ignored) {
        }
        return null;
    }

    public static OrderDTO update(final OrderDTO source) {
        CompletableFuture<OrderDTO> future =
            ORDERS.compute(source.getOrderId(), (key, value) ->
                CompletableFuture.supplyAsync(() -> {
                    if (value == null) {
                        return source;
                    }
                    try {
                        if (!value.get().equals(source)) {
                            final Order order = ORDER_MAPPER.mapToOrder(value.get());
                            ORDER_MAPPER.updateOrder(source, order);
                            if (order.getStrategy() != null) {
                                return ORDER_MAPPER.mapToOrderDTO(repository.save(order));
                            } else {
                                return ORDER_MAPPER.mapToOrderDTO(order);
                            }
                        }
                    } catch (InterruptedException | ExecutionException ignored) {
                    }
                    return null;
                }, EXECUTOR));

        try {
            final OrderDTO order = future.get();
            if (order.getStatus().isFinal()) {
                ORDERS.remove(order.getOrderId());
            }
            return order;
        } catch (InterruptedException | ExecutionException ignored) {
        }
        return null;
    }

    public static OrderDTO update(final Order source) {
        return update(ORDER_MAPPER.mapToOrderDTO(source));
    }

    public static OrderDTO update(final org.knowm.xchange.dto.Order source) {
        return update(ORDER_MAPPER.mapToOrderDTO(source));
    }

    public static OrderDTO get(final String orderId) {
        try {
            CompletableFuture<OrderDTO> future = ORDERS.get(orderId);
            if (future != null) {
                return future.get();
            }
        } catch (InterruptedException | ExecutionException ignored) {
        }
        return null;

    }

    public static void initialize(final OrderRepository thatRepository) {

        ActiveOrderController.repository = thatRepository;
    }

    /**
     * Saves the order, but using a background thread to ensure that there are no issues sharing the
     * repository between threads.
     *
     * @param order the order to save
     * @return the order after it has been saved
     */
    public static Optional<Order> save(final Order order) {
        return Optional.of(repository.save(order));
    }

    /**
     * Returns an order, looking first in the futures collection and then in the repository.
     * @param orderId the order id
     * @return the order
     */
    public static Optional<Order> findByOrderId(final String orderId) {
        return repository.findByOrderId(orderId);
    }

    /**
     * Returns a list of orders having the specified status.
     * @param status the order status
     * @return the list of orders
     */
    public static List<Order> findByStatus(final OrderStatusDTO status) {
        return repository.findByStatus(status);
    }

    /**
     * Returns a list of orders having the specified status.
     * @return the list of orders
     */
    public static List<Order> findAll() {
        return repository.findAll();
    }

    /**
     * Returns a list of orders having the specified status.
     * @return the list of orders
     */
    public static List<Order> findByOrderByTimestampAsc() {
        return repository.findByOrderByTimestampAsc();
    }
}
