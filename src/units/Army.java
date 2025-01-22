package units;

import java.util.ArrayList;

import exceptions.MaxCapacityException;

public class Army {
	
	// Attributes
	private Status currentStatus = Status.IDLE;
	private ArrayList<Unit> units;
	private int distancetoTarget = -1;
	private String target = "";
	private String currentLocation;
	private int turnsSieging = 0;
	private final int maxToHold = 10; // Read only
	
	public Status getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(Status currentStatus) {
		this.currentStatus = currentStatus;
	}

	public ArrayList<Unit> getUnits() {
		return units;
	}
	public void setUnits(ArrayList<Unit> units) {
		this.units = units;
	}

	public int getDistancetoTarget() {
		return distancetoTarget;
	}
	public void setDistancetoTarget(int distancetoTarget) {
		this.distancetoTarget = distancetoTarget;
	}

	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}
	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	public int getTurnsSieging() {
		return turnsSieging;
	}
	public void setTurnsSieging(int turnsSieging) {
		this.turnsSieging = turnsSieging;
	}
	public int getMaxToHold() {
		return maxToHold;
	}

	// Constructor
	public Army(String currentLocation) {
		this.currentLocation = currentLocation;	
		this.units = new ArrayList<Unit>();
	}
	
	// Methods
	public void addUnit(String type, int level) {
		Unit unit = Unit.createUnit(type,level);
		unit.setParentArmy(this);
		units.add(unit);
	}
	
	public void relocateUnit(Unit unit) throws MaxCapacityException {
		if(units.size()>=maxToHold) 
			throw new MaxCapacityException("Army has maximum number of units");
		unit.getParentArmy().units.remove(unit);
		unit.setParentArmy(this);
		units.add(unit);
	}
	
	public void handleAttackedUnit(Unit unit) {
		if(unit.getCurrentSoldierCount()<=0) {
			unit.setCurrentSoldierCount(0);
			units.remove(unit);
			unit.setParentArmy(null);
		}
	}
	
	public double foodNeeded() {
		double food = 0;
		for(Unit unit:units) {
			double keep=0;
			switch(currentStatus) {
				case IDLE: keep = unit.getIdleUpkeep(); break;
				case BESIEGING: keep = unit.getSiegeUpkeep(); break;
				case MARCHING: keep = unit.getMarchingUpkeep(); break;
			}
			food += keep*unit.getCurrentSoldierCount();
		}
		return food;
	}
	
	public static Army clone(Army a) {
		Army clone = new Army(a.getCurrentLocation());
		clone.setCurrentStatus(a.getCurrentStatus());
		clone.setDistancetoTarget(a.getDistancetoTarget());
		clone.setTurnsSieging(a.getTurnsSieging());
		clone.setTarget(a.getTarget());
		for(Unit u : a.getUnits()) {
			clone.getUnits().add(Unit.clone(u));
		}
		return clone;
	}
	
}
