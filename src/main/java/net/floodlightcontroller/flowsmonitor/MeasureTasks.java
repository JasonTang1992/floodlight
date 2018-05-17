package floodlightcontroller.flowsmonitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MeasureTasks {
	private HashMap<FlowMatch,MeasureConfig> measureTasks;
	
	public FlowMatch contains(int srcIP, int srcPort, int dstIP, int dstPort, int Protocol, long Dpid) {
		int tasknum = 0;
		Iterator<FlowMatch> flowMatches = measureTasks.keySet().iterator();
		while(flowMatches.hasNext()) {
			FlowMatch tmp = flowMatches.next();
			if(srcIP == tmp.getScrIP() &&
					srcPort == tmp.getScrPort() &&
					dstIP == tmp.getDstIP() &&
					dstPort == tmp.getDstPort() &&
					Protocol == tmp.getProtocol() &&
					Dpid == tmp.getDpid()) {
				return tmp;
			}
		}
		return null;
	}
	
	
	public MeasureConfig getMeasureConfig(int srcIP, int srcPort, int dstIP, int dstPort, int Protocol, long Dpid) {
		return measureTasks.get(this.contains(srcIP, srcPort, dstIP, dstPort, Protocol, Dpid));
	}
	
	private class FlowMatch{
		int scrIP;		
		int scrPort;		
		int dstIP;		
		int dstPort;	
		int Protocol;
		long dpid;
		
		public int getScrIP() {
			return scrIP;
		}
		public void setScrIP(int scrIP) {
			this.scrIP = scrIP;
		}
		public int getScrPort() {
			return scrPort;
		}
		public void setScrPort(int scrPort) {
			this.scrPort = scrPort;
		}
		public int getDstIP() {
			return dstIP;
		}
		public void setDstIP(int dstIP) {
			this.dstIP = dstIP;
		}
		public int getDstPort() {
			return dstPort;
		}
		public void setDstPort(int dstPort) {
			this.dstPort = dstPort;
		}
		public int getProtocol() {
			return Protocol;
		}
		public void setProtocol(int protocol) {
			Protocol = protocol;
		}
		public long getDpid() {
			return dpid;
		}
		public void setDpid(long dpid) {
			this.dpid = dpid;
		}
		
	
		
	}
	
}
