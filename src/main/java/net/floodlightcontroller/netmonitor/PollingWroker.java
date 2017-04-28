package net.floodlightcontroller.netmonitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFFlowModFlags;
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
	
	public void polling(Flow flow,IOFSwitch sw, FloodlightContext cntx)
	{
		Set<OFStatsRequestFlags> flagset = new HashSet<OFStatsRequestFlags>();
		flagset.add(OFStatsRequestFlags.REQ_MORE);
		if(flow == null) logger.info("flow.match is null");
		if(sw == null) logger.info("sw == null");
		OFFlowStatsRequest pkt = sw.getOFFactory().buildFlowStatsRequest()
				.setMatch(flow.match)
				.setTableId(TableId.ALL)
				.setOutPort(OFPort.ANY)
				.setOutGroup(OFGroup.ANY)
//				.setFlags(flagset)
				.build();
		
		if(sw.write(pkt)) logger.info("pollingWorker send success!");;
	}
	
	public void init(Flow flow,IOFSwitch sw, FloodlightContext cntx)
	{
		this.flow = flow;
		this.sw = sw;
		this.cntx = cntx;
	}
	
}
