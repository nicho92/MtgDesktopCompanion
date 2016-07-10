package org.magic.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;

public class Player extends Observable{

	private int life;
	private String name;
	private MagicDeck deck;
	private List<MagicCard> graveyard;
	private List<MagicCard> exil;
	private List<MagicCard> library;
	private List<MagicCard> hand;
	private List<MagicCard> battlefield;
	private Map<String,Integer> manaPool;

	private int poisonCounter;
	
	
	public void init()
	{
		graveyard=new ArrayList<MagicCard>();
		exil=new ArrayList<MagicCard>();
		hand=new ArrayList<MagicCard>();
		library=deck.getAsList();
		battlefield=new ArrayList<MagicCard>();
		manaPool = new HashMap<String,Integer>();
		
	}
	
	public Player(MagicDeck deck) {
		super();
		name="player 1";
		life=20;
		this.deck=deck;
		init();
		
	}
	
	public Player(String name,int life,MagicDeck deck) {
		super();
		this.name=name;
		this.life=life;
		this.deck=deck;
		init();
	}

	

	public Player() {
		super();
		name="player 1";
		life=20;
		deck = new MagicDeck();
		init();
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
		logAction("has " + poisonCounter + " poison counter");
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
			logAction("Add " + number + " " + color + " to manapool" );
			manaPool.put(color, manaPool.get(color)+number);
		}catch(NullPointerException e)
		{
			manaPool.put(color, number);
		}
	}
	
	public List<MagicCard> scry(int number)
	{
		logAction("Scry " + number + " cards");
		List<MagicCard> list = library.subList(0, number);
		return list;
	}
	
	
	public void setMana(String color, int number)
	{
			logAction("Set " + number + " " + color + " to manapool" );
			manaPool.put(color, number);
		
	}
	
	public void lifeLoose(int lost)
	{
		logAction("Loose " + lost + " life (" + life +")"  );
		life=life-lost;
	}
	
	public void lifeGain(int gain)
	{
		logAction("Gain " + gain + " life (" + life +")");
		life=life+gain;
	}
	
	public void shuffleLibrary()
	{
		logAction("Shuffle his library");
		Collections.shuffle(library);
	}
	
	public void drawCard(int number)
	{
		logAction("Draw " + number +" cards" );
		for(int i=0;i<number;i++)
		{ 
			hand.add(library.get(i));
			library.remove(i);
		}
	}
	
	public void discardCardFromBattleField(MagicCard mc) {
		logAction("Sacrifice " + mc);
		
		battlefield.remove(mc);
		graveyard.add(mc);
		
	}
	
	public void discardCardFromHand(MagicCard mc)
	{
		logAction("Discard " + mc );
		
		hand.remove(mc);
		graveyard.add(mc);
	}
	
	public void discardCardFromLibrary(MagicCard mc)
	{
		logAction("Discard " + mc +" from library" );
		
		library.remove(mc);
		graveyard.add(mc);
	}


	public void exileCardFromBattleField(MagicCard mc) {
		logAction("Exil " + mc + " from battlefield");
		
		battlefield.remove(mc);
		exil.add(mc);
		
	}
	
	public void exileCardFromLibrary(MagicCard mc)
	{
		logAction("Exil " + mc +" from library" );
		
		library.remove(mc);
		exil.add(mc);
	}
	
	public void exileCardFromHand(MagicCard mc)
	{
		logAction("Exil " + mc +" from Hand" );
		
		hand.remove(mc);
		exil.add(mc);
	}
	
	public void exileCardFromGraveyard(MagicCard mc) {
		logAction("Exil " + mc +" from graveyard" );
		
		graveyard.remove(mc);
		exil.add(mc);
		
	}
	
	public void returnCardFromBattleField(MagicCard mc)
	{
		logAction("get " + mc +" back in hand" );
		
		battlefield.remove(mc);
		hand.add(mc);
	}
	public void returnCardFromGraveyard(MagicCard mc)
	{
		logAction("return " + mc +" from graveyard in hand" );
		
		graveyard.remove(mc);
		hand.add(mc);
	}
	
	public void mixGraveyardAndLibrary()
	{
		logAction("Shuffle graveyard in library" );
		
		library.addAll(graveyard);
		graveyard.clear();
	}
	
	public void mixHandAndLibrary()
	{
		logAction("Shuffle hand in library" );
		
		library.addAll(hand);
		hand.clear();
	}

	public int getLife() {
		return life;
	}

	public void setLife(int l) {
		
		int previouslife = this.life;
		
		if(previouslife>l)
			lifeLoose(previouslife-l);
		
		if(previouslife<l)
			lifeGain(l-previouslife);
		
		
		
		//this.life = life;
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
		init();
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
		logAction("Play " + mc );
		
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
		
		build.append("]\n " );
		
		return build.toString();
	}

	public void logAction(String string) {
		setChanged();
		notifyObservers(string);
		GameManager.getInstance().getActualTurn().getActions().add(string);
		
	}

	public void playCardFromLibrary(MagicCard mc) {
		logAction("play " + mc + " from library");
		battlefield.add(mc);
		library.remove(mc);
		
	}

	public void searchCardFromLibrary(MagicCard mc) {
		logAction("search " + mc + " from library into hand");
		hand.add(mc);
		library.remove(mc);
		
	}
	
	
	

}
