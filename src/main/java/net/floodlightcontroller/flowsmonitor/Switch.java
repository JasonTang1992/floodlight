package net.floodlightcontroller.flowsmonitor;

import java.util.ArrayList;

public class Switch {
	private long dpid;
	private FlowEntrySet flowentrySet = new FlowEntrySet();
	
	public Switch(long dpid) {
		this.dpid = dpid;
	}

	public long getDpid() {
		return dpid;
	}

	public FlowEntrySet getFlowentrySet() {
		return flowentrySet;
	}
	
}
