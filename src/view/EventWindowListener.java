package view;

import engine.City;
import exceptions.BuildingInCoolDownException;
import exceptions.MaxLevelException;
import exceptions.MaxRecruitedException;
import exceptions.NotEnoughGoldException;
import units.Army;
import units.Unit;

public interface EventWindowListener {
	void onBuild(String type, String cityName) throws NotEnoughGoldException;
	void onUpgrade(String type, String cityName) throws NotEnoughGoldException, BuildingInCoolDownException, MaxLevelException;
	void onRecruit(String type, String cityName) throws BuildingInCoolDownException, MaxRecruitedException, NotEnoughGoldException;
	void onWindowClosed();
	void onInitiateArmy(String cityName, Unit unit);
	void onRelocateUnit(Unit unit, String cityName);
	void onBattle(City city);
	void onLaySiege(City city);
	void onTargetCity(City city);
	void onSelectArmy(Army army);
}
