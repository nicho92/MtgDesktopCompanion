package org.magic.api.beans.messages;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.interfaces.MTGProduct;

public class SearchMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	private MTGProduct item;
	
	public SearchMessage(MTGProduct item) {
		this.item=item;
		setMessage("search "+ item);
	}

	public MTGProduct getItem() {
		return item;
	}

}
