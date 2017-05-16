package net.floodlightcontroller.netmonitor;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;

import net.floodlightcontroller.core.FloodlightContext;

/**
 * @author jason
 * @version 1.0
 * @created 09-May-2017 11:00:57 PM
 */
public class PollingTask implements Runnable {

	private FloodlightContext cntx;
	private Match match;
	private Map<Long,Double> statictis4v;
	private DatapathId swId;
	public Date m_Date;
	public boolean status;

	public PollingTask(){

	}

	public PollingTask(FloodlightContext cntx,DatapathId swId,Match match){
		this.cntx = cntx;
		this.swId = swId;
		this.match = match;
		this.status = true;
	}

	
	public void finalize() throws Throwable {

	}

	public OFStatsReply polling(){
		return null;
	}

	public void run(){
		if(this.status == false) 
		{
			
		}

	}

	/**
	 * 
	 * @param msg
	 */
	public int update(OFStatsReply msg){
		return 0;
	}

}