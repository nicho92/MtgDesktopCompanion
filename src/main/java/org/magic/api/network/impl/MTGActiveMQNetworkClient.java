package org.magic.api.network.impl;

import java.awt.Color;
import java.io.IOException;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.game.model.Player.STATUS;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.utils.patterns.observer.Observer;

public class MTGActiveMQNetworkClient implements MTGNetworkClient{
	private ClientProducer producer;
	private ClientConsumer consumer;
	private ClientSessionFactory factory;
	private ClientSession session;
	private ServerLocator locator;
	private Logger logger = MTGLogger.getLogger(MTGActiveMQNetworkClient.class);
	
	
	@Override
	public void join() throws IOException {
		try {
		
		 factory =  locator.createSessionFactory();
		 session = factory.createSession();
		 producer = session.createProducer("welcome");
		 consumer = session.createConsumer("welcome");
		}
		catch(Exception e)
		{
			logger.error(e);
			throw new IOException(e);
		}
	}
	
	
	public MTGActiveMQNetworkClient(String url, int port) throws IOException {
		try {
			locator = ActiveMQClient.createServerLocator(url+":"+port);
		} catch (Exception e) {
			logger.error(e);
			throw new IOException(e);
		}
	}
	

	@Override
	public void sendMessage(String text) throws IOException {
		 var message = session.createMessage(true);
		  message.getBodyBuffer().writeString(text);
		 try {
			producer.send(message);
		} catch (ActiveMQException e) {
			logger.error(e);
			throw new IOException(e);
		}		
	}

	@Override
	public void sendMessage(String text, Color c) throws IOException {
		sendMessage(text);
		
	}

	@Override
	public void logout()  throws IOException{
		try{ 
			session.close();
			producer.close();
			consumer.close();
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public void changeStatus(STATUS selectedItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isActive() {
		return !session.isClosed();
	}

}
