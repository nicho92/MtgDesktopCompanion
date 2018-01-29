package org.magic.game.model;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.game.network.actions.SpeakAction;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public class Player extends Observable implements Serializable{

	public static enum STATE { CONNECTED, BUSY, AWAY, GAMING};
	
	private Long id;
	private STATE state;
	private int life;
	private String name;
	private MagicDeck deck;
	private Graveyard graveyard;
	private Exile exil;
	private Library library;
	private List<MagicCard> hand;
	private BattleField battlefield;
	private ManaPool manaPool;
	private Locale local;
	private BufferedImage icon;
	
	private int poisonCounter;
	
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
		this.state = state;
	}

	public BufferedImage getIcon() {
		return icon;
	}

	public void setIcon(BufferedImage icon) {
		this.icon = icon;
	}

	public Locale getLocal() {
		return local;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void init()
	{
		graveyard=new Graveyard();
		exil=new Exile();
		hand=new ArrayList<MagicCard>();
		library=new Library(deck.getAsList());
		battlefield=new BattleField();
		manaPool = new ManaPool();
		local=Locale.getDefault();
		mixHandAndLibrary();
		shuffleLibrary();
	}
	
	public Player(String name,MagicDeck deck) {
		super();
		this.name=name;
		life=20;
		this.deck=deck;
		init();
		
	}
	
	public Player(MagicDeck deck) {
		super();
		name="Player";
		life=20;
		this.deck=deck;
		init();
		
	}
	
	public Player(String name,int life) {
		super();
		this.name=name;
		this.life=life;
		deck=new MagicDeck();
		init();
	}

	

	public Player() {
		super();
		name="Player";
		life=20;
		deck = new MagicDeck();
		init();
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
		logAction("has " + poisonCounter + " poison counter");

	}

	public ManaPool getManaPool() {
		return manaPool;
	}

	public void setManaPool(ManaPool manaPool) {
		this.manaPool = manaPool;
	}

	public void addMana(String color, int number)
	{
		manaPool.addMana(color, number);
		
		String mana="";
		for(int i=0;i<number;i++)
			mana+=color;
		
		logAction("Add " + mana + " to manapool" );
	}
	
	public void setMana(String color,int number)
	{
		manaPool.setMana(color, number);
		logAction("set manapool to " + manaPool);
	}
	
	
	
	public void reoderCardInLibrary(MagicCard mc,boolean top)
	{
		logAction("todo change order");
	}
	
	

	public void putCardInLibraryFromExile(MagicCard mc, boolean b) {
		if(b)
		{
			library.add(0, mc);
			logAction("put a card on top of library from exile");
		}
		else
		{
			library.add(mc);
			logAction("put a card on bottom of library from exile");
		}
		exil.remove(mc);
		
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
		library.shuffle();
		logAction("Shuffle his library");

	}
	
	public void drawCard(int number)
	{
		for(int i=0;i<number;i++)
		{ 
			hand.add(library.getCards().get(i));
			library.getCards().remove(i);
		}
		if(number>1)
			logAction("Draw " + number +" cards" );
		else
			logAction("Draw " + number +" card" );
		
	}
	
	public void discardCardFromExile(MagicCard mc) {
		exil.remove(mc);
		graveyard.add(mc);
		logAction("put " + mc +" from exil to graveyard");
		
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

	

	public List<MagicCard> discardCardFromLibrary(int parseInt) {
		
		List<MagicCard> ret = new ArrayList<MagicCard>();
		for(int i=0;i<parseInt;i++)
		{
			MagicCard mc = library.getCards().get(i);
			ret.add(mc);
			graveyard.add(mc);
			library.getCards().remove(i);
			
		}
		logAction("Discard " + parseInt +" cards from library" );
		return ret;
		
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
	

	public void returnCardFromExile(MagicCard mc) {
		exil.remove(mc);
		hand.add(mc);
		logAction("get " + mc +" back in hand from exil" );
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
	
	public void playCardFromGraveyard(MagicCard mc)
	{
		graveyard.remove(mc);
		battlefield.add(mc);
		logAction("play " + mc +" from graveyard" );
		

	}
	
	public void playCardFromExile(MagicCard mc)
	{
		exil.remove(mc);
		battlefield.add(mc);
		logAction("play " + mc +" from exile" );
		

	}
	
	public void mixGraveyardAndLibrary()
	{
		library.getCards().addAll(graveyard.getCards());
		graveyard.clear();
		logAction("Shuffle graveyard in library" );
		

	}
	
	public void mixHandAndLibrary()
	{
		library.getCards().addAll(hand);
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

	public Exile getExil() {
		return exil;
	}

	public void setExil(Exile exil) {
		this.exil = exil;
	}

	public List<MagicCard> getHand() {
		return hand;
	}

	public void setHand(List<MagicCard> hand) {
		this.hand = hand;
	}

	public Library getLibrary() {
		return library;
	}

	public void setLibrary(Library library) {
		this.library = library;
	}

	public void playCard(MagicCard mc) {
		hand.remove(mc);
		manaPool.useMana(mc);
		battlefield.add(mc);
		logAction("Play " + mc );
	}

	@Override
	public String toString() {
		return getName();
	}
	
	
	public String toDetailledString() {
		StringBuilder build = new StringBuilder();
		
		build.append("Turn :" ).append(GameManager.getInstance().getTurns().size()).append("\n");
		build.append("Phases:" ).append(GameManager.getInstance().getActualTurn().currentPhase()).append("\n");
		build.append("Library :" ).append(library.size()).append("\n");
		build.append("Graveyard :" ).append(graveyard).append("\n");
		build.append("Hand:" ).append(hand.size()).append("\n");
		build.append("BattleField :" ).append(battlefield.size()).append("\n");
		build.append("Exil :" ).append(exil.size()).append("\n");
		build.append("Pool : [ ").append(manaPool).append("]\n" );
		build.append("Stack : [ ").append(GameManager.getInstance().getStack()).append("]\n " );
		
		return build.toString();
	}

	public void logAction(String string) {
		setChanged();
		notifyObservers(new SpeakAction(this, string));
		GameManager.getInstance().getActualTurn().getActions().add(string);
		logger.debug(toDetailledString());
	}
	
	public void logAction(AbstractAction act) {
		setChanged();
		notifyObservers(act);
		GameManager.getInstance().getActualTurn().getActions().add(act.toString());
	}
	

	public void playCardFromLibrary(MagicCard mc) {
		battlefield.add(mc);
		library.remove(mc);
		logAction("play " + mc + " from library");
		
	}

	public void searchCardFromLibrary(MagicCard mc) {
		hand.add(mc);
		library.remove(mc);
		logAction("search " + mc + " from library into hand");
	}

	public void say(String text) {
		logAction("say:" + text);
		
	}
	
	
	public void moveCard(PositionEnum from,PositionEnum to,MagicCard mc)
	{
		
	}
	
	@Override
	public int hashCode() {
		return this.hashCode();
	}
	

	@Override
	public boolean equals(Object obj) {
		
	 if (obj == null)
	    return false;

	  if (this.getClass() != obj.getClass())
	    return false;
		
	  Player p2 = (Player)obj;
		return getId()==p2.getId();
	}

	public void playToken(MagicCard tok) {
		battlefield.add(tok);
		logAction("Create token "+tok);
		
	}


}
