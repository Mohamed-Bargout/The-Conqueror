package view;

import units.Army;
import units.Unit;

public interface UnitArmyListener {

	void onInitiateArmy(String cityName, Unit unit);
	void onRelocateUnit(Unit unit);
	void onSelectArmy(Army army);
}
