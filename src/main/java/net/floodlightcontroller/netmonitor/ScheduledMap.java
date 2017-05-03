package net.floodlightcontroller.netmonitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;

public class ScheduledMap {
	static Map<Double,Map<Match,DatapathId>> scheduledmaps = new HashMap<Double,Map<Match,DatapathId>>();
	static ScheduledMap self = null;
	
	static public ScheduledMap getInstance()
	{
		if(self == null)
		{
			self = new ScheduledMap();
		}
		
		return self;
	}
	
	public Map<Match, DatapathId> getScheduledMap(double timeout)
	{
		if(self != null)
		{
			return scheduledmaps.get(Double.valueOf(timeout));
		}
		return null;
	}
	
	public void addScheduledMap(Match match,DatapathId id,double timeout)
	{
		Entry<Double,Map<Match,DatapathId>> tmp;
		Iterator it = scheduledmaps.entrySet().iterator();
		
		if(scheduledmaps.containsKey(Double.valueOf(timeout)) == false)
		{
			Map<Match,DatapathId> map = new HashMap<Match,DatapathId>();
			map.put(match, id);
			scheduledmaps.put(Double.valueOf(timeout), map);
		}
		
		while(it.hasNext())
		{
			tmp = (Entry<Double, Map<Match, DatapathId>>) it.next();
			if(tmp.getValue().containsKey(match))
			{
				tmp.getValue().remove(match);
			}
			
			if(tmp.getKey() == timeout)
			{
				tmp.getValue().put(match, id);
			}
		}
	}
	
	public boolean rmFlow(Match match)
	{
		Entry<Double,Map<Match,DatapathId>> tmp;
		Iterator it = scheduledmaps.entrySet().iterator();
		
		while(it.hasNext())
		{
			tmp = (Entry<Double, Map<Match, DatapathId>>) it.next();
			if(tmp.getValue().containsKey(match))
			{
				tmp.getValue().remove(match);
				//TODO if map is cleared , the related polling worker will shutdown
				return true;
			}
		}
		return false;
	}
	
	public boolean rmFlow(DatapathId id)
	{
		Entry<Double,Map<Match,DatapathId>> tmp;
		Iterator it = scheduledmaps.entrySet().iterator();
		
		while(it.hasNext())
		{
			tmp = (Entry<Double, Map<Match, DatapathId>>) it.next();
			if(tmp.getValue().containsValue(id))
			{
				tmp.getValue().containsValue(id);
				//TODO if map is cleared , the related polling worker will shutdown
				return true;
			}
		}
		return false;
	}
	
	public boolean rmFlow(Match match,DatapathId id)
	{
		Entry<Double,Map<Match,DatapathId>> tmp;
		Iterator it = scheduledmaps.entrySet().iterator();
		
		while(it.hasNext())
		{
			tmp = (Entry<Double, Map<Match, DatapathId>>) it.next();
			if(tmp.getValue().containsKey(match) && tmp.getValue().containsValue(id))
			{
				tmp.getValue().remove(match, id);
				//TODO if map is cleared , the related polling worker will shutdown
				return true;
			}
		}
		return false;
	}
	
	

	
	
	
	
}
