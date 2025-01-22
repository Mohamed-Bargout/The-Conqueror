package units;

public class Infantry extends Unit{

	public Infantry(int level, int maxSoldierCount, double idleUpkeep, double marchingUpkeep, double siegeUpkeep) {
		super(level, maxSoldierCount, idleUpkeep, marchingUpkeep, siegeUpkeep);
	}

	protected double getFactor(Unit target) {
		if(target instanceof Archer) {
			switch(getLevel()) {
				case 1: return 0.3;
				case 2: return 0.4;
				case 3: return 0.5;
			}
		}
		else if(target instanceof Infantry) {
			switch(getLevel()) {
				case 1: return 0.1;
				case 2: return 0.2;
				case 3: return 0.3;
			}
		}
		else if(target instanceof Cavalry) {
			switch(getLevel()) {
				case 1: return 0.1;
				case 2: return 0.2;
				case 3: return 0.25;
			}
		}
		return 0;
	}
	
}
