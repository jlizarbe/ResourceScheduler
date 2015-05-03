package uk.jpmorgan.resourcescheduler;

import java.util.ArrayList;
import java.util.List;

public abstract class JPMorganMessage implements Message {
	
	protected List<ResourceScheduler> observers;
	
	
	/**
	 * Message's groupId
	 * 
	 * @return
	 */
	public abstract String getGroupId();

	
	/**
	 * Is the message the last of the group
	 * 
	 * @return
	 */
	public abstract boolean isTerminationMessage();

	
	/**
	 * Register a ResourceScheduler to notify it when this message has been processed
	 * @param observer
	 */
	public void registerObserver(ResourceScheduler observer) {
		if (observers == null) {
			observers = new ArrayList<>();
		}
		
		observers.add(observer);
	}
	

	/**
	 * Method called when the message has been processed. 
	 */
	@Override
	public void completed() {
		if (observers != null) {
			for (ResourceScheduler observer : observers) {
				observer.notifyScheduler();
			}
		}
	}

	
	
}
