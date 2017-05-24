package net.floodlightcontroller.netmonitor;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.projectfloodlight.openflow.protocol.OFFlowStatsEntry;
import org.projectfloodlight.openflow.protocol.OFFlowStatsReply;
import org.projectfloodlight.openflow.protocol.OFFlowStatsRequest;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsRequestFlags;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFGroup;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TableId;

import com.google.common.util.concurrent.ListenableFuture;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFSwitch;

/**
 * @author jason
 * @version 1.0
 * @created 09-May-2017 11:00:57 PM
 */
public class PollingTask implements Runnable {

	private FloodlightContext cntx;
	private Match match;
	private ConcurrentMap<Long,Double> statictis4v;
	private DatapathId swId;
	public Date m_Date;
	public boolean status;
	public int taskId;
	
	Logger logger = Logger.getLogger(this.getClass().toString());

	public PollingTask(){

	}

	public PollingTask(FloodlightContext cntx,DatapathId swId,Match match){
		this.cntx = cntx;
		this.swId = swId;
		this.match = match;
		this.status = true;
	}

	public PollingTask(int taskId){
		this.taskId = taskId;
	}
	
	public void finalize() throws Throwable {

	}

	public OFStatsReply polling(){
		return null;
	}

	public void run(){
		polling(SwitchMap.getInstance().getSwitch(swId).getFlow(match),
				SwitchMap.getInstance().getSwitch(swId).sw,
				this.cntx);
		System.out.println(SwitchMap.getInstance().getSwitch(swId).getFlow(match).toString());
	}
	
	public int polling(Flow flow,IOFSwitch sw, FloodlightContext cntx)
	{
		Set<OFStatsRequestFlags> flagset = new HashSet<OFStatsRequestFlags>();
		flagset.add(OFStatsRequestFlags.REQ_MORE);
		if(flow == null)
			{
			logger.info("flow.match is null");
			return 1;
			}
		if(sw == null) logger.info("sw == null");
		logger.info("pollingWorker send waiting!!!");
		OFFlowStatsRequest pkt = sw.getOFFactory().buildFlowStatsRequest()
				.setMatch(flow.match)
				.setTableId(TableId.ALL)
				.setOutPort(OFPort.ANY)
				.setOutGroup(OFGroup.ANY)
//				.setFlags(flagset)
				.build();
		
//		ArrayList<OFFlowStatsReply> reply = (ArrayList<OFFlowStatsReply>) sw.writeStatsRequest(pkt);
		ListenableFuture<?> future = sw.writeStatsRequest(pkt);
		List<OFStatsReply> values = null;
		
		try {
			values = (List<OFStatsReply>) future.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("pollingWorker send success!!!");
		logger.info(values.get(0).getStatsType().toString());
		logger.info(((OFStatsReply)values.get(0)).getType().toString());
		logger.info(((OFFlowStatsReply)values.get(0)).getEntries().toString());
		
		OFFlowStatsEntry entry = ((OFFlowStatsReply)values.get(0)).getEntries().get(0);
		
		try
		{
			SwitchMap.getInstance().update(flow.match,sw.getId(), 
					(
					Double.valueOf(entry.getDurationSec()) + 
					(Double.valueOf(entry.getDurationNsec())/1000000000)
					)
					, 
					entry.getByteCount().getValue());
			
		}
		catch(Exception e)
		{
			logger.info("pollingTask get a Exception!!!");
			e.printStackTrace();
		}
		
		
//		logger.info(String.valueOf(reply.size()));
//		logger.info(reply.get(0).getStatsType().toString());
		
		return 0;
	}

	/**
	 * 
	 * @param msg
	 */
	public int update(OFStatsReply msg){
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		PollingTask task = (PollingTask)obj;
		if(this.match.equals(task.match) 
				&&this.swId.equals(task.swId)
				)
//		if(this.match == task.match 
//				&&this.swId == task.swId
//				)
		{
			return true;
		}
		return false;
	}

	
	

}