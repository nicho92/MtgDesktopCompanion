package org.magic.api.beans.messages;

import java.util.List;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.beans.game.Player;

public class UsersTechnicalMessage extends AbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Player> players;
	
	public UsersTechnicalMessage(List<Player> players) {
		this.players=players;
		setTypeMessage(MSG_TYPE.SYSTEM);
		setMessage("Send online users");
	}
	
	public List<Player> getPlayers() {
		return players;
	}

}
