package buildings;

import exceptions.BuildingInCoolDownException;
import exceptions.MaxLevelException;

public class Market extends EconomicBuilding {
	
	// Constructor
	public Market() {
		super(1500, 700);
		setBaseScore(150);
	}
	
	// Methods
	public void upgrade() throws BuildingInCoolDownException, MaxLevelException {
		super.upgrade();
		if(getLevel()==2)
			setUpgradeCost(1000);
		else
			setUpgradeCost(0);
	}

	public int harvest() {
		switch(getLevel()) {
			case 1: return 1000;
			case 2: return 1500;
			case 3: return 2000;
			default: return 0;
		}
	}
	
}
