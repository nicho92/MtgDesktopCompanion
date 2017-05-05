package org.magic.game.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.gui.components.dialog.JDeckChooserDialog;

public class GameManager {

	private List<Player> players;
	
	private static GameManager instance;
	private List<Turn> turns;

	private GameManager()
	{
		turns = new ArrayList<Turn>();
		players=new ArrayList<Player>();
	}
	

	public List<Turn> getTurns() {
		return turns;
	}

	public Turn getActualTurn()
	{
		if(turns.size()==0)
			return new Turn();
		
		return turns.get(turns.size()-1);
	}
	
	
	public void endTurn(Player p)
	{
		p.logAction("End the turn " + turns.size());
		turns.add(new Turn());
	}
	
	public void logAction(Player p, String action)
	{
		p.logAction(action);
	}
	
	public void setPlayer(Player p)
	{
		players.clear();
		players.add(p);
	}
	
	public static GameManager getInstance()
	{
		if(instance==null)
			instance = new GameManager();
		
		return instance;
	}
	
	
	public void addPlayer(Player p) {
		players.add(p);
	
	}
	

	public List<Player> getPlayers() {
		return players;
	}

	
	public static void main(String[] args) throws Exception {
		
		Player p1 = new Player();

		GameManager.getInstance().initGame();
		GameManager.getInstance().endTurn(p1);
		JFrame f = new JFrame("Game Simulator " + GameManager.getInstance().getPlayers().size() + " players");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GamePanelGUI p = GamePanelGUI.getInstance();
		
		p.setPlayer(p1);
		f.getContentPane().add(p);
		f.setVisible(true);
		f.setSize(1024, 800);
	}



	public void initGame() {
		for(Player player : players)
			player.init();
		
		turns = new ArrayList<Turn>();
	}


	public void removePlayers() {
		players.clear();
		
	}
	
}
