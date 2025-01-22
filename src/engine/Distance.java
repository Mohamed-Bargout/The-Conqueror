package engine;

public class Distance {
	
	// Attributes
	private String from; // Read only
	private String to; // Read only
	private int distance; // Read only
	
	public String getFrom() {
		return from;
	}
	public String getTo() {
		return to;
	}
	public int getDistance() {
		return distance;
	}
	
	// Constructor
	 public Distance(String from,String to, int distance) {
		 this.from = from;
		 this.to = to;
		 this.distance = distance;
	 }
}
