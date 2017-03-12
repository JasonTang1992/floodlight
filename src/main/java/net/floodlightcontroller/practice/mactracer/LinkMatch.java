package net.floodlightcontroller.practice.mactracer;

public class LinkMatch implements Comparable{
	
	private long swId;
	private int inputPort;

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getSwId() {
		return swId;
	}

	public void setSwId(long swId) {
		this.swId = swId;
	}

	public int getInputPort() {
		return inputPort;
	}

	public void setInputPort(int inputPort) {
		this.inputPort = inputPort;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	

}
