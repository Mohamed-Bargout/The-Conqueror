package view;

import javax.swing.*;

public class StartWindow extends JFrame{

	private JTextField playerNameField;
	private JComboBox<String> cityChoiceField;
	private JButton playButton;
	private JComboBox<String> fightAI;
	private JComboBox<Integer> levelAI;
	
	public StartWindow() {
		super();
		setTitle("The Conqueror");
		setSize(800,600);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		initialize();
		setIconImage(new ImageIcon("Assets/logo.png").getImage());
		setVisible(true);
		refresh();
	}
	
	public JTextField getPlayerNameField() {
		return playerNameField;
	}
	public JComboBox<String> getCityChoiceField(){
		return cityChoiceField;
	}
	public JButton getPlayButton() {
		return playButton;
	}
	public JComboBox<String> getFightAI(){
		return fightAI;
	}
	public JComboBox<Integer> getLevelAI(){
		return levelAI;
	}
	
	public void initialize() {
		JLabel playerNameLabel = new JLabel("Name: ");
		playerNameLabel.setBounds(200,200,50,30);
		add(playerNameLabel);
		
		JTextField playerName = new JTextField();
		playerName.setBounds(300, 200, 100, 30);
		add(playerName);
		playerNameField = playerName;
		
		JLabel startingCityLabel = new JLabel("Starting City: ");
		startingCityLabel.setBounds(200,235,100,30);
		add(startingCityLabel);
		
		JComboBox<String> cityChoice = new JComboBox<String>(new String[]{"Cairo","Rome","Sparta"});
		cityChoice.setBounds(300, 235, 100, 30);
		add(cityChoice);
		cityChoiceField = cityChoice;
		
		JLabel battleAI = new JLabel("Battle AI?");
		battleAI.setBounds(200,270,100,30);
		add(battleAI);
		
		fightAI = new JComboBox<String>(new String[] {"No","Yes"});
		fightAI.setBounds(300,270,100,30);
		add(fightAI);
		
		JLabel difficultAI = new JLabel("Level of AI");
		difficultAI.setBounds(200,305,100,30);
		add(difficultAI);
		
		levelAI = new JComboBox<Integer>(new Integer[] {1,2,3,4,5});
		levelAI.setBounds(300,305,100,30);
		add(levelAI);
		
		JButton submit = new JButton("Play");
		submit.setBounds(250, 350, 100, 30);
		playButton = submit;
		add(submit);
	}
	
	public void refresh() {
		this.revalidate();
		this.repaint();
	}
	
}
