package client;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.CellWalls;
import com.googlecode.blacken.terminal.CursesLikeAPI;
import com.googlecode.blacken.terminal.TerminalStyle;

public class ClientMain {
	
	private Logger LOGGER = LoggerFactory.getLogger(ClientMain.class);
	
	private CursesLikeAPI term;
			
	public ClientMain() {
		LOGGER.info("Starting to draw terminal windows");
		
		// Set up various game resouces here
		SwingTerminal window = new SwingTerminal();
		window.init("TSP Rouglike", 25, 80);
		
		this.term = new CursesLikeAPI(window);
		this.term.resize(24, 80);
		
		ColorPalette palette = new ColorPalette();
		palette.addAll(ColorNames.XTERM_256_COLORS, false);
		palette.putMapping(ColorNames.SVG_COLORS);
		
		this.term.setPalette(palette);
	}
	
	public void loop() {
		EnumSet<CellWalls> walls = EnumSet.noneOf(CellWalls.class);
		
		// Hide the cursor
		this.term.setCursorLocation(-1, -1);
		this.term.set(0, 0, "@", 255, 0, EnumSet.noneOf(TerminalStyle.class), walls);
		this.term.refresh();
	}
	
	public static void main(String[] arguments) {
		ClientMain cm = new ClientMain();
		cm.loop();
	}
}
