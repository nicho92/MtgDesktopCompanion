package org.magic.api.network.impl;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;

public class ActiveMQNetworkClient implements MTGNetworkClient {

	private ClientSession session;
	private ClientProducer producer;
	private ClientConsumer consumer;
	private ClientSessionFactory factory;
	private ServerLocator locator;

	
	@Override
	public String consume() throws IOException {
		try {
			session.start();
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}
		ClientMessage msg;
		try {
			msg = consumer.receive();
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}
		return msg.getBodyBuffer().readString();
	}

	@Override
	public List<String> listTopics() {
		
		return new ArrayList<>();
	}

	@Override
	public void join(Player p, String url) throws IOException {
		try {
			locator = ActiveMQClient.createServerLocator(url);
		} catch (Exception e) {
			throw new IOException(e); 
		}
		
		try {
			factory=  locator.createSessionFactory();
		} catch (Exception e) {
			throw new IOException(e); 
		}
		try {
			session = factory.createSession(p.getName(),"", true,true,true,true,5000);;
		} catch (ActiveMQException e) {
			throw new IOException(e); 
		}
	 
	}

	@Override
	public void switchTopic(String topicName) throws IOException {
		  try {
			producer = session.createProducer(topicName);
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}
		  try {
			consumer = session.createConsumer(topicName);
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void sendMessage(String text, Color c) throws IOException {
		 var message = session.createMessage(true);
		 message.getBodyBuffer().writeString(text);
		 try {
			producer.send(message);
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}		
		
	}

	@Override
	public void logout() throws IOException {
		try {
			session.close();
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}
		
		locator.close();
		
		factory.close();
		
		try {
			producer.close();
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}
		try {
			consumer.close();
		} catch (ActiveMQException e) {
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

	@Override
	public void disconnect() throws IOException {
		// TODO Auto-generated method stub
		
	}




}
