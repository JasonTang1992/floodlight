package net.floodlightcontroller.practice.mactracer;

import java.util.HashMap;

public class SwitchMap {

	HashMap<LinkMatch,LinkStatistics>LinkStatTable = 
			new HashMap<LinkMatch,LinkStatistics>();
	HashMap<FlowMatch,FlowStatistics>FlowStatTable = 
			new HashMap<FlowMatch,FlowStatistics>();
	long swId;
	
	public void addLinkStat(LinkMatch match,LinkStatistics stat)
	{
		LinkStatTable.put(match, stat);
	}
	
	public void rmLinkStat(LinkMatch match)
	{
		LinkStatTable.remove(match);
	}
	
	public LinkStatistics getLinkStat(LinkMatch match)
	{
		return LinkStatTable.get(match);
	}

	public void addFlowStat(FlowMatch match,FlowStatistics stat)
	{
		FlowStatTable.put(match, stat);
	}
	
	public void rmFlowStat(FlowMatch match)
	{
		FlowStatTable.remove(match);
	}
	
	public FlowStatistics getFlowStat(FlowMatch match)
	{
		return FlowStatTable.get(match);
	}

	public HashMap<LinkMatch, LinkStatistics> getLinkStatTable() {
		return LinkStatTable;
	}

	public void setLinkStatTable(HashMap<LinkMatch, LinkStatistics> linkStatTable) {
		LinkStatTable = linkStatTable;
	}

	public HashMap<FlowMatch, FlowStatistics> getFlowStatTable() {
		return FlowStatTable;
	}

	public void setFlowStatTable(HashMap<FlowMatch, FlowStatistics> flowStatTable) {
		FlowStatTable = flowStatTable;
	}

	public long getSwId() {
		return swId;
	}

	public void setSwId(long swId) {
		this.swId = swId;
	}
	

}
