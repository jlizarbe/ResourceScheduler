package uk.jpmorgan.resourcescheduler;

import uk.jpmorgan.resourcescheduler.prioritizing.GroupMessagePrioritation;
import uk.jpmorgan.resourcescheduler.prioritizing.PrioritizingStrategy;


public class ResourceScheduler {

	private Gateway gateway;
	private Integer resources;
	private PrioritizingStrategy prioritizingStrategy;
	
	
	public ResourceScheduler(Integer resourcesNumber) {
		
		initResourceScheduler(resourcesNumber, new GroupMessagePrioritation());
	}
	
	public ResourceScheduler(Integer resourcesNumber, PrioritizingStrategy prioritizingStrategy) {
		
		initResourceScheduler(resourcesNumber, prioritizingStrategy);
	}

	
	private void initResourceScheduler(Integer resourcesNumber, PrioritizingStrategy prioritizingStrategy) {
		if (resourcesNumber == null || resourcesNumber <= 0) {
			throw new IllegalArgumentException();
		}
		
		gateway = GatewayFactory.getGateway();
		resources = resourcesNumber;
		this.prioritizingStrategy = prioritizingStrategy;
	}

	
	public void processMessage(JPMorganMessage message) {
		if (message == null) {
			throw new IllegalArgumentException();
		}
		
		message.registerObserver(this);
		prioritizingStrategy.queueMessage(message);
		tryToSendMessage();
	}


	private void tryToSendMessage() {
		synchronized (resources) {
			if (resources > 0) {
				JPMorganMessage message = prioritizingStrategy.getMessageToSend();
				if (message != null) {
					sendMessage(message);
				}
			}
		}
	}



	private void sendMessage(JPMorganMessage message) {
		resources--;
		gateway.send(message);
	}


	public void notifyScheduler() {
		
		synchronized (resources) {
			resources++;
		}
		tryToSendMessage();
	}


	public void cancelGroup(String groupId) {
		synchronized (resources) {
			prioritizingStrategy.cancelGroup(groupId);
		}
	}

}
