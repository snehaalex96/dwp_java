package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public final class TicketTypeRequest {
    public enum Type {
        INFANT, CHILD, ADULT
    }

    private final Type type;
    private final int numberOfTickets;

    public TicketTypeRequest(Type type, int numberOfTickets) {
        this.type = type;
        this.numberOfTickets = numberOfTickets;
    }

    public Type getType() {
        return type;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }
}
