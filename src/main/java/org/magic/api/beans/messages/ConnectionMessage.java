package org.magic.api.beans.messages;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.game.model.Player;

public class ConnectionMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	public ConnectionMessage(Player p, boolean online) {
		setTypeMessage(online?MSG_TYPE.CONNECT:MSG_TYPE.DISCONNECT);
		setAuthor(p);
	}
	
}
