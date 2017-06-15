package net.floodlightcontroller.netmonitor;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.projectfloodlight.openflow.protocol.OFMatchV3;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.DatapathId;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.util.internal.ConcurrentSet;
import net.floodlightcontroller.core.FloodlightContext;

/**
 * @author jason
 * @version 1.0
 * @created 09-May-2017 11:01:10 PM
 */
public class PollingThreadControl {
	
	static PollingThreadControl pollingThreadControl = null;

	private FloodlightContext cntx;
//	private ConcurrentMap<Long,List<Runnable>> scheduledMap = new ConcurrentHashMap<Long,List<Runnable>>();
	private ConcurrentMap<Entry<DatapathId,Match>,Runnable> scheduledMap = new ConcurrentHashMap<Entry<DatapathId,Match>,Runnable>();
//	private CopyOnWriteArrayList<Runnable> scheduledMap = new CopyOnWriteArrayList<Runnable>();
	private ConcurrentMap<Runnable,ScheduledFuture<?>> taskmap = new ConcurrentHashMap<Runnable,ScheduledFuture<?>>();
	private ScheduledExecutorService e = (ScheduledExecutorService) Executors.newScheduledThreadPool(10);
	
	Logger logger = Logger.getLogger("PollingThreadControl");

	public PollingThreadControl(){

	}
	
	static PollingThreadControl getInstance()
	{
		if(pollingThreadControl == null) pollingThreadControl = new PollingThreadControl();
		return pollingThreadControl;
	}

	public void finalize() throws Throwable {

	}

	/**
	 * 
	 * @param task
	 * @param period
	 */
	public int addTask(Runnable task, long period){
		this.cntx = ((PollingTask)task).getCntx();
		PollingTask pollingTask = (PollingTask)task;
		Entry<DatapathId,Match> cookie = new SimpleEntry<DatapathId,Match>(pollingTask.getSwId(),pollingTask.getMatch());
		if(containsTask(pollingTask.getSwId(),pollingTask.getMatch()) != null) return 0;
		else
		{
//			if(this.scheduledMap.containsKey(Long.valueOf(period)) == false) 
//				this.scheduledMap.put(Long.valueOf(period), new ArrayList<Runnable>());
//			this.scheduledMap.get(Long.valueOf(period)).add((Runnable)task);
			this.scheduledMap.put(cookie, task);
			logger.info("Adding "+((OFMatchV3)(pollingTask).getMatch()).getOxmList().toString());
			pollingTask.setTimeout(period/10);
			ScheduledFuture<?> schedule = e.scheduleAtFixedRate(task, 0, 10, TimeUnit.MILLISECONDS);
			taskmap.put(task, schedule);
		}
		return 0;
	}
	
	

	/**
	 * 
	 * @param task
	 */
	public Runnable containsTask(DatapathId id,Match match){
		Entry<DatapathId,Match> tmp = new SimpleEntry(id,match);
		if(this.scheduledMap.containsKey(tmp)) return this.scheduledMap.get(tmp);
		return null;
	}

	/**
	 * 
	 * @param task
	 * @param newperiod
	 */
	public int modifyTask(DatapathId id,Match match, long newperiod){
		PollingTask task = (PollingTask)this.containsTask(id, match);
		if(task==null) return -1;
		task.setTimeout(newperiod/10);		
		return 0;
	}

	public long getTaskPeriod(DatapathId id,Match match)
	{
		PollingTask task = (PollingTask)this.containsTask(id, match);
		if(task==null) return -1;	
		return task.getTimeout();			
	}
	/**
	 * 
	 * @param task
	 */
	public int rmTask(Runnable task){
		PollingTask pollingTask = (PollingTask)task;
		Entry<DatapathId,Match> cookie = new SimpleEntry<DatapathId,Match>(pollingTask.getSwId(),pollingTask.getMatch());
		
		if(taskmap.containsKey(task)){
			logger.info("Remove task from taskmap "+((OFMatchV3)((PollingTask)task).getMatch()).getOxmList().toString());
			this.taskmap.get(task).cancel(true);
			this.taskmap.remove(task);
		}
		if(this.scheduledMap.containsKey(cookie))
		{
			logger.info("Remove task from scheduledMap "+((OFMatchV3)((PollingTask)task).getMatch()).getOxmList().toString());
			this.scheduledMap.remove(cookie);
		}
		System.out.println("remove " + task.toString());
		
		return 0;
	}
	
	public Runnable getTask(DatapathId id,Match match)
	{
		Entry<DatapathId,Match> cookie = new SimpleEntry<DatapathId,Match>(id,match);

		return this.scheduledMap.get(cookie);
	}
	
	public FloodlightContext getCntx() {
		return cntx;
	}

	public void setCntx(FloodlightContext cntx) {
		this.cntx = cntx;
	}

	public static void main(String[] args)
	{
		PollingThreadControl ctrl = PollingThreadControl.getInstance();
		
	}

}