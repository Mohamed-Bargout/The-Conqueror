package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import units.Army;
import units.Unit;

public class BattleView extends JFrame implements ActionListener{

	private JPanel battlePanel;
	private JLabel choiceLabel;
	private JButton manualButton;
	private JButton autoButton;
	private JPanel attackersPanel;
	private JPanel defendersPanel;
	private JTextArea logArea;
	private JButton selectedAttacker;
	private JButton selectedDefender;
	private JPanel bottomPanel;
	private JButton attackButton;
	private JLabel autoResultLabel;
	private WindowClosingListener windowClosingListener;
	private int timesWrote = 0;
	
	public BattleView(TopPanel topPanel) {
		super();
		setSize(800,600);
		setResizable(false);
		setTitle("The Conqueror");
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		add(topPanel,BorderLayout.NORTH);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		battlePanel = new JPanel();
		battlePanel.setLayout(new BoxLayout(battlePanel,BoxLayout.Y_AXIS));
		battlePanel.add(Box.createVerticalGlue());
		choiceLabel = new JLabel("City has been under siege for 3 turns. You must now enter battle");
		choiceLabel.setAlignmentX(CENTER_ALIGNMENT);
		battlePanel.add(choiceLabel);
		JLabel battleLabel = new JLabel("How would you like to battle?");
		battleLabel.setAlignmentX(CENTER_ALIGNMENT);
		battlePanel.add(battleLabel);
		manualButton = new JButton("Manual");
		autoButton = new JButton("Auto");
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(manualButton);
		buttonsPanel.add(autoButton);
		battlePanel.add(buttonsPanel);
		battlePanel.add(Box.createVerticalGlue());
		
		attackersPanel = new JPanel(new GridLayout(0,2));
		defendersPanel = new JPanel(new GridLayout(0,3));
		logArea = new JTextArea();
		logArea.setEditable(false);
		logArea.setPreferredSize(new Dimension(200,battlePanel.getHeight()));
		attackButton = new JButton("Attack");
		bottomPanel = new JPanel();
		autoResultLabel = new JLabel();
		autoResultLabel.setAlignmentX(CENTER_ALIGNMENT);
		add(battlePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		setVisible(true);
		refresh();
	}
	
	public void refresh() {
		revalidate();
		repaint();
	}
	
	public void done() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				windowClosingListener.onWindowClosed();
				dispose();
			}
		});
	}

	public JPanel getBattlePanel() {
		return battlePanel;
	}

	public JPanel getBottomPanel() {
		return bottomPanel;
	}
	
	public JLabel getChoiceLabel() {
		return choiceLabel;
	}

	public JButton getManualButton() {
		return manualButton;
	}

	public JButton getAutoButton() {
		return autoButton;
	}

	public JPanel getAttackersPanel() {
		return attackersPanel;
	}

	public JPanel getDefendersPanel() {
		return defendersPanel;
	}

	public JTextArea getLogArea() {
		return logArea;
	}

	public JButton getSelectedAttacker() {
		return selectedAttacker;
	}
	public void setSelectedAttacker(JButton selectedAttacker) {
		this.selectedAttacker =  selectedAttacker;
	}

	public JButton getSelectedDefender() {
		return selectedDefender;
	}
	public void setSelectedDefender(JButton selectedDefender) {
		this.selectedDefender = selectedDefender;
	}

	public JButton getAttackButton() {
		return attackButton;
	}
	
	public JLabel getAutoResultLabel() {
		return autoResultLabel;
	}
	
	public void setWindowClosingListener(WindowClosingListener windowClosingListener) {
		this.windowClosingListener = windowClosingListener;
	}

	public void updateAttackersPanel(Army army) {
		attackersPanel = new JPanel(new GridLayout(0,2));
		for(Unit unit : army.getUnits()) {
			JButton unitButton = new JButton();
			unitButton.setText(String.format("<html> %s<br>Level: %d<br>Current Soldier Count: %d",unit.getClass().getSimpleName(),unit.getLevel(), unit.getCurrentSoldierCount()));
			unitButton.setActionCommand("Attacker");
			unitButton.addActionListener(this);
			attackersPanel.add(unitButton);
		}
		updateManualView();
	}
	
	public void updateDefendersPanel(Army army) {
		defendersPanel = new JPanel(new GridLayout(0,3));
		for(Unit unit : army.getUnits()) {
			JButton unitButton = new JButton();
			unitButton.setText(String.format("<html> %s<br>Level: %d<br>Current Soldier Count: %d",unit.getClass().getSimpleName(),unit.getLevel(), unit.getCurrentSoldierCount()));
			unitButton.setActionCommand("Defender");
			unitButton.addActionListener(this);
			defendersPanel.add(unitButton);
		}
		updateManualView();
	}
	
	public void updateManualView() {
		battlePanel.removeAll();
		battlePanel.add(attackersPanel);
		battlePanel.add(defendersPanel);
		battlePanel.add(logArea);
	}
	
	public void writeToLog(String s) {
		if(timesWrote>25) {
			timesWrote = 0;
			clearLog();
		}
		logArea.append("\n"+s);
		timesWrote++;
	}

	public void clearLog() {
		logArea.setText("");
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Attacker")) {
			if(selectedAttacker == e.getSource()) {
				selectedAttacker.setBackground(null);
				selectedAttacker = null;
			}
			else {
				if(selectedAttacker != null)
					selectedAttacker.setBackground(null);
				selectedAttacker = (JButton)(e.getSource());
				selectedAttacker.setBackground(new Color(0,170,0));
			}
		}
		else if(e.getActionCommand().equals("Defender")) {
			if(selectedDefender == e.getSource()) {
				selectedDefender.setBackground(null);
				selectedDefender = null;
			}
			else {
				if(selectedDefender != null)
					selectedDefender.setBackground(null);
				selectedDefender = (JButton)(e.getSource());
				selectedDefender.setBackground(new Color(170,0,0));
			}
		}
	}
	
}
