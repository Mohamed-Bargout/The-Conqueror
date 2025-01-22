package buildings;

import exceptions.BuildingInCoolDownException;
import exceptions.MaxLevelException;

public abstract class Building {
	
	// Attributes
	private int cost; // Read only
	private int level = 1;
	private int upgradeCost;
	private boolean coolDown = true;
	private int baseScore;
	
	public int getCost() {
		return cost;
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getUpgradeCost() {
		return upgradeCost;
	}
	public void setUpgradeCost(int upgradeCost) {
		this.upgradeCost = upgradeCost;
	}
	
	public boolean isCoolDown() {
		return coolDown;
	}
	public void setCoolDown(boolean coolDown) {
		this.coolDown = coolDown;
	}
	
	public int getScore() {
		return baseScore * level;
	}
	public void setBaseScore(int baseScore) {
		this.baseScore = baseScore;
	}
	
	// Constructors
	public Building(int cost, int upgradeCost) {
		this.cost = cost;
		this.upgradeCost = upgradeCost;
	}
	
	// Methods
	public void upgrade() throws BuildingInCoolDownException, MaxLevelException {
		if(level==3)
			throw new MaxLevelException("Building is already at maximum level");
		if(coolDown)
			throw new BuildingInCoolDownException("Building is in cool down");
		level++;
		coolDown = true;
	}
	
public static Building clone(Building b) {
		
		if(b instanceof Farm) {
			Farm clone = new Farm();
			clone.setCoolDown(b.isCoolDown());
			clone.setLevel(b.getLevel());
			clone.setUpgradeCost(b.getUpgradeCost());
			return clone;
		}
		if(b instanceof Market) {
			Market clone = new Market();
			clone.setCoolDown(b.isCoolDown());
			clone.setLevel(b.getLevel());
			clone.setUpgradeCost(b.getUpgradeCost());
			return clone;
		}
		if(b instanceof ArcheryRange) {
			ArcheryRange clone = new ArcheryRange();
			clone.setCoolDown(b.isCoolDown());
			clone.setLevel(b.getLevel());
			clone.setUpgradeCost(b.getUpgradeCost());
			clone.setRecruitmentCost(((ArcheryRange) b).getRecruitmentCost());
			return clone;
		}
		if(b instanceof Barracks) {
			Barracks clone = new Barracks();
			clone.setCoolDown(b.isCoolDown());
			clone.setLevel(b.getLevel());
			clone.setUpgradeCost(b.getUpgradeCost());
			clone.setRecruitmentCost(((Barracks) b).getRecruitmentCost());
			return clone;
		}
		if(b instanceof Stable) {
			Stable clone = new Stable();
			clone.setCoolDown(b.isCoolDown());
			clone.setLevel(b.getLevel());
			clone.setUpgradeCost(b.getUpgradeCost());
			clone.setRecruitmentCost(((Stable) b).getRecruitmentCost());
			return clone;
		}
		return null;
	}
}
