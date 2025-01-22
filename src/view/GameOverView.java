package view;

import java.awt.Color;

import javax.swing.*;

public class GameOverView extends JFrame {

	public GameOverView(int mode) {
		//setLayout(null);
		setSize(800,600);
		setResizable(false);
		setTitle("The Conqueror");
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		if(mode==0) {
			JLabel winText = new JLabel("Congratulations on conquering the world!~");
			winText.setHorizontalAlignment(SwingConstants.CENTER);
			winText.setForeground(new Color(255,0,0));
			add(winText);
		}
		else if(mode==1){
			JLabel loseText = new JLabel("You were defeated...");
			loseText.setHorizontalAlignment(SwingConstants.CENTER);
			loseText.setForeground(new Color(255,0,0));
			add(loseText);
		}
		else {
			JLabel loseText = new JLabel("You could not conquer the world in time...");
			loseText.setHorizontalAlignment(SwingConstants.CENTER);
			loseText.setForeground(new Color(255,0,0));
			add(loseText);
		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		revalidate();
		repaint();
	}
	
}
