package org.magic.api.beans.messages;

import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.abstracts.AbstractMessage;

public class DeckMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	private MTGDeck item;
	
	public DeckMessage(MTGDeck item) {
		this.item=item;
		setTypeMessage(MSG_TYPE.DECK);
		setMessage("i share my deck  "+ item);
	}

	public MTGDeck getMagicDeck() {
		return item;
	}

}
