package net.floodlightcontroller.netmonitor;

import java.util.HashMap;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;

import net.floodlightcontroller.core.IOFSwitch;

public class SwitchMap {
	static SwitchMap map;
	static Map<Double,Map<Match,Flow>> scheduledmap;
	Map<DatapathId,Switch> switches = new HashMap<DatapathId,Switch>();

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

	public void addSwitch(DatapathId id,IOFSwitch sw) {
		// TODO Auto-generated method stub
		if(this.contains(id) == false)
		{
			this.switches.put(id, new Switch(id,sw));
		}
	}

	public Switch getSwitch(DatapathId id) {
		// TODO Auto-generated method stub
		return this.switches.get(id);
	}
	
	public void update(Match match,DatapathId id,double now,long l)
	{
		this.switches.get(id).update(match, now, l);
	}


}
