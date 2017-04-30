package net.floodlightcontroller.netmonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFFlowModFlags;
import org.projectfloodlight.openflow.protocol.OFFlowRemoved;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsType;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.Masked;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.threadpool.ThreadPool;

public class NetMonitor implements IFloodlightModule, IOFMessageListener {

	IFloodlightProviderService floodlightProvider;
	Logger logger;
	
	PollingWroker pollingworker;
//	private final IThreadPoolService scheduler = new ThreadPool();
    private final ScheduledExecutorService scheduler =
    	       Executors.newScheduledThreadPool(1);

	
	@Override
	public String getName() {
		return this.getClass().toString();
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
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg,
			FloodlightContext cntx) {
		SwitchMap swmap = SwitchMap.getMap();
		if(!swmap.contains(sw.getId()))
		{
			swmap.addSwitch(sw.getId());
		}
		Switch tmp = swmap.getSwitch(sw.getId());
		
		switch(msg.getType())
		{
		case PACKET_IN:
			logger.info("PACKET_IN message");
//			if(!tmp.contains(((OFPacketIn)msg).getMatch()))
//			{
//				tmp.addFlow(((OFPacketIn)msg).getMatch());
//			}
			Flow flow = new Flow();
			flow.match = ((OFPacketIn)msg).getMatch();
			pollingworker.init(flow, sw, cntx);
			Set<OFFlowModFlags> flagset = new HashSet<OFFlowModFlags>();
			flagset.add(OFFlowModFlags.SEND_FLOW_REM);
			
//			ScheduledExecutorService e = scheduler.getScheduledExecutor();
//			e.scheduleAtFixedRate(new PollingWroker(), 1, 5, TimeUnit.SECONDS);
//			pollingworker = new PollingWroker();
//			pollingworker.run();
//			e.execute(pollingworker);
			
			OFFlowAdd pkt = sw.getOFFactory().buildFlowAdd()
					.setMatch(sw.getOFFactory().buildMatchV3()
//							.setMasked(MatchField.IPV4_DST, Masked.of(IPv4Address.of("8.8.8.8"),IPv4Address.of("255.255.255.255")))
							.build())
					.setMatch(((OFPacketIn)msg).getMatch())
//					.setMatch(null)
					.setHardTimeout(500)
					.setIdleTimeout(100)
					.setFlags(flagset)
					.build();
			if(sw.write(pkt)) logger.info("send success");
			else logger.info("send failed");
//			ScheduledExecutorService e = scheduler.getScheduledExecutor();
			scheduler.scheduleAtFixedRate(pollingworker, 1, 5, TimeUnit.SECONDS);

			break;
		case FLOW_REMOVED:
			logger.info("FLOW_REMOVED message");
//			if(tmp.contains(((OFFlowRemoved)msg).getMatch()))
//			{
//				tmp.rmFlow(((OFFlowRemoved)msg).getMatch());
//			}
			break;
		case STATS_REPLY:
			logger.info("STATS_REPLY message");
//			if(tmp.contains(((OFStatsReply)msg).getXid()))
//			{
//				tmp.update(((OFStatsReply)msg).getXid());
//			}
			break;
		case FLOW_MOD:
			logger.info("FLOW_MOD message");
			break;
		case STATS_REQUEST:
			logger.info("STATS_REQUEST message");
			
			break;
		default:
			break;
		}
		return Command.CONTINUE;
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
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l = 
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
		logger = Logger.getLogger(this.getName());
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		pollingworker = new PollingWroker();
//		final ScheduledFuture<?> workerhandle = 
//				scheduler.scheduleAtFixedRate(pollingworker, 5, 1, TimeUnit.SECONDS);

	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		floodlightProvider.addOFMessageListener(OFType.FLOW_REMOVED, this);
		floodlightProvider.addOFMessageListener(OFType.FLOW_MOD, this);
		floodlightProvider.addOFMessageListener(OFType.STATS_REPLY, this);
		floodlightProvider.addOFMessageListener(OFType.STATS_REQUEST, this);
		scheduler.scheduleAtFixedRate(pollingworker, 1, 5, TimeUnit.SECONDS);
	
	}

}
