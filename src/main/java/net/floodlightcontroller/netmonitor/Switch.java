package net.floodlightcontroller.netmonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;

import net.floodlightcontroller.core.IOFSwitch;

public class Switch {
	
	Map<Match,Flow> flows = new HashMap<Match,Flow>();
	IOFSwitch sw = null;
	DatapathId id = null;

	public Switch(DatapathId id,IOFSwitch sw) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.sw = sw;
	}

	public boolean contains(Match match) {
		// TODO Auto-generated method stub
		
		return this.flows.containsKey(match);
	}

	public void addFlow(Match match) {
		// TODO Auto-generated method stub
		if(this.contains(match) == false)
		{
			flows.put(match, new Flow(match));
		}
	}

	public void rmFlow(Match match) {
		// TODO Auto-generated method stub
		this.flows.remove(match);
	}
	
	public Flow getFlow(Match match)
	{
		return this.flows.get(match);
	}
	
	public void update(Match match,double now,long l)
	{
		Flow flow = this.flows.get(match);
		flow.update(now, l);
	}


}
