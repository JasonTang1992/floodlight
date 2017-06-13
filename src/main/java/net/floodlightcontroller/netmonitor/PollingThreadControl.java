package net.floodlightcontroller.netmonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.projectfloodlight.openflow.protocol.OFMatchV3;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.floodlightcontroller.core.FloodlightContext;

/**
 * @author jason
 * @version 1.0
 * @created 09-May-2017 11:01:10 PM
 */
public class PollingThreadControl {
	
	static PollingThreadControl pollingThreadControl = null;

	private FloodlightContext cntx;
	private ConcurrentMap<Long,List<Runnable>> scheduledMap = new ConcurrentHashMap<Long,List<Runnable>>();
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
		if(containsTask(task) != 0) return 0;
		else
		{
			if(this.scheduledMap.containsKey(Long.valueOf(period)) == false) 
				this.scheduledMap.put(Long.valueOf(period), new ArrayList<Runnable>());
			this.scheduledMap.get(Long.valueOf(period)).add((Runnable)task);
			logger.info("Adding "+((OFMatchV3)((PollingTask)task).getMatch()).getOxmList().toString());
			ScheduledFuture<?> schedule = e.scheduleAtFixedRate(task, 0, period, TimeUnit.MILLISECONDS);
			taskmap.put(task, schedule);
		}
		return 0;
	}

	/**
	 * 
	 * @param task
	 */
	public long containsTask(Runnable task){
		Iterator it = this.scheduledMap.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<Long,List<Runnable>> pair = (Entry<Long,List<Runnable>>)it.next();
			List<Runnable> list = pair.getValue();
			if(list.contains(task)) return pair.getKey().longValue();
		}
		return 0;
	}

	/**
	 * 
	 * @param task
	 * @param newperiod
	 */
	public int modifyTask(Runnable task, long newperiod){
		long taskKey = 0;
		
		taskKey = containsTask(task);
		
		if(taskKey == 0) return 0;
		else
		{
			if(taskKey == newperiod) return 1;
			this.rmTask(task);
			this.addTask(task,newperiod);
		}
		return 0;
	}

	/**
	 * 
	 * @param task
	 * @param newperiod
	 * @param oldperiod
	 */
	public int modifyTask(Runnable task, long newperiod, long oldperiod){
		if(containsTask(task) != oldperiod) return 0;
		else
		{
			this.rmTask(task);
			if(this.scheduledMap.containsKey(Long.valueOf(newperiod)) == false) 
				this.scheduledMap.put(Long.valueOf(newperiod), new ArrayList<Runnable>());
			this.scheduledMap.get(Long.valueOf(newperiod)).add((Runnable)task);
		}
		return 0;
	}

	/**
	 * 
	 * @param task
	 */
	public int rmTask(Runnable task){
		if(taskmap.containsKey(task)){
			logger.info("Remove task from taskmap "+((OFMatchV3)((PollingTask)task).getMatch()).getOxmList().toString());
			this.taskmap.get(task).cancel(true);
			this.taskmap.remove(task);
		}
		if(this.scheduledMap.containsKey(task))
		{
			logger.info("Remove task from scheduledMap "+((OFMatchV3)((PollingTask)task).getMatch()).getOxmList().toString());
			this.scheduledMap.get(Long.valueOf(containsTask(task))).remove(task);
		}
		System.out.println("remove " + task.toString());
		
		return 0;
	}

	/**
	 * 
	 * @param task
	 * @param period
	 */
	public int rmTask(Runnable task, long period){
		this.taskmap.get(task).cancel(true);
		this.taskmap.remove(task);
		this.scheduledMap.get(Long.valueOf(period)).remove(task);
		
		return 0;
	}
	
	public Runnable getTask(Runnable task)
	{
		List list;
		list = this.scheduledMap.get(containsTask(task));
		Iterator it = list.iterator();
		while(it.hasNext())
		{
			PollingTask tmp = (PollingTask) it.next();
			if(tmp.equals(task)) return tmp;
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		PollingThreadControl ctrl = PollingThreadControl.getInstance();
		PollingTask t1 = new PollingTask(1001);
		PollingTask t2 = new PollingTask(888);
		
		ctrl.addTask(t1, 1000);
		ctrl.rmTask(t1);
//		ctrl.taskmap.get(t1).cancel(true);
		
	}

}