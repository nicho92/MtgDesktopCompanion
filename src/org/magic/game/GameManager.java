package org.magic.game;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.magic.api.beans.MagicDeck;
import org.magic.gui.game.GamePanel;
import org.magic.services.exports.MagicSerializer;

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
		return turns.get(turns.size()-1);
	}
	
	
	public void nextTurn()
	{
		turns.add(new Turn());
		
		for(Player player : players)
			player.logAction("New turn : " + turns.size());
		
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
		//Player p1 = new Player(System.getProperty("user.name"), 20,MagicSerializer.read(new File("C:/Users/Pihen/magicDeskCompanion/decks/Jund.deck"), MagicDeck.class));
		//Player p2 = new Player("Player 2", 20,MagicSerializer.read(new File("C:/Users/Pihen/magicDeskCompanion/decks/Mr Toad's Wild Ride.deck"), MagicDeck.class));
		
		Player p1 = new Player(MagicSerializer.read(new File("C:/Users/Nicolas/magicDeskCompanion/decks/GW TOKENS.deck"), MagicDeck.class));
		Player p2 = new Player(MagicSerializer.read(new File("C:/Users/Nicolas/magicDeskCompanion/decks/Jeskai mentor.deck"), MagicDeck.class));
		
		GameManager.getInstance().addPlayer(p1);
		GameManager.getInstance().addPlayer(p2);
		GameManager.getInstance().initGame();
		GameManager.getInstance().nextTurn();
		JFrame f = new JFrame("Game Simulator " + GameManager.getInstance().getPlayers().size() + " players");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GamePanel p = GamePanel.getInstance();
		
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
	
}
