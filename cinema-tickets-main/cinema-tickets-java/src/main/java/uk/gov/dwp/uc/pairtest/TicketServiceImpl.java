package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
public class TicketServiceImpl implements TicketService {
   
	/**
     * Should only have private methods other than the one below.
     */

	private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService) {
        this.ticketPaymentService = paymentService;
        this.seatReservationService = reservationService;
    }    
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) {
    	if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new IllegalArgumentException("At least one ticket must be purchased.");
        }
    	// Validate accountId
        if (accountId <= 0) {
            throw new IllegalArgumentException("Invalid account ID");
        }

        int totalTickets = 0;
        int adultTickets = 0;
        int totalCost = 0;
        int seatsToReserve = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            int quantity = request.getNumberOfTickets();
            totalTickets += quantity;

            switch (request.getType()) {
                case ADULT:
                    totalCost += quantity * 25;
                    seatsToReserve += quantity;
                    adultTickets += quantity;
                    break;
                case CHILD:
                    totalCost += quantity * 15;
                    seatsToReserve += quantity;
                    break;
                case INFANT:
                    // Infants don't cost and don't reserve seats
                    break;
            }
        }

        // Check if more than 25 tickets
        if (totalTickets > 25) {
            throw new IllegalArgumentException("Cannot purchase more than 25 tickets");
        }

        // Ensure there are adult tickets if infants or children are purchased
        if (adultTickets == 0 && totalTickets > 0) {
            throw new IllegalArgumentException("At least one adult ticket must be purchased");
        }

        // Process payment and reserve seats
        ticketPaymentService.makePayment(accountId, totalCost);
        seatReservationService.reserveSeat(accountId, seatsToReserve);
    }
}
