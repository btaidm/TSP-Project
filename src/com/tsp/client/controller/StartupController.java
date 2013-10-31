package com.tsp.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.tsp.client.view.AboutView;
import com.tsp.client.view.HelpView;
import com.tsp.client.view.StartupView;

public class StartupController implements ActionListener, MouseListener {

	private StartupView view;
	private HelpView helpScreen;
	private AboutView aboutScreen;
	private String server;
	private String playerName;
	private boolean gameStart;
	private boolean host;

	public StartupController(StartupView view) {
		this.view = view;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Create new server and join game
		if (arg0.getActionCommand().equals("Host Server")) {
			playerName = view.getPlayer();
			host = true;
		}
		// Join game on existing server
		else if (arg0.getActionCommand().equals("Join Game")) {		
			server = view.getServer();
			playerName = view.getPlayer();
			gameStart = true;
			view.dispose();
		}
		// Launch About window
		else if (arg0.getActionCommand().equals("About")) {	
			aboutScreen = new AboutView(this);
			aboutScreen.setVisible(true);
		} 
		// Launch help window
		else if (arg0.getActionCommand().equals("Help")) {
			helpScreen = new HelpView(this);
			helpScreen.setVisible(true);
		}
	}
	
	public boolean hasGameStarted() {
		return gameStart;
	}
	
	public String getServer() {
		return server;
	}
	
	public String getPlayer() {
		return playerName;
	}
	
	public boolean isHost() {
		return host;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}