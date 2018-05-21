package net.floodlightcontroller.flowsmonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFFlowModCommand;
import org.projectfloodlight.openflow.protocol.OFFlowRemoved;
import org.projectfloodlight.openflow.protocol.OFMatchV3;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFOxmList;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.protocol.match.MatchFields;
import org.projectfloodlight.openflow.protocol.oxm.OFOxm;
import org.projectfloodlight.openflow.protocol.oxm.OFOxmIpv4Dst;
import org.projectfloodlight.openflow.protocol.oxm.OFOxmIpv4Src;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.TransportPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.PortChangeType;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.restserver.RestletRoutable;

public class FlowsMonitor implements IOFMessageListener, IFloodlightModule, IOFSwitchListener, IRestApiService {
	private String Name = "floodlightcontroller.flowsmonitor.FlowsMonitor";
	private IFloodlightProviderService floodlightProvider;
	private SwitchSet switchSet = new SwitchSet();
	private IRestApiService restApiService;
	@Override
	public String getName() {
		return Name;
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
	public void switchAdded(DatapathId switchId) {
//		this.globalView.addSwitches(switchId);
//		this.switchSet.addSwitch(switchId.getLong());
		System.out.println("New Switch Added:");
		System.out.println(switchId.getLong());
	}

	@Override
	public void switchRemoved(DatapathId switchId) {
//		this.globalView.removeSwitches(switchId);
	}

	@Override
	public void switchActivated(DatapathId switchId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void switchPortChanged(DatapathId switchId, OFPortDesc port, PortChangeType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchChanged(DatapathId switchId) {
		// TODO Auto-generated method stub
		
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
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
//		l.add(IRestApiService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		System.out.println("Hello world");
//		restApiService = context.getServiceImpl(IRestApiService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
//		Timer timer = new Timer();
//		timer.schedule(new FlowMeasure(), 500);
		floodlightProvider.addOFMessageListener(OFType.FLOW_MOD, this);
		floodlightProvider.addOFMessageListener(OFType.FLOW_REMOVED, this);
	}

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		System.out.println(sw.getId().getLong());
		this.switchSet.addSwitch(sw.getId().getLong());
		System.out.println(msg.getType().toString());
		switch(msg.getType()) {
		case FLOW_MOD:
			OFFlowMod flowMod = (OFFlowMod)msg;
			System.out.println(flowMod.getMatch().toString());
			Iterator it = flowMod.getMatch().getMatchFields().iterator();
			if(flowMod.getCommand().compareTo(OFFlowModCommand.ADD) == 0) {
				Match matchField = flowMod.getMatch();
				Switch s = this.switchSet.getSwitch(sw.getId().getLong());
				FlowEntrySet fwset = s.getFlowentrySet();
				fwset.addFlowEntry(new FlowEntry(matchField,
						flowMod.getTableId().getLength(),
						sw.getId().getLong()));
//				OFMatchV3 matchField = (OFMatchV3)flowMod.getMatch();
//				OFOxmList oxmList = matchField.getOxmList();
//				IpProtocol proto = matchField.get(MatchField.IP_PROTO);
//				int scrIP = 0,scrPort = 0,dstIP = 0,dstPort = 0,Protocol;
//				Protocol = proto.getIpProtocolNumber();
//				if(Protocol == IpProtocol.UDP.getIpProtocolNumber()) {
//					scrPort = matchField.get(MatchField.UDP_SRC).getPort();
//					dstPort = matchField.get(MatchField.UDP_DST).getPort();
//					scrIP = matchField.get(MatchField.IPV4_SRC).getInt();
//					dstIP = matchField.get(MatchField.IPV4_DST).getInt();
//				}
//				else if(Protocol == IpProtocol.TCP.getIpProtocolNumber()) {
//					scrPort = matchField.get(MatchField.TCP_SRC).getPort();
//					dstPort = matchField.get(MatchField.TCP_DST).getPort();
//					scrIP = matchField.get(MatchField.IPV4_SRC).getInt();
//					dstIP = matchField.get(MatchField.IPV4_DST).getInt();
//				}
//				else if(Protocol == IpProtocol.SCTP.getIpProtocolNumber()) {
//					scrPort = matchField.get(MatchField.SCTP_SRC).getPort();
//					dstPort = matchField.get(MatchField.SCTP_DST).getPort();
//					scrIP = matchField.get(MatchField.IPV4_SRC).getInt();
//					dstIP = matchField.get(MatchField.IPV4_DST).getInt();
//				}
			}
			else if(flowMod.getCommand().compareTo(OFFlowModCommand.MODIFY) == 0) {
				;
			}
			else if(flowMod.getCommand().compareTo(OFFlowModCommand.DELETE) == 0) {
				;
			}
			break;
		case FLOW_REMOVED:
			OFFlowRemoved flowRemoved = (OFFlowRemoved)msg;
			Match matchField = flowRemoved.getMatch();
			Switch s = this.switchSet.getSwitch(sw.getId().getLong());
			FlowEntrySet fwset = s.getFlowentrySet();
			FlowEntry flowentry = fwset.getFlowEntry(matchField);
			flowentry.addRate(flowRemoved.getDurationSec()+flowRemoved.getDurationNsec()/1000000000.0,
					(flowRemoved.getByteCount().getValue()-flowentry.getByteCounter())/
					(flowRemoved.getDurationSec()+flowRemoved.getDurationNsec()/1000000000.0-flowentry.getDuringTime()));
			flowentry.setByteCounter(flowRemoved.getByteCount().getValue());
			flowentry.setDuringTime((float) (flowRemoved.getDurationNsec() + flowRemoved.getDurationNsec()/1000000000.0));
			flowentry.setPacketCounter(flowRemoved.getPacketCount().getValue());
			System.out.println(flowentry.getMatchfield().toString());
			System.out.println(flowentry.getRateMap().values().toString());
			break;
		default:
			;
		}
		return Command.CONTINUE;
	}

	@Override
	public void addRestletRoutable(RestletRoutable routable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
