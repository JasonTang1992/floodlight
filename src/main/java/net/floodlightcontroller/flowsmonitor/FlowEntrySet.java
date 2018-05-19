package net.floodlightcontroller.flowsmonitor;

import java.util.ArrayList;
import java.util.HashMap;

import org.projectfloodlight.openflow.protocol.match.Match;

public class FlowEntrySet {
	private ArrayList<FlowEntry> flowentrySet;
	private HashMap<FlowEntry,Method> flowmethodMap;
	
	public FlowEntry getFlowEntry(Match matchfield) {
		int len = flowentrySet.size();
		for(int i=0;i<len;i++) {
			if(flowentrySet.get(i).getMatchfield().equals(matchfield)) {
				return flowentrySet.get(i);
			}
		}
		return null;
	}
	
	public int addFlowEntry(FlowEntry flowEntry) {
		int len = flowentrySet.size();
		for(int i=0;i<len;i++) {
			if(flowentrySet.get(i).getMatchfield().equals(flowEntry.getMatchfield())) {
				return 1;
			}
		}
		this.flowentrySet.add(flowEntry);
		return 0;
	}
	
	public int setMethod(FlowEntry flowEntry, Method method) {
		if(this.flowmethodMap.containsKey(flowEntry)) {
			this.flowmethodMap.replace(flowEntry, method);
			return 0;
		}else {
			this.flowmethodMap.put(flowEntry, method);
			return 0;
		}
	}
}
