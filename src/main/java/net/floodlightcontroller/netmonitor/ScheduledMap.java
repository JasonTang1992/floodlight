package net.floodlightcontroller.netmonitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.projectfloodlight.openflow.protocol.match.Match;

public class ScheduledMap {
	static Map<Double,Map<Match,Flow>> scheduledmaps = new HashMap<Double,Map<Match,Flow>>();
	static ScheduledMap self = null;
	
	static public ScheduledMap getInstance()
	{
		if(self == null)
		{
			self = new ScheduledMap();
		}
		
		return self;
	}
	
	public Map<Match,Flow> getScheduledMap(double timeout)
	{
		if(self != null)
		{
			return scheduledmaps.get(Double.valueOf(timeout));
		}
		return null;
	}
	
	public void addScheduledMap(Match match,double timeout)
	{
		Entry<Double,Map<Match,Flow>> tmp;
		Iterator it = scheduledmaps.entrySet().iterator();
		
		if(scheduledmaps.containsKey(Double.valueOf(timeout)) == false)
		{
			Map<Match,Flow> map = new HashMap<Match,Flow>();
			map.put(match, new Flow(match));
			scheduledmaps.put(Double.valueOf(timeout), map);
		}
		
		while(it.hasNext())
		{
			tmp = (Entry<Double, Map<Match, Flow>>) it.next();
			if(tmp.getValue().containsKey(match))
			{
				tmp.getValue().remove(match);
			}
			
			if(tmp.getKey() == timeout)
			{
				tmp.getValue().put(match, new Flow(match));
			}
		}
	}
	
	public boolean rmFlow(Match match)
	{
		Entry<Double,Map<Match,Flow>> tmp;
		Iterator it = scheduledmaps.entrySet().iterator();
		
		while(it.hasNext())
		{
			tmp = (Entry<Double, Map<Match, Flow>>) it.next();
			if(tmp.getValue().containsKey(match))
			{
				tmp.getValue().remove(match);
				return true;
			}
		}
		return false;
	}
	
	
	
	
}
