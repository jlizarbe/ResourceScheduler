package uk.jpmorgan.resourcescheduler.prioritizing;

import uk.jpmorgan.resourcescheduler.JPMorganMessage;

public interface PrioritizingStrategy {

	
	void queueMessage(JPMorganMessage message);
	
	JPMorganMessage getMessageToSend();
	
	void cancelGroup(String groupId);
}
