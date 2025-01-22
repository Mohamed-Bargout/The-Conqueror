package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;

import buildings.ArcheryRange;
import buildings.Barracks;
import buildings.EconomicBuilding;
import buildings.Farm;
import buildings.Market;
import buildings.MilitaryBuilding;
import buildings.Stable;
import engine.City;
import exceptions.BuildingInCoolDownException;
import exceptions.MaxLevelException;
import exceptions.MaxRecruitedException;
import exceptions.NotEnoughGoldException;
import units.Army;
import units.Unit;

public class CityWindow extends JFrame implements ActionListener, WindowClosingListener, UnitArmyListener{
	
	private City city;
	private TopPanel topPanel;
	private JButton barracks;
	private JButton archeryRange;
	private JButton stable;
	private JButton farm;
	private JButton market;
	private JButton buildButton;
	private JButton upgradeButton;
	private JButton recruitButton;
	private JButton targetCityButton;
	private JButton battleButton;
	private JButton selectedButton;
	private JButton localArmiesButton;
	public static ArrayList<Army> localArmies;
	private EventWindowListener eventWindowListener;
	
	public JButton getBuildButton() {
		return buildButton;
	}

	public JButton getUpgradeButton() {
		return upgradeButton;
	}


	public JButton getRecruitButton() {
		return recruitButton;
	}

	public CityWindow(TopPanel topPanel, City city, ArrayList<Army> localArmies, boolean isPlayerCity) {
		super();
		setTitle("The Conqueror - "+city.getName());
		setSize(800,600);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(topPanel,BorderLayout.NORTH);
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		this.topPanel = topPanel;
		this.city = city;
		CityWindow.localArmies = localArmies;
		if(isPlayerCity) {
			localArmies.add(0,city.getDefendingArmy());
			
			JPanel buildingsGrid = new JPanel(new GridLayout(2,3));
			barracks = new JButton("<html><em>Barracks</em><br>Cost: "+new Barracks().getCost());
			barracks.setActionCommand("Barracks");
			barracks.addActionListener(this);
			archeryRange = new JButton("<html><em>Archery Range</em><br>Cost: "+new ArcheryRange().getCost());
			archeryRange.setActionCommand("ArcheryRange");
			archeryRange.addActionListener(this);
			stable = new JButton("<html><em>Stable</em><br>Cost: "+new Stable().getCost());
			stable.setActionCommand("Stable");
			stable.addActionListener(this);
			
			farm = new JButton("<html><em>Farm</em><br>Cost: "+new Farm().getCost());
			farm.setActionCommand("Farm");
			farm.addActionListener(this);
			market = new JButton("<html><em>Market</em><br>Cost: "+new Market().getCost());
			market.setActionCommand("Market");
			market.addActionListener(this);
			
			localArmiesButton = new JButton("<html><div style='text-align:center;'>Defending / Local<br> Armies</div></html>");
			localArmiesButton.setActionCommand("Armies");
			localArmiesButton.addActionListener(this);
			
			updateBuildingsView();
			
			buildingsGrid.add(barracks);
			buildingsGrid.add(archeryRange);
			buildingsGrid.add(stable);
			buildingsGrid.add(farm);
			buildingsGrid.add(market);
			buildingsGrid.add(localArmiesButton);
			add(buildingsGrid,BorderLayout.CENTER);
			
			JPanel buttonsPanel = new JPanel();
			buildButton = new JButton("Build");
			buildButton.addActionListener(this);
			buttonsPanel.add(buildButton);
			upgradeButton = new JButton("Upgrade");
			upgradeButton.addActionListener(this);
			buttonsPanel.add(upgradeButton);
			recruitButton = new JButton("Recruit");
			recruitButton.addActionListener(this);
			buttonsPanel.add(recruitButton);
			targetCityButton = new JButton("Target City");
			targetCityButton.addActionListener(this);
			buttonsPanel.add(targetCityButton);
			battleButton = new JButton("Battle");
			battleButton.addActionListener(this);
			buttonsPanel.add(battleButton);
			add(buttonsPanel,BorderLayout.SOUTH);
		}
		else {
			JPanel attackPanel = new JPanel();
			attackPanel.setLayout(new BoxLayout(attackPanel,BoxLayout.X_AXIS));
			JButton target = new JButton("Target City");
			target.addActionListener(this);
			JButton siege = new JButton("Lay Siege");
			siege.addActionListener(this);
			JButton yourArmies = new JButton("Your Armies");
			yourArmies.addActionListener(this);
			JButton battleButton = new JButton("Battle");
			battleButton.addActionListener(this);
			target.setHorizontalAlignment(SwingConstants.CENTER);
			attackPanel.add(Box.createHorizontalGlue());
			attackPanel.add(target);
			attackPanel.add(siege);
			attackPanel.add(yourArmies);
			attackPanel.add(battleButton);
			attackPanel.add(Box.createHorizontalGlue());
			add(attackPanel,BorderLayout.CENTER);
		}
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				eventWindowListener.onWindowClosed();
				dispose();
			}
		});
		
		setVisible(true);
		refresh();
	}
	
	public JButton getSelectedButton() {
		return selectedButton;
	}
	
	public void setEventWindowListener(EventWindowListener eventWindowListener) {
		this.eventWindowListener = eventWindowListener;
	}
	
	public void refresh() {
		this.revalidate();
		this.repaint();
	}
	
	public void updateBuildingsView() {
		for(MilitaryBuilding mb : city.getMilitaryBuildings()) {
			if(mb instanceof Barracks)
				barracks.setText(String.format("<html><em>Barracks</em><br>Level: %d<br>Upgrade Cost: %d<br>Recruitement Cost: %d<br>Recruited: %d/%d<br>On Cooldown: %s",
						mb.getLevel(),mb.getUpgradeCost(),mb.getRecruitmentCost(),mb.getCurrentRecruit(),mb.getMaxRecruit(),(mb.isCoolDown()?"Yes":"No")));
			if(mb instanceof ArcheryRange)
				archeryRange.setText(String.format("<html><em>Archery Range</em><br>Level: %d<br>Upgrade Cost: %d<br>Recruitement Cost: %d<br>Recruited: %d/%d<br>On Cooldown: %s",
						mb.getLevel(),mb.getUpgradeCost(),mb.getRecruitmentCost(),mb.getCurrentRecruit(),mb.getMaxRecruit(),(mb.isCoolDown()?"Yes":"No")));
			if(mb instanceof Stable)
				stable.setText(String.format("<html><em>Stable</em><br>Level: %d<br>Upgrade Cost: %d<br>Recruitement Cost: %d<br>Recruited: %d/%d<br>On Cooldown: %s",
						mb.getLevel(),mb.getUpgradeCost(),mb.getRecruitmentCost(),mb.getCurrentRecruit(),mb.getMaxRecruit(),(mb.isCoolDown()?"Yes":"No")));
		}
		for(EconomicBuilding eb : city.getEconomicalBuildings()) {
			if(eb instanceof Farm)
				farm.setText(String.format("<html><em>Farm</em><br>Level: %d<br>Upgrade Cost: %d<br>On Cooldown: %s",
						eb.getLevel(),eb.getUpgradeCost(),(eb.isCoolDown()?"Yes":"No")));
			if(eb instanceof Market)
				market.setText(String.format("<html><em>Market</em><br>Level: %d<br>Upgrade Cost: %d<br>On Cooldown: %s",
						eb.getLevel(),eb.getUpgradeCost(),(eb.isCoolDown()?"Yes":"No")));
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Build")) {
			if(selectedButton != null) {
				try {
					eventWindowListener.onBuild(selectedButton.getActionCommand(), city.getName());
				}
				catch (NotEnoughGoldException ne) {
					new ErrorDialog(this, ne.getMessage());
				}
				updateBuildingsView();
			}
		}
		else if(e.getActionCommand().equals("Upgrade")) {
			if(selectedButton != null) {
				try {
					eventWindowListener.onUpgrade(selectedButton.getActionCommand(), city.getName());
					updateBuildingsView();
				}
				catch (BuildingInCoolDownException be) {
					new ErrorDialog(this, be.getMessage());
				}
				catch (NotEnoughGoldException ne) {
					new ErrorDialog(this, ne.getMessage());
				}
				catch (MaxLevelException me) {
					new ErrorDialog(this, me.getMessage());
				}
			}
		}
		else if(e.getActionCommand().equals("Recruit")) {
			if(selectedButton != null){
				try {
					if(selectedButton.getActionCommand().equals("Barracks"))
						eventWindowListener.onRecruit("Infantry", city.getName());
					if(selectedButton.getActionCommand().equals("ArcheryRange"))
						eventWindowListener.onRecruit("Archer", city.getName());
					if(selectedButton.getActionCommand().equals("Stable"))
						eventWindowListener.onRecruit("Cavalry", city.getName());
					updateBuildingsView();
				}
				catch(MaxRecruitedException me) {
					new ErrorDialog(this, me.getMessage());
				}
				catch(BuildingInCoolDownException be) {
					new ErrorDialog(this, be.getMessage());
				}
				catch(NotEnoughGoldException ne) {
					new ErrorDialog(this, ne.getMessage());
				}
			}
		}
		else if(e.getActionCommand().equals("Armies")) {
			CityArmyView cityArmyView = new CityArmyView(topPanel, localArmies);
			cityArmyView.setWindowClosingListener(this);
			cityArmyView.setEventWindowListener(eventWindowListener);
			setVisible(false);
		}
		else if(e.getActionCommand().equals("Target City")) {
			eventWindowListener.onTargetCity(city);
			dispose();
		}
		else if(e.getActionCommand().equals("Lay Siege")) {
			if(city.isUnderSiege())
				return;
			eventWindowListener.onLaySiege(city);
			dispose();
		}
		else if(e.getActionCommand().equals("Battle")) {
			eventWindowListener.onBattle(city);
			dispose();
		}
		else if(e.getActionCommand().equals("Your Armies")) {
			ForeignCityArmyView fcav = new ForeignCityArmyView(topPanel,localArmies);
			fcav.setWindowClosingListener(this);
			fcav.setEventWindowListener(eventWindowListener);
			setVisible(false);
		}
		else {
			if(selectedButton == e.getSource()) {
				selectedButton.setBackground(null);
				selectedButton = null;
			}
			else {
				if(selectedButton != null)
					selectedButton.setBackground(null);
				selectedButton = (JButton)(e.getSource());
				selectedButton.setBackground(new Color(0,170,0));
				}
			}
	}

	public void onWindowClosed() {
		setVisible(true);
		add(topPanel,BorderLayout.NORTH);
		refresh();
	}

	public void onRelocateUnit(Unit unit) {
		eventWindowListener.onRelocateUnit(unit, localArmies.get(0).getCurrentLocation());
	}

	public void onSelectArmy(Army army) {}
	public void onInitiateArmy(String cityName, Unit unit) {}
	
}
