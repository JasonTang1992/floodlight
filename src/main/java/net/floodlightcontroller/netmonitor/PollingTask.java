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
import org.projectfloodlight.openflow.protocol.OFMatchV3;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsRequestFlags;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFGroup;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TableId;
import org.python.modules.time.Time;

import com.google.common.util.concurrent.ListenableFuture;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFSwitch;

/**
 * @author jason
 * @version 1.0
 * @created 09-May-2017 11:00:57 PM
 */
/**
 * @author jason
 *
 */
public class PollingTask implements Runnable {

	private FloodlightContext cntx;
	private Match match;
	private ConcurrentMap<Long,Double> statictis4v;
	private DatapathId swId;
	public Date m_Date;
	public boolean status;
//	public int taskId;
	private long timeout = 0;
	private long counter = 0;
	
	Logger logger = Logger.getLogger(this.getClass().toString());

	public PollingTask(){

	}

	public PollingTask(FloodlightContext cntx,DatapathId swId,Match match){
		this.cntx = cntx;
		this.swId = swId;
		this.match = match;
		this.status = true;
		this.timeout = 100;
	}
	
	

	public FloodlightContext getCntx() {
		return cntx;
	}

	public void setCntx(FloodlightContext cntx) {
		this.cntx = cntx;
	}

	
	public void finalize() throws Throwable {

	}

	public OFStatsReply polling(){
		return null;
	}

	public void run(){
		if(counter<timeout)
		{
			counter = counter + 1;
		}else
		{
			polling(SwitchMap.getInstance().getSwitch(swId).getFlow(match),
					SwitchMap.getInstance().getSwitch(swId).sw,
					this.cntx);
			counter = 0;
		}
	}
	
	public synchronized int polling(Flow flow,IOFSwitch sw, FloodlightContext cntx)
	{
		Set<OFStatsRequestFlags> flagset = new HashSet<OFStatsRequestFlags>();
		flagset.add(OFStatsRequestFlags.REQ_MORE);
		if(flow == null)
			{
				logger.info("Flow missing in PollingTask");
				return -1;
			}
		if(sw == null) logger.info("sw == null");
//		logger.info("pollingWorker send waiting!!!");
		logger.info("polling for "+((OFMatchV3)flow.match).getOxmList().toString());
		Match match = sw.getOFFactory().buildMatchV3()
				.setOxmList(((OFMatchV3)flow.match).getOxmList())
				.build();
		OFFlowStatsRequest pkt = sw.getOFFactory().buildFlowStatsRequest()
				.setMatch(match)
				.setTableId(TableId.ALL)
				.setOutPort(OFPort.ANY)
				.setOutGroup(OFGroup.ANY)
//				.setFlags(flagset)
				.build();
		
//		ArrayList<OFFlowStatsReply> reply = (ArrayList<OFFlowStatsReply>) sw.writeStatsRequest(pkt);
		ListenableFuture<List<OFFlowStatsReply>> future = sw.writeStatsRequest(pkt);
		List<OFFlowStatsReply> values = null;
		
		try {
			values = future.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			future.notify();
		}
		if(values == null)
		{
			logger.info("values is null");
			return 1;
		}
		
//		logger.info("pollingWorker send success!!!");
//		logger.info(values.get(0).getStatsType().toString());
//		logger.info(((OFStatsReply)values.get(0)).getType().toString());
		logger.info(((OFFlowStatsReply)values.get(0)).getEntries().toString());
		logger.info(String.valueOf(((OFFlowStatsReply)values.get(0)).getEntries().size()));
		logger.info(String.valueOf(values.size()));
		
		long byteCounter = 0;
		for(int i = 0;i<((OFFlowStatsReply)values.get(0)).getEntries().size();i++)
		{
//			byteCounter = byteCounter + ((OFFlowStatsReply)values.get(0)).getEntries().get(i).getByteCount().getValue();
			if(byteCounter < ((OFFlowStatsReply)values.get(0)).getEntries().get(i).getByteCount().getValue())
				byteCounter = ((OFFlowStatsReply)values.get(0)).getEntries().get(i).getByteCount().getValue();
		}
		logger.info(String.valueOf(byteCounter));
		
		OFFlowStatsEntry entry = ((OFFlowStatsReply)values.get(0)).getEntries().get(0);
		
		if(entry == null)
		{
			logger.info("entry is null");
			return 1;
		}
		try
		{
//			SwitchMap.getInstance().update(flow.match,sw.getId(), 
//			(
//			Double.valueOf(entry.getDurationSec()) + 
//			(Double.valueOf(entry.getDurationNsec())/1000000000)
//			)
//			, 
//			byteCounter);
			SwitchMap.getInstance().update(flow.match,sw.getId(), 
			(
					Time.time()
			)
			, 
			byteCounter);
			
		}
		catch(Exception e)
		{
			logger.info("pollingTask get a Exception!!!");
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * @param msg
	 */
	public int update(OFStatsReply msg){
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((match == null) ? 0 : match.hashCode());
		result = prime * result + ((swId == null) ? 0 : swId.hashCode());
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
		if (!(obj instanceof PollingTask))
			return false;
		PollingTask other = (PollingTask) obj;
		if (match == null) {
			if (other.match != null)
				return false;
		} else if (!match.equals(other.match))
			return false;
		if (swId == null) {
			if (other.swId != null)
				return false;
		} else if (!swId.equals(other.swId))
			return false;
		return true;
	}

	/**
	 * @return the match
	 */
	public Match getMatch() {
		return match;
	}

	/**
	 * @param match the match to set
	 */
	public void setMatch(Match match) {
		this.match = match;
	}

	public DatapathId getSwId() {
		return swId;
	}

	public void setSwId(DatapathId swId) {
		this.swId = swId;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	

}