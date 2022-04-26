package uk.gov.dwp.uc.pairtest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

	/**
	 * Should only have private methods other than the one below.
	 * 
	 */

	private int ticketCount, amount, seatCount;

	@Override
	public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
			throws InvalidPurchaseException {

		if (accountId < 0 || ticketTypeRequests == null) {
			throw new InvalidPurchaseException();
		}

		Map<Type, Integer> ticketMap = new HashMap<>();
		for (TicketTypeRequest request : ticketTypeRequests) {
			populateTicketMap(ticketMap, request);
		}

		if (ticketMap.containsKey(Type.ADULT)) {
			populateSeatAndTicketCount(ticketMap);
			if (ticketCount <= 20) {
				TicketPaymentService paymentService= (account,amt)->{};
				paymentService.makePayment(accountId, amount);
				SeatReservationService resService = (account,totalSeatsToAllocate)->{};
				resService.reserveSeat(accountId, seatCount);
			} else {
				throw new InvalidPurchaseException();
			}
		} else {
			throw new InvalidPurchaseException();
		}
	}

	private void populateTicketMap(Map<TicketTypeRequest.Type, Integer> ticketMap, TicketTypeRequest request) {
		TicketTypeRequest.Type type = request.getTicketType();
		if (ticketMap.containsKey(type)) {
			ticketMap.put(type, ticketMap.get(type) + request.getNoOfTickets());
		} else {
			ticketMap.put(type, request.getNoOfTickets());
		}
	}

	private void populateSeatAndTicketCount(Map<TicketTypeRequest.Type, Integer> ticketMap) {
		for (Entry<Type, Integer> set : ticketMap.entrySet()) {
			Integer noOfTickets = set.getValue();
			if (set.getKey() == Type.ADULT) {
				amount += noOfTickets * 20;
				seatCount += noOfTickets;
			} else if (set.getKey() == Type.CHILD) {
				amount += noOfTickets * 10;
				seatCount += noOfTickets;
			}
			ticketCount += noOfTickets;
		}
	}
	
	public int getTicketCount() {
		return ticketCount;
	}

	public int getAmount() {
		return amount;
	}

	public int getSeatCount() {
		return seatCount;
	}
}
