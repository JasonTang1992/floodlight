package net.floodlightcontroller.practice.mactracer;

public class SwitchPollingWorker implements Runnable{

    private int timeout;
    private Object container;
    private static SwitchPollingWorker self = null;
    
    public static SwitchPollingWorker CreateSwitchPollingWorker(Object container)
    {
    	if(self == null)
    	{
    		self = new SwitchPollingWorker(container);
    	}
    	
    	return self;
    }
    
    public static SwitchPollingWorker CreateSwitchPollingWorker(int timeout, Object container)
    {
    	if(self == null)
    	{
    		self = new SwitchPollingWorker(timeout,container);
    	}
    	
    	return self;
    }
    
    

    private SwitchPollingWorker(Object container)
    {
        timeout = 0;
        this.container = container;
    }

    private SwitchPollingWorker(int timeout, Object container)
    {
        this.timeout = timeout;
        this.container = container;
    }
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.print("SwitchPollingWorker : working **************************");
	}
	
	

}
