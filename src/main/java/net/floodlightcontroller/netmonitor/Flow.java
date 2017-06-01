package net.floodlightcontroller.netmonitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.protocol.match.MatchFields;

import com.google.common.collect.Multiset.Entry;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public class Flow {
	
	Match match;
	
	double duration = 0;
	long bytescounter = 0;
	Map<Double,Double> v = new LinkedHashMap<Double,Double>();
	Map<Double,Double> a = new LinkedHashMap<Double,Double>();
	
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	public Flow()
	{
		
	}
	
	public Flow(Match match)
	{
		this.match = match;
		v.putIfAbsent(Double.valueOf(0), Double.valueOf(0));
		a.putIfAbsent(Double.valueOf(0), Double.valueOf(0));
	}
	
	public synchronized void update(double now,long l)
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
		String rs = new String("Speed Table");
		while(it.hasNext()){
			Map.Entry<Double, Double> entry = (Map.Entry<Double, Double>)it.next();
			rs = rs + "\r\n" + "TimeStamp: " + entry.getKey().doubleValue() + " Speed: " + Double.valueOf(entry.getValue().doubleValue()/125000).toString()+"Mbps";
//			logger.info("TimeStamp: " + entry.getKey().toString() + " Speed: " + entry.getValue().toString());
		}
		
		return rs;
	}
	
	
}
