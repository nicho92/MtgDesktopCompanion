package org.magic.api.interfaces.abstracts;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.beans.abstracts.AbstractMessage.MSG_TYPE;
import org.magic.api.beans.messages.StatutMessage;
import org.magic.api.beans.messages.TalkMessage;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;

public abstract class AbstractNetworkProvider extends AbstractMTGPlugin implements MTGNetworkClient {

	protected Player player;
	private JsonExport serializer;

	
	public Player getPlayer() {
		return player;
	}

	
	@Override
	public PLUGINS getType() {
		return PLUGINS.NETWORK;
	}
	
	
	protected abstract void createConnection(String url,String adress) throws IOException;
	protected abstract String read() throws IOException;

	
	protected AbstractNetworkProvider() {
		serializer = new JsonExport();
		serializer.removePrettyString();
	}
	

	@Override
	public TalkMessage consume() throws IOException {
		return serializer.fromJson(read(),TalkMessage.class);
	}


	protected String toJson(AbstractMessage obj) {
		return serializer.toJson(obj);
	}
	

	@Override
	public void join(Player p, String url,String adress) throws IOException {
		this.player = p;
		player.setOnlineConnectionTimeStamp(Instant.now().toEpochMilli());
		player.setState(STATUS.CONNECTED);
		
		createConnection(url,adress);
		
		switchAddress(adress);
		
		sendMessage(new StatutMessage(player,Player.STATUS.CONNECTED));
		
		logger.info("Connected to server {} with id={}",url,player.getId());
	}
	
	@Override
	public void changeStatus(STATUS selectedItem) throws IOException {
		player.setState(selectedItem);
		sendMessage(new StatutMessage(player,selectedItem));
	}

	
	@Override
	public void sendMessage(String text, Color c,MSG_TYPE type) throws IOException {
	
		sendMessage(new TalkMessage(player,text,c));
		
	}

}
