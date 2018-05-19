package net.floodlightcontroller.flowsmonitor;

public class MeasureTask {
	private FlowEntry flowentry;
	private Method method;
	
	public MeasureTask(FlowEntry flowentry, Method method) {
		this.flowentry = flowentry;
		this.method = method;
	}

	public FlowEntry getFlowentry() {
		return flowentry;
	}

	public void setFlowentry(FlowEntry flowentry) {
		this.flowentry = flowentry;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
}
