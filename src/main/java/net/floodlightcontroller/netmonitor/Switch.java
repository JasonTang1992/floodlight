package net.floodlightcontroller.netmonitor;

import java.util.ArrayList;
import java.util.Iterator;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;

import net.floodlightcontroller.core.IOFSwitch;

public class Switch {
	
	ArrayList<Flow> flows = new ArrayList<Flow>();
	IOFSwitch sw = null;
	DatapathId id = null;

	public boolean contains(Match match) {
		// TODO Auto-generated method stub
		
		Iterator it = flows.iterator();
		
		while(it.hasNext())
		{
			if(((Flow)it.next()).equals(match))
			{
				return true;
			}
		}
		return false;
	}

	public void addFlow(Match match) {
		// TODO Auto-generated method stub
		Flow flow = new Flow(match);
	
		flows.add(flow);
	}

	public void rmFlow(Match match) {
		// TODO Auto-generated method stub
		Iterator it = flows.iterator();
		
		while(it.hasNext())
		{
			Flow tmp = (Flow)it.next();
			if((tmp).equals(match))
			{
			
			}
		}
		
	}
	
	public Flow getFlow(Match match)
	{
		return null;
	}

	public boolean contains(long xid) {
		// TODO Auto-generated method stub
		return false;
	}

	public void update(long xid) {
		// TODO Auto-generated method stub
		
	}

}
