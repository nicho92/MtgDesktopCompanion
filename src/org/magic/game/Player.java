package org.magic.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;

public class Player extends Observable{

	private int life;
	private String name;
	private MagicDeck deck;
	private Graveyard graveyard;
	private List<MagicCard> exil;
	private List<MagicCard> library;
	private List<MagicCard> hand;
	private List<MagicCard> battlefield;
	private Map<String,Integer> manaPool;

	private int poisonCounter;
	
	
	public void init()
	{
		graveyard=new Graveyard();
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
		this.poisonCounter = poisonCounter;
		logAction("has " + poisonCounter + " poison counter");

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
			logAction("Add " + number + " " + color + " to manapool" );
			
		}catch(NullPointerException e)
		{
			manaPool.put(color, number);
		}
	}
	
	
	public void reoderCardInLibrary(MagicCard mc,boolean top)
	{
		logAction("todo change order");
	}
	
	
	public void putCardInLibraryFromHand(MagicCard mc,boolean top)
	{
		if(top)
		{
			library.add(0, mc);
			logAction("put a card on top of library from hand");
		}
		else
		{
			library.add(mc);
			logAction("put a card on bottom of library from hand");
		}
		hand.remove(mc);
		
		
	}
	
	public void putCardInLibraryFromBattlefield(MagicCard mc,boolean top)
	{
		if(top)
		{
			library.add(0, mc);
			logAction("put "+mc+" on top of library from battlefield");
		}
		else
		{
			library.add(mc);
			logAction("put "+mc+" on bottom of library from battlefield");
		}
		battlefield.remove(mc);
	}
	
	public void putCardInLibraryFromGraveyard(MagicCard mc,boolean top)
	{
		if(top)
		{
			library.add(0, mc);
			logAction("put a card on top of library from graveyard");
		}
		else
		{
			library.add(mc);
			logAction("put a card on bottom of library from graveyard");
		}
		graveyard.remove(mc);
	}
	
	public List<MagicCard> scry(int number)
	{
		List<MagicCard> list = library.subList(0, number);
		logAction("Scry " + number + " cards");

		return list;
	}
	
	
	public void setMana(String color, int number)
	{
			manaPool.put(color, number);
			logAction("Set " + number + " " + color + " to manapool" );
			
		
	}
	
	public void lifeLoose(int lost)
	{
		life=life-lost;
		logAction("Loose " + lost + " life (" + life +")"  );
		
	}
	
	public void lifeGain(int gain)
	{
		life=life+gain;
		logAction("Gain " + gain + " life (" + life +")");

	}
	
	public void shuffleLibrary()
	{
		Collections.shuffle(library);
		logAction("Shuffle his library");

	}
	
	public void drawCard(int number)
	{
		for(int i=0;i<number;i++)
		{ 
			hand.add(library.get(i));
			library.remove(i);
		}
		logAction("Draw " + number +" cards" );
		
	}
	
	public void discardCardFromBattleField(MagicCard mc) {
		battlefield.remove(mc);
		graveyard.add(mc);
		logAction("Sacrifice " + mc);
		
		
	}
	
	public void discardCardFromHand(MagicCard mc)
	{
		hand.remove(mc);
		graveyard.add(mc);
		logAction("Discard " + mc );
		
		
	}
	
	public void discardCardFromLibrary(MagicCard mc)
	{
		library.remove(mc);
		graveyard.add(mc);
		logAction("Discard " + mc +" from library" );
		
		
	}


	public void exileCardFromBattleField(MagicCard mc) {
		battlefield.remove(mc);
		exil.add(mc);
		logAction("Exil " + mc + " from battlefield");
		

	}
	
	public void exileCardFromLibrary(MagicCard mc)
	{
		library.remove(mc);
		exil.add(mc);
		logAction("Exil " + mc +" from library" );
		
		
	}
	
	public void exileCardFromHand(MagicCard mc)
	{
		hand.remove(mc);
		exil.add(mc);
		logAction("Exil " + mc +" from Hand" );
		

	}
	
	public void exileCardFromGraveyard(MagicCard mc) {
		graveyard.remove(mc);
		exil.add(mc);
		logAction("Exil " + mc +" from graveyard" );
		

		
	}
	
	public void returnCardFromBattleField(MagicCard mc)
	{
		battlefield.remove(mc);
		hand.add(mc);
		logAction("get " + mc +" back in hand" );
		

	}
	public void returnCardFromGraveyard(MagicCard mc)
	{
		graveyard.remove(mc);
		hand.add(mc);
		logAction("return " + mc +" from graveyard in hand" );
		

	}
	
	public void mixGraveyardAndLibrary()
	{
		library.addAll(graveyard.getCards());
		graveyard.clear();
		logAction("Shuffle graveyard in library" );
		

	}
	
	public void mixHandAndLibrary()
	{
		library.addAll(hand);
		hand.clear();
		logAction("Shuffle hand in library" );
		

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

	public Graveyard getGraveyard() {
		return graveyard;
	}

	public void setGraveyard(Graveyard graveyard) {
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
		logAction("Play " + mc );
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		
		build.append("Library :" ).append(library.size()).append("\n");
		build.append("Graveyard :" ).append(graveyard).append("\n");
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
		hand.add(mc);
		library.remove(mc);
		logAction("search " + mc + " from library into hand");
		
		
	}

	public void say(String text) {
		logAction("say:" + text);
		
	}

	public void flipCoin() {
		
		boolean b = new Random().nextBoolean();
		
		if(b)
			logAction("Flip a coin : Tails");
		else
			logAction("Flip a coin : Heads");
		
		
		
		// TODO Auto-generated method stub
		
	}
	
	
	

}
