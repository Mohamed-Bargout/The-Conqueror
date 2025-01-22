package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;

public class TopPanel extends JPanel{
	
	private JLabel currentTurnLabel;
	private JLabel foodLabel;
	private JLabel treasuryLabel;
	
	public TopPanel(String playerName) {
		super();
		setLayout(new GridLayout(0,2));
		setMaximumSize(new Dimension(800,100));
		JLabel playerNameLabel = new JLabel();
		if(playerName.length()>12)
			playerNameLabel.setText("<html><div style='color:white;'>Name: "+playerName.substring(0, 13)+"...");
		else
			playerNameLabel.setText("<html><div style='color:white;'>Name: "+playerName);
		add(playerNameLabel);
		currentTurnLabel = new JLabel("<html><div style='color:red;'>Current Turn: 1");
		add(currentTurnLabel);
		foodLabel = new JLabel("<html><div style='color:#00FF00;'>Food: 0.0");
		add(foodLabel);
		treasuryLabel = new JLabel("<html><div style='color:yellow;'>Gold: 10000.0");
		add(treasuryLabel);
		setBackground(new Color(0,0,250));
	}
	
	public void refresh() {
		revalidate();
		repaint();
	}
	
	public void onFoodChanged(double food) {
		foodLabel.setText("<html><div style='color:#00FF00;'>Food: "+food);
		refresh();
	}
	
	public void onTreasuryChanged(double treasury) {
		treasuryLabel.setText("<html><div style='color:yellow;'>Gold: "+treasury);
		refresh();
	}
	
	public void onCurrentTurnChanged(int turn) {
		currentTurnLabel.setText("<html><div style='color:red;'>Current Turn: "+turn);
		refresh();
	}
}
