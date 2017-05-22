package net.floodlightcontroller.netmonitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.projectfloodlight.openflow.protocol.match.Match;

import com.google.common.collect.Multiset.Entry;

public class Flow {
	
	Match match;
	
	double duration = 0;
	long bytescounter = 0;
	ConcurrentMap<Double,Double> v = new ConcurrentHashMap<Double,Double>();
	ConcurrentMap<Double,Double> a = new ConcurrentHashMap<Double,Double>();
	
	public Flow()
	{
		
	}
	
	public Flow(Match match)
	{
		this.match = match;
		v.putIfAbsent(Double.valueOf(0), Double.valueOf(0));
		a.putIfAbsent(Double.valueOf(0), Double.valueOf(0));
	}
	
	public void update(double now,long l)
	{
		double v,a;
		v=a=0;
		
		v = (l-this.bytescounter)/(now-this.duration);
		a = (v-this.v.get(Double.valueOf(this.duration)).doubleValue())/(now-this.duration);
		
		this.v.put(Double.valueOf(now), Double.valueOf(v));
		this.a.put(Double.valueOf(now), Double.valueOf(a));
		
		this.duration = now;
		this.bytescounter = l;
	}
	

	public String getv()
	{
		String str = null;
		double v;
		int time;
		Map<Double,Double> tmp;
		return this.v.toString();
	}
	
	public String geta()
	{
		return this.a.toString();
	}
}
