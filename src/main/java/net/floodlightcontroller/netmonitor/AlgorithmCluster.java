/**
 * 
 */
package net.floodlightcontroller.netmonitor;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;

/**
 * @author jason
 *
 */
public class AlgorithmCluster {
	
	private static AlgorithmCluster self = null;
	
	private SwitchMap swmap = SwitchMap.getInstance();
	private PollingThreadControl pool = PollingThreadControl.getInstance();
	
	private int POLLING_PERIOD = 500;
	
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
		
		pool.modifyTask(id, match, 2000);
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
