package org.magic.main;

import java.awt.Color;
import java.io.IOException;

import org.magic.api.beans.game.Player;
import org.magic.api.beans.messages.TalkMessage;
import org.magic.api.interfaces.MTGIA;
import org.magic.api.network.impl.ActiveMQNetworkClient;
import org.magic.servers.impl.ActiveMQServer;
import org.magic.services.MTGControler;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;

public class IAVirtualUser {

	
	public static void main(String[] args) throws Exception {
		MTGControler.getInstance().init();
		
		var address = "tcp://my.mtgcompanion.org:61616";
			init(address);
	}

	public static void init(String address) throws IOException {
		
		var client = new ActiveMQNetworkClient();

		 var plug = MTG.getEnabledPlugin(MTGIA.class);
		
		
		var ia = AiServices.builder(MTGIA.class)
        .chatModel(plug.getEngine(null))
        .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
        .systemMessage("You are a Magic The Gathering Assistant")
        .build();
		
		var p = new Player(plug.getName());
			 p.setAvatar(ImageTools.toImage(plug.getIcon()));
			
		client.join(p,address,ActiveMQServer.DEFAULT_TOPIC);
		
		while(client.isActive())
		{
			var msg = client.consume();
			if(msg instanceof TalkMessage t && !t.getMessage().isEmpty())
			{
				if(!msg.getAuthor().getName().equals(plug.getName()))
				{
					var resp = ia.ask(t.getMessage());
					var tresp = new TalkMessage(resp,Color.ORANGE);
					client.sendMessage(tresp);
				}
			}
		}
		
	}
	
}
