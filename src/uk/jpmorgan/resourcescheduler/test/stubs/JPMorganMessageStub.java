package uk.jpmorgan.resourcescheduler.test.stubs;

import uk.jpmorgan.resourcescheduler.JPMorganMessage;


public class JPMorganMessageStub extends JPMorganMessage {

	private String groupId;
	private Boolean isTerminationMessage;
		
	public JPMorganMessageStub(String groupId) {
		this.groupId = groupId;
		this.isTerminationMessage = false;
	}

	public JPMorganMessageStub(String groupId, boolean isTerminationMessage) {
		this.groupId = groupId;
		this.isTerminationMessage = isTerminationMessage;
		
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public boolean isTerminationMessage() {
		return isTerminationMessage;
	}
}
