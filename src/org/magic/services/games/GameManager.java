package org.magic.services.games;

import java.io.File;

import org.magic.api.beans.MagicDeck;
import org.magic.services.exports.MagicSerializer;

public class GameManager {

	private Player player1;
	private Player player2;
	
	
	
	public GameManager(Player p1, Player p2) {
		player1=p1;
		player2=p2;
	}
	
	
	
	public Player getPlayer1() {
		return player1;
	}



	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}



	public Player getPlayer2() {
		return player2;
	}



	public void setPlayer2(Player player2) {
		this.player2 = player2;
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
