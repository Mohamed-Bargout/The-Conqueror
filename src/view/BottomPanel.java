package view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;

public class BottomPanel extends JPanel {

	private JButton endTurnButton;
	private JButton marchingArmiesButton;
	private JButton distancesButton;
	
	public BottomPanel() {
		super();
		setMaximumSize(new Dimension(800,100));
		endTurnButton = new JButton("End Turn");
		endTurnButton.setBackground(new Color(235,0,0));
		marchingArmiesButton = new JButton("Marching Armies");
		distancesButton = new JButton("Distances");
		add(marchingArmiesButton);
		add(endTurnButton);
		add(distancesButton);
	}
	
	public JButton getEndTurnButton() {
		return endTurnButton;
	}
	
	public JButton getMarchingArmiesButton() {
		return marchingArmiesButton;
	}
	
	public JButton getDistancesButton() {
		return distancesButton;
	}
	
}
