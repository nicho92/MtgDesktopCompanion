package org.magic.api.beans.messages;

import java.util.Set;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.game.model.Player;

public class TechMessageUsers extends AbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Set<Player> players;
	
	public TechMessageUsers(Set<Player> players) {
		this.players=players;
		setTypeMessage(MSG_TYPE.SYSTEM);
		setMessage("Send online users");
	}
	
	public Set<Player> getPlayers() {
		return players;
	}

}
