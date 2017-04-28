package net.floodlightcontroller.netmonitor;

import org.projectfloodlight.openflow.types.DatapathId;

public class SwitchMap {
	static SwitchMap map;

	public static SwitchMap getMap() {
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
