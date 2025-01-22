package view;

import javax.swing.*;

public class ErrorDialog extends JDialog{

	public ErrorDialog(JFrame f, String message) {
		super(f);
		setBounds(200,200,350,100);
		setTitle("The Conquerer");
		JLabel label = new JLabel(message);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		add(label);
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);
		refresh();
	}
	
	public void refresh() {
		revalidate();
		repaint();
	}
	
}
