package net.floodlightcontroller.netmonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFFlowModFlags;
import org.projectfloodlight.openflow.protocol.OFFlowStatsReply;
import org.projectfloodlight.openflow.protocol.OFFlowStatsRequest;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsRequestFlags;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.Masked;
import org.projectfloodlight.openflow.types.OFGroup;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TableId;

import com.google.common.util.concurrent.ListenableFuture;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFSwitch;

public class PollingWroker implements Runnable {
	
	Flow flow = null;
	IOFSwitch sw = null;
	FloodlightContext cntx;
	
	Logger logger = Logger.getLogger(this.getClass().toString());

	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.info("PollingWorker is working");
		polling(this.flow,this.sw,this.cntx);

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
		
		logger.info("pollingWorker send success!");
		logger.info(values.get(0).getStatsType().toString());
		logger.info(((OFStatsReply)values.get(0)).getType().toString());
		logger.info(((OFFlowStatsReply)values.get(0)).getEntries().toString());
		
//		logger.info(String.valueOf(reply.size()));
//		logger.info(reply.get(0).getStatsType().toString());
		
		return 0;
	}
	
	public void init(Flow flow,IOFSwitch sw, FloodlightContext cntx)
	{
		this.flow = flow;
		this.sw = sw;
		this.cntx = cntx;
	}
	
}
