/**
 * 
 */
package net.floodlightcontroller.netmonitor;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import java.math.*;

import org.projectfloodlight.openflow.protocol.OFMatchV3;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.oxm.OFOxm;
import org.projectfloodlight.openflow.types.DatapathId;

/**
 * @author jason
 *
 */
public class AlgorithmCluster {
	
	private static AlgorithmCluster self = null;
	
	private SwitchMap swmap = SwitchMap.getInstance();
	private PollingThreadControl pool = PollingThreadControl.getInstance();

	Logger logger = Logger.getLogger(this.getClass().toString());
	
	//polling
	private int POLLING_PERIOD = 500;
	
	//payless
	private double PAYLESS_HIGHFIELD = 0.6;
	private double PAYLESS_LOWFIELD = 0.3;
	private int MIN_POLLING_PERIOD = 500;
	private int MAX_POLLING_PERIOD = 5000;
	private int INCREASE_INTERVAL = 1000;
	private int REDUCE_INTERVAL = 500;
	private int payless_polling_period = 500;
	private int TIME_FUZZ = 1;
	
	//FAM
	private double FLOW_A_UP = 0;
	private double FLOW_A_DOWN = 0;
	private double FLOW_V_UP = 0;
	private double FLOW_V_DOWN = 0;
	private double FLOW_C = 0;
	
	static public enum Algorithms{FLOWSENSE,PAYLESS,POLLING,MYSELF,FAM;}
	
	static public AlgorithmCluster getInstance()
	{
		if(self == null) self = new AlgorithmCluster();
		return self;
	}
	
	public void PollingAlogrithm(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount;
		v=a=0;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		
		
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
		pool.modifyTask(id, match, POLLING_PERIOD);
	}
	
	public void FlowsneseAlogrithm(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount;
		v=a=0;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		
		
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
	}
	
	public void PaylessAlogrithm(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount;
		v=a=0;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		
		
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));

		SwitchMap swmap = SwitchMap.getInstance();
		Switch sw = swmap.getSwitch(id);
		Iterator<Entry<Match, Flow>> it = sw.flows.entrySet().iterator();
		int linport = Integer.valueOf(
				((OFMatchV3)match).getOxmList().iterator().next().getValue().toString()
				).intValue();
		double allutils = 0;
		while(it.hasNext())
		{
			Entry<Match, Flow> entry = it.next();
			OFMatchV3 mch = (OFMatchV3)entry.getKey();
			Iterator<OFOxm<?>> oxm = mch.getOxmList().iterator();
			int inport = Integer.valueOf(oxm.next().getValue().toString()).intValue();
			if(inport == linport)
			{
				Flow tmp = entry.getValue();
				Iterator<Entry<Double, Double>> it4tmp = tmp.getv().entrySet().iterator();
				Double latestutil = null;
				Entry<Double, Double> entry4tmp = null;
				while(it4tmp.hasNext())
				{
					entry4tmp = it4tmp.next();
				}
				if(now - entry4tmp.getKey().doubleValue() <= TIME_FUZZ)
				{
					latestutil = entry4tmp.getValue();
					allutils = allutils + latestutil.doubleValue();
				}
				else
				{
					//pass
				}
				
				
//				logger.info(latestutil.toString());
			}
		}
		double utils = v/allutils;
		
		//payless
		if(utils >= PAYLESS_LOWFIELD && utils <= PAYLESS_HIGHFIELD)
		{
			//pass
		}else if(utils >= 0 && utils < PAYLESS_LOWFIELD)
		{
			//reduce ferquence of polling
			int tmp = payless_polling_period;
			payless_polling_period = payless_polling_period + INCREASE_INTERVAL;
			if(payless_polling_period >= MAX_POLLING_PERIOD)
			{
				payless_polling_period = MAX_POLLING_PERIOD;
			}
			if(tmp != payless_polling_period)
			{
				pool.modifyTask(id, match, payless_polling_period);
			}
		}else if(utils > PAYLESS_HIGHFIELD && utils <= 1)
		{
			//increase ferquence of polling
			int tmp = payless_polling_period;
			payless_polling_period = payless_polling_period - REDUCE_INTERVAL;
			if(payless_polling_period <= MIN_POLLING_PERIOD)
			{
				payless_polling_period = MIN_POLLING_PERIOD;
			}
			if(tmp != payless_polling_period)
			{
				pool.modifyTask(id, match, payless_polling_period);
			}
		}else
		{
			//error
			logger.info("utils error:"+String.valueOf(utils));
		}
	}
	
	public void MyAlogrithm(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount,c;
		int period = 0;
		v=a=0;
		c = 10000;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		
		
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
		if((a/v) < 0.3)
		{
			period = 2000;
		}else if((a/v) >= 0.3 && (a/v) < 0.6)
		{
			period = 1000;
		}else
		{
			period = 500;
		}
		
		pool.modifyTask(id, match, period);
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
