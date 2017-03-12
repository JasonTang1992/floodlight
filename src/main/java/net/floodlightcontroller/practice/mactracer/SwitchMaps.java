package net.floodlightcontroller.practice.mactracer;

import java.util.HashMap;

public class SwitchMaps {

	private HashMap<Long,SwitchMap>SwitchMapTable
		= new HashMap<Long,SwitchMap>();
	private static boolean object_already_existing = false;
	private static SwitchMaps self = null;
	
	public static SwitchMaps CreatSwitchMaps()
	{
		if(object_already_existing)
		{
			
		}
		else
		{
			SwitchMaps.self = new SwitchMaps();
			SwitchMaps.object_already_existing = true;
		}
		
		return SwitchMaps.self;
	}
	
	public void addSwitchMap(Long swId,SwitchMap swMap)
	{
		SwitchMapTable.put(swId, swMap);
	}
	
	public void rmSwitchMap(Long swId)
	{
		SwitchMapTable.remove(swId);
	}
	
	public SwitchMap getSwitchMap(Long swId)
	{
		return SwitchMapTable.get(swId);
	}

	public HashMap<Long, SwitchMap> getSwitchMapTable() {
		return SwitchMapTable;
	}

	public void setSwitchMapTable(HashMap<Long, SwitchMap> switchMapTable) {
		SwitchMapTable = switchMapTable;
	}
}
