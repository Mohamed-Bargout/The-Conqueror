package view;

import javax.swing.*;
import java.awt.*;

public class WorldMapPanel extends JPanel{

	private JButton cairoButton;
	private JButton romeButton;
	private JButton spartaButton;
	
	public WorldMapPanel() {
		setLayout(null);
		setMaximumSize(new Dimension(800,500));
		cairoButton = new JButton("Cairo");
		cairoButton.setActionCommand("Cairo");
		cairoButton.setBounds(345, 400, 100, 50);
		cairoButton.setOpaque(true);
		cairoButton.setContentAreaFilled(true);
		cairoButton.setBorderPainted(true);
		add(cairoButton);
		
		romeButton = new JButton("Rome");
		romeButton.setActionCommand("Rome");
		romeButton.setBounds(680, 50, 100, 50);
		romeButton.setOpaque(true);
		romeButton.setContentAreaFilled(true);
		romeButton.setBorderPainted(true);
		add(romeButton);
		
		spartaButton = new JButton("Sparta");
		spartaButton.setActionCommand("Sparta");
		spartaButton.setBounds(10, 50, 100, 50);
		spartaButton.setOpaque(true);
		spartaButton.setContentAreaFilled(true);
		spartaButton.setBorderPainted(true);
		add(spartaButton);
	}
	
	public JButton getCairoButton() {
		return cairoButton;
	}
	
	public JButton getRomeButton() {
		return romeButton;
	}
	
	public JButton getSpartaButton() {
		return spartaButton;
	}
	
	public JButton getCityButton(String cityName) {
		switch(cityName) {
		case "Cairo": return cairoButton;
		case "Rome": return romeButton;
		default: return spartaButton;
		}
	}
}
