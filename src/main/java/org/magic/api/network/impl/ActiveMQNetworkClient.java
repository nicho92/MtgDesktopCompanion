package org.magic.api.network.impl;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.JsonMessage;
import org.magic.api.beans.JsonMessage.MSG_TYPE;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;
import org.magic.services.logging.MTGLogger;

public class ActiveMQNetworkClient implements MTGNetworkClient {

	private ClientSession session;
	private ClientProducer producer;
	private ClientConsumer consumer;
	private ClientSessionFactory factory;
	private ServerLocator locator;
	private Player player;
	private Logger logger = MTGLogger.getLogger(ActiveMQNetworkClient.class);
	private JsonExport serializer = new JsonExport();
	
	
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
	public void join(Player p, String url,String topic) throws IOException {
		this.player = p;
		player.setOnlineConnectionDate(new Date());
		player.setState(STATUS.CONNECTED);
		player.setId(RandomUtils.nextLong());
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
			session = factory.createSession(p.getName(),"password",false,true,true,false, 0, "ID-"+player.getId());
		} catch (ActiveMQException e) {
			throw new IOException(e); 
		}
		
		switchTopic(topic);
		
		sendMessage(new JsonMessage(player,"connected",Color.black,MSG_TYPE.CONNECT));
		
	}
	

	@Override
	public void changeStatus(STATUS selectedItem) throws IOException {
		player.setState(selectedItem);
		sendMessage(new JsonMessage(player,selectedItem.name(),Color.black,MSG_TYPE.CHANGESTATUS));
		
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
	public void sendMessage(JsonMessage obj) throws IOException {
		var message = session.createMessage(obj.getTypeMessage()==MSG_TYPE.TALK);
		var jsonMsg = serializer.toJson(obj);
		message.getBodyBuffer().writeString(jsonMsg);
		
		try {
			producer.send(message);
			logger.info("send {}",obj);
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}		
	}
	 
	
	
	@Override
	public void sendMessage(String text, Color c) throws IOException {
	
		sendMessage(new JsonMessage(player,text,c,MSG_TYPE.TALK));
		
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
	public boolean isActive() {
		return !session.isClosed();
	}




}
