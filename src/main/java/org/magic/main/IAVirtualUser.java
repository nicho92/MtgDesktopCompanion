package org.magic.main;

import java.io.IOException;
import org.magic.api.beans.game.Player;
import org.magic.api.beans.messages.TalkMessage;
import org.magic.api.interfaces.MTGIA;
import org.magic.api.network.impl.ActiveMQNetworkClient;
import org.magic.servers.impl.ActiveMQServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;

public class IAVirtualUser {

	private static ActiveMQNetworkClient client;

	public static void main(String[] args) throws Exception {
		MTGControler.getInstance().init();
		init(MTGConstants.MTG_CHAT_DEFAULT_URI);
	}

	public static void stop() throws IOException {
		client.logout();
	}

	public static void init(String address) throws IOException {

		client = new ActiveMQNetworkClient();
		var plug = MTG.getEnabledPlugin(MTGIA.class);
		var ia = plug.toAssistant();

		var p = new Player(plug.getName());
		p.setAvatar(ImageTools.toImage(plug.getIcon()));
		p.setColor(plug.getChatColor());

		client.join(p, address, ActiveMQServer.DEFAULT_TOPIC);

		while (client.isActive()) {
			var msg = client.consume();

			// response only to my question
			if (msg instanceof TalkMessage t && !t.getMessage().isEmpty()
					&& t.getAuthor().getName().equals(MTGControler.getInstance().getProfilPlayer().getName()))
				client.sendMessage(new TalkMessage(ia.ask(t.getMessage())));

		}

	}

}
