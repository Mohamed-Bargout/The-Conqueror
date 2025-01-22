package view;

import javax.swing.*;

import units.Archer;
import units.Army;
import units.Infantry;
import units.Unit;

public class UnitWindow extends JDialog {

	public UnitWindow(Unit u) {
		super();
		this.setSize(300, 300);
		this.setTitle("The Conqueror");
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		JLabel typeLabel = new JLabel();
		if(u instanceof Archer) {
			typeLabel.setIcon(new ImageIcon("Assets/Archer.png"));
			typeLabel.setText("Archer");
		}
		else if(u instanceof Infantry) {
			typeLabel.setIcon(new ImageIcon("Assets/Infantry.png"));
			typeLabel.setText("Infantry");
		}
		else {
			typeLabel.setIcon(new ImageIcon("Assets/Cavalry.png"));
			typeLabel.setText("Cavalry");
		}
		add(typeLabel);
		add(new JLabel("Level: "+u.getLevel()));
		add(new JLabel("Current Soldier Count: "+u.getCurrentSoldierCount()));
		add(new JLabel("Max Soldier Count: "+u.getMaxSoldierCount()));
		Army army = u.getParentArmy();
		switch(army.getCurrentStatus()) {
		case IDLE:
			add(new JLabel("Status: Idle"));
			add(new JLabel("Current Location: "+army.getCurrentLocation()));
			break;
		case MARCHING:
			add(new JLabel("Status: Marching"));
			add(new JLabel("Targeted City: "+army.getTarget()));
			add(new JLabel("Distance to Target: "+army.getDistancetoTarget()));
			break;
		case BESIEGING:
			add(new JLabel("Status: Besieging"));
			add(new JLabel("Attacking City: "+army.getCurrentLocation()));
			add(new JLabel("Has been besieging for "+army.getTurnsSieging()+" turns"));
			break;
		}
		setVisible(true);
		refresh();
	}
	
	public void refresh() {
		this.revalidate();
		this.repaint();
	}
}
