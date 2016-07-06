package org.magic.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.magic.api.beans.MagicDeck;
import org.magic.gui.game.GamePanel;
import org.magic.services.exports.MagicSerializer;

public class GameManager {

	private Player player;
	
	private static GameManager instance;
	
	public static GameManager getInstance()
	{
		if(instance==null)
			instance = new GameManager();
		
		return instance;
	}
	
	
	public void setPlayer(Player p) {
		player=p;
	}
	

	public Player getPlayer() {
		return player;
	}

	

	public static void main(String[] args) throws Exception {
		Player p1 = new Player(MagicSerializer.read(new File("C:/Users/Pihen/magicDeskCompanion/decks/Mr Toad's Wild Ride.deck"), MagicDeck.class));
		
		GameManager.getInstance().setPlayer(p1);
		JFrame f = new JFrame(p1.getName() +"->" + p1.getDeck().getName());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GamePanel p = new GamePanel();
		p.initGame();
		f.getContentPane().add(p);
		f.setVisible(true);
		f.setSize(1024, 800);
	}
	
}
