package com.tsp.viewer.view;

import com.tsp.viewer.controller.StartupController;

import javax.swing.*;
import java.awt.*;

public class InfoEntryView extends JFrame {

	private JTextField playerEntry, serverEntry;
	
	public InfoEntryView(StartupController controller) {
		setTitle("Please enter...");
		setSize(350,250);
		setResizable(false);
		setLayout(new FlowLayout(FlowLayout.CENTER));
	
		JLabel nameLabel = new JLabel("Player Name:");
		JLabel serverLabel = new JLabel("Server IP:");
		playerEntry = new JTextField(20);
		serverEntry = new JTextField(20);
		nameLabel.setPreferredSize(new Dimension(275,35));
		serverLabel.setPreferredSize(new Dimension(275,35));
		playerEntry.setPreferredSize(new Dimension(275,25));
		serverEntry.setPreferredSize(new Dimension(275,25));
		add(nameLabel);
		add(playerEntry);
		add(serverLabel);
		add(serverEntry);
		
		JButton startButton = new JButton("Begin!");
		startButton.addActionListener(controller);
		startButton.setPreferredSize(new Dimension(275,50));
		add(startButton);
		
	}
	public String getPlayer() {
		return playerEntry.getText();
	}
	public String getServer() {
		return serverEntry.getText();
	}
}
