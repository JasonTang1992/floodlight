package net.floodlightcontroller.flowsmonitor;

import java.util.ArrayList;

public class SwitchSet {
	private ArrayList<Switch> switchSet = new ArrayList<Switch>();
	
	public Switch getSwitch(long dpid) {
		int len = this.switchSet.size();
		for(int i=0;i<len;i++) {
			if(this.switchSet.get(i).getDpid() == dpid) {
				return this.switchSet.get(i);
			}
		}
		return null;
	}
	
	public int addSwitch(long dpid) {
		int len = this.switchSet.size();
		if(len > 0) {
			for(int i = 0;i< len;i++) {
				if(this.switchSet.get(i).getDpid() == dpid) {
					return 1;
				}
			}
		}
		this.switchSet.add(new Switch(dpid));
		return 0;
	}
}
