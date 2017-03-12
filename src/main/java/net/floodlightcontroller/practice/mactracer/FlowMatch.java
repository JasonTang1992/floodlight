package net.floodlightcontroller.practice.mactracer;

public class FlowMatch implements Comparable{
	
	private int srcIP;
	private int destIP;
	private int srcPort;
	private int destPort;
	private int nwProto;	

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSrcIP() {
		return srcIP;
	}

	public void setSrcIP(int srcIP) {
		this.srcIP = srcIP;
	}

	public int getDestIP() {
		return destIP;
	}

	public void setDestIP(int destIP) {
		this.destIP = destIP;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public int getDestPort() {
		return destPort;
	}

	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}

	public int getNwProto() {
		return nwProto;
	}

	public void setNwProto(int nwProto) {
		this.nwProto = nwProto;
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
