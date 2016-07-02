package org.magic.services.games;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicDeck;
import org.magic.services.exports.MagicSerializer;

public class GameManager {

	private List<Player> players;
	
	private static GameManager instance;
	
	public static GameManager getInstance()
	{
		if(instance==null)
			instance = new GameManager();
		
		return instance;
	}
	
	
	private GameManager() {
		
		players = new ArrayList<Player>();
	}
	
	public void addPlayer(Player player) {
		
		players.add(player);
		
	}

	public static void main(String[] args) throws Exception {
		Player p1 = new Player(MagicSerializer.read(new File("C:/Users/Pihen/magicDeskCompanion/decks/Mr Toad's Wild Ride.deck"), MagicDeck.class));
		
		System.out.println("SHUFFLE");
		p1.shuffleLibrary();
		
		System.out.println("DRAW HAND");
		p1.drawCard(7);
		System.out.println(p1.getHand());
		System.out.println("L="+p1.getLibrary().size()  + " G=" +  p1.getGraveyard().size());
		
		System.out.println("DRAW A CARD");
		p1.drawCard(1);
		System.out.println(p1.getHand());
		System.out.println("L="+p1.getLibrary().size()  + " G=" +  p1.getGraveyard().size());
		
		System.out.println("DISCARD 2nd CARD");
		p1.discardCardFromHand(p1.getHand().get(1));
		System.out.println(p1.getHand());
		System.out.println("L="+p1.getLibrary().size()  + " G=" +  p1.getGraveyard().size());
		System.out.println("G=" +  p1.getGraveyard().get(0));
	}
	
}
