package net.floodlightcontroller.netmonitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		if(containsTask(task) == true) return 0;
		else
		{
			if(this.scheduledMap.containsKey(Long.valueOf(period)) == false) 
				this.scheduledMap.put(Long.valueOf(period), new ArrayList<Runnable>());
			this.scheduledMap.get(Long.valueOf(period)).add((Runnable)task);
		}
		return 0;
	}

	/**
	 * 
	 * @param task
	 */
	public boolean containsTask(Runnable task){
		Iterator it = this.scheduledMap.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<Long,List<Runnable>> pair = (Entry<Long,List<Runnable>>)it.next();
			List<Runnable> list = pair.getValue();
			if(list.contains(task)) return true;
		}
		return false;
	}

	/**
	 * 
	 * @param task
	 * @param newperiod
	 */
	public int modifyTask(Runnable task, long newperiod){
		if(containsTask(task) == false) return 0;
		else
		{
			if(this.scheduledMap.containsKey(Long.valueOf(period)) == false) 
				this.scheduledMap.put(Long.valueOf(period), new ArrayList<Runnable>());
			this.scheduledMap.get(Long.valueOf(period)).add((Runnable)task);
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
		return 0;
	}

	/**
	 * 
	 * @param task
	 */
	public int rmTask(Runnable task){
		return 0;
	}

	/**
	 * 
	 * @param task
	 * @param period
	 */
	public int rmTask(Runnable task, long period){
		return 0;
	}

}