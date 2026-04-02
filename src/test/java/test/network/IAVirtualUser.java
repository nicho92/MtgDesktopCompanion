package test.network;

import java.awt.Color;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
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

	@Before
	public void initTest() throws Exception
	{
			MTGControler.getInstance().init();
	}
	
	@Test
	public void join() throws IOException
	{
		var client = new ActiveMQNetworkClient();
		var address = "tcp://my.mtgcompanion.org:61616";
		 var plug = MTG.getEnabledPlugin(MTGIA.class);
		
		
		var ia = AiServices.builder(MTGIA.class)
        .chatModel(plug.getEngine(null))
        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
        .build();
		
		
		
		var p = new Player(plug.getName());
			p.setAvatar(ImageTools.toImage(plug.getIcon()));
			
			
			
		client.join(p,address,ActiveMQServer.DEFAULT_TOPIC);
		
		while(client.isActive())
		{
			var msg = client.consume();
			if(msg instanceof TalkMessage t)
			{
				if(!msg.getAuthor().getName().equals(plug.getName()))
				{
					var resp = ia.ask(t.getMessage());
					var tresp = new TalkMessage(resp,Color.BLUE);
					client.sendMessage(tresp);
				}
			}
		}
	}
	
}
