package org.magic.api.beans.messages;

import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.abstracts.AbstractMessage;

public class DeckMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	private MagicDeck item;
	
	public DeckMessage(MagicDeck item) {
		this.item=item;
		setTypeMessage(MSG_TYPE.DECK);
		setMessage("i share my deck  "+ item);
	}

	public MagicDeck getMagicDeck() {
		return item;
	}

}
