package net.floodlightcontroller.flowsmonitor;

import org.restlet.Context;
import org.restlet.Restlet;

import net.floodlightcontroller.restserver.RestletRoutable;

public class FlowsMonitorWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String basePath() {
		// TODO Auto-generated method stub
		return "/test/flowsmonitor";
	}

}
