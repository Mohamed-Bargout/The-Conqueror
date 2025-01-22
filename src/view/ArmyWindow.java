package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import units.Army;
import units.Status;
import units.Unit;

public class ArmyWindow extends JFrame implements ActionListener{

	private WindowClosingListener windowClosingListener;
	private Army army;
	private JPanel unitsView;
	private JButton viewButton;
	private JButton initiateButton;
	private JButton relocateButton;
	private JButton selectedUnit;
	private UnitArmyListener unitArmyListener;
	
	public ArmyWindow(TopPanel topPanel, Army army) {
		super();
		setSize(800,600);
		setResizable(false);
		setTitle("The Conqueror");
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		this.army = army;
		add(topPanel, BorderLayout.NORTH);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				windowClosingListener.onWindowClosed();
				dispose();
			}
		});
		unitsView = new JPanel(new GridLayout(0,3));
		updateUnitsView();
		JPanel bottomPanel = new JPanel();
		viewButton = new JButton("View");
		viewButton.addActionListener(this);
		initiateButton = new JButton("Initiate Army");
		initiateButton.setActionCommand("Initiate");
		initiateButton.addActionListener(this);
		relocateButton = new JButton("Relocate Unit");
		relocateButton.setActionCommand("Relocate");
		relocateButton.addActionListener(this);
		bottomPanel.add(viewButton);
		bottomPanel.add(initiateButton);
		bottomPanel.add(relocateButton);
		add(bottomPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		refresh();
	}

	private void updateUnitsView() {
		remove(unitsView);
		unitsView = new JPanel(new GridLayout(0,3));
		for(Unit unit : army.getUnits()) {
			JButton unitButton = new JButton(unit.getClass().getSimpleName());
			unitButton.setActionCommand("Unit");
			unitButton.addActionListener(this);
			unitsView.add(unitButton);
		}
		add(unitsView, BorderLayout.CENTER);
	}

	public JButton getInitiateButton() {
		return initiateButton;
	}

	public JButton getRelocateButton() {
		return relocateButton;
	}

	public void refresh() {
		revalidate();
		repaint();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Unit")) {
			if(selectedUnit == e.getSource()) {
				selectedUnit.setBackground(null);
				selectedUnit = null;
			}
			else {
				if(selectedUnit != null)
					selectedUnit.setBackground(null);
				selectedUnit = (JButton)(e.getSource());
				selectedUnit.setBackground(new Color(0,170,0));
			}
		}
		else if(e.getActionCommand().equals("View") && selectedUnit!=null) {
			for(int i=0;i<unitsView.getComponents().length;i++)
				if(selectedUnit == unitsView.getComponents()[i])
					new UnitWindow(army.getUnits().get(i));
		}
		else if(e.getActionCommand().equals("Initiate") && selectedUnit!=null) {
			for(int i=0;i<unitsView.getComponents().length;i++)
				if(selectedUnit == unitsView.getComponents()[i]) {
					unitArmyListener.onInitiateArmy(army.getCurrentLocation(), army.getUnits().get(i));
					selectedUnit.setBackground(null);
					selectedUnit = null;
					updateUnitsView();
					refresh();
				}
		}
		else if(e.getActionCommand().equals("Relocate") && selectedUnit!=null) {
			if(army.getCurrentStatus()==Status.BESIEGING)
				return;
			for(int i=0;i<unitsView.getComponents().length;i++)
				if(selectedUnit == unitsView.getComponents()[i]) {
					unitArmyListener.onRelocateUnit(army.getUnits().get(i));
					dispose();
				}
		}
	}
	
	public void setWindowClosingListener(WindowClosingListener windowClosingListener) {
		this.windowClosingListener = windowClosingListener;
	}
	
	public void setUnitArmyListener(UnitArmyListener unitArmyListener) {
		this.unitArmyListener = unitArmyListener;
	}
	
}
