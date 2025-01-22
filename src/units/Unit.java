package units;

import exceptions.FriendlyFireException;

public abstract class Unit {
	
	// Attributes
	private int level; // Read only
	private int maxSoldierCount; // Read only
	private int currentSoldierCount;
	private double idleUpkeep; // Read only
	private double marchingUpkeep; // Read only
	private double siegeUpkeep; // Read only
	private Army parentArmy;
	
	public int getLevel() {
		return level;
	}
	
	public int getMaxSoldierCount() {
		return maxSoldierCount;
	}
	
	public int getCurrentSoldierCount() {
		return currentSoldierCount;
	}
	public void setCurrentSoldierCount(int currentSoldierCount) {
		this.currentSoldierCount = currentSoldierCount;
	}

	public double getIdleUpkeep() {
		return idleUpkeep;
	}

	public double getMarchingUpkeep() {
		return marchingUpkeep;
	}

	public double getSiegeUpkeep() {
		return siegeUpkeep;
	}
	
	public Army getParentArmy() {
		return parentArmy;
	}
	public void setParentArmy(Army parentArmy) {
		this.parentArmy = parentArmy;
	}

	// Constructor
	public Unit(int level,int maxSoldierCount,double idleUpkeep, double marchingUpkeep,double siegeUpkeep) {
		this.level = level;
		this.maxSoldierCount = maxSoldierCount;
		this.currentSoldierCount = maxSoldierCount;
		this.idleUpkeep = idleUpkeep;
		this.marchingUpkeep = marchingUpkeep;
		this.siegeUpkeep = siegeUpkeep;	
	}
	
	// Methods
	public int attack(Unit target) throws FriendlyFireException {
		if(this.parentArmy.equals(target.parentArmy))
			throw new FriendlyFireException("Target is of the same army!");
		double factor = getFactor(target);
		int damage = effDamage(target);
		target.currentSoldierCount -= (int)(factor * currentSoldierCount);
		target.getParentArmy().handleAttackedUnit(target);
		return damage;
	}
	
	public int effDamage(Unit target) {
		int damage = (int)(getFactor(target)*currentSoldierCount);
		int effDamage;
		if(target.currentSoldierCount >= damage)
			effDamage = damage;
		else
			effDamage = target.currentSoldierCount;
		return effDamage;
	}
	
	public static Unit clone(Unit u) {
		Unit clone = null;
		if(u instanceof Archer) {
			clone = createUnit("Archer",u.getLevel());
			clone.setCurrentSoldierCount(u.getCurrentSoldierCount());
			clone.setParentArmy(u.getParentArmy());
		}
		if(u instanceof Infantry) {
			clone = createUnit("Infantry",u.getLevel());
			clone.setCurrentSoldierCount(u.getCurrentSoldierCount());
			clone.setParentArmy(u.getParentArmy());
		}
		if(u instanceof Cavalry) {
			clone = createUnit("Cavalry",u.getLevel());
			clone.setCurrentSoldierCount(u.getCurrentSoldierCount());
			clone.setParentArmy(u.getParentArmy());
		}
		return clone;
	}

	protected abstract double getFactor(Unit target);
	
	public static Unit createUnit(String type, int level) {
		Unit unit = null;
		switch(type) {
			case "Archer":
				switch(level) {
					case 1: unit = new Archer(1,60,0.4,0.5,0.6);break;
					case 2: unit = new Archer(2,60,0.4,0.5,0.6);break;
					case 3: unit = new Archer(3,70,0.5,0.6,0.7);break;
				} break;
	
			case "Infantry":
				switch(level) {
					case 1: unit = new Infantry(1,50,0.5,0.6,0.7);break;
					case 2: unit = new Infantry(2,50,0.5,0.6,0.7);break;
					case 3: unit = new Infantry(3,60,0.6,0.7,0.8);break;
				} break;
		
			case "Cavalry":
				switch(level) {
					case 1: unit = new Cavalry(1,40,0.6,0.7,0.75);break;
					case 2: unit = new Cavalry(2,40,0.6,0.7,0.75);break;
					case 3: unit = new Cavalry(3,60,0.7,0.8,0.9);break;
				} break;
		}
		return unit;
	}
}
