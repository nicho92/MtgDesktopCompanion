package org.magic.api.beans.game;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumPlayerStatus;
import org.magic.services.tools.CryptoUtils;
import org.magic.services.tools.ImageTools;
import org.utils.patterns.observer.Observable;

public class Player extends Observable implements Serializable {

	private static final String PLAY_TERM = "Play ";
	private static final String EXIL_TERM = "Exil ";
	private static final String DISCARD_TERM = "Discard ";
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private EnumPlayerStatus state;
	private transient int life;
	private String name;
	private transient MTGDeck deck;
	private transient Zone graveyard;
	private transient Zone exil;
	private transient Zone library;
	private transient Zone hand;
	private transient Zone battlefield;
	private transient ManaPool manaPool;
	private Locale local;
	private byte[] avatar;
	private transient int poisonCounter;
	private long onlineConnectionTimeStamp;
	private boolean admin=false;

	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
	public EnumPlayerStatus getState() {
		return state;
	}

	public void setState(EnumPlayerStatus state) {
		this.state = state;
	}

	public BufferedImage getAvatar() {
		return ImageTools.fromByteArray(avatar);
	}

	public void setAvatar(BufferedImage icon) {
		this.avatar = ImageTools.toByteArray(icon);
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

	public void init() {
		graveyard = new Zone(ZoneEnum.GRAVEYARD);
		battlefield = new Zone(ZoneEnum.BATTLEFIELD);
		library = new Zone(deck.getMainAsList(),ZoneEnum.LIBRARY);
		exil = new Zone(ZoneEnum.EXIL);
		hand = new Zone(ZoneEnum.HAND);
		manaPool = new ManaPool();
		local = Locale.getDefault();

		onlineConnectionTimeStamp= Instant.now().toEpochMilli();
		
		setId(CryptoUtils.randomLong());
		
		mixHandAndLibrary();
		shuffleLibrary();
	}

	public Player(String name) {
		this(name,false);
	}

	public Player(String name, boolean admin) {
		super();
		this.name = name;
		this.admin=admin;
		deck = new MTGDeck();
		init();
	}

	public Zone getBattlefield() {
		return battlefield;
	}

	public void setBattlefield(Zone battlefield) {
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

	public void addMana(String color, int number) {
		manaPool.addMana(color, number);

		var mana = new StringBuilder();
		for (var i = 0; i < number; i++)
			mana.append(color);

		logAction("Add " + mana + " to manapool");
	}

	public void setMana(String color, int number) {
		manaPool.setMana(color, number);
		logAction("set manapool to " + manaPool);
	}

	public void reoderCardInLibrary(MTGCard mc, boolean top) {
		logAction("todo change order for " + mc + " " + top);
	}

	public void putCardInLibraryFromExile(MTGCard mc, boolean b) {
		if (b) {
			library.add(0, mc);
			logAction("put a card on top of library from exile");
		} else {
			library.add(mc);
			logAction("put a card on bottom of library from exile");
		}
		exil.remove(mc);

	}

	public void putCardInLibraryFromHand(MTGCard mc, boolean top) {
		if (top) {
			library.add(0, mc);
			logAction("put a card on top of library from hand");
		} else {
			library.add(mc);
			logAction("put a card on bottom of library from hand");
		}
		hand.remove(mc);

	}

	public void putCardInLibraryFromBattlefield(MTGCard mc, boolean top) {
		if (top) {
			library.add(0, mc);
			logAction("put " + mc + " on top of library from battlefield");
		} else {
			library.add(mc);
			logAction("put " + mc + " on bottom of library from battlefield");
		}
		battlefield.remove(mc);
	}

	public void putCardInLibraryFromGraveyard(MTGCard mc, boolean top) {
		if (top) {
			library.add(0, mc);
			logAction("put a card on top of library from graveyard");
		} else {
			library.add(mc);
			logAction("put a card on bottom of library from graveyard");
		}
		graveyard.remove(mc);
	}

	public List<MTGCard> scry(int number) {
		List<MTGCard> list = library.subList(0, number);
		logAction("Scry " + number + " cards");

		return list;
	}

	public void lifeLoose(int lost) {
		life = life - lost;
		logAction("Loose " + lost + " life (" + life + ")");

	}

	public void lifeGain(int gain) {
		life = life + gain;
		logAction("Gain " + gain + " life (" + life + ")");

	}

	public void shuffleLibrary() {
		library.shuffle();
		logAction("Shuffle his library");

	}

	public void drawCard(int number) {
		for (var i = 0; i < number; i++) {
			hand.add(library.getCards().get(i));
			library.getCards().remove(i);
		}

		if (number > 1)
			logAction("Draw " + number + " cards");
		else
			logAction("Draw " + number + " card");

	}

	public void discardCardFromExile(MTGCard mc) {
		exil.remove(mc);
		graveyard.add(mc);
		logAction("put " + mc + " from exil to graveyard");

	}

	public void discardCardFromBattleField(MTGCard mc) {

		battlefield.remove(mc);
		graveyard.add(mc);
		logAction("Sacrifice " + mc);

	}

	public void discardCardFromHand(MTGCard mc) {
		hand.remove(mc);
		graveyard.add(mc);
		logAction(DISCARD_TERM + mc);

	}

	public void discardCardFromLibrary(MTGCard mc) {
		library.remove(mc);
		graveyard.add(mc);
		logAction(DISCARD_TERM + mc + " from library to graveyard");

	}

	public List<MTGCard> discardCardFromLibrary(int parseInt) {

		List<MTGCard> ret = new ArrayList<>();
		for (var i = 0; i < parseInt; i++) {
			MTGCard mc = library.getCards().get(i);
			ret.add(mc);
			graveyard.add(mc);
			library.getCards().remove(i);

		}
		logAction(DISCARD_TERM + parseInt + " cards from library");
		return ret;

	}

	public void exileCardFromBattleField(MTGCard mc) {
		battlefield.remove(mc);
		exil.add(mc);
		logAction(EXIL_TERM + mc + " from battlefield");

	}

	public void exileCardFromLibrary(MTGCard mc) {
		library.remove(mc);
		exil.add(mc);
		logAction(EXIL_TERM + mc + " from library");

	}

	public void exileCardFromHand(MTGCard mc) {
		hand.remove(mc);
		exil.add(mc);
		logAction(EXIL_TERM + mc + " from Hand");

	}

	public void exileCardFromGraveyard(MTGCard mc) {
		graveyard.remove(mc);
		exil.add(mc);
		logAction(EXIL_TERM + mc + " from graveyard");

	}

	public void returnCardFromExile(MTGCard mc) {
		exil.remove(mc);
		hand.add(mc);
		logAction("get " + mc + " back in hand from exil");
	}

	public void returnCardFromBattleField(MTGCard mc) {
		battlefield.remove(mc);
		hand.add(mc);
		logAction("get " + mc + " back in hand");

	}

	public void returnCardFromGraveyard(MTGCard mc) {
		graveyard.remove(mc);
		hand.add(mc);
		logAction("return " + mc + " from graveyard in hand");
	}

	public void playCardFromGraveyard(MTGCard mc) {
		graveyard.remove(mc);
		battlefield.add(mc);
		logAction(PLAY_TERM + mc + " from graveyard");

	}

	public void playCardFromExile(MTGCard mc) {
		exil.remove(mc);
		battlefield.add(mc);
		logAction(PLAY_TERM + mc + " from exile");

	}

	public void mixGraveyardAndLibrary() {
		library.getCards().addAll(graveyard.getCards());
		graveyard.clear();
		logAction("Shuffle graveyard in library");

	}

	public void mixHandAndLibrary() {
		library.getCards().addAll(hand.getCards());
		hand.clear();
		logAction("Shuffle hand in library");

	}

	public int getLife() {
		return life;
	}

	public void setLife(int l) {

		int previouslife = this.life;

		if (previouslife > l)
			lifeLoose(previouslife - l);

		if (previouslife < l)
			lifeGain(l - previouslife);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MTGDeck getDeck() {
		return deck;
	}

	public void setDeck(MTGDeck deck) {
		this.deck = deck;
		init();
	}

	public Zone getGraveyard() {
		return graveyard;
	}

	public void setGraveyard(Zone graveyard) {
		this.graveyard = graveyard;
	}

	public Zone getExil() {
		return exil;
	}

	public void setExil(Zone exil) {
		this.exil = exil;
	}

	public Zone getHand() {
		return hand;
	}

	public void setHand(Zone hand) {
		this.hand = hand;
	}

	public Zone getLibrary() {
		return library;
	}

	public void setLibrary(Zone library) {
		this.library = library;
	}

	public void playCard(MTGCard mc) {
		hand.remove(mc);
		manaPool.useMana(mc);
		battlefield.add(mc);
		logAction(PLAY_TERM + mc);
	}

	@Override
	public String toString() {
		return getName();
	}

	public String toDetailledString() {
		var build = new StringBuilder();

		build.append("Turn :").append(GameManager.getInstance().getTurns().size()).append("\n");
		build.append("Phases:").append(GameManager.getInstance().getActualTurn().currentPhase()).append("\n");
		build.append("Library :").append(library.size()).append("\n");
		build.append("Graveyard :").append(graveyard).append("\n");
		build.append("Hand:").append(hand.size()).append("\n");
		build.append("BattleField :").append(battlefield.size()).append("\n");
		build.append("Exil :").append(exil.size()).append("\n");
		build.append("Pool : [ ").append(manaPool).append("]\n");
		build.append("Stack : [ ").append(GameManager.getInstance().getStack()).append("]\n ");

		return build.toString();
	}

	public void logAction(String string) {
		setChanged();
		notifyObservers(string);
		GameManager.getInstance().getActualTurn().getActions().add(string);
	}

	public void logAction(AbstractAction act) {
		setChanged();
		notifyObservers(act);
		GameManager.getInstance().getActualTurn().getActions().add(act.toString());
	}

	public void playCardFromLibrary(MTGCard mc) {
		battlefield.add(mc);
		library.remove(mc);
		logAction(PLAY_TERM + mc + " from library");

	}

	public void searchCardFromLibrary(MTGCard mc) {
		hand.add(mc);
		library.remove(mc);
		logAction("search " + mc + " from library into hand");
	}

	public void say(String text) {
		logAction("say:" + text);

	}

	public void moveCard(ZoneEnum from, ZoneEnum to, MTGCard mc) {
		// do nothing
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getName()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		Player p2 = (Player) obj;
		return getId().equals(p2.getId());
	}

	public void playToken(MTGCard tok) {
		battlefield.add(tok);
		logAction("Create token " + tok);

	}

	public void setOnlineConnectionTimeStamp(long onlineConnectionTimeStamp) {
		this.onlineConnectionTimeStamp = onlineConnectionTimeStamp;
	}
	
	public long getOnlineConnectionTimeStamp() {
		return onlineConnectionTimeStamp;
	}
	
	public Date getOnlineConnectionDate()
	{
		return new Date(onlineConnectionTimeStamp);
	}
	
	
	
	
}
