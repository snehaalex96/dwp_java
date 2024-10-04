package uk.gov.dwp.uc.pairtest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class TicketServiceImplTest {

	@Test
	public void testPurchaseWithValidTickets() {
	    TicketPaymentService paymentService = mock(TicketPaymentService.class);
	    SeatReservationService reservationService = mock(SeatReservationService.class);
	    TicketServiceImpl ticketService = new TicketServiceImpl(paymentService, reservationService);

	    TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
	    TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

	    ticketService.purchaseTickets(1L, adult, child);

	    verify(paymentService).makePayment(1L, 65);
	    verify(reservationService).reserveSeat(1L, 3);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testPurchaseExceedingMaximumTickets() {
	    TicketPaymentService mockPaymentService = mock(TicketPaymentService.class);
	    SeatReservationService mockReservationService = mock(SeatReservationService.class);
	    
	    TicketServiceImpl ticketService = new TicketServiceImpl(mockPaymentService, mockReservationService);

	    // Exceed the maximum allowed 25 tickets
	    TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

	    
	        ticketService.purchaseTickets(1L, adultRequest);
	    
	}
	@Test(expected = IllegalArgumentException.class)
	public void testInfantsWithoutAdults() {
	    TicketPaymentService mockPaymentService = mock(TicketPaymentService.class);
	    SeatReservationService mockReservationService = mock(SeatReservationService.class);
	    
	    TicketServiceImpl ticketService = new TicketServiceImpl(mockPaymentService, mockReservationService);

	    TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
	    
	    // Expect exception since there's no adult
	    
	        ticketService.purchaseTickets(1L, infantRequest);
	    
	}
	

	@Test
	public void testInfantsDoNotRequireSeats() {
	    TicketPaymentService mockPaymentService = mock(TicketPaymentService.class);
	    SeatReservationService mockReservationService = mock(SeatReservationService.class);
	    
	    TicketServiceImpl ticketService = new TicketServiceImpl(mockPaymentService, mockReservationService);

	    TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
	    TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

	    ticketService.purchaseTickets(1L, adultRequest, infantRequest);

	    // Ensure only the adult seat is reserved
	    verify(mockPaymentService).makePayment(1L, 25); // Only Adult's £25
	    verify(mockReservationService).reserveSeat(1L, 1); // Only 1 seat for Adult
	}
	@Test
	public void testPurchaseOnlyAdultTickets() {
	    TicketPaymentService mockPaymentService = mock(TicketPaymentService.class);
	    SeatReservationService mockReservationService = mock(SeatReservationService.class);
	    
	    TicketServiceImpl ticketService = new TicketServiceImpl(mockPaymentService, mockReservationService);

	    TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

	    ticketService.purchaseTickets(1L, adultRequest);

	    // Verify that the correct payment and reservation is done
	    verify(mockPaymentService).makePayment(1L, 50); // 2 Adults * £25 = £50
	    verify(mockReservationService).reserveSeat(1L, 2); // 2 seats reserved
	}
	@Test(expected = IllegalArgumentException.class)
	public void testNoTicketsRequested() {
	    TicketPaymentService mockPaymentService = mock(TicketPaymentService.class);
	    SeatReservationService mockReservationService = mock(SeatReservationService.class);
	    
	    TicketServiceImpl ticketService = new TicketServiceImpl(mockPaymentService, mockReservationService);

	    // No tickets should result in an exception
	   
	        ticketService.purchaseTickets(1L);
	   
	}

	
}
