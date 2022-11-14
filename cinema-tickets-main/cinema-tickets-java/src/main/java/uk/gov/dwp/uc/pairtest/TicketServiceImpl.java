package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
public class TicketServiceImpl implements TicketService {
   
	/**
     * Should only have private methods other than the one below.
     */

    public  void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
    	 try {
	    	 int noOfTickets=0, noOfAdults=0, noOfChild=0,totalAmount=0;	
	    	 if(checkAccountIdValid(accountId))
	    	 {
		    	 for (TicketTypeRequest i : ticketTypeRequests) {
		            if(i.getTicketType()!= TicketTypeRequest.Type.INFANT ) {
		    	 	noOfTickets=noOfTickets+i.getNoOfTickets();
		            }
		            if(i.getTicketType()== TicketTypeRequest.Type.ADULT ) {
		            	noOfAdults=noOfAdults+i.getNoOfTickets();	
		            }
		            if(i.getTicketType()== TicketTypeRequest.Type.CHILD ) {
		            	noOfChild=noOfChild+i.getNoOfTickets();
		            }
		    	 }
		    	 System.out.println("No of tickets " + noOfTickets);	
		    	 System.out.println("No of Child " + noOfChild);	
		    	 System.out.println("No of Adults " + noOfAdults);
		    	 if(noOfAdults!=0) {
			    	 if(noOfTickets<=20) {
			      	   totalAmount= calculateTicketAmount(noOfAdults,noOfChild) ;
			      	   new TicketPaymentServiceImpl().makePayment(accountId, totalAmount);
			      	   new SeatReservationServiceImpl().reserveSeat(accountId, noOfTickets);
			         }
			         else 
			      	   System.out.println("Invalid purchase please purchase 20 tickets at a time");   
		    	 }else
		    		 System.out.println("Reservation and purchase Invalid - No Adult");  
	    	 } 
	    }
    	 catch(InvalidPurchaseException e) {
		      System.out.println("InvalidPurchaseException => " + e.getMessage());
		  } 
    }
    
   private boolean checkAccountIdValid(long accountId ) {
	   boolean isValid= false;
	   try {
		     if(accountId >0)
		     {
		    	 isValid= true; 
		     }
		     else
		     {
		    	 isValid=false;
		    	 System.out.println("Account invalid");
		     }
		    }

	  catch (InvalidPurchaseException e) {
		      System.out.println("InvalidPurchaseException => " + e.getMessage());
		    } 
      return isValid;
   }
	   
	private int calculateTicketAmount(int noOfAdults, int noOfChild) {
		int adultTicketAmount=20*noOfAdults;
		int childTicketAmount=10*noOfChild;
		int totalAmount=adultTicketAmount+childTicketAmount;
		System.out.println("Total " +totalAmount + " pounds");
		return totalAmount;
		
	}

    public static void main(String args[]) {
    	TicketServiceImpl ts= new TicketServiceImpl();
    	 TicketTypeRequest tr1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,21);
    	 TicketTypeRequest tr2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,0);
    	 TicketTypeRequest tr3 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,21);
    	 ts.purchaseTickets((long) 1,new TicketTypeRequest[] { tr1,tr2,tr3});
    }
}
