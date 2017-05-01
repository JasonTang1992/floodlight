package net.floodlightcontroller.netmonitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.match.Match;

import com.google.common.collect.Multiset.Entry;

public class Flow {
	
	Match match;
	
	double duration;
	int bytescounter;
	Map<Double,Double> v = new HashMap<Double,Double>();
	Map<Double,Double> a = new HashMap<Double,Double>();
	
	public Flow()
	{
		
	}
	
	public Flow(Match match)
	{
		this.match = match;
	}
	
	public void update(double now,int counter)
	{
		double v,a;
		v=a=0;
		
		v = (counter-this.bytescounter)/(now-this.duration);
		a = (v-this.v.get(Double.valueOf(this.duration)).doubleValue())/(now-this.duration);
		
		this.v.put(Double.valueOf(now), Double.valueOf(v));
		this.a.put(Double.valueOf(now), Double.valueOf(a));
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
