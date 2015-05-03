package uk.jpmorgan.resourcescheduler;

public interface Gateway {
	
	void send(Message msg);
}