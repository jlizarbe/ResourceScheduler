package uk.jpmorgan.resourcescheduler;

import uk.jpmorgan.resourcescheduler.test.stubs.GatewayStub;

public class GatewayFactory {

	private static Gateway gateway; 
	
	public static Gateway getGateway() {
		if (gateway == null) {
			gateway = new GatewayStub(); 
		}
		return gateway;
	}
}
