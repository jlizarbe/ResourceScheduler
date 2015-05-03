package uk.jpmorgan.resourcescheduler.test.stubs;

import java.util.ArrayList;
import java.util.List;

import uk.jpmorgan.resourcescheduler.Gateway;
import uk.jpmorgan.resourcescheduler.Message;

public class GatewayStub implements Gateway {

	private List<Message> messagesSent = new ArrayList<>();
	
	
	@Override
	public void send(Message msg) {
		messagesSent.add(msg);
	}


	public List<Message> getMessagesSent() {
		return messagesSent;
	}
	
	public void resetMessagesSent() {
		messagesSent = new ArrayList<>();
	}
}
