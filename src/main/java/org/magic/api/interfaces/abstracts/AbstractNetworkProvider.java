package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.time.Instant;
import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.beans.enums.EnumPlayerStatus;
import org.magic.api.beans.game.Player;
import org.magic.api.beans.messages.DeckMessage;
import org.magic.api.beans.messages.SearchMessage;
import org.magic.api.beans.messages.SendStockMessage;
import org.magic.api.beans.messages.StatutMessage;
import org.magic.api.beans.messages.TalkMessage;
import org.magic.api.beans.messages.TechnicalMessage;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.services.network.URLTools;

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

	protected abstract void createConnection(String url) throws IOException;
	protected abstract String read() throws IOException;

	protected AbstractNetworkProvider() {
		serializer = new JsonExport();
		serializer.removePrettyString();
	}

	@Override
	public AbstractMessage consume() throws IOException {

		var txt = read();

		if (txt == null)
			return null;

		var json = URLTools.toJson(txt);

		switch (AbstractMessage.MSG_TYPE.valueOf(json.getAsJsonObject().get("typeMessage").getAsString())) {
			case TALK :
				return serializer.fromJson(txt, TalkMessage.class);
			case CONNECT :
				return serializer.fromJson(txt, StatutMessage.class);
			case DISCONNECT :
				return serializer.fromJson(txt, StatutMessage.class);
			case CHANGESTATUS :
				return serializer.fromJson(txt, StatutMessage.class);
			case SEARCH :
				return serializer.fromJson(txt, SearchMessage.class);
			case SYSTEM :
				return serializer.fromJson(txt, TechnicalMessage.class);
			case DECK :
				return serializer.fromJson(txt, DeckMessage.class);
			case ANSWER :
				return serializer.fromJson(txt, SendStockMessage.class);
			default :
				return serializer.fromJson(txt, TalkMessage.class);
		}
	}

	protected String toJson(AbstractMessage obj) {
		return serializer.toJson(obj);
	}

	@Override
	public void join(Player p, String url, String channel) throws IOException {
		this.player = p;
		player.setOnlineConnectionTimeStamp(Instant.now().toEpochMilli());

		createConnection(url);
		switchAddress(channel);
		changeStatus(EnumPlayerStatus.ONLINE);

		logger.info("{} connected to server {}", player.getName(), url);
	}

	@Override
	public void changeStatus(EnumPlayerStatus selectedItem) throws IOException {
		player.setState(selectedItem);
		sendMessage(new StatutMessage(selectedItem));
	}

	@Override
	public void sendMessage(String text) throws IOException {
		sendMessage(new TalkMessage(text));

	}

}
