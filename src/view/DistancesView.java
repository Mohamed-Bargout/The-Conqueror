package view;

import java.util.ArrayList;

import javax.swing.*;

import engine.Distance;

public class DistancesView extends JDialog {

	public DistancesView(JFrame f, ArrayList<Distance> distances) {
		super();
		this.setSize(300, 300);
		this.setTitle("The Conqueror");
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		add(Box.createVerticalGlue());
		for(Distance d : distances) {
			add(Box.createHorizontalGlue());
			JLabel disLabel = new JLabel();
			disLabel.setText(String.format("%s <-----> %s : %d turns",d.getFrom(),d.getTo(),d.getDistance()));
			disLabel.setAlignmentX(CENTER_ALIGNMENT);
			add(disLabel);
		}
		add(Box.createVerticalGlue());
		setVisible(true);
		revalidate();
		repaint();
	}
	
}
