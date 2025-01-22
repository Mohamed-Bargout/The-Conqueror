package buildings;

import exceptions.BuildingInCoolDownException;
import exceptions.MaxLevelException;
import exceptions.MaxRecruitedException;
import units.Unit;

public class Barracks extends MilitaryBuilding {
	
	// Constructor
	public Barracks() {
		super(2000, 1000, 500);
		setBaseScore(50);
	}
	
	// Methods
	public void upgrade() throws BuildingInCoolDownException, MaxLevelException {
		super.upgrade();
		if(getLevel()==2) {
			setUpgradeCost(1500);
			setRecruitmentCost(550);
		}
		else {
			setUpgradeCost(0);
			setRecruitmentCost(600);
		}
	}
	
	public Unit recruit() throws BuildingInCoolDownException, MaxRecruitedException {
		if(getCurrentRecruit()>=getMaxRecruit()) {
			throw new MaxRecruitedException("The maximum number of units has been recruited");
		}
		if(isCoolDown()) {
			throw new BuildingInCoolDownException("Building is in cool down");
		}
		setCurrentRecruit(getCurrentRecruit()+1);
		return Unit.createUnit("Infantry", getLevel());
	}
	
}
