/**
 * 
 */
package net.floodlightcontroller.netmonitor;

import java.util.Iterator;
import java.util.List;
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
	
	static public enum Algorithms{FLOWSENSE,PAYLESS,POLLING,MYSELF,FAM,Elastic, SWT;}
	
	static public AlgorithmCluster getInstance()
	{
		if(self == null) self = new AlgorithmCluster();
		return self;
	}
	
	
	public double Normaldistribution(){
		double u1,u2;
		u1 = Math.random();
		u2 = Math.random();
		return Math.sqrt(-2*Math.log(u1)*Math.cos(2*Math.PI*u2));
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
		int period = 500;
		v=a=0;
		c = 10000;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		period = flow.period;
		
		
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
//		if((a/v) < 0.2)
//		{
//			period = 2000;
//		}else if((a/v) >= 0.2 && (a/v) < 0.6)
//		{
//			period = 1000;
//		}else
//		{
//			period = 500;
//		}
		
		if((a/v) > 0.2)
		{
			period = ((period/2)>500)?(period/2):500;
		}else
		{
			period = ((period*3)<5000)?(period*3):5000;
		}

		
		pool.modifyTask(id, match, period);
		flow.period = period;
		
	}

	public void Elastic(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount,c,slidewin;
		int period = 500;
		v=a=0;
		c = 10000;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		period = flow.period;
		
		
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		slidewin =(a/((v+now-lasttime)/2));
		
		if(Math.abs(slidewin-flow.slidewin) > 0.2)
		{
			period = ((period/2)>500)?(period/2):500;
		}else
		{
			period = ((period*3)<5000)?(period*3):5000;
		}
		flow.slidewin = slidewin;
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
		pool.modifyTask(id, match, period);
		flow.period = period;
		
	}

	public void SWT(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount,c,mean;
		int period = 500;
		long var = 0;
		List<Long> win;
		v=a=0;
		c = 10000;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		win = flow.win;
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		period = flow.period;
		
		var = (long) (l-lastbytecount);
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		win.add(Long.valueOf(var));
		Iterator<Long> it =  win.iterator();
		mean = 0;
		while(it.hasNext()){
			mean = mean + (it.next().doubleValue()/win.size());
		}
		
	    double rval = 0;  
	    for (int i = 0; i < win.size(); i++) {  
	        rval += Math.pow((win.get(i).doubleValue() - mean), 2);  
	    }  
	    rval /= win.size();  
	    rval = Math.sqrt(rval);  

		
		if(var > mean + 2*rval)
		{
			period = ((period/2)>500)?(period/2):500;
			flow.ws = (flow.ws/2<3)?flow.ws/2:3;
		}else
		{
			period = ((period*2)<5000)?(period*3):5000;
			flow.ws = flow.ws + 1;
		}
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
		if(win.size() > flow.ws){
			win.remove(0);
		}
		pool.modifyTask(id, match, period);
		flow.period = period;
		
	}

	public void MyAlogrithm2(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount,c;
		int period = 500;
		v=a=0;
		c = 10000;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		period = flow.period;
		
		
		v = (l-lastbytecount)/(now-lasttime);
		a = Math.abs(v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
		if((a/v)/(Math.exp(-(double)(now-lasttime-500)/500)) > 0.2)
		{
			period = ((period/2)>500)?(period/2):500;
		}else
		{
			period = ((period*3)<5000)?(period*3):5000;
		}
		
		pool.modifyTask(id, match, period);
		flow.period = period;
		
	}

	
	public void MyAlogrithm3(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount,c;
		int period = 500;
		v=a=0;
		c = 10000;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		period = flow.period;
		
		
		v = (l-lastbytecount)/(now-lasttime);
		a = Math.abs(v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		double factor = 0;
//		factor = (a/Math.pow(v, 0.999))/(Math.exp(-(double)(now-lasttime-500)/500)) + (1-5*Math.exp(-Math.pow(v/125000, 0.999)));
		factor = ((a+0.1)/Math.pow(v, 0.999))/(Math.exp(-(double)(now-lasttime-500)/500));
		logger.info(String.valueOf(factor));
		if(factor > 0.2)
		{
			period = ((period/2)>500)?(period/2):500;
		}else
		{
			logger.info("period"+String.valueOf(period));
			period = Math.min(period*2,5000);
		}
		
		pool.modifyTask(id, match, period);
		flow.period = period;
	}
	
	public void MyAlogrithm4(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount,c,mean;
		int period = 500;
		long var = 0;
		List<Long> win;
		v=a=0;
		c = 10000;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		win = flow.win;
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		period = flow.period;
		
		var = (long) (l-lastbytecount);
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		win.add(Long.valueOf(var));
		Iterator<Long> it =  win.iterator();
		mean = 0;
		while(it.hasNext()){
			mean = mean + (it.next().doubleValue()/win.size());
		}
		
	    double rval = 0;  
	    for (int i = 0; i < win.size(); i++) {  
	        rval += Math.pow((win.get(i).doubleValue() - mean), 2);  
	    }  
	    rval /= win.size();  
	    rval = Math.sqrt(rval);  
	    
	    if(Math.random() > 0.7)
	    {
	    	period = 500;
	    	flow.ws = flow.ws + 1;
	    }
	    else if(var > mean + 2*rval)
		{
			period = ((period/2)>500)?(period/2):500;
			flow.ws = (flow.ws/2<3)?flow.ws/2:3;
		}else
		{
			period = ((period*2)<5000)?(period*3):5000;
			flow.ws = flow.ws + 1;
		}
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
		if(win.size() > flow.ws){
			win.remove(0);
		}
		pool.modifyTask(id, match, period);
		flow.period = period;
		
	}

	public void MyAlogrithm5(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount,c,mean;
		int period = 500;
		long var = 0;
		List<Long> win;
		v=a=0;
		c = 10000;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		win = flow.win;
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		period = flow.period;
		
		var = (long) (l-lastbytecount);
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		win.add(Long.valueOf(var));
		Iterator<Long> it =  win.iterator();
		mean = 0;
		while(it.hasNext()){
			mean = mean + (it.next().doubleValue()/win.size());
		}
		
	    double rval = 0;  
	    for (int i = 0; i < win.size(); i++) {  
	        rval += Math.pow((win.get(i).doubleValue() - mean), 2);  
	    }  
	    rval /= win.size();  
	    rval = Math.sqrt(rval);  
	    
	    if(Normaldistribution() > 0.7)
	    {
	    	period = 500;
	    	flow.ws = flow.ws + 1;
	    }
	    else if(var > mean + 2*rval)
		{
			period = ((period/2)>500)?(period/2):500;
			flow.ws = (flow.ws/2<3)?flow.ws/2:3;
		}else
		{
			period = ((period*2)<5000)?(period*3):5000;
			flow.ws = flow.ws + 1;
		}
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
		if(win.size() > flow.ws){
			win.remove(0);
		}
		pool.modifyTask(id, match, period);
		flow.period = period;
		
	}

	public void RAdaRate(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount,c,mean;
		int period = 500;
		long var = 0;
		List<Double> win;
		v=a=0;
		c = 10000;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		win = flow.dwin;
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		period = flow.period;
		
//		var = (long) (l-lastbytecount);
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
		win.add(Double.valueOf(v));
		Iterator<Double> it =  win.iterator();
		mean = 0;
		while(it.hasNext()){
			mean = mean + (it.next().doubleValue()/win.size());
		}
		
	    double rval = 0;  
	    for (int i = 0; i < win.size(); i++) {  
	        rval += Math.pow((win.get(i).doubleValue() - mean), 2);  
	    }  
	    rval /= win.size();  
	    rval = Math.sqrt(rval);  
	    
	    if(Normaldistribution() > 0.9)
	    {
	    	period = 500;
	    	flow.ws = flow.ws + 1;
	    }
	    else if(rval > 0.1*mean)
		{
			period = ((period/2)>500)?(period/2):500;
			flow.ws = (flow.ws/2<3)?flow.ws/2:3;
		}else
		{
			period = ((period*2)<5000)?(period*3):5000;
			flow.ws = flow.ws + 1;
		}
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
		if(win.size() > flow.ws){
			win.remove(0);
		}
		pool.modifyTask(id, match, period);
		flow.period = period;
		
	}

	public void AdaRate(DatapathId id ,Match match,double now,long l)
	{
		double v,a,lasttime,lastbytecount,c,mean;
		int period = 500;
		long var = 0;
		List<Double> win;
		v=a=0;
		c = 10000;
		
		Flow flow = swmap.getSwitch(id).getFlow(match);
		win = flow.dwin;
		lasttime = flow.duration;
		lastbytecount = flow.bytescounter;
		period = flow.period;
		
//		var = (long) (l-lastbytecount);
		v = (l-lastbytecount)/(now-lasttime);
		a = (v-flow.getv().get(Double.valueOf(lasttime)).doubleValue())/(now-lasttime);
		
	
		win.add(Double.valueOf(v));
		Iterator<Double> it =  win.iterator();
		mean = 0;
		while(it.hasNext()){
			mean = mean + (it.next().doubleValue()/win.size());
		}
		
	    double rval = 0;  
	    for (int i = 0; i < win.size(); i++) {  
	        rval += Math.pow((win.get(i).doubleValue() - mean), 2);  
	    }  
	    rval /= win.size();  
	    rval = Math.sqrt(rval);  
	    
	    if(rval > 0.1*mean)
		{
			period = ((period/2)>500)?(period/2):500;
			flow.ws = (flow.ws/2<3)?flow.ws/2:3;
		}else
		{
			period = ((period*2)<5000)?(period*3):5000;
			flow.ws = flow.ws + 1;
		}
		flow.getv().put(Double.valueOf(now), Double.valueOf(v));
		flow.geta().put(Double.valueOf(now), Double.valueOf(a));
		
		if(win.size() > flow.ws){
			win.remove(0);
		}
		pool.modifyTask(id, match, period);
		flow.period = period;
		
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
