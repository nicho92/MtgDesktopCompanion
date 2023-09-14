package org.magic.api.beans.messages;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.interfaces.MTGProduct;
import org.magic.game.model.Player;

public class SearchMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	private MTGProduct item;
	
	protected SearchMessage(Player p, MTGProduct item) {
		super(p);
		this.item=item;
	}

	public MTGProduct getItem() {
		return item;
	}
	
	
}