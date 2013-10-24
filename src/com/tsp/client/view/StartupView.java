package com.tsp.client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.tsp.client.controller.StartupController;

public class StartupView extends JFrame {

	public StartupView(StartupController controller) {
		setTitle("Welcome!");
		setSize(350,250);
		setResizable(false);
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
		blankLabel.setPreferredSize(new Dimension(275,30));
		add(blankLabel);

		JButton startButton = new JButton("New Game");
		startButton.addActionListener(controller);
		startButton.setPreferredSize(new Dimension(275,50));
		add(startButton);

		JPanel buttons = new JPanel();
		JButton aboutButton = new JButton("About");
		JButton helpButton = new JButton("Help");
		helpButton.addActionListener(controller);
		aboutButton.addActionListener(controller);
		buttons.add(helpButton);
		buttons.add(aboutButton);
		add(buttons);

		setBackground(Color.BLACK);
	}
}
