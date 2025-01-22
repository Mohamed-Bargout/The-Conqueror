package engine;

import java.util.ArrayList;
import java.util.Arrays;

import buildings.ArcheryRange;
import buildings.Barracks;
import buildings.Building;
import buildings.EconomicBuilding;
import buildings.Farm;
import buildings.Market;
import buildings.MilitaryBuilding;
import buildings.Stable;
import exceptions.BuildingInCoolDownException;
import exceptions.MaxLevelException;
import exceptions.MaxRecruitedException;
import exceptions.NotEnoughGoldException;
import units.Archer;
import units.Army;
import units.Cavalry;
import units.Infantry;
import units.Status;
import units.Unit;

public class SmartAgent extends Player{
	
	
	//moves
	//[Build Farm, build Market, build ArcheryRange, build Barracks, build Stable, Upgrade Farm, Upgrade Market, Upgrade, Barracks, Upgrade Market ]
	
	// Attributes
	private Player player;
	private static Game game;
	private int depth;
	
	// Constructor
	public SmartAgent(Player player, Game game) {
		super("AI");
		this.player = player;
		SmartAgent.game = game;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	// Methods
	public static double minmax(SmartAgent AI,int depth, int turnCount) throws NotEnoughGoldException, BuildingInCoolDownException, MaxLevelException, MaxRecruitedException {
		
		if(depth == 0 || turnCount == 50) {
			return positonEval(AI);
		}
		
		double maxEval = 0;
		int[] bestMove = {1,1,1,1,1,0,0,0,0,0,0,0,0,0};
	    ArrayList<int[]> allMoves = AI.generateMoves();
//		for(int[] array : allMoves) {
//			System.out.println(Arrays.toString(array));
//		}
		for(int[] m : allMoves) {
//			System.out.println(Arrays.toString(m));
			SmartAgent clone = clone(AI);
			clone.execute(m);
			endTurn(clone);
			double currentEval = minmax(clone,depth-1,turnCount+1);
//			System.out.println(maxEval + " " + currentEval);
			if(currentEval >= maxEval) {
				maxEval = currentEval;
				bestMove = m;
			}
			
		}
		if(AI.canAttack())
			bestMove[13] = 1;
		AI.execute(bestMove);
//		System.out.println(Arrays.toString(bestMove));
		return maxEval;
		
	}
	
	
	
	private static double positonEval(Player AI) {
		double eval = 0;
		
		int total = 0;
		double nCav = 0;
		double nInf = 0;
		double nArc = 0;
		double idealCav = 0.55;
		double idealInf = 0.3;
		double idealArc = 0.15;
		
		ArrayList<Army> defendingArmies = new ArrayList<Army>();
		for(City c : AI.getControlledCities()) {
			defendingArmies.add(c.getDefendingArmy());
			for(EconomicBuilding eb : c.getEconomicalBuildings())
				eval += eb.getScore();
			for(MilitaryBuilding mb : c.getMilitaryBuildings())
				eval += mb.getScore();
		}
		defendingArmies.addAll(AI.getControlledArmies());
		
		for(Army A : defendingArmies) {
			for(Unit U : A.getUnits()) {
				if(U instanceof Infantry)
					nInf++;
				if(U instanceof Archer)
					nArc++;
				if(U instanceof Cavalry)
					nCav++;
				total++;
			}
		}
		nCav = 1+(idealCav - nCav/total);
		nInf = 1+(idealInf - nInf/total);
		nArc = 1+(idealArc - nArc/total);
		
		
		for(Army A : defendingArmies) {
			for(Unit U : A.getUnits()) {
				if(U instanceof Archer)
					eval += ratio(U)*modifier(U)*nArc;
				if(U instanceof Infantry)
					eval += ratio(U)*modifier(U)*nInf;
				if(U instanceof Cavalry)
					eval += ratio(U)*modifier(U)*nCav;
			}
		}
		
		return eval;
	}
	
	private static double ratio(Unit unit) { 
		return (double)(unit.getCurrentSoldierCount())/unit.getMaxSoldierCount();
	}
	private static double modifier(Unit unit) { 
		if(unit instanceof Archer) {
			switch(unit.getLevel()) {
				case 1: return 1;
				case 2: return 1.2;
				case 3: return 1.5;
			}
		}
		else if(unit instanceof Infantry) {
			switch(unit.getLevel()) {
				case 1: return 1.1;
				case 2: return 1.3;
				case 3: return 1.6;
			}
		}
		else if(unit instanceof Cavalry) {
			switch(unit.getLevel()) {
				case 1: return 1.2;
				case 2: return 1.4;
				case 3: return 1.8;
			}
		}
		return 0;
	}
	private int canBuild(String building, int deduction) {
		ArrayList<Building> buildings = new ArrayList<Building>();
		for(City c : this.getControlledCities()) {
			buildings.addAll(c.getEconomicalBuildings());
			buildings.addAll(c.getMilitaryBuildings());
		}
		boolean exists = false;
		for(Building b : buildings) {
			switch(building) {
			case "Farm": if(b instanceof Farm) exists = true;break;
			case "Market": if(b instanceof Market) exists = true;break;
			case "ArcheryRange": if(b instanceof ArcheryRange) exists = true;break;
			case "Barracks": if(b instanceof Barracks) exists = true;break;
			case "Stable": if(b instanceof Stable) exists = true;break;
			}
		}
		
		if(exists == true) // Building is already built
			return -1;
		
		switch(building) {
		case "Farm": if(this.getTreasury() - deduction >= 1000)  return 1000; else return -1;
		case "Market": if(this.getTreasury() - deduction >= 1500)  return 1500; else return -1;
		case "ArcheryRange": if(this.getTreasury() - deduction >= 1500)  return 1500; else return -1;
		case "Barracks": if(this.getTreasury() - deduction >= 2000)  return 2000; else return -1;
		case "Stable": if(this.getTreasury() - deduction >= 2500)  return 2500; else return -1;
		}
		
		return -1;
	}
	private int canUpgrade(String name, int deduction) {
		ArrayList<Building> buildings = new ArrayList<Building>();
		for(City c : this.getControlledCities()) {
			buildings.addAll(c.getEconomicalBuildings());
			buildings.addAll(c.getMilitaryBuildings());
		}
		
		Building building = null;
		
		for(Building b : buildings) {
			switch(name) {
			case "Farm": if(b instanceof Farm) building  = b;break;
			case "Market": if(b instanceof Market) building  = b;break;
			case "ArcheryRange": if(b instanceof ArcheryRange) building  = b;break;
			case "Barracks": if(b instanceof Barracks) building  = b;break;
			case "Stable": if(b instanceof Stable) building  = b;break;
			}
		}
		
		if(building == null || building.getLevel() == 3 || building.isCoolDown()) { //Building doesnt exist or exists but can upgrade from 3 to 4 or in cooldown
			return -1;
		}
		if(building.getUpgradeCost() > this.getTreasury() - deduction ) { // Cannot afford upgrade
			return -1;
		}
		return building.getUpgradeCost();
	}
	private int canRecruit(String unit, int number, int deduction) {
		ArrayList<Building> buildings = new ArrayList<Building>();
		for(City c : this.getControlledCities()) {
			buildings.addAll(c.getMilitaryBuildings());
		}
		
		Building building = null;
		
		for(Building b : buildings) {
			switch(unit) {
			case "Archer": if(b instanceof ArcheryRange) building  = b;break;
			case "Infantry": if(b instanceof Barracks) building  = b;break;
			case "Cavalry": if(b instanceof Stable) building  = b;break;
			}
		}
		if(building == null) // Building doesnt exist
			return -1;
		if(building.isCoolDown()) // Building in Cooldown
			return -1;
		
		switch(unit) {
		case "Infantry" :  if(this.getTreasury() - deduction >= number*(((Barracks)building).getRecruitmentCost())) return number*((Barracks)building).getRecruitmentCost(); else return -1  ;
		case "Archer" :  if(this.getTreasury() - deduction >= number*(((ArcheryRange)building).getRecruitmentCost())) return number*((ArcheryRange)building).getRecruitmentCost(); else return -1  ;
		case "Cavalry" :  if(this.getTreasury() - deduction >= number*(((Stable)building).getRecruitmentCost())) return number*((Stable)building).getRecruitmentCost(); else return -1  ;
		}
		
		return -1;
		
	}
	private boolean canAttack() {
		for(Army a : this.getControlledArmies()) 
			if(a.getCurrentStatus() == Status.IDLE && a.getUnits().size() >=  10 && a.getCurrentLocation().equals(this.getControlledCities().get(0).getName())) {
				return true;
			}
		return false;
	}
	private int[] possibleMoves(){
		//moves
		//[Build Farm, build Market, build ArcheryRange, build Barracks, build Stable, Upgrade Farm, Upgrade Market, Upgrade, Barracks, Upgrade Market,Cav,Inf,Arc, Attack ]
		int[] move = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		if(canBuild("Farm",0) != -1) {
			move[0] = 1;
		}
		if(canBuild("Market",0) != -1) {
			move[1] = 1;
		}
		if(canBuild("ArcheryRange",0) != -1) {
			move[2] = 1;
		}
		if(canBuild("Barracks",0) != -1) {
			move[3] = 1;
		}
		if(canBuild("Stable",0) != -1) {
			move[4] = 1;
		}
		
		if(canUpgrade("Farm",0) != -1) {
			move[5] = 1;
		}
		if(canUpgrade("Market",0) != -1) {
			move[6] = 1;
		}
		if(canUpgrade("ArcheryRange",0) != -1) {
			move[7] = 1;
		}
		if(canUpgrade("Barracks",0) != -1) {
			move[8] = 1;
		}
		if(canUpgrade("Stable",0) != -1) {
			move[9] = 1;
		}
		
		if(canRecruit("Cavalry",3,0) != -1)
			move[10] = 3;
		else 
			if(canRecruit("Cavalry",2,0) != -1)
			move[10] = 2;
		else
			if(canRecruit("Cavalry",1,0) != -1)
			move[10] = 1;
		
		if(canRecruit("Infantry",3,0) != -1)
			move[11] = 3;
		else 
			if(canRecruit("Infantry",2,0) != -1)
			move[11] = 2;
		else
			if(canRecruit("Infantry",1,0) != -1)
			move[11] = 1;
		
		if(canRecruit("Archer",3,0) != -1)
			move[12] = 3;
		else 
			if(canRecruit("Archer",2,0) != -1)
			move[12] = 2;
		else
			if(canRecruit("Archer",1,0) != -1)
			move[12] = 1;
		
		for(Army a : this.getControlledArmies()) {
			if(a.getCurrentStatus() == Status.IDLE && a.getUnits().size() >=  10 && a.getCurrentLocation().equals(this.getControlledCities().get(0).getName())) {
				move[13] = 1;
				break;
			}
		}
		
		return move;
	}
	private static boolean compare(int[] A, int[] B) {
		if(A.length != B.length)
			return false;
		for(int i = 0; i<A.length ; i++) {
			if(A[i] != B[i])
				return false;
		}
		return true;
	}
	private static boolean MoveNotMade(int[] move, ArrayList<int[]> allMoves ) {
		for(int[] m : allMoves) {
			if(compare(m,move) == true)
				return false;
		}
		return true;
	}
	private ArrayList<int[]> generateMoves(){
		
		int[] moves = possibleMoves();
		
		ArrayList<int[]> allPossible = new ArrayList<int[]>();
		ArrayList<int[]> allRealPossible = new ArrayList<int[]>();
		
		allPossible = genSequence(moves);
		
		for(int[] m : allPossible) {
			m = genReal(m);
			if(MoveNotMade(m,allRealPossible)) {
				allRealPossible.add(m);
			}
		}
		
		return allRealPossible;
	}
	private static ArrayList<int[]> genSequence(int[] max){
    	ArrayList<int[]> a = new ArrayList<>();
    	genSequence(a,new int[max.length],max);
    	return a;
    }

    private static void genSequence(ArrayList<int[]> a, int[] curr, int[] max){
    	a.add(curr.clone());
    	if(Arrays.equals(curr,max))
    		return;
    	int index = curr.length-1;
    	curr[index]++;
    	while(index>=0 && curr[index]>max[index]){
    		curr[index] = 0;
    		index--;
    		if(index>=0)
    			curr[index]++;
    	}
    	genSequence(a,curr,max);
    }
    private int[] genReal(int[] moves) { 
    	int[] realMoves = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    	int deduction = 0;
    	int cost = 0;
    	
    	if(moves[13] == 1) {
    		realMoves[13] = 1;
    	}
    	
    	if(moves[0] == 1) {
    		cost = this.canBuild("Farm", deduction);
			if(cost != -1) {
				realMoves[0] = 1;
			}
			else {
				return realMoves;
			}
			deduction += cost;
		}
    	if(moves[1] == 1) {
    		cost = this.canBuild("Market", deduction);
			if(cost != -1) {
				realMoves[1] = 1;
			}
			else {
				return realMoves;
			}
			deduction += cost;
		}
    	if(moves[2] == 1) {
    		cost = this.canBuild("ArcheryRange", deduction);
			if(cost != -1) {
				realMoves[2] = 1;
			}
			else {
				return realMoves;
			}
			deduction += cost;
		}
    	if(moves[3] == 1) {
    		cost = this.canBuild("Barracks", deduction);
			if(cost != -1) {
				realMoves[3] = 1;
			}
			else {
				return realMoves;
			}
			deduction += cost;
		}
    	if(moves[4] == 1) {
    		cost = this.canBuild("Stable", deduction);
			if(cost != -1) {
				realMoves[4] = 1;
			}
			else {
				return realMoves;
			}
			deduction += cost;
		}
		if(moves[5] == 1) {
			cost = this.canUpgrade("Farm", deduction);
			if(cost != -1) {
				realMoves[5] = 1;
			}
			else {
				return realMoves;
			}
			deduction += cost;
		}
		if(moves[6] == 1) {
			cost = this.canUpgrade("Market", deduction);
			if(cost != -1) {
				realMoves[6] = 1;
			}
			else {
				return realMoves;
			}
			deduction += cost;
		}
		if(moves[7] == 1) {
			cost = this.canUpgrade("ArcheryRange", deduction);
			if(cost != -1) {
				realMoves[7] = 1;
			}
			else {
				return realMoves;
			}
			deduction += cost;
		}
		if(moves[8] == 1) {
			cost = this.canUpgrade("Barracks", deduction);
			if(cost != -1) {
				realMoves[8] = 1;
			}
			else {
				return realMoves;
			}
			deduction += cost;
		}
		if(moves[9] == 1) {
			cost = this.canUpgrade("Stable", deduction);
			if(cost != -1) {
				realMoves[9] = 1;
			}
			else {
				return realMoves;
			}
			deduction += cost;
		}
		if(realMoves[9] != 1 && realMoves[4] != 1) {
			if(moves[10] != 0) {
				for(int i = 0; i < moves[10] ; i++) {
					cost = this.canRecruit("Cavalry", 1, deduction);
					if(cost != -1) {
						realMoves[10] += 1;
					}
					else {
						return realMoves;
					}
					deduction += cost;
				}
			}
		}
		if(realMoves[8] != 1 && realMoves[3] != 1) {
			if(moves[11] != 0) {
				for(int i = 0; i < moves[11] ; i++) {
					cost = this.canRecruit("Infantry", 1, deduction);
					if(cost != -1) {
						realMoves[11] += 1;
					}
					else {
						return realMoves;
					}
					deduction += cost;
				}
			}
		}
		if(realMoves[7] != 1 && realMoves[2] != 1) {
			if(moves[12] != 0) {
				for(int i = 0; i < moves[12] ; i++) {
					cost = this.canRecruit("Archer", 1, deduction);
					if(cost != -1) {
						realMoves[12] += 1;
					}
					else {
						return realMoves;
					}
					deduction += cost;
				}
			}
		}
		
		return realMoves;
    }
	private void execute(int[] moves) throws NotEnoughGoldException, BuildingInCoolDownException, MaxLevelException, MaxRecruitedException {
		String cityName = this.getControlledCities().get(0).getName();
		
		if(moves[0] == 1) {
			this.build("Farm", cityName);
		}
		if(moves[1] == 1) {
			this.build("Market", cityName);
		}
		if(moves[2] == 1) {
			this.build("ArcheryRange", cityName);
		}
		if(moves[3] == 1) {
			this.build("Barracks", cityName);
		}
		if(moves[4] == 1) {
			this.build("Stable", cityName);
		}
		if(moves[5] == 1) {
			this.upgradeBuildingByName("Farm", cityName);
		}
		if(moves[6] == 1) {
			this.upgradeBuildingByName("Market", cityName);
		}
		if(moves[7] == 1) {
			this.upgradeBuildingByName("ArcheryRange", cityName);
		}
		if(moves[8] == 1) {
			this.upgradeBuildingByName("Barracks", cityName);
		}
		if(moves[9] == 1) {
			this.upgradeBuildingByName("Stable", cityName);
		}
		if(moves[10] != 0) {
			for(int i = 0; i < moves[10] ; i++)
				this.recruitUnit("Cavalry", cityName);
		}
		if(moves[11] != 0) {
			for(int i = 0; i < moves[11] ; i++)
				this.recruitUnit("Infantry", cityName);
		}
		if(moves[12] != 0) {
			for(int i = 0; i < moves[12] ; i++)
				this.recruitUnit("Archer", cityName);
		}
		if(moves[13] != 0) {
			for(Army a : this.getControlledArmies()) {
				if(a.getCurrentStatus() == Status.IDLE && a.getUnits().size() >= 10) {
					try {
						game.targetCity(a, player.getControlledCities().get(0).getName());
					}
					catch(IndexOutOfBoundsException e) {
						for(City c: game.getAvailableCities())
							if(!getControlledCities().contains(c))
								game.targetCity(a, c.getName());
					}
				}
			}
		}
//		if(army.getCurrentStatus().equals(Status.IDLE)) {
//			int distance = 0;
//			for(Distance d : distances) {
//				if(d.getFrom().equals(army.getCurrentLocation()) && d.getTo().equals(targetName)
//					|| d.getTo().equals(army.getCurrentLocation()) && d.getFrom().equals(targetName)) {
//					distance = d.getDistance();
//					break;
//				}
//			}
//			army.setDistancetoTarget(distance);
//			army.setTarget(targetName);
//			army.setCurrentStatus(Status.MARCHING);
//			army.setCurrentLocation("onRoad");
//		}
	}
	private static void test(SmartAgent AI) throws NotEnoughGoldException, BuildingInCoolDownException, MaxLevelException, MaxRecruitedException {
		int[] move = {1,1,1,1,1,0,0,0,0,0,0,0,0,0,0};
		AI.execute(move);
		return;
	}
	private static SmartAgent clone(SmartAgent AI) {
		SmartAgent clone = new SmartAgent(Player.clone(AI.player),game);
		
		for(City c : AI.getControlledCities()) {
			clone.getControlledCities().add(City.clone(c));
		}
		for(Army a : AI.getControlledArmies()) {
			clone.getControlledArmies().add(Army.clone(a));
		}
		clone.setTreasury(AI.getTreasury());
		clone.setFood(AI.getFood());
		
//		Game
		return clone;
//		private PlayerListener playerListener;
	}
	
	private static void endTurn(SmartAgent ai) {
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
	}
	
	// Test
	public static void main(String[] args) throws NotEnoughGoldException, BuildingInCoolDownException, MaxLevelException, MaxRecruitedException {
		
//		int[] moves = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};
//		
//		Player enemey = new Player("HI");
//		City Cairo = new City("Cairo");
//		enemey.getControlledCities().add(Cairo);
//		enemey.setTreasury(100);
//		
//		SmartAgent ai = new SmartAgent(enemey,game);
//		
//		ai.setTreasury(50000);
//		City rome = new City("Rome");
//		ai.getControlledCities().add(rome);
//		Army a = new Army(ai.getControlledCities().get(0).getName());
//		a.addUnit("Infantry", 1);
//		a.addUnit("Infantry", 1);
//		a.addUnit("Infantry", 1);
//		a.addUnit("Infantry", 1);
//		a.addUnit("Infantry", 1);
//		a.addUnit("Infantry", 1);
//		a.addUnit("Infantry", 1);
//		a.addUnit("Infantry", 1);
//		a.addUnit("Infantry", 1);
//		a.addUnit("Infantry", 1);
//		ai.getControlledArmies().add(a);
		
//		ai.getControlledCities().get(0).getDefendingArmy().addUnit("Archer", 3);
//		ai.getControlledCities().get(0).getDefendingArmy().getUnits().get(0).setCurrentSoldierCount(10);
//		ai.getControlledCities().get(0).getDefendingArmy().addUnit("Infantry", 3);
//		ai.getControlledCities().get(0).getDefendingArmy().addUnit("Cavalry", 3);
//		System.out.println(SmartAgent.positonEval(ai));
		
//		System.out.println(Arrays.toString(ai.possibleMoves()));
//		System.out.println(Arrays.toString(ai.genReal(ai.possibleMoves())));
//		System.out.println("-------------------------");
		
//		rome.getEconomicalBuildings().add(new Farm());
//		rome.getEconomicalBuildings().add(new Market());
//		rome.getMilitaryBuildings().add(new ArcheryRange());
//		rome.getMilitaryBuildings().add(new Barracks());
//		rome.getMilitaryBuildings().add(new Stable());
		
//		System.out.println(Arrays.toString(ai.possibleMoves()));
//		System.out.println(Arrays.toString(ai.genReal(ai.possibleMoves())));
//		System.out.println("-------------------------");
		
//		System.out.println(ai.canBuild("Farm", 0));
//		System.out.println(ai.canBuild("Market", 0));
//		System.out.println(ai.canBuild("ArcheryRange", 0));
//		System.out.println(ai.canBuild("Barracks", 0));
//		System.out.println(ai.canBuild("Stable", 0));
//		
//		System.out.println("-----------------------");
		
//		System.out.println(ai.canUpgrade("Farm", 0));
//		System.out.println(ai.canUpgrade("Market", 0));
//		System.out.println(ai.canUpgrade("ArcheryRange", 0));
//		System.out.println(ai.canUpgrade("Barracks", 0));
//		System.out.println(ai.canUpgrade("Stable", 0));
		
//		ArrayList<Building> buildings = new ArrayList<Building>();
//		for(Building b : rome.getEconomicalBuildings()) {
//			buildings.add(b);
//		}
//		for(Building b : rome.getMilitaryBuildings()) {
//			buildings.add(b);
//		}
//		for(Building b : buildings) {
//			b.setCoolDown(false);
//		}
		
//		System.out.println(Arrays.toString(ai.possibleMoves()));
//		System.out.println(Arrays.toString(ai.genReal(ai.possibleMoves())));
//		System.out.println("-------------------------");
//		System.out.println("-----------------------");
//		System.out.println(ai.canUpgrade("Farm", 0));
//		System.out.println(ai.canUpgrade("Market", 0));
//		System.out.println(ai.canUpgrade("ArcheryRange", 0));
//		System.out.println(ai.canUpgrade("Barracks", 0));
//		System.out.println(ai.canUpgrade("Stable", 0));
//		for(Building b : buildings) {
//			b.upgrade();
//			b.setCoolDown(false);
//			b.upgrade();
//			b.setCoolDown(false);
//		}
//		System.out.println(Arrays.toString(ai.possibleMoves()));
//		System.out.println(Arrays.toString(ai.genReal(ai.possibleMoves())));
//		System.out.println(compare(ai.possibleMoves(),ai.possibleMoves()));
//		System.out.println("-------------------------");
//		System.out.println("-----------------------");
//		System.out.println(ai.canUpgrade("Farm", 0));
//		System.out.println(ai.canUpgrade("Market", 0));
//		System.out.println(ai.canUpgrade("ArcheryRange", 0));
//		System.out.println(ai.canUpgrade("Barracks", 0));
//		System.out.println(ai.canUpgrade("Stable", 0));
		
//		System.out.println(ai.canRecruit("Archer", 3, 0));
//		System.out.println(ai.canRecruit("Cavalry", 3, 0));
//		System.out.println(ai.canRecruit("Infantry", 3, 0));
		
		
//		System.out.println(Arrays.toString(ai.possibleMoves()));
//		System.out.println(Arrays.toString(ai.genReal(ai.possibleMoves())));
//		System.out.println("-------------------------");
		
		
		
//		System.out.println(Arrays.toString(ai.possibleMoves()));
//		moves = ai.possibleMoves();
//		System.out.println("----------------");
//		System.out.println(a.getTarget());
//		ai.execute(moves);
//		System.out.println(a.getTarget());
		
		
//		ArrayList<int[]> allMoves = ai.generateMoves();
//		for(int[] array : allMoves) {
//			System.out.println(Arrays.toString(array));
//		}
//		System.out.println(minmax(ai,3,0));
//		
		
	}

}
