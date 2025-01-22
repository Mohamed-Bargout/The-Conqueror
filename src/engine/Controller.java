package engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import exceptions.BuildingInCoolDownException;
import exceptions.FriendlyCityException;
import exceptions.FriendlyFireException;
import exceptions.MaxCapacityException;
import exceptions.MaxLevelException;
import exceptions.MaxRecruitedException;
import exceptions.NotEnoughGoldException;
import exceptions.TargetNotReachedException;
import units.Army;
import units.Status;
import units.Unit;
import view.ArmySelector;
import view.BattleView;
import view.BottomPanel;
import view.CityArmyView;
import view.CityWindow;
import view.DistancesView;
import view.ErrorDialog;
import view.EventWindowListener;
import view.ForeignCityArmyView;
import view.GameOverView;
import view.StartWindow;
import view.TopPanel;
import view.WindowClosingListener;
import view.WorldWindow;

public class Controller implements ActionListener, GameListener, EventWindowListener, WindowClosingListener {

	private StartWindow gameWindow;
	private WorldWindow worldWindow;
	private Game game;
	private TopPanel topPanel;
	private BottomPanel bottomPanel;
	private BattleView battleView;
	private City siegeCity;
	private Army attackers;
	private Army defenders;
	private Army selectedArmy;
	private String intent;
	private City selectedCity;
	private Unit relocatedUnit;
	private String foreignCityName;
	private Player player;
	private SmartAgent ai;
	
	private Controller() {
		gameWindow = new StartWindow();
		gameWindow.getPlayButton().addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Play")) {
			try {
				String playerName = gameWindow.getPlayerNameField().getText();
				game = new Game(playerName,
						gameWindow.getCityChoiceField().getSelectedItem().toString(),
						gameWindow.getFightAI().getSelectedItem().equals("Yes"));
				game.setGameListener(this);
				player = game.getPlayer();
				ai = game.getAi();
				if(ai!=null)
					ai.setDepth((Integer)gameWindow.getLevelAI().getSelectedItem());
				
				topPanel = new TopPanel(playerName);
				bottomPanel = new BottomPanel();
				bottomPanel.getEndTurnButton().addActionListener(this);
				bottomPanel.getMarchingArmiesButton().addActionListener(this);
				bottomPanel.getDistancesButton().addActionListener(this);
			} catch (IOException e1) {}
			gameWindow.dispose();
			worldWindow = new WorldWindow(topPanel,bottomPanel);
			worldWindow.getWorldMapPanel().getCairoButton().addActionListener(this);
			worldWindow.getWorldMapPanel().getSpartaButton().addActionListener(this);
			worldWindow.getWorldMapPanel().getRomeButton().addActionListener(this);
			updateCityButtons();
		}
		else if(e.getActionCommand().equals("End Turn")) {
			game.endTurn();
			if(game.isGameOver()) {
				if(game.getAvailableCities().size()==game.getPlayer().getControlledCities().size()) {
					new GameOverView(0);
					worldWindow.dispose();
				}
				else if(player.getControlledCities().size()==0){
					new GameOverView(1);
					worldWindow.dispose();
				}
				else {
					new GameOverView(2);
					worldWindow.dispose();
				}
			}
		}
		else if(e.getActionCommand().equals("Marching Armies")) {
			ArmySelector as = new ArmySelector(topPanel,game.getPlayer().getMarchingArmies(),true,false);
			as.setWindowClosingListener(this);
			as.getSelectButton().setVisible(false);
			worldWindow.setVisible(false);
		}
		else if(e.getActionCommand().equals("Distances")) {
			DistancesView dv = new DistancesView(worldWindow, game.getDistances());
		}
		else if(e.getActionCommand().equals("Manual")) {
			JPanel battlePanel = battleView.getBattlePanel();
			battlePanel.removeAll();
			battlePanel.setLayout(new BoxLayout(battlePanel,BoxLayout.X_AXIS));
			battlePanel.add(battleView.getAttackersPanel());
			battlePanel.add(battleView.getDefendersPanel());
			battlePanel.add(battleView.getLogArea());
			battleView.updateAttackersPanel(attackers);
			battleView.updateDefendersPanel(defenders);
			battleView.add(battleView.getAttackButton(),BorderLayout.SOUTH);
			battleView.getAttackButton().addActionListener(this);
			battleView.refresh();
		}
		else if(e.getActionCommand().equals("Auto")) {
					try {
						if(player.getControlledCities().contains(siegeCity))
							game.autoResolve(defenders, attackers, game.getAi());
						else
							game.autoResolve(attackers, defenders, game.getPlayer());
						if(attackers.getUnits().size()==0) {
							battleView.getBattlePanel().removeAll();
							battleView.getAutoResultLabel().setText("You lost the battle...");
							battleView.getBattlePanel().add(Box.createVerticalGlue());
							battleView.getBattlePanel().add(battleView.getAutoResultLabel());
							battleView.getBattlePanel().add(Box.createVerticalGlue());
							defenders.setCurrentStatus(Status.IDLE);
							defenders.setTurnsSieging(0);
							player.getControlledCities().remove(siegeCity);
						}
						else {
							battleView.getBattlePanel().removeAll();
							battleView.getAutoResultLabel().setText("You won the battle!");
							battleView.getBattlePanel().add(Box.createVerticalGlue());
							battleView.getBattlePanel().add(battleView.getAutoResultLabel());
							battleView.getBattlePanel().add(Box.createVerticalGlue());
							attackers.setCurrentStatus(Status.IDLE);
							attackers.setTurnsSieging(0);
							if(ai!=null)
								ai.getControlledCities().remove(siegeCity);
						}
						battleView.done();
						battleView.refresh();
						updateCityButtons();
					} catch (FriendlyFireException ff) {
						new ErrorDialog(battleView, ff.getMessage());
					}
		}
		else if(e.getActionCommand().equals("Attack")) {
			JButton attackerButton = battleView.getSelectedAttacker();
			JButton defenderButton = battleView.getSelectedDefender();
			if(attackerButton == null || defenderButton == null) {
				if(defenders.getUnits().size()==0) {
					battleView.clearLog();
					battleView.writeToLog("You Won!");
					JButton doneButton = battleView.getAttackButton();
					doneButton.setActionCommand("Done");
					doneButton.setText("Done");
					battleView.done();
					battleView.refresh();
					if(!player.getControlledCities().contains(siegeCity))
						game.occupy(attackers, attackers.getCurrentLocation(), game.getPlayer());
					if(ai!=null)
						ai.getControlledCities().remove(selectedCity);
					updateCityButtons();
					return;
				}
				if(attackers.getUnits().size()==0) {
					battleView.clearLog();
					battleView.writeToLog("You Lost!");
					JButton doneButton = battleView.getAttackButton();
					doneButton.setActionCommand("Done");
					doneButton.setText("Done");
					battleView.done();
					battleView.refresh();
					if(player.getControlledCities().contains(siegeCity) && ai != null) {
						game.occupy(defenders, siegeCity.getName(), ai);
						player.getControlledCities().remove(siegeCity);
					}	
					updateCityButtons();
					return;
				}
			}
			for(int i=0;i<battleView.getAttackersPanel().getComponents().length;i++)
				if(attackerButton == battleView.getAttackersPanel().getComponents()[i])
					for(int j=0;j<battleView.getDefendersPanel().getComponents().length;j++)
						if(defenderButton == battleView.getDefendersPanel().getComponents()[j]) {
							try {
								//Random rand = new Random();
								int damageDealt = attackers.getUnits().get(i).attack(defenders.getUnits().get(j));
								battleView.writeToLog("Dealt "+damageDealt+" damage");
								battleView.updateAttackersPanel(attackers);
								battleView.updateDefendersPanel(defenders);
								battleView.refresh();
								if(defenders.getUnits().size()==0) {
									battleView.clearLog();
									battleView.writeToLog("You Won!");
									JButton doneButton = battleView.getAttackButton();
									doneButton.setActionCommand("Done");
									doneButton.setText("Done");
									battleView.done();
									battleView.refresh();
									if(!player.getControlledCities().contains(siegeCity))
										game.occupy(attackers, attackers.getCurrentLocation(), game.getPlayer());
									if(ai!=null)
										ai.getControlledCities().remove(selectedCity);
									updateCityButtons();
									return;
								}
								Unit[] maxUnits = selectUnits(defenders, attackers);
								//int damageTaken = defenders.getUnits().get(rand.nextInt(defenders.getUnits().size())).attack(attackers.getUnits().get(rand.nextInt(attackers.getUnits().size())));
								Unit defending = maxUnits[0];
								Unit attacking = maxUnits[1];
								int damageTaken = defending.attack(attacking);
								battleView.writeToLog("Took "+damageTaken+" damage");
								battleView.setSelectedAttacker(null);
								battleView.setSelectedDefender(null);
								battleView.updateAttackersPanel(attackers);
								battleView.updateDefendersPanel(defenders);
								battleView.refresh();
								if(attackers.getUnits().size()==0) {
									battleView.clearLog();
									battleView.writeToLog("You Lost!");
									JButton doneButton = battleView.getAttackButton();
									doneButton.setActionCommand("Done");
									doneButton.setText("Done");
									battleView.done();
									battleView.refresh();
									if(player.getControlledCities().contains(siegeCity) && ai != null) {
										game.occupy(defenders, siegeCity.getName(), ai);
										player.getControlledCities().remove(siegeCity);
									}	
									updateCityButtons();
									return;
								}
							} catch (FriendlyFireException ff) {
								new ErrorDialog(battleView, ff.getMessage());
							}
						}
		}
		else if(e.getActionCommand().equals("Done")) {
			battleView.dispose();
			onWindowClosed();
		}
		else {
			for(City city : game.getAvailableCities()) {
				if(city.getName().equals(e.getActionCommand())) {
					if(game.getPlayer().getControlledCities().contains(city)) {
						CityWindow cityWindow = new CityWindow(topPanel, city, game.getPlayer().getLocalArmies(city.getName()),true);
						cityWindow.setEventWindowListener(this);
						worldWindow.setVisible(false);
						return;
					}
					else {
						CityWindow cityWindow = new CityWindow(topPanel, city, game.getPlayer().getLocalArmies(city.getName()),false);
						cityWindow.setEventWindowListener(this);
						worldWindow.setVisible(false);
						return;
					}
				}
			}
		}	
	}
	
	public static void main(String[] args) {
		new Controller();
	}
	
	private Unit[] selectUnits(Army attacking, Army defending) {
		Unit[] selection = new Unit[2];
		int maxDamage = 0;
		int soldierCount = 0;
		for(Unit a : attacking.getUnits())
			for(Unit d : defending.getUnits())
				if(a.effDamage(d) > maxDamage || maxDamage == 0) {
					maxDamage = a.effDamage(d);
					selection[0] = a;
					selection[1] = d;
					soldierCount = d.getCurrentSoldierCount();
				}
				else if(a.effDamage(d) == maxDamage) {
					if(d.getCurrentSoldierCount() > soldierCount) {
						maxDamage = a.effDamage(d);
						selection[0] = a;
						selection[1] = d;
						soldierCount = d.getCurrentSoldierCount();
					}
					else if(d.getCurrentSoldierCount() == soldierCount && new Random().nextInt(2)==0) {
						maxDamage = a.effDamage(d);
						selection[0] = a;
						selection[1] = d;
					}
				}
		return selection;
	}

	public void onFoodChanged(double food) {
		topPanel.onFoodChanged(food);
	}

	public void onTreasuryChanged(double treasury) {
		topPanel.onTreasuryChanged(treasury);
	}

	public void onTurnCountChanged(int turn) {
		topPanel.onCurrentTurnChanged(turn);
	}

	public void onBuild(String type, String cityName) throws NotEnoughGoldException {
		game.getPlayer().build(type, cityName);
	}

	public void onUpgrade(String type, String cityName) throws NotEnoughGoldException, BuildingInCoolDownException, MaxLevelException {
		game.getPlayer().upgradeBuildingByName(type, cityName);
	}
	
	public void onRecruit(String type, String cityName) throws BuildingInCoolDownException, MaxRecruitedException, NotEnoughGoldException {
		game.getPlayer().recruitUnit(type, cityName);
	}
	
	public void onWindowClosed() {
		worldWindow.setVisible(true);
		worldWindow.add(topPanel,BorderLayout.NORTH);
		worldWindow.refresh();
	}
	
	public void onInitiateArmy(String cityName, Unit unit) {
		for(City city : game.getPlayer().getControlledCities())
			if(city.getName().equals(cityName))
				game.getPlayer().initiateArmy(city, unit);
		ArrayList<Army> localArmies = game.getPlayer().getLocalArmies(cityName);
		for(City city : game.getPlayer().getControlledCities())
			if(city.getName().equals(cityName))
				localArmies.add(0,city.getDefendingArmy());
		CityWindow.localArmies = localArmies;
		CityArmyView.localArmies = localArmies;
		ArmySelector.armies = localArmies;
	}
	
	public void onRelocateUnit(Unit unit, String cityName) {
		ArmySelector as = new ArmySelector(topPanel, game.getPlayer().getLocalArmies(cityName),false,false);
		as.setEventWindowListener(this);
		as.setWindowClosingListener(this);
		worldWindow.setVisible(false);
		relocatedUnit = unit;
		intent = "Relocate";
		foreignCityName = cityName;
	}

	public void onSiegeLimitReached(City city) {
		if(player.getControlledCities().contains(city)) {
			for(Army army : game.getAi().getSiegingArmies())
				if(army.getCurrentLocation().equals(city.getName())) {
					worldWindow.setVisible(false);
					battleView = new BattleView(topPanel);
					battleView.getChoiceLabel().setText(city.getName()+" has been under siege for 3 turns. You must now enter battle");
					battleView.getManualButton().addActionListener(this);
					battleView.getAutoButton().addActionListener(this);
					battleView.setWindowClosingListener(this);
					siegeCity = city;
					attackers = city.getDefendingArmy();
					defenders = army;
					return;
				}
		}
		for(Army army : game.getPlayer().getSiegingArmies())
			if(army.getCurrentLocation().equals(city.getName())) {
				worldWindow.setVisible(false);
				battleView = new BattleView(topPanel);
				battleView.getChoiceLabel().setText(city.getName()+" has been under siege for 3 turns. You must now enter battle");
				battleView.getManualButton().addActionListener(this);
				battleView.getAutoButton().addActionListener(this);
				battleView.setWindowClosingListener(this);
				siegeCity = city;
				attackers = army;
				defenders = city.getDefendingArmy();
			}
		
	}

	public void onBattle(City city) {
		if(player.getControlledCities().contains(city)) {
			if(!city.isUnderSiege()) {
				onWindowClosed();
				return;
			}
			attackers = city.getDefendingArmy();
			for(Army army : game.getAi().getSiegingArmies())
				if(army.getCurrentLocation().equals(city.getName()))
					defenders = army;
			siegeCity = city;
			selectedCity = city;
			worldWindow.setVisible(false);
			battleView = new BattleView(topPanel);
			battleView.getChoiceLabel().setText("");
			battleView.getManualButton().addActionListener(this);
			battleView.getAutoButton().addActionListener(this);
			battleView.setWindowClosingListener(this);
			return;
		}
		for(Army army : game.getPlayer().getSiegingArmies())
			if(army.getCurrentLocation().equals(city.getName())) {
				worldWindow.setVisible(false);
				battleView = new BattleView(topPanel);
				battleView.getChoiceLabel().setText("");
				battleView.getManualButton().addActionListener(this);
				battleView.getAutoButton().addActionListener(this);
				battleView.setWindowClosingListener(this);
				siegeCity = city;
				attackers = army;
				defenders = city.getDefendingArmy();
				return;
			}
		ArmySelector as = new ArmySelector(topPanel, game.getPlayer().getLocalArmies(city.getName()),false,false);
		as.setEventWindowListener(this);
		as.setWindowClosingListener(this);
		worldWindow.setVisible(false);
		selectedCity = city;
		intent = "Battle";
	}

	public void onLaySiege(City city) {
		ArmySelector as = new ArmySelector(topPanel, game.getPlayer().getLocalArmies(city.getName()),false,false);
		as.setEventWindowListener(this);
		as.setWindowClosingListener(this);
		worldWindow.setVisible(false);
		intent = "Siege";
		selectedCity = city;
	}

	public void onTargetCity(City city) {
		ArrayList<Army> armyList = new ArrayList<Army>();
		for(City c : game.getAvailableCities())
			if(!city.getName().equals(c.getName()))
				armyList.addAll(game.getPlayer().getLocalArmies(c.getName()));
		ArmySelector as = new ArmySelector(topPanel, armyList,false,false);
		as.setEventWindowListener(this);
		as.setWindowClosingListener(this);
		worldWindow.setVisible(false);
		intent = "Target";
		selectedCity = city;
	}

	public void onSelectArmy(Army army) {
		selectedArmy = army;
		if("Battle".equals(intent)) {
					worldWindow.setVisible(false);
					battleView = new BattleView(topPanel);
					battleView.getChoiceLabel().setText("");
					battleView.getManualButton().addActionListener(this);
					battleView.getAutoButton().addActionListener(this);
					battleView.setWindowClosingListener(this);
					siegeCity = selectedCity;
					attackers = selectedArmy;
					defenders = selectedCity.getDefendingArmy();
					return;
		}
		else if("Siege".equals(intent)) {
			try {
				onWindowClosed();
				game.getPlayer().laySiege(army, selectedCity);
			} catch (TargetNotReachedException e) {
				new ErrorDialog(worldWindow,e.getMessage());
			} catch (FriendlyCityException e) {
				new ErrorDialog(worldWindow,e.getMessage());
			}
		}
		else if("Target".equals(intent)) {
			if(army.getCurrentStatus()==Status.IDLE);
				game.targetCity(army, selectedCity.getName());
			onWindowClosed();
		}
		else if("Relocate".equals(intent)) {
			try {
				army.relocateUnit(relocatedUnit);
				onWindowClosed();
				ForeignCityArmyView.localArmies = game.getPlayer().getLocalArmies(foreignCityName);
			} catch (MaxCapacityException mc) {
				new ErrorDialog(worldWindow, mc.getMessage());
				onWindowClosed();
			}
		}
	}
	
	private void updateCityButtons() {
		if(ai!=null) {
			for(City c : player.getControlledCities()) {
				worldWindow.getWorldMapPanel().getCityButton(c.getName()).setBackground(new Color(0,170,0));
				if(c.isUnderSiege())
					worldWindow.getWorldMapPanel().getCityButton(c.getName()).setBackground(new Color(255,170,0));
			}
			for(City c : ai.getControlledCities()) {
				worldWindow.getWorldMapPanel().getCityButton(c.getName()).setBackground(new Color(250,0,0));
			}
		}
	}
	
	public void onCityReached(City city) {
		if(player.getControlledCities().contains(city))
			new ErrorDialog(worldWindow,city.getName()+" is under siege by an opponent army!");
		updateCityButtons();
	}
	
}
