package net.floodlightcontroller.netmonitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.protocol.match.MatchFields;

import com.google.common.collect.Multiset.Entry;

public class Flow {
	
	Match match;
	
	double duration;
	long bytescounter;
	ConcurrentMap<Double,Double> v = new ConcurrentHashMap<Double,Double>();
	ConcurrentMap<Double,Double> a = new ConcurrentHashMap<Double,Double>();
	
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	public Flow()
	{
		
	}
	
	public Flow(Match match)
	{
		this.match = match;
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

	@Override
	public String toString() {
		Iterator it = this.v.entrySet().iterator();
		String rs = new String();
		while(it.hasNext()){
			Map.Entry<Double, Double> entry = (Map.Entry<Double, Double>)it.next();
			rs = rs + "\r\n" + "TimeStamp: " + entry.getKey().toString() + " Speed: " + entry.getValue().toString();
//			logger.info("TimeStamp: " + entry.getKey().toString() + " Speed: " + entry.getValue().toString());
		}
		
		return rs;
	}
	
	
}
