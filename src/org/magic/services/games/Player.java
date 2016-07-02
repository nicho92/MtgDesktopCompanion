package org.magic.services.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;

public class Player {

	private int life;
	private String name;
	private MagicDeck deck;
	private List<MagicCard> graveyard;
	private List<MagicCard> exil;
	private List<MagicCard> library;
	private List<MagicCard> hand;
	private BattleField battlefield;
	private int poisonCounter;
	
	private Map<String,Integer> manaPool;
	
	
	public Player(MagicDeck deck) {
		name="player 1";
		life=20;
		this.deck=deck;
		graveyard=new ArrayList<MagicCard>();
		exil=new ArrayList<MagicCard>();
		hand=new ArrayList<MagicCard>();
		library=deck.getAsList();
		battlefield=new BattleField();
		manaPool = new HashMap<String,Integer>();
	}
	
	public Player(String name,int life,MagicDeck deck) {
		this.name=name;
		this.life=life;
		this.deck=deck;
		graveyard=new ArrayList<MagicCard>();
		exil=new ArrayList<MagicCard>();
		hand=new ArrayList<MagicCard>();
		library=deck.getAsList();
		manaPool = new HashMap<String,Integer>();
	}

	
	
	public BattleField getBattlefield() {
		return battlefield;
	}

	public void setBattlefield(BattleField battlefield) {
		this.battlefield = battlefield;
	}

	public int getPoisonCounter() {
		return poisonCounter;
	}

	public void setPoisonCounter(int poisonCounter) {
		this.poisonCounter = poisonCounter;
	}

	public Map<String, Integer> getManaPool() {
		return manaPool;
	}

	public void setManaPool(Map<String, Integer> manaPool) {
		this.manaPool = manaPool;
	}

	public void addMana(String color, int number)
	{
		manaPool.put(color, manaPool.get(color)+number);
	}
	
	public void lifeLoose(int lost)
	{
		life=life-lost;
	}
	
	public void lifeGain(int gain)
	{
		life=life+gain;
	}
	
	public void shuffleLibrary()
	{
		Collections.shuffle(library);
	}
	
	public void drawCard(int number)
	{
		for(int i=0;i<number;i++)
		{ 
			hand.add(library.get(i));
			library.remove(i);
		}
	}
	
	public void discardTopCardFromLibrary(int number)
	{
		for(int i=0;i<number;i++)
		{
			graveyard.add(library.get(i));
			library.remove(i);
		}
	}
	
	
	public void discardCardFromHand(MagicCard mc)
	{
		hand.remove(mc);
		graveyard.add(mc);
	}
	
	public void discardCardFromLibrary(MagicCard mc)
	{
		library.remove(mc);
		graveyard.add(mc);
	}

	
	public void exileCardFromLibrary(MagicCard mc)
	{
		library.remove(mc);
		exil.add(mc);
	}
	
	public void exileCardFromHand(MagicCard mc)
	{
		hand.remove(mc);
		exil.add(mc);
	}
	
	public void mixGraveyardAndLibrary()
	{
		library.addAll(graveyard);
		graveyard.clear();
	}
	
	public void mixHandAndLibrary()
	{
		library.addAll(hand);
		hand.clear();
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MagicDeck getDeck() {
		return deck;
	}

	public void setDeck(MagicDeck deck) {
		this.deck = deck;
	}

	public List<MagicCard> getGraveyard() {
		return graveyard;
	}

	public void setGraveyard(List<MagicCard> graveyard) {
		this.graveyard = graveyard;
	}

	public List<MagicCard> getExil() {
		return exil;
	}

	public void setExil(List<MagicCard> exil) {
		this.exil = exil;
	}

	public List<MagicCard> getHand() {
		return hand;
	}

	public void setHand(List<MagicCard> hand) {
		this.hand = hand;
	}

	public List<MagicCard> getLibrary() {
		return library;
	}

	public void setLibrary(List<MagicCard> library) {
		this.library = library;
	}
	
}
