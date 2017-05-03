package net.floodlightcontroller.netmonitor;

import java.util.Map;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;

public class SwitchMap {
	static SwitchMap map;
	static Map<Double,Map<Match,Flow>> scheduledmap;

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
		return false;
	}

	public void addSwitch(DatapathId id) {
		// TODO Auto-generated method stub
		
	}

	public Switch getSwitch(DatapathId id) {
		// TODO Auto-generated method stub
		return null;
	}


}
