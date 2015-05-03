package uk.jpmorgan.resourcescheduler.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.jpmorgan.resourcescheduler.GatewayFactory;
import uk.jpmorgan.resourcescheduler.GroupTerminatedException;
import uk.jpmorgan.resourcescheduler.JPMorganMessage;
import uk.jpmorgan.resourcescheduler.ResourceScheduler;
import uk.jpmorgan.resourcescheduler.test.stubs.GatewayStub;
import uk.jpmorgan.resourcescheduler.test.stubs.JPMorganMessageStub;

public class ResourceSchedulerTest {

	
	@Before
	public void resetData () {
		GatewayStub gateway = (GatewayStub) GatewayFactory.getGateway();
		
		gateway.resetMessagesSent();
	}
	
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testLaunchWithNullResources() {
		new ResourceScheduler(null);
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void testLaunchWithNegativeResources() {
		new ResourceScheduler(-1);
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void testLaunchWithZeroResources() {
		new ResourceScheduler(0);
	}

	
	@Test
	public void testLaunchWithOneResources() {
		new ResourceScheduler(1);
	}

	
	@Test
	public void testLaunchWithMultipleResources() {
		new ResourceScheduler(5);
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testForwardingNullMessage() {
		ResourceScheduler resourceScheduler = new ResourceScheduler(1);
		
		resourceScheduler.processMessage(null);
	}
	
	
	@Test
	public void testForwarding1Resource1Message() {
		
		// GIVEN 
		ResourceScheduler resourceScheduler = new ResourceScheduler(1);
		JPMorganMessage message = new JPMorganMessageStub("Group1"); 
		
		
		// WHEN
		resourceScheduler.processMessage(message);
		
		// THEN
		GatewayStub gateway = (GatewayStub) GatewayFactory.getGateway();
		assertEquals(1, gateway.getMessagesSent().size());
		assertEquals(message, gateway.getMessagesSent().get(0));
		
	}
	
	
	@Test
	public void testForwarding2Resource2Message() {
		
		// GIVEN 
		ResourceScheduler resourceScheduler = new ResourceScheduler(2);
		JPMorganMessage message = new JPMorganMessageStub("Group1"); 
		JPMorganMessage message2 = new JPMorganMessageStub("Group1");
		
		
		// WHEN
		resourceScheduler.processMessage(message);
		resourceScheduler.processMessage(message2);
		
		// THEN
		GatewayStub gateway = (GatewayStub) GatewayFactory.getGateway();
		assertEquals(2, gateway.getMessagesSent().size());
		assertEquals(message, gateway.getMessagesSent().get(0));
		assertEquals(message2, gateway.getMessagesSent().get(1));
	}
	
	
	@Test
	public void testQueuing1Resource2Message() {
		
		// GIVEN 
		ResourceScheduler resourceScheduler = new ResourceScheduler(1);
		JPMorganMessage message = new JPMorganMessageStub("Group1"); 
		JPMorganMessage message2 = new JPMorganMessageStub("Group1");
		
		
		// WHEN
		resourceScheduler.processMessage(message);
		resourceScheduler.processMessage(message2);
		
		// THEN
		GatewayStub gateway = (GatewayStub) GatewayFactory.getGateway();
		assertEquals(1, gateway.getMessagesSent().size());
		assertEquals(message, gateway.getMessagesSent().get(0));
	}
	
	
	@Test
	public void testRespondingResource2Message() {
		
		// GIVEN 
		ResourceScheduler resourceScheduler = new ResourceScheduler(1);
		JPMorganMessage message = new JPMorganMessageStub("Group1"); 
		JPMorganMessage message2 = new JPMorganMessageStub("Group1");
		
		
		// WHEN
		resourceScheduler.processMessage(message);
		resourceScheduler.processMessage(message2);
		message.completed();
		
		// THEN
		GatewayStub gateway = (GatewayStub) GatewayFactory.getGateway();
		assertEquals(2, gateway.getMessagesSent().size());
		assertEquals(message, gateway.getMessagesSent().get(0));
		assertEquals(message2, gateway.getMessagesSent().get(1));
	}
	
	
	@Test
	public void testPrioritising1Resource3MessageInterleaved() {
		
		// GIVEN 
		ResourceScheduler resourceScheduler = new ResourceScheduler(1);
		JPMorganMessage message = new JPMorganMessageStub("Group1"); 
		JPMorganMessage message2 = new JPMorganMessageStub("Group2");
		JPMorganMessage message3 = new JPMorganMessageStub("Group1");
		
		
		// WHEN
		resourceScheduler.processMessage(message);
		resourceScheduler.processMessage(message2);
		resourceScheduler.processMessage(message3);
		message.completed();
		
		// THEN
		GatewayStub gateway = (GatewayStub) GatewayFactory.getGateway();
		assertEquals(2, gateway.getMessagesSent().size());
		assertEquals(message, gateway.getMessagesSent().get(0));
		assertEquals(message3, gateway.getMessagesSent().get(1));
	}
	
	
	@Test
	public void testPrioritising1Resource5MessageInterleaved() {
		
		// GIVEN 
		ResourceScheduler resourceScheduler = new ResourceScheduler(1);
		JPMorganMessage message = new JPMorganMessageStub("Group1"); 
		JPMorganMessage message2 = new JPMorganMessageStub("Group2");
		JPMorganMessage message3 = new JPMorganMessageStub("Group1");
		JPMorganMessage message4 = new JPMorganMessageStub("Group2");
		JPMorganMessage message5 = new JPMorganMessageStub("Group3");
		
		
		// WHEN
		resourceScheduler.processMessage(message);
		resourceScheduler.processMessage(message2);
		resourceScheduler.processMessage(message3);
		message.completed();
		message3.completed();
		resourceScheduler.processMessage(message4);
		message2.completed();
		resourceScheduler.processMessage(message5);
		message4.completed();
		message5.completed();
		
		// THEN
		GatewayStub gateway = (GatewayStub) GatewayFactory.getGateway();
		assertEquals(5, gateway.getMessagesSent().size());
		assertEquals(message, gateway.getMessagesSent().get(0));
		assertEquals(message3, gateway.getMessagesSent().get(1));
		assertEquals(message2, gateway.getMessagesSent().get(2));
		assertEquals(message4, gateway.getMessagesSent().get(3));
		assertEquals(message5, gateway.getMessagesSent().get(4));
	}
	
	
	@Test
	public void testCancellationQueuedMessages() {
		
		// GIVEN 
		ResourceScheduler resourceScheduler = new ResourceScheduler(1);
		JPMorganMessage message = new JPMorganMessageStub("Group1"); 
		JPMorganMessage message2 = new JPMorganMessageStub("Group2");
		JPMorganMessage message3 = new JPMorganMessageStub("Group1");
		
		
		// WHEN
		resourceScheduler.processMessage(message);
		resourceScheduler.processMessage(message2);
		resourceScheduler.processMessage(message3);
		resourceScheduler.cancelGroup("Group2");
		message.completed();
		message3.completed();
		
		// THEN
		GatewayStub gateway = (GatewayStub) GatewayFactory.getGateway();
		assertEquals(2, gateway.getMessagesSent().size());
		assertEquals(message, gateway.getMessagesSent().get(0));
		assertEquals(message3, gateway.getMessagesSent().get(1));
	}
	
	
	@Test
	public void testCancellationSendNewMessages() {
		
		// GIVEN 
		ResourceScheduler resourceScheduler = new ResourceScheduler(1);
		JPMorganMessage message = new JPMorganMessageStub("Group1"); 
		JPMorganMessage message2 = new JPMorganMessageStub("Group2");
		JPMorganMessage message3 = new JPMorganMessageStub("Group1");
		JPMorganMessage message4 = new JPMorganMessageStub("Group2");
		
		
		// WHEN
		resourceScheduler.processMessage(message);
		resourceScheduler.processMessage(message2);
		resourceScheduler.processMessage(message3);
		resourceScheduler.cancelGroup("Group2");
		message.completed();
		message3.completed();
		resourceScheduler.processMessage(message4);
		
		// THEN
		GatewayStub gateway = (GatewayStub) GatewayFactory.getGateway();
		assertEquals(2, gateway.getMessagesSent().size());
		assertEquals(message, gateway.getMessagesSent().get(0));
		assertEquals(message3, gateway.getMessagesSent().get(1));
	}
	
	
	@Test(expected=GroupTerminatedException.class)
	public void testTerminationMessage() {
		
		// GIVEN 
		ResourceScheduler resourceScheduler = new ResourceScheduler(1);
		JPMorganMessage message = new JPMorganMessageStub("Group1"); 
		JPMorganMessage message2 = new JPMorganMessageStub("Group2");
		JPMorganMessage message3 = new JPMorganMessageStub("Group1", true);
		JPMorganMessage message4 = new JPMorganMessageStub("Group1");
		
		
		// WHEN
		resourceScheduler.processMessage(message);
		resourceScheduler.processMessage(message2);
		resourceScheduler.processMessage(message3);
		resourceScheduler.processMessage(message4);
		
		// THEN
	}
}
