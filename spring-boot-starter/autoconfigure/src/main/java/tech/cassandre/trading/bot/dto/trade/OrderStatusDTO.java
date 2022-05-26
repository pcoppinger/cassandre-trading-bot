package tech.cassandre.trading.bot.dto.trade;

/**
 * Order status for {@link OrderDTO}.
 */
public enum OrderStatusDTO {

    /** The exchange returned a state which is not in the exchange's API documentation. The state of the order cannot be confirmed. */
    UNKNOWN(-1),

    /** Initial order when instantiated. */
    PENDING_NEW(0),

    /** Initial order when placed on the order book at exchange. */
    NEW(1),

    /** Order is open and waiting to be filled. */
    OPEN(2),

    /** Partially match against opposite order on order book at exchange. */
    PARTIALLY_FILLED(3),

    /** Fully match against opposite order on order book at exchange. */
    FILLED(4),

    /** Waiting to be removed from order book at exchange. */
    PENDING_CANCEL(5),

    /** Order partially canceled at exchange. */
    PARTIALLY_CANCELED(6),

    /** Removed from order book at exchange. */
    CANCELED(7),

    /** Waiting to be replaced by another order on order book at exchange. */
    PENDING_REPLACE(8),

    /** Order has been replaced by another order on order book at exchange. */
    REPLACED(9),

    /** Order has been triggered at stop price. */
    STOPPED(10),

    /** Order has been rejected by exchange and not place on order book. */
    REJECTED(11),

    /** Order has expired it's time to live or trading session and been removed from order book. */
    EXPIRED(12),

    /** Order has been either filled or cancelled. */
    CLOSED(13);

    /** the priority of this status. */
    private int priority;

    public int getPriority() {
        return priority;
    }

    OrderStatusDTO(final int thatPriority) {
        this.priority = thatPriority;
    }

    /**
     * Returns true for final.
     *
     * @return Returns true for final
     */
    public final boolean isFinal() {
        switch (this) {
            case FILLED:
            case PARTIALLY_CANCELED: // Cancelled, partially-executed order is final status.
            case CANCELED:
            case REPLACED:
            case STOPPED:
            case REJECTED:
            case EXPIRED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns true if the status indicates an error.
     *
     * @return Returns true for final
     */
    public final boolean isInError() {
        switch (this) {
            case CANCELED:
            case PARTIALLY_CANCELED:
            case REPLACED:
            case STOPPED:
            case REJECTED:
            case EXPIRED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns true when open.
     *
     * @return Returns true when open
     */
    public final boolean isOpen() {
        switch (this) {
            case PENDING_NEW:
            case NEW:
            case OPEN:
            case PARTIALLY_FILLED:
                return true;
            default:
                return false;
        }
    }

}
