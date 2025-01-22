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

import units.Army;
import units.Status;
import units.Unit;

public class ArmySelector extends JFrame implements ActionListener, WindowClosingListener{
	
	private WindowClosingListener windowClosingListener;
	private JButton selectedButton;
	private JButton selectButton;
	private JButton viewButton;
	public static ArrayList<Army> armies;
	private EventWindowListener eventWindowListener;
	private TopPanel topPanel;
	private JPanel armiesList;
	private Unit selectedUnit;
	private boolean isMarching;
	
	public ArmySelector(TopPanel topPanel, ArrayList<Army> armies, boolean isMarching, boolean hasDefending) {
		super();
		setSize(800,600);
		setTitle("The Conqueror");
		setResizable(false);
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		ArmySelector.armies = armies;
		this.topPanel = topPanel;
		this.isMarching = isMarching;
		add(topPanel,BorderLayout.NORTH);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				if(windowClosingListener!=null)
					windowClosingListener.onWindowClosed();
				dispose();
			}
		});
		selectButton = new JButton("Select");
		selectButton.addActionListener(this);
		viewButton = new JButton("View");
		viewButton.addActionListener(this);
		JPanel bottom = new JPanel();
		bottom.add(viewButton);
		bottom.add(selectButton);
		add(bottom, BorderLayout.SOUTH);
		armiesList = new JPanel(new GridLayout(0,3));
		resetArmiesView(isMarching, hasDefending);
		setVisible(true);
		refresh();
	}
	
	private void resetArmiesView(boolean isMarching, boolean hasDefending) {
		remove(armiesList);
		armiesList = new JPanel(new GridLayout(0,3));
		if(!isMarching) {
			if(hasDefending) {
				JButton defendingArmyButton = new JButton(String.format("<html>Defending Army<br>Number Of Units: %d",armies.get(0).getUnits().size()));
				defendingArmyButton.setActionCommand("Army");
				armiesList.add(defendingArmyButton);
				defendingArmyButton.addActionListener(this);
				for(int i=1;i<armies.size();i++) {
					JButton armyButton = new JButton();
					armyButton.setActionCommand("Army");
					armyButton.setText(String.format("<html>Army %d<br>Number Of Units: %d",i,armies.get(i).getUnits().size()));
					armyButton.addActionListener(this);
					armiesList.add(armyButton);
				}
			}
			else {
				for(int i=0;i<armies.size();i++) {
					JButton armyButton = new JButton();
					armyButton.setActionCommand("Army");
					if(armies.get(i).getCurrentStatus().equals(Status.IDLE))
						armyButton.setText(String.format("<html>Army %d<br>Number Of Units: %d<br>Status: Idle",i+1,armies.get(i).getUnits().size()));
					else
						armyButton.setText(String.format("<html>Army %d<br>Number Of Units: %d<br>Status: Siegeing<br>Turns siegeing: %d",i+1,armies.get(i).getUnits().size(),armies.get(i).getTurnsSieging()));
					armyButton.addActionListener(this);
					armiesList.add(armyButton);
				}
			}
		}
		else {
			for(int i=0;i<armies.size();i++) {
				JButton armyButton = new JButton();
				armyButton.setActionCommand("Army");
				armyButton.setText(String.format("<html>Army %d<br>Number Of Units: %d<br>Target: %s<br>Distance: %d",i+1,armies.get(i).getUnits().size(),armies.get(i).getTarget(),armies.get(i).getDistancetoTarget()));
				armyButton.addActionListener(this);
				armiesList.add(armyButton);
			}
		}
		add(armiesList,BorderLayout.CENTER);
	}
	
	public void refresh() {
		revalidate();
		repaint();
	}
	
	public JButton getSelectButton() {
		return selectButton;
	}

	public void setWindowClosingListener(WindowClosingListener windowClosingListener) {
		this.windowClosingListener = windowClosingListener;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Select")) {
			if(selectedButton!=null) {
				for(int i=0;i<armiesList.getComponents().length;i++)
					if(selectedButton == armiesList.getComponents()[i]) {
						eventWindowListener.onSelectArmy(armies.get(i));
						dispose();
						break;
					}
			}
		}
		else if(e.getActionCommand().equals("View")) {
			if(selectedButton!=null) {
				for(int i=0;i<armiesList.getComponents().length;i++)
					if(selectedButton == armiesList.getComponents()[i]) {
						ArmyWindow aw = new ArmyWindow(topPanel, armies.get(i));
						aw.getInitiateButton().setVisible(false);
						aw.getRelocateButton().setVisible(false);
						aw.setWindowClosingListener(this);
						setVisible(false);
						break;
					}
			}
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
		add(topPanel, BorderLayout.NORTH);
		setVisible(true);
		refresh();
	}

	public void setEventWindowListener(EventWindowListener eventWindowListener) {
		this.eventWindowListener = eventWindowListener;
	}
	
}
