package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import buildings.EconomicBuilding;
import buildings.Market;
import buildings.MilitaryBuilding;
import exceptions.BuildingInCoolDownException;
import exceptions.FriendlyCityException;
import exceptions.FriendlyFireException;
import exceptions.MaxCapacityException;
import exceptions.MaxLevelException;
import exceptions.MaxRecruitedException;
import exceptions.NotEnoughGoldException;
import exceptions.TargetNotReachedException;
import units.Army;
import units.Status;
import units.Unit;

public class Game implements PlayerListener {

	// Attributes
	private Player player;
	private SmartAgent ai;
	private ArrayList<City> availableCities; // Read only
	private ArrayList<Distance> distances; // Read only
	private final int maxTurnCount = 50; // Read only
	private int currentTurnCount = 1;
	private GameListener gameListener;
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public SmartAgent getAi() {
		return ai;
	}
	public void setAi(SmartAgent ai) {
		this.ai = ai;
	}
	public ArrayList<City> getAvailableCities(){
		return availableCities;
	}
	
	public ArrayList<Distance> getDistances(){
		return distances;
	}
	
	public int getMaxTurnCount() {
		return maxTurnCount;
	}
	
	public int getCurrentTurnCount() {
		return currentTurnCount;
	}
	public void setCurrentTurnCount(int currentTurnCount) {
		this.currentTurnCount = currentTurnCount;
		if(gameListener!=null)
			gameListener.onTurnCountChanged(currentTurnCount);
	}
	
	public void setGameListener(GameListener gameListener) {
		this.gameListener = gameListener;
	}
	
	// Constructor
	public Game(String playerName,String playerCity, boolean battleAI) throws IOException {
		 this.player = new Player(playerName);
		 player.setPlayerListener(this);
		 player.setTreasury(10000);
		 if(battleAI) {
			 this.ai = new SmartAgent(player, this);
			 ai.setTreasury(10000);
		 }
		 this.availableCities = new ArrayList<City>();
		 this.distances = new ArrayList<Distance>();
		 loadCitiesAndDistances();
		 boolean ignored = false;
		 for(City c : availableCities) {
			 if(c.getName().equals(playerCity)) 
				 player.getControlledCities().add(c);
			 else {
				 if(battleAI && ((!ignored && ai.getControlledCities().size()==0 && new Random().nextInt(2)==0)||ignored)) {
					 ai.getControlledCities().add(c);
				 }
				 else {
					 loadArmy(
							 c.getName(),
							 String.format("%s_army.csv",c.getName().toLowerCase())
							 );
					 ignored = true;
				 } 
			 }
		 }
	 }
	
	// Methods
	public static ArrayList<String[]> readCSVFile(String path) throws IOException{
		String currentLine;
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String[]> lines = new ArrayList<String[]>();
		while((currentLine = br.readLine())!=null) 
			lines.add(currentLine.split(","));
		fr.close();
		br.close();
		return lines;
	}
	
	public void loadArmy(String cityName,String path) throws IOException {
		City city = null;
		for(City c : availableCities)
			if(c.getName().equals(cityName)) {
				city = c;
				break;
			}
		Army army = city.getDefendingArmy();
		for (String[] values : readCSVFile(path)) 
			army.addUnit(values[0],Integer.parseInt(values[1]));
	}
	
	private void loadCitiesAndDistances() throws IOException {
		String path = "distances.csv";
		for(String[] values : readCSVFile(path)){
			distances.add(new Distance(values[0],values[1],Integer.parseInt(values[2])));
			//distances.add(new Distance(values[1],values[0],Integer.parseInt(values[2])));
			
			boolean fromFound = false, toFound = false;
			for(City c : availableCities) {
				if(values[0].equals(c.getName()))
					fromFound = true;
				if(values[1].equals(c.getName()))
					toFound = true;
			}
			if(!fromFound)
				availableCities.add(new City(values[0]));
			// Second condition needed to handle a case such as
			// Cairo Cairo 0. This way, Cairo wont be added twice
			if(!toFound  && !values[0].equals(values[1]))
				availableCities.add(new City(values[1]));
		}
	}
	
	public void targetCity(Army army, String targetName) {
		if(army.getCurrentStatus().equals(Status.IDLE)) {
			int distance = 0;
			for(Distance d : distances) {
				if(d.getFrom().equals(army.getCurrentLocation()) && d.getTo().equals(targetName)
					|| d.getTo().equals(army.getCurrentLocation()) && d.getFrom().equals(targetName)) {
					distance = d.getDistance();
					break;
				}
			}
			army.setDistancetoTarget(distance);
			army.setTarget(targetName);
			army.setCurrentStatus(Status.MARCHING);
			army.setCurrentLocation("onRoad");
		}
	}
	
	public void endTurn() {
		setCurrentTurnCount(currentTurnCount+1);
		for(City c : player.getControlledCities()) {
			for(MilitaryBuilding mb : c.getMilitaryBuildings()) {
				mb.setCoolDown(false);
				mb.setCurrentRecruit(0);
			}
			for(EconomicBuilding eb : c.getEconomicalBuildings()) {
				eb.setCoolDown(false);
				if(eb instanceof Market)
					player.setTreasury(player.getTreasury()+eb.harvest());
				else
					player.setFood(player.getFood()+eb.harvest());
			}
		}
		double totalFoodNeeded = 0;
		for(Army army : player.getControlledArmies()) {
			totalFoodNeeded += army.foodNeeded();
		}
		for(City city : player.getControlledCities())
				totalFoodNeeded += city.getDefendingArmy().foodNeeded();
		if(player.getFood()>=totalFoodNeeded)
			player.setFood(player.getFood()-totalFoodNeeded);
		else {
			player.setFood(0);
			for(Army army : player.getControlledArmies())
				for(Unit unit : army.getUnits())
					unit.setCurrentSoldierCount((unit.getCurrentSoldierCount()-(int)(unit.getCurrentSoldierCount()*0.1)));
			for(City city : player.getControlledCities())
				for(Unit unit : city.getDefendingArmy().getUnits())
					unit.setCurrentSoldierCount((unit.getCurrentSoldierCount()-(int)(unit.getCurrentSoldierCount()*0.1)));
		}
		for(Army army : player.getControlledArmies()) {
			if(army.getDistancetoTarget()>0 && !army.getTarget().equals("")) {
				army.setDistancetoTarget(army.getDistancetoTarget()-1);
				if(army.getDistancetoTarget()==0) {
					army.setCurrentStatus(Status.IDLE);
					army.setCurrentLocation(army.getTarget());
					army.setTarget("");
					army.setDistancetoTarget(-1);
				}
			}
			if(army.getCurrentStatus()==Status.BESIEGING) {
					army.setTurnsSieging(army.getTurnsSieging()+1);
			}
		}
		endAITurn();
	}
	
	public void endAITurn() {
		if(ai!=null) {
			try {
				if(ai.getControlledCities().size()!=0)
					SmartAgent.minmax(ai, ai.getDepth(), currentTurnCount);
			} 
			catch (NotEnoughGoldException e1) {} 
			catch (BuildingInCoolDownException e1) {} 
			catch (MaxLevelException e1) {} 
			catch (MaxRecruitedException e1) {}
			for(City c : ai.getControlledCities()) {
				for(MilitaryBuilding mb : c.getMilitaryBuildings()) {
					mb.setCoolDown(false);
					mb.setCurrentRecruit(0);
				}
				for(EconomicBuilding eb : c.getEconomicalBuildings()) {
					eb.setCoolDown(false);
					if(eb instanceof Market)
						ai.setTreasury(ai.getTreasury()+eb.harvest());
					else
						ai.setFood(ai.getFood()+eb.harvest());
				}
			}
			double totalFoodNeeded = 0;
			for(Army army : ai.getControlledArmies()) {
				totalFoodNeeded += army.foodNeeded();
			}
			for(City city : ai.getControlledCities())
					totalFoodNeeded += city.getDefendingArmy().foodNeeded();
			if(ai.getFood()>=totalFoodNeeded)
				ai.setFood(ai.getFood()-totalFoodNeeded);
			else {
				ai.setFood(0);
				for(Army army : ai.getControlledArmies())
					for(Unit unit : army.getUnits())
						unit.setCurrentSoldierCount((unit.getCurrentSoldierCount()-(int)(unit.getCurrentSoldierCount()*0.1)));
				for(City city : ai.getControlledCities())
					for(Unit unit : city.getDefendingArmy().getUnits())
						unit.setCurrentSoldierCount((unit.getCurrentSoldierCount()-(int)(unit.getCurrentSoldierCount()*0.1)));
			}
			for(Army army : ai.getControlledArmies()) {
				if(army.getDistancetoTarget()>0 && !army.getTarget().equals("")) {
					army.setDistancetoTarget(army.getDistancetoTarget()-1);
					if(army.getDistancetoTarget()==0) {
						army.setCurrentStatus(Status.IDLE);
						army.setCurrentLocation(army.getTarget());
						army.setTarget("");
						army.setDistancetoTarget(-1);
						for(City c : getAvailableCities()) {
							if(army.getCurrentLocation().equals(c.getName()) && !ai.getControlledCities().contains(c) && !c.isUnderSiege()) {
								try {
									ai.laySiege(army,c);
									gameListener.onCityReached(c);
								}
								catch (TargetNotReachedException e) {}
								catch (FriendlyCityException e) {}
							}
						}
					}
				}
				if(army.getCurrentStatus()==Status.BESIEGING) {
						army.setTurnsSieging(army.getTurnsSieging()+1);
				}
			}
			for(City c : ai.getControlledCities()) {
				if(c.getDefendingArmy().getUnits().size()>16) {
					ArrayList<Unit> darmy = c.getDefendingArmy().getUnits();
					ai.initiateArmy(c, darmy.get(new Random().nextInt(darmy.size())));
					for(Army a : ai.getControlledArmies()) {
						while(a.getCurrentLocation().equals(c.getName()) && c.getDefendingArmy().getUnits().size()>20) {
							try {
								a.relocateUnit(darmy.get(new Random().nextInt(darmy.size())));
							} catch (MaxCapacityException e) {
								break;
							}
						}
					}
				}
			}
		}
		for(City c : availableCities)
			if(c.isUnderSiege()) {
				if(c.getTurnsUnderSiege()==3) {
					c.setUnderSiege(false);
					gameListener.onSiegeLimitReached(c);
				}
				else {
					c.setTurnsUnderSiege(c.getTurnsUnderSiege()+1);
					for(Unit unit:c.getDefendingArmy().getUnits())
						unit.setCurrentSoldierCount((unit.getCurrentSoldierCount()-(int)(unit.getCurrentSoldierCount()*0.1)));
				}
			}
	}
	
	public void occupy(Army a,String cityName, Player p) {
		for(City c:availableCities)
			if(c.getName().equals(cityName)) {
				a.setCurrentStatus(Status.IDLE);
				a.setTurnsSieging(0);
				p.getControlledCities().add(c);
				p.getControlledArmies().remove(a);
				c.setDefendingArmy(a);
				c.setTurnsUnderSiege(-1);
				c.setUnderSiege(false);
			}
	}
	
	public void autoResolve(Army attacker, Army defender, Player p) throws
	FriendlyFireException {
		for(Army army:player.getControlledArmies())
			if(defender.equals(army))
				throw new FriendlyFireException("Target army is under your control!");
		Random rand = new Random();
		for(int i=0;attacker.getUnits().size()!=0 && defender.getUnits().size()!=0;i++) {
			Unit unitA = attacker.getUnits().get(rand.nextInt(attacker.getUnits().size()));
			Unit unitB = defender.getUnits().get(rand.nextInt(defender.getUnits().size()));
			if(i%2==0) {
				unitA.attack(unitB);
			}
			else {
				unitB.attack(unitA);
			}
		}
		if(attacker.getUnits().size()!=0)
			occupy(attacker,defender.getCurrentLocation(),p);
	}
	
	public boolean isGameOver() {
		return player.getControlledCities().size() == availableCities.size() || 
				(player.getControlledCities().size()==0)
				|| currentTurnCount>maxTurnCount;
	}
	
	public void onTreasuryChanged(double treasury) {
		if(gameListener!=null)
			gameListener.onTreasuryChanged(treasury);
	}
	
	public void onFoodChanged(double food) {
		if(gameListener!=null)
			gameListener.onFoodChanged(food);
	}
	
}
