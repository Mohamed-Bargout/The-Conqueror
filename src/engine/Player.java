package engine;

import java.util.ArrayList;

import buildings.ArcheryRange;
import buildings.Barracks;
import buildings.Building;
import buildings.EconomicBuilding;
import buildings.Farm;
import buildings.Market;
import buildings.MilitaryBuilding;
import buildings.Stable;
import exceptions.BuildingInCoolDownException;
import exceptions.FriendlyCityException;
import exceptions.MaxLevelException;
import exceptions.MaxRecruitedException;
import exceptions.NotEnoughGoldException;
import exceptions.TargetNotReachedException;
import units.Army;
import units.Status;
import units.Unit;

public class Player {
	
	// Attributes
	private String name; // Read only
	private ArrayList<City> controlledCities; // Read only
	private ArrayList<Army> controlledArmies; // Read only
	private double treasury;
	private double food;
	private PlayerListener playerListener;
	
	public String getName() {
		return name;
	}
	
	public ArrayList<City> getControlledCities() {
		return controlledCities;
	}
	
	public ArrayList<Army> getControlledArmies() {
		return controlledArmies;
	}
	
	public double getTreasury() {
		return treasury;
	}
	public void setTreasury(double treasury) {
		this.treasury = treasury;
		if(playerListener!=null)
			playerListener.onTreasuryChanged(treasury);
	}
	
	public double getFood() {
		return food;
	}
	public void setFood(double food) {
		this.food = food;
		if(playerListener!=null)
			playerListener.onFoodChanged(food);
	}

	public void setPlayerListener(PlayerListener playerListener) {
		this.playerListener = playerListener;
	}

	// Constructor
	public Player(String name) {
		this.name = name;
		this.controlledCities = new ArrayList<City>();
		this.controlledArmies = new ArrayList<Army>();
	}
	
	// Methods
	public void recruitUnit(String type,String cityName) throws
	BuildingInCoolDownException, MaxRecruitedException, NotEnoughGoldException {
		City city = null;
		for(City c : controlledCities)
			if(c.getName().equals(cityName)) {
				city = c;
				break;
			}
		if(city==null) return;
		String buildingType = "";
		switch(type) {
			case "Archer": buildingType = "buildings.ArcheryRange";break;
			case "Infantry": buildingType = "buildings.Barracks";break;
			case "Cavalry": buildingType = "buildings.Stable";break;
		}
		MilitaryBuilding mb = null;
		for(MilitaryBuilding m : city.getMilitaryBuildings()) {
			if(m.getClass().getName().equals(buildingType)) {
				mb = m;
				break;
			}
		}
		if(mb==null) return;
		if(treasury<mb.getRecruitmentCost()) {
			throw new NotEnoughGoldException("Not enough gold to complete recruitement!");
		}
		Unit unit = mb.recruit();
		unit.setParentArmy(city.getDefendingArmy());
		city.getDefendingArmy().getUnits().add(unit);
		setTreasury(treasury - mb.getRecruitmentCost());
	}
	
	public void build(String type,String cityName) throws NotEnoughGoldException {
		City city = null;
		for(City c : controlledCities)
			if(c.getName().equals(cityName)) {
				city = c;
				break;
			}
		Building b = null;
		switch(type) {
			case "ArcheryRange": b = new ArcheryRange();break;
			case "Barracks": b = new Barracks();break;
			case "Stable": b = new Stable();break;
			case "Farm": b = new Farm();break;
			case "Market": b = new Market();break;
		}
		for(Building built : city.getEconomicalBuildings()) {
			if(b.getClass().equals(built.getClass()))
				return;
		}
		for(Building built : city.getMilitaryBuildings()) {
			if(b.getClass().equals(built.getClass()))
				return;
		}
		if(treasury<b.getCost())
			throw new NotEnoughGoldException("Not enough gold to build!");
		setTreasury(treasury - b.getCost());
		if(b instanceof EconomicBuilding)
			city.getEconomicalBuildings().add((EconomicBuilding)b);
		else
			city.getMilitaryBuildings().add((MilitaryBuilding)b);
	}
	
	public void upgradeBuilding(Building b) throws NotEnoughGoldException,
	BuildingInCoolDownException, MaxLevelException {
		if(treasury<b.getUpgradeCost())
			throw new NotEnoughGoldException("Not enough gold to upgrade!");
		double cost = b.getUpgradeCost();
		b.upgrade();
		setTreasury(treasury - cost);
	}
	
	public void upgradeBuildingByName(String type, String cityName) throws NotEnoughGoldException, BuildingInCoolDownException, MaxLevelException {
		for(City city : controlledCities)
			if(city.getName().equals(cityName)) {
				for(MilitaryBuilding mb : city.getMilitaryBuildings()) {
					if(mb.getClass().getName().endsWith(type))
						upgradeBuilding(mb);
				}
				for(EconomicBuilding eb : city.getEconomicalBuildings()) {
					if(eb.getClass().getName().endsWith(type))
						upgradeBuilding(eb);
				}
				break;
			}
	}
	
	
	public void initiateArmy(City city,Unit unit) {
		Army army = new Army(city.getName());
		army.getUnits().add(unit);
		unit.setParentArmy(army);
		for(Army inArmy : controlledArmies)
			for(Unit thisUnit : inArmy.getUnits())
				if(thisUnit == unit) {
					inArmy.getUnits().remove(unit);
					break;
				}
		city.getDefendingArmy().getUnits().remove(unit);
		controlledArmies.add(army);
	}
	
	public void laySiege(Army army,City city) throws TargetNotReachedException,
	FriendlyCityException {
		for(City c:controlledCities) {
			if(c.getName().equals(city.getName()))
				throw new FriendlyCityException("This city is already under your control");
		}
		if(!army.getCurrentLocation().equals(city.getName()))
			throw new TargetNotReachedException("This army is not yet at the city");
		army.setCurrentStatus(Status.BESIEGING);
		city.setUnderSiege(true);
		city.setTurnsUnderSiege(0);
	}
	
	public ArrayList<Army> getLocalArmies(String cityName){
		ArrayList<Army> local = new ArrayList<>();
		for(Army a : controlledArmies) {
			if(a.getCurrentLocation().equals(cityName) && a.getUnits().size()!=0)
				local.add(a);
		}
		return local;
	}
	
	public ArrayList<Army> getIdleArmies(){
		ArrayList<Army> idle = new ArrayList<>();
		for(Army a : controlledArmies) {
			if(a.getCurrentStatus()==Status.IDLE && a.getUnits().size()!=0)
				idle.add(a);
		}
		return idle;
	}
	
	public ArrayList<Army> getMarchingArmies(){
		ArrayList<Army> march = new ArrayList<>();
		for(Army a : controlledArmies) {
			if(a.getCurrentStatus()==Status.MARCHING && a.getUnits().size()!=0)
				march.add(a);
		}
		return march;
	}
	
	public ArrayList<Army> getSiegingArmies(){
		ArrayList<Army> siege = new ArrayList<>();
		for(Army a : controlledArmies) {
			if(a.getCurrentStatus()==Status.BESIEGING && a.getUnits().size()!=0)
				siege.add(a);
		}
		return siege;
	}
	public static Player clone(Player player) {
		Player clone = new Player(player.getName());
		
		for(City c : player.getControlledCities()) {
			clone.getControlledCities().add(City.clone(c));
		}
		for(Army a : player.getControlledArmies()) {
			clone.getControlledArmies().add(Army.clone(a));
		}
		clone.setTreasury(player.getTreasury());
		clone.setFood(player.getFood());
		
		return clone;
//		private PlayerListener playerListener;
	}
}
