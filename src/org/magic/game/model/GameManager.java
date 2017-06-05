package org.magic.game.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import org.magic.api.beans.MagicCard;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.gui.components.dialog.JDeckChooserDialog;

public class GameManager {

	private List<Player> players;
	
	private static GameManager instance;
	private List<Turn> turns;

	private Stack stack;

	private GameManager()
	{
		turns = new ArrayList<Turn>();
		players=new ArrayList<Player>();
		stack=new Stack();
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
	
	public Player getCurrentPlayer()
	{
		return players.get(0);
	}
	
	public void endTurn(Player p)
	{
		p.logAction("End the turn " + turns.size());
		turns.add(new Turn());
		Collections.rotate(players, 1);
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
		JFrame f = new JFrame("Game Simulator");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(GamePanelGUI.getInstance());
		f.setVisible(true);
		f.setSize(1024, 800);
	}



	public void initGame() {
		for(Player player : players)
			player.init();
		
		turns = new ArrayList<Turn>();
		
		turns.add(new Turn());
	}


	public void removePlayers() {
		players.clear();
		
	}

	public Stack getStack() {
		return stack;
	}
	

}
