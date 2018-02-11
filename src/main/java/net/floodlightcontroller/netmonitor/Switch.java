package net.floodlightcontroller.netmonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.TableId;

import net.floodlightcontroller.core.IOFSwitch;

public class Switch {
	
	ConcurrentMap<Match,Flow> flows = new ConcurrentHashMap<Match,Flow>();
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
			flows.put(match, new Flow(match,this.id));
		}
	}

	public void addFlow(Match match,TableId tableId,int priority) {
		// TODO Auto-generated method stub
		if(this.contains(match) == false)
		{
			flows.put(match, new Flow(match,this.id,tableId,priority));
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Switch))
			return false;
		Switch other = (Switch) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


}
