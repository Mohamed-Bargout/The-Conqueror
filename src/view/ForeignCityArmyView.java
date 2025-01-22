package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;

import exceptions.MaxCapacityException;
import units.Army;
import units.Status;
import units.Unit;

public class ForeignCityArmyView extends JFrame implements ActionListener, WindowClosingListener, UnitArmyListener{

	private WindowClosingListener windowClosingListener;
	public static ArrayList<Army> localArmies;
	private JPanel armiesList;
	private TopPanel topPanel;
	private EventWindowListener eventWindowListener;
	private Unit selectedUnit;
	
	public ForeignCityArmyView(TopPanel topPanel, ArrayList<Army> localArmies) {
		super();
		setSize(800,600);
		setResizable(false);
		setTitle("The Conqueror");
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		CityArmyView.localArmies = localArmies;
		this.topPanel = topPanel;
		ForeignCityArmyView.localArmies = localArmies;
		add(topPanel,BorderLayout.NORTH);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				windowClosingListener.onWindowClosed();
				dispose();
			}
		});
		armiesList = new JPanel(new GridLayout(0,3));
		resetArmiesView();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		refresh();
	}
	
	private void resetArmiesView() {
		remove(armiesList);
		armiesList = new JPanel(new GridLayout(0,3));
		for(int i=0;i<localArmies.size();i++) {
			JButton armyButton = new JButton();
			armyButton.setActionCommand("Army");
			if(localArmies.get(i).getCurrentStatus().equals(Status.IDLE))
				armyButton.setText(String.format("<html>Army %d<br>Number Of Units: %d<br>Status: Idle",i+1,localArmies.get(i).getUnits().size()));
			else
				armyButton.setText(String.format("<html>Army %d<br>Number Of Units: %d<br>Status: Siegeing<br>Turns siegeing: %d",i+1,localArmies.get(i).getUnits().size(),localArmies.get(i).getTurnsSieging()));
			armyButton.addActionListener(this);
			armiesList.add(armyButton);
		}
		add(armiesList,BorderLayout.CENTER);
	}

	public void refresh() {
		revalidate();
		repaint();
	}
	
	public void setWindowClosingListener(WindowClosingListener windowClosingListener) {
		this.windowClosingListener = windowClosingListener;
	}
	
	public void setEventWindowListener(EventWindowListener eventWindowListener) {
		this.eventWindowListener = eventWindowListener;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Army")) {
			for(int i=0; i<armiesList.getComponents().length;i++)
				if(armiesList.getComponents()[i]==e.getSource()) {
					ArmyWindow aw = new ArmyWindow(topPanel, localArmies.get(i));
					aw.getInitiateButton().setVisible(false);
					aw.setWindowClosingListener(this);
					aw.setUnitArmyListener(this);
					setVisible(false);
				}
		}
		
	}

	public void onWindowClosed() {
		setVisible(true);
		add(topPanel,BorderLayout.NORTH);
		resetArmiesView();
		refresh();
	}

	public void onInitiateArmy(String cityName, Unit unit) {
		eventWindowListener.onInitiateArmy(cityName, unit);
	}

	public void onRelocateUnit(Unit unit) {
		eventWindowListener.onRelocateUnit(unit, localArmies.get(0).getCurrentLocation());
	}

	public void onSelectArmy(Army army) {
		try {
			army.relocateUnit(selectedUnit);
		} catch (MaxCapacityException mc) {
			new ErrorDialog(this, mc.getMessage());
		}
	}
	
}
