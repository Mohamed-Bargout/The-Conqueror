package view;

import java.awt.BorderLayout;

import javax.swing.*;

public class WorldWindow extends JFrame{
	
	private WorldMapPanel worldMapPanel;
	
	public WorldWindow(TopPanel topPanel, BottomPanel bottomPanel) {
		super();
		setTitle("The Conqueror");
		setSize(800,600);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		add(topPanel,BorderLayout.NORTH);
		worldMapPanel = new WorldMapPanel();
		add(worldMapPanel,BorderLayout.CENTER);
		add(bottomPanel,BorderLayout.SOUTH);
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		setVisible(true);
		refresh();
	}
	
	public void refresh() {
		this.revalidate();
		this.repaint();
	}
	
	public WorldMapPanel getWorldMapPanel() {
		return worldMapPanel;
	}
	
}
