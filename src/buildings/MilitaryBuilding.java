package buildings;

import exceptions.BuildingInCoolDownException;
import exceptions.MaxRecruitedException;
import units.Unit;

public abstract class MilitaryBuilding extends Building {
	
	// Attributes
	private int recruitmentCost;
	private int currentRecruit;
	private final int maxRecruit = 3; // Read only
	
	public int getRecruitmentCost() {
		return recruitmentCost;
	}
	public void setRecruitmentCost(int recruitmentCost) {
		this.recruitmentCost = recruitmentCost;
	}

	public int getCurrentRecruit() {
		return currentRecruit;
	}
	public void setCurrentRecruit(int currentRecruit) {
		this.currentRecruit = currentRecruit;
	}

	public int getMaxRecruit() {
		return maxRecruit;
	}

	// Constructor
	public MilitaryBuilding(int cost, int upgradeCost, int recruitmentCost) {
		super(cost, upgradeCost);
		this.recruitmentCost = recruitmentCost;
	}
	
	// Methods
	public abstract Unit recruit() throws BuildingInCoolDownException,
	MaxRecruitedException;
}
