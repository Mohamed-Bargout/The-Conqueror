package buildings;

import exceptions.BuildingInCoolDownException;
import exceptions.MaxLevelException;
import exceptions.MaxRecruitedException;
import units.Unit;

public class ArcheryRange extends MilitaryBuilding {
	
	// Constructor
	public ArcheryRange() {
		super(1500, 800, 400);
		setBaseScore(30);
	}
	
	// Methods
	public void upgrade() throws BuildingInCoolDownException, MaxLevelException {
		super.upgrade();
		if(getLevel()==2) {
			setUpgradeCost(700);
			setRecruitmentCost(450);
		}
		else {
			setUpgradeCost(0);
			setRecruitmentCost(500);
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
		return Unit.createUnit("Archer", getLevel());
	}
	
}
