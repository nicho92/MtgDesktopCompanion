package org.magic.api.beans.messages;

import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.beans.game.Player;

public class TechnicalMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;
	private List<Player> players;
	private List<String> channels;
	
	public TechnicalMessage() {
		players = new ArrayList<>();
		channels = new ArrayList<>();
		setTypeMessage(MSG_TYPE.SYSTEM);
		setMessage("Technical Message");
	}
	
	
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	public void setChannels(List<String> channels) {
		this.channels = channels;
	}
	
	public List<Player> getPlayers() {
		return players;
	}

	public List<String> getChannels() {
		return channels;
	}
	
	
}
