package net.floodlightcontroller.netmonitor;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFFlowModCommand;
import org.projectfloodlight.openflow.protocol.OFFlowModFlags;
import org.projectfloodlight.openflow.protocol.OFFlowRemoved;
import org.projectfloodlight.openflow.protocol.OFMatchV3;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsType;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.Masked;
import org.python.modules.time.Time;

import javafx.scene.chart.PieChart.Data;
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
	String start_time;
	
//	private final IThreadPoolService scheduler = new ThreadPool();
//    private final ScheduledExecutorService scheduler =
//    	       Executors.newScheduledThreadPool(1);
	private PollingThreadControl ctrl;
	
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
		SwitchMap swmap = SwitchMap.getInstance();
		if(!swmap.contains(sw.getId()))
		{
			swmap.addSwitch(sw.getId(), sw);
		}
		Switch tmp = swmap.getSwitch(sw.getId());
		
		switch(msg.getType())
		{
		case PACKET_IN:
//			logger.info("PACKET_IN message");
			break;
		case FLOW_REMOVED:
			logger.info("FLOW_REMOVED message");
			SwitchMap.getInstance().update(((OFFlowRemoved)msg).getMatch(),
					sw.getId(), 
					Time.time(), 
					((OFFlowRemoved)msg).getByteCount().getValue());
			
			String log4v;
			if(!SwitchMap.getInstance()
					.getSwitch(sw.getId())
					.contains(((OFFlowRemoved)msg).getMatch()))
					{
						logger.info("Remove task "+((OFMatchV3)((OFFlowRemoved)msg).getMatch()).getOxmList().toString());
						logger.info("Match is missing");
						break;
					}
			log4v = SwitchMap.getInstance().getSwitch(sw.getId()).getFlow(((OFFlowRemoved)msg).getMatch()).toString();
			logger.info(log4v);

			
			SwitchMap.getInstance().getSwitch(sw.getId()).rmFlow(((OFFlowRemoved)msg).getMatch());
			PollingThreadControl.getInstance().rmTask(new PollingTask(cntx,sw.getId(),((OFFlowRemoved)msg).getMatch()));
			break;
		case FLOW_MOD:
			if(((OFFlowMod)msg).getVersion() != OFVersion.OF_13) break; 
			if(((OFFlowMod)msg).getCommand() != OFFlowModCommand.ADD) break; 
			OFMatchV3 match = (OFMatchV3)((OFFlowMod)msg).getMatch();
			
			if(match.getOxmList().equals(match.getOxmList().EMPTY)) break; 

			logger.info("FLOW_MOD message");
			logger.info(((OFMatchV3)((OFFlowMod)msg).getMatch()).getOxmList().toString());
			Flow flow = new Flow(match,sw.getId());
			
			if(SwitchMap.getInstance().getSwitch(sw.getId()).contains(((OFFlowMod)msg).getMatch()))
			{
				logger.info("Conflict");
			}
			SwitchMap.getInstance().addSwitch(sw.getId(),sw);
			SwitchMap.getInstance().getSwitch(sw.getId()).addFlow(((OFFlowMod)msg).getMatch());
			
//			PollingThreadControl.getInstance().addTask(new PollingTask(cntx,sw.getId(),((OFFlowMod)msg).getMatch()), 1000);
			
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
		ctrl = PollingThreadControl.getInstance();
		start_time = String.valueOf(Calendar.getInstance().getTimeInMillis());
	}

}
