package net.floodlightcontroller.practice.mactracer;

import java.util.TreeMap;

public class FlowStatistics {
	
	private FlowMatch match;
	private TreeMap<Long,Double> UtilizationTable = new TreeMap<Long,Double>();
	private long timestamp_satrt=0;
	private long timestamp_end = 0;
	private long idle_time = 0xFFFFFFFF;

	public void addUtilization(long timestamp,double ultilization)
	{
		UtilizationTable.put(Long.valueOf(timestamp)
				, Double.valueOf(ultilization));
	}
	
	public void rmUtilization(long timestamp)
	{
		UtilizationTable.remove(Long.valueOf(timestamp));
	}
	
	public double getUtilization(long timestamp)
	{
		return UtilizationTable.get(Long.valueOf(timestamp))
				.doubleValue();
	}
	
	public long getlasttimestamp()
	{
		return UtilizationTable.lastKey().longValue();
	}

	public FlowMatch getMatch() {
		return match;
	}

	public void setMatch(FlowMatch match) {
		this.match = match;
	}

	public TreeMap<Long, Double> getUtilizationTable() {
		return UtilizationTable;
	}

	public void setUtilizationTable(TreeMap<Long, Double> utilizationTable) {
		UtilizationTable = utilizationTable;
	}

	public long getTimestamp_satrt() {
		return timestamp_satrt;
	}

	public void setTimestamp_satrt(long timestamp_satrt) {
		this.timestamp_satrt = timestamp_satrt;
	}

	public long getTimestamp_end() {
		return timestamp_end;
	}

	public void setTimestamp_end(long timestamp_end) {
		this.timestamp_end = timestamp_end;
	}

	public long getIdle_time() {
		return idle_time;
	}

	public void setIdle_time(long idle_time) {
		this.idle_time = idle_time;
	}

	
}
