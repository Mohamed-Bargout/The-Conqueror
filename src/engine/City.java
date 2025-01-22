package engine;

import java.util.ArrayList;

import buildings.Building;
import buildings.EconomicBuilding;
import buildings.MilitaryBuilding;
import units.Army;

public class City {
	
	// Attributes
	private String name; // Read only
	private ArrayList<EconomicBuilding> economicalBuildings; // Read only
	private ArrayList<MilitaryBuilding> militaryBuildings; // Read only
	private Army defendingArmy;
	private int turnsUnderSiege = -1;
	private boolean underSiege = false;
	
	public String getName() {
		return name;
	}
	
	public ArrayList<EconomicBuilding> getEconomicalBuildings() {
		return economicalBuildings;
	}
	
	public ArrayList<MilitaryBuilding> getMilitaryBuildings() {
		return militaryBuildings;
	}
	
	public Army getDefendingArmy() {
		return defendingArmy;
	}
	public void setDefendingArmy(Army defendingArmy) {
		this.defendingArmy = defendingArmy;
	}
	
	public int getTurnsUnderSiege() {
		return turnsUnderSiege;
	}
	public void setTurnsUnderSiege(int turnsUnderSiege) {
		this.turnsUnderSiege = turnsUnderSiege;
	}
	
	public boolean isUnderSiege() {
		return underSiege;
	}
	public void setUnderSiege(boolean underSiege) {
		this.underSiege = underSiege;
	}
	
	// Constructor
	public City(String name) {
		this.name = name;
		this.defendingArmy = new Army(name);
		this.economicalBuildings = new ArrayList<EconomicBuilding>();
		this.militaryBuildings = new ArrayList<MilitaryBuilding>();
	}
	
	// Methods
	public static City clone(City city) {
		City clone = new City(city.getName());
		for(Building b : city.getEconomicalBuildings()) {
			clone.getEconomicalBuildings().add((EconomicBuilding)Building.clone(b));
		}
		for(Building b : city.getMilitaryBuildings()) {
			clone.getMilitaryBuildings().add((MilitaryBuilding)Building.clone(b));
		}
		clone.setDefendingArmy(Army.clone(city.getDefendingArmy()));
		clone.setTurnsUnderSiege(city.getTurnsUnderSiege());
		clone.setUnderSiege(city.isUnderSiege());
		return clone;
	}
}
