package uk.jpmorgan.resourcescheduler.prioritizing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.jpmorgan.resourcescheduler.GroupTerminatedException;
import uk.jpmorgan.resourcescheduler.JPMorganMessage;

public class GroupMessagePrioritation implements PrioritizingStrategy {
	
	private Map<String, List<JPMorganMessage>> openedGroups;
	private Map<String, List<JPMorganMessage>> notOpenedGroups;
	private Set<String> canceledGroups;
	private Set<String> terminatedGroups;

	
	public GroupMessagePrioritation() {
		openedGroups = new HashMap<String, List<JPMorganMessage>>();
		notOpenedGroups = new HashMap<String, List<JPMorganMessage>>();
		canceledGroups = new HashSet<>();
		terminatedGroups = new HashSet<>();
	}

	
	@Override
	public void queueMessage(JPMorganMessage message) {
		
		if (canceledGroups.contains(message.getGroupId())) {
			return;
		}
		
		if (terminatedGroups.contains(message.getGroupId())) {
			throw new GroupTerminatedException();
		}
		
		
		List<JPMorganMessage> groupQueue = getGroupQueue(message.getGroupId());
		groupQueue.add(message);
		
		if (message.isTerminationMessage()) {
			terminatedGroups.add(message.getGroupId());
		}
	}

	
	private List<JPMorganMessage> getGroupQueue(String groupId) {
		List<JPMorganMessage> groupQueue = openedGroups.get(groupId);
		if (groupQueue != null) {
			return groupQueue;
		} 
		
		
		groupQueue = notOpenedGroups.get(groupId);
		if (groupQueue != null) {
			return groupQueue;
		}
		
		groupQueue = new ArrayList<JPMorganMessage>();
		notOpenedGroups.put(groupId, groupQueue);
		return groupQueue;
	}
	
	
	@Override
	public JPMorganMessage getMessageToSend() {
		JPMorganMessage message = null;
		if (!openedGroups.isEmpty()) {
			message = getMessageFromQueues(openedGroups);
		}
		
		if (message == null && !notOpenedGroups.isEmpty()) {
			message = getMessageFromQueues(notOpenedGroups);
		}
		
		return message;
	}
	
	
	private JPMorganMessage getMessageFromQueues(Map<String, List<JPMorganMessage>> queues) {
		
		JPMorganMessage message = null;
		Iterator<List<JPMorganMessage>> iterator = queues.values().iterator();
		while (message == null && iterator.hasNext()) {
			List<JPMorganMessage> groupQueue = iterator.next();
			if (!groupQueue.isEmpty()) {
				message = groupQueue.remove(0);
			}
		}
		return message;
	}

	@Override
	public void cancelGroup(String groupId) {
		List<JPMorganMessage> groupQueue = openedGroups.remove(groupId);
		if (groupQueue == null) {
			notOpenedGroups.remove(groupId);
		}
		canceledGroups.add(groupId);
	}
	

	
}
