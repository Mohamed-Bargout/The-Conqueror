package buildings;

import exceptions.BuildingInCoolDownException;
import exceptions.MaxLevelException;
import exceptions.MaxRecruitedException;
import units.Unit;

public class Stable extends MilitaryBuilding {
	
	// Constructor
	public Stable() {
		super(2500, 1500, 600);
		setBaseScore(70);
	}
	
	// Methods
	public void upgrade() throws BuildingInCoolDownException, MaxLevelException {
		super.upgrade();
		if(getLevel()==2) {
			setUpgradeCost(2000);
			setRecruitmentCost(650);
		}
		else {
			setUpgradeCost(0);
			setRecruitmentCost(700);
		}
	}

	public Unit recruit() throws BuildingInCoolDownException, MaxRecruitedException {
		if(getCurrentRecruit()>=getMaxRecruit()) {
			throw new MaxRecruitedException("The maximum number of units has been recruited");
		}
		if(isCoolDown()) {
			throw new BuildingInCoolDownException("Building is in cooldown");
		}
		setCurrentRecruit(getCurrentRecruit()+1);
		return Unit.createUnit("Cavalry", getLevel()); 
	}
}
