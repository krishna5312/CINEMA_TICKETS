package uk.gov.dwp.uc.pairtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;


@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

	@InjectMocks
	@Spy
	private TicketServiceImpl service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = InvalidPurchaseException.class)
	public void testWithNoAdults() {
		TicketTypeRequest request = new TicketTypeRequest(Type.CHILD, 2);
		TicketTypeRequest request1 = new TicketTypeRequest(Type.INFANT, 2);
		service.purchaseTickets(100L, request, request1);
	}

	@Test(expected = InvalidPurchaseException.class)
	public void testWithNoRequests() {
		service.purchaseTickets(100L, null);
	}

	@Test(expected = InvalidPurchaseException.class)
	public void testWithInvalidAccount() {
		service.purchaseTickets(-100L, new TicketTypeRequest(Type.CHILD, 2));
	}

	@Test
	public void testWithProperData() {
		TicketTypeRequest request = new TicketTypeRequest(Type.CHILD, 2);
		TicketTypeRequest request1 = new TicketTypeRequest(Type.INFANT, 2);
		TicketTypeRequest request2 = new TicketTypeRequest(Type.ADULT, 2);
		service.purchaseTickets(100L, request, request1, request2);
		Assert.assertEquals(6, service.getTicketCount());
		Assert.assertEquals(4, service.getSeatCount());
		Assert.assertEquals(60, service.getAmount());
	}

	@Test(expected = InvalidPurchaseException.class)
	public void testWithMoreThan20Tickets() {
		TicketTypeRequest request = new TicketTypeRequest(Type.CHILD, 12);
		TicketTypeRequest request1 = new TicketTypeRequest(Type.INFANT, 3);
		TicketTypeRequest request2 = new TicketTypeRequest(Type.ADULT, 6);
		service.purchaseTickets(100L, request, request1);
	}

}
