package buildings;

import exceptions.BuildingInCoolDownException;
import exceptions.MaxLevelException;

public class Farm extends EconomicBuilding {
	
	// Constructor
	public Farm() {
		super(1000, 500);
		setBaseScore(100);
	}
	
	// Methods
	public void upgrade() throws BuildingInCoolDownException, MaxLevelException {
		super.upgrade();
		if(getLevel()==2)
			setUpgradeCost(700);
		else
			setUpgradeCost(0);
	}
	
	public int harvest() {
		switch(getLevel()) {
			case 1: return 500;
			case 2: return 700;
			case 3: return 1000;
			default: return 0;
		}
	}
	
}
