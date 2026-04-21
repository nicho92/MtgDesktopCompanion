package org.magic.api.beans.messages;

import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.abstracts.AbstractMessage;

public class DeckMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	private MTGDeck item;

	public DeckMessage(MTGDeck item) {
		this.item = item;
		setTypeMessage(MSG_TYPE.DECK);

		if (item.getDescription().isEmpty())
			setMessage("i share my deck  " + item.getName());
		else
			setMessage(item.getDescription());
	}

	public MTGDeck getAttachement() {
		return item;
	}

}
