package net.floodlightcontroller.practice.mactracer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.projectfloodlight.openflow.protocol.OFAsyncSet;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFFlowModFlags;
import org.projectfloodlight.openflow.protocol.OFFlowRemoved;
import org.projectfloodlight.openflow.protocol.OFFlowRemovedReason;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.IListener;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.threadpool.IThreadPoolService;

public class NetMonitor implements IOFMessageListener, IFloodlightModule {

	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;
	protected boolean flow_miss_flag = false;
    protected IThreadPoolService threadPool;
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = 
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(NetMonitor.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		floodlightProvider.addOFMessageListener(OFType.FLOW_REMOVED, this);
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		Ethernet eth = 
				IFloodlightProviderService.bcStore.get(cntx,
						IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
//		logger.info("Switch Id:{},Payload:{}",sw.getId().getLong(),eth.getPayload().toString());
 
		
		switch(msg.getType())
		{
		//OpenFlow 1.3.0
		case PACKET_IN:
			//print PACKET_IN informations
			OFPacketIn  pkt_in = (OFPacketIn)msg;
			logger.info("PACKET_IN informations:");
			logger.info("BufferId:{}",pkt_in.getBufferId());
			logger.info("Cookie:{}",pkt_in.getCookie());
			logger.info("Data:{}",pkt_in.getData());
			logger.info("Match:{}",pkt_in.getMatch());
			logger.info("Reason:{}",pkt_in.getReason());
			logger.info("TableId:{}",pkt_in.getTableId());
			logger.info("Type:{}",pkt_in.getType());
			logger.info("TotalLen:{}",pkt_in.getTotalLen());
			logger.info("Version:{}",pkt_in.getVersion());
			logger.info("Xid:{}",pkt_in.getXid());
			logger.info("======================================================");
			
			OFFlowMod pkt_mod = (OFFlowMod)sw.getOFFactory().buildFlowModify()
					.setMatch(pkt_in.getMatch())
					.setIdleTimeout(10)
					.setHardTimeout(200)
					.build();
			
			
			Set<OFFlowModFlags> Flags = new HashSet<OFFlowModFlags>();
			Flags.add(OFFlowModFlags.SEND_FLOW_REM);
			OFFlowAdd pkt_add = (OFFlowAdd)sw.getOFFactory().buildFlowAdd()
					.setMatch(pkt_in.getMatch())
					.setIdleTimeout(10)
					.setHardTimeout(200)
					.setPriority(500)
					.setFlags(Flags)
					.build();
			
			sw.write(pkt_add);
			
			SwitchPollingWorker spworker = SwitchPollingWorker.CreateSwitchPollingWorker(200, this);
			ScheduledExecutorService ses = threadPool.getScheduledExecutor();
			threadPool.getScheduledExecutor().scheduleAtFixedRate(spworker, 100, 100, TimeUnit.MILLISECONDS);
			
			break;
		case FLOW_REMOVED:
			OFFlowRemoved pkt_rm = (OFFlowRemoved)msg;
			logger.info("FLOW_REMOVED informations:");
			logger.info("ByteCount:{}",pkt_rm.getByteCount());
			logger.info("Cookie:{}",pkt_rm.getCookie());
			logger.info("DurationNsec:{}",pkt_rm.getDurationNsec());
			logger.info("DurationSec:{}",pkt_rm.getDurationSec());
			logger.info("Match:{}",pkt_rm.getMatch());
			logger.info("Reason:{}",pkt_rm.getReason());
			logger.info("TableId:{}",pkt_rm.getTableId());
			logger.info("Type:{}",pkt_rm.getType());
			logger.info("HardTimeout:{}",pkt_rm.getHardTimeout());
			logger.info("IdleTimeout:{}",pkt_rm.getIdleTimeout());
			logger.info("PacketCount:{}",pkt_rm.getPacketCount());
			logger.info("Priority:{}",pkt_rm.getPriority());
			logger.info("Version:{}",pkt_rm.getVersion());
			logger.info("Xid:{}",pkt_rm.getXid());
			logger.info("======================================================");
			
			break;
		case STATS_REPLY:
			OFStatsReply pkt_reply = (OFStatsReply)msg;
			logger.info("STATS_REPLY informations:");
			logger.info("Flags:{}",pkt_reply.getFlags());
			logger.info("StatsType:{}",pkt_reply.getStatsType());
			logger.info("Type:{}",pkt_reply.getType());
			logger.info("Version:{}",pkt_reply.getVersion());
			logger.info("Xid:{}",pkt_reply.getXid());
			logger.info("======================================================");
			
			break;
		default:
			break;
		}
		return Command.CONTINUE;
	}

}
