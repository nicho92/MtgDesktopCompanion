package org.magic.game;

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
	private List<MagicCard> battlefield;
	private int poisonCounter;
	private List<Turn> turns;
	
	
	private Map<String,Integer> manaPool;
	
	
	private void init()
	{
		graveyard=new ArrayList<MagicCard>();
		exil=new ArrayList<MagicCard>();
		hand=new ArrayList<MagicCard>();
		library=deck.getAsList();
		battlefield=new ArrayList<MagicCard>();
		manaPool = new HashMap<String,Integer>();
		turns = new ArrayList<Turn>();
		nextTurn();
	}
	
	public Player(MagicDeck deck) {
		name="player 1";
		life=20;
		this.deck=deck;
		init();
		
	}
	
	public Player(String name,int life,MagicDeck deck) {
		this.name=name;
		this.life=life;
		this.deck=deck;
		init();
	}

	public void nextTurn()
	{
		turns.add(new Turn());
	}

	public List<MagicCard> getBattlefield() {
		return battlefield;
	}

	public void setBattlefield(List<MagicCard> battlefield) {
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
		try{
			manaPool.put(color, manaPool.get(color)+number);
		}catch(NullPointerException e)
		{
			manaPool.put(color, number);
		}
	}
	
	public void setMana(String color, int number)
	{
		
			manaPool.put(color, number);
		
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
	
	public void discardCardFromBattleField(MagicCard mc) {
		battlefield.remove(mc);
		graveyard.add(mc);
		
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


	public void exileCardFromBattleField(MagicCard mc) {
		battlefield.remove(mc);
		exil.add(mc);
		
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
	
	public void exileCardFromGraveyard(MagicCard mc) {
		graveyard.remove(mc);
		exil.add(mc);
		
	}
	
	public void returnCardFromBattleField(MagicCard mc)
	{
		battlefield.remove(mc);
		hand.add(mc);
	}
	public void returnCardFromGraveyard(MagicCard mc)
	{
		graveyard.remove(mc);
		hand.add(mc);
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

	public void playCard(MagicCard mc) {
		hand.remove(mc);
		battlefield.add(mc);
		
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		
		build.append("Library :" ).append(library.size()).append("\n");
		build.append("Graveyard :" ).append(graveyard.size()).append("\n");
		build.append("Hand:" ).append(hand.size()).append("\n");
		build.append("BattleField :" ).append(battlefield.size()).append("\n");
		build.append("Exil :" ).append(exil.size()).append("\n");
		build.append("Pool : [ " );
		for(String key : manaPool.keySet())
			build.append(key).append(":").append(manaPool.get(key));
		
		build.append("]\n");
		
		
		return build.toString();
	}

	public List<Turn> getTurns() {
		return turns;
	}

	public void logAction(String string) {
		getTurns().get(getTurns().size()-1).getActions().add(string);
		
	}
	
	
	

}
