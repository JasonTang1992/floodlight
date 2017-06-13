package net.floodlightcontroller.netmonitor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;

import net.floodlightcontroller.core.IOFSwitch;

public class SwitchMap {
	static SwitchMap map;
//	static ConcurrentMap<Double,Map<Match,Flow>> scheduledmap;
	ConcurrentMap<DatapathId,Switch> switches = new ConcurrentHashMap<DatapathId,Switch>();

	public static SwitchMap getInstance() {
		// TODO Auto-generated method stub
		if(map == null)
		{
			map = new SwitchMap();
		}
		return map;
	}

	public boolean contains(DatapathId id) {
		// TODO Auto-generated method stub
		return switches.containsKey(id);
	}

	public synchronized void addSwitch(DatapathId id,IOFSwitch sw) {
		// TODO Auto-generated method stub
		if(this.contains(id) == false)
		{
			this.switches.put(id, new Switch(id,sw));
		}
		else if(!this.getSwitch(id).equals(sw))
		{
			Switch tmp = new Switch(id,sw);
			tmp.flows.putAll(this.switches.get(id).flows);
			this.switches.remove(id);
			this.switches.put(id, tmp);
		}
	}

	public Switch getSwitch(DatapathId id) {
		// TODO Auto-generated method stub
		return this.switches.get(id);
	}
	
	public synchronized void update(Match match,DatapathId id,double now,long l)
	{
		this.switches.get(id).update(match, now, l);
	}


}
