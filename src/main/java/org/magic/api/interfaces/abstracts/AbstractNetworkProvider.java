package org.magic.api.interfaces.abstracts;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;

import org.apache.commons.lang3.RandomUtils;
import org.magic.api.beans.JsonMessage;
import org.magic.api.beans.JsonMessage.MSG_TYPE;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;

public abstract class AbstractNetworkProvider extends AbstractMTGPlugin implements MTGNetworkClient {

	protected Player player;

	public Player getPlayer() {
		return player;
	}

	
	@Override
	public PLUGINS getType() {
		return PLUGINS.NETWORK;
	}
	
	
	protected abstract void joiningConnection(String url,String adress) throws IOException;


	@Override
	public void join(Player p, String url,String adress) throws IOException {
		this.player = p;
		player.setOnlineConnectionTimeStamp(Instant.now().toEpochMilli());
		player.setState(STATUS.CONNECTED);
		player.setId(RandomUtils.nextLong());
		
		
		joiningConnection(url,adress);
		
		switchAddress(adress);
		
		sendMessage(new JsonMessage(player,"connected",Color.black,MSG_TYPE.CONNECT));
		
	}
	
	@Override
	public void changeStatus(STATUS selectedItem) throws IOException {
		player.setState(selectedItem);
		sendMessage(new JsonMessage(player,selectedItem.name(),Color.black,MSG_TYPE.CHANGESTATUS));
	}

	
	@Override
	public void sendMessage(String text, Color c) throws IOException {
	
		sendMessage(new JsonMessage(player,text,c,MSG_TYPE.TALK));
		
	}

}
