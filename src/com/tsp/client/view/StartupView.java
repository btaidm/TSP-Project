package com.tsp.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class StartupView extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8133151033849808443L;
	private JTextField playerEntry, serverEntry;
	private ArrayList<ActionListener> listeners;
	
	public StartupView() {
		this.listeners = new ArrayList<ActionListener>();
		
		setTitle("Welcome!");
		setSize(550,350);
		//setResizable(false);
		setLayout(new FlowLayout(FlowLayout.CENTER));

		JLabel title = new JLabel("Dungeon Brawl");
		Font titleFont = title.getFont();
		title.setFont(new Font(titleFont.getName(), Font.BOLD, 30));
		title.setPreferredSize(new Dimension(275,60));
		JLabel subtitle = new JLabel("A Multiplayer Roguelike developed by Team Sexy");
		subtitle.setFont(new Font(titleFont.getName(), Font.PLAIN, 10));
		subtitle.setPreferredSize(new Dimension(275,15));
		add(title);
		add(subtitle);

		JLabel blankLabel = new JLabel("");
		blankLabel.setPreferredSize(new Dimension(275,20));
		add(blankLabel);
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(2, 2));
		
		JLabel nameLabel = new JLabel("Player Name:");
		JLabel serverLabel = new JLabel("Server IP:");
		playerEntry = new JTextField(20);
		serverEntry = new JTextField(20);
		nameLabel.setPreferredSize(new Dimension(200,35));
		serverLabel.setPreferredSize(new Dimension(200,35));
		playerEntry.setPreferredSize(new Dimension(200,25));
		serverEntry.setPreferredSize(new Dimension(200,25));
		
		playerEntry.setText("Player" + new Random().nextInt(100));
		serverEntry.setText("localhost");
		
		labelPanel.add(nameLabel);
		labelPanel.add(playerEntry);
		labelPanel.add(serverLabel);
		labelPanel.add(serverEntry);
		
		JPanel buttons = new JPanel();
		
		JButton hostButton = new JButton("Host Server");
		hostButton.setPreferredSize(new Dimension(250,30));
		
		JButton joinButton = new JButton("Join Game");
		joinButton.setPreferredSize(new Dimension(250,30));
		JButton aboutButton = new JButton("About");
		JButton helpButton = new JButton("Help");
		
		helpButton.addActionListener(this);
		aboutButton.addActionListener(this);
		joinButton.addActionListener(this);
		hostButton.addActionListener(this);
		
		add(labelPanel);
		add(hostButton);
		add(joinButton);
		buttons.add(helpButton);
		buttons.add(aboutButton);
		add(buttons);

		setBackground(Color.BLACK);
	}
	
	public String getPlayer() {
		return playerEntry.getText();
	}
	public String getServer() {
		return serverEntry.getText();
	}

	public void addListener(ActionListener l) {
		listeners.add(l);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		for (ActionListener l : this.listeners) {
			l.actionPerformed(e);
		}
	}
}
