package net.floodlightcontroller.flowsmonitor;

import java.util.HashMap;
import java.util.Iterator;

import org.projectfloodlight.openflow.protocol.match.Match;

public class FlowEntry {
	private Match matchfield;
	private int tableID;
	private long dpid;
	private long byteCounter;
	private long packetCounter;
	private float duringTime;
	private HashMap<Double,Double> rateMap = new HashMap<Double,Double>();
	
	public FlowEntry(Match matchfield, int tableID, long dpid) {
		this.matchfield = matchfield;
		this.tableID = tableID;
		this.dpid = dpid;
		this.byteCounter = 0;
		this.packetCounter = 0;
		this.duringTime = 0;
	}

	public long getByteCounter() {
		return byteCounter;
	}

	public void setByteCounter(long byteCounter) {
		this.byteCounter = byteCounter;
	}

	public long getPacketCounter() {
		return packetCounter;
	}

	public void setPacketCounter(long packetCounter) {
		this.packetCounter = packetCounter;
	}

	public float getDuringTime() {
		return duringTime;
	}

	public void setDuringTime(float duringTime) {
		this.duringTime = duringTime;
	}

	public Match getMatchfield() {
		return matchfield;
	}

	public int getTableID() {
		return tableID;
	}

	public long getDpid() {
		return dpid;
	}

	public HashMap<Double,Double> getRateMap() {
		return rateMap;
	}
	
	public Double getRate(Double ts) {
		return this.rateMap.get(ts);
	}
	
	public void addRate(double d, double e) {
		this.rateMap.put(Double.valueOf(d), Double.valueOf(e));
	}

	@Override
	public boolean equals(Object obj) {
		FlowEntry flow = (FlowEntry)obj;
		Iterator ita,itb;
		ita = this.getMatchfield().getMatchFields().iterator();
		itb = flow.getMatchfield().getMatchFields().iterator();
		while(ita.hasNext() && itb.hasNext()) {
			if(!ita.next().toString().equals(itb.next().toString())) {
				return false;
			}
		}
		if(		this.getTableID() == flow.getTableID() &&
				this.getDpid() == flow.getDpid()) {
			return true;
		}else 
		{
			return false;
		}
	}
}
