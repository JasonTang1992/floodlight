package net.floodlightcontroller.netmonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.projectfloodlight.openflow.protocol.OFMatchV3;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.protocol.match.MatchFields;
import org.projectfloodlight.openflow.types.DatapathId;
import org.python.modules.time.Time;

import com.google.common.collect.Multiset.Entry;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public class Flow {
	
	Match match;
	DatapathId swId;
	
	double duration = 0;
	long bytescounter = 0;
	Map<Double,Double> v = new LinkedHashMap<Double,Double>();
	Map<Double,Double> a = new LinkedHashMap<Double,Double>();
	
	List<Long> win = new ArrayList<Long>();
	List<Double> dwin = new ArrayList<Double>();

	List<Double> arimalist = new ArrayList<Double>();

	int ws = 3;
	double slidewin = 0;
	
	int period = 500;
	
	AlgorithmCluster.Algorithms Algorithm = AlgorithmCluster.Algorithms.MYSELF;
	
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	public Flow()
	{
		
	}
	
	public Flow(Match match,DatapathId id)
	{
		this.match = match;
		this.swId = id;
		this.duration = Time.time();
		v.putIfAbsent(this.duration, Double.valueOf(0));
		a.putIfAbsent(this.duration, Double.valueOf(0));
	}
	
	public synchronized void update(double now,long l)
	{
		
		AlgorithmCluster ag = AlgorithmCluster.getInstance();
		switch(Algorithm)
		{
		case PAYLESS:
			ag.PaylessAlogrithm(swId, match, now, l);
			break;
		case FLOWSENSE:
			ag.FlowsneseAlogrithm(swId, match, now, l);
			break;
		case POLLING:
			ag.PollingAlogrithm(swId, match, now, l);
			break;
		case MYSELF:
			ag.ARIMAPeriodic(swId, match, now, l);
			break;
		case Elastic:
			ag.Elastic(swId, match, now, l);
			break;
		case SWT:
			ag.SWT(swId, match, now, l);
			break;
		default:
			logger.info("algorithm miss matching");
		}
		
//		
		
		this.duration = now;
		this.bytescounter = l;
	}
	

	public Map<Double,Double> getv()
	{
		return this.v;
	}
	
	public Map<Double,Double> geta()
	{
		return this.a;
	}

	@Override
	public String toString() {
		Iterator it = this.v.entrySet().iterator();
		String rs = new String("Speed Table");
		rs = rs + "\r\n";
		rs = rs + ((OFMatchV3)this.match).getOxmList().toString();
		while(it.hasNext()){
			Map.Entry<Double, Double> entry = (Map.Entry<Double, Double>)it.next();
			rs = rs + "\r\n" + "TimeStamp: " + entry.getKey().doubleValue() + " Speed: " + Double.valueOf(entry.getValue().doubleValue()/125000).toString()+"Mbps";
		}
		
		return rs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((match == null) ? 0 : match.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Flow))
			return false;
		Flow other = (Flow) obj;
		if (match == null) {
			if (other.match != null)
				return false;
		} else if (!match.equals(other.match))
			return false;
		return true;
	}
	
	
	
	
}
