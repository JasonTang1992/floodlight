package net.floodlightcontroller.netmonitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

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
	private Map<Long,List<Runnable>> scheduledMap;
	private ScheduledExecutorService e = (ScheduledExecutorService) Executors.newScheduledThreadPool(10);

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
			e.scheduleAtFixedRate(task, 0, period, TimeUnit.MICROSECONDS);
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
			this.scheduledMap.get(Long.valueOf(taskKey)).remove(task);
			if(this.scheduledMap.containsKey(Long.valueOf(newperiod)) == false) 
				this.scheduledMap.put(Long.valueOf(newperiod), new ArrayList<Runnable>());
			this.scheduledMap.get(Long.valueOf(newperiod)).add((Runnable)task);
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
			this.scheduledMap.get(Long.valueOf(oldperiod)).remove(task);
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
		
		this.scheduledMap.get(Long.valueOf(containsTask(task))).remove(task);
		
		return 0;
	}

	/**
	 * 
	 * @param task
	 * @param period
	 */
	public int rmTask(Runnable task, long period){
		this.scheduledMap.get(Long.valueOf(period)).remove(task);
		return 0;
	}

}