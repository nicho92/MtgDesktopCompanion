package org.magic.api.network.impl;
import java.awt.Color;
import java.io.IOException;
import java.time.Instant;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.commons.lang3.RandomUtils;
import org.magic.api.beans.JsonMessage;
import org.magic.api.beans.JsonMessage.MSG_TYPE;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.AbstractNetworkProvider;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;

public class ActiveMQNetworkClient extends AbstractNetworkProvider {

	private ClientSession session;
	private ClientProducer producer;
	private ClientConsumer consumer;
	private ClientSessionFactory factory;
	private ServerLocator locator;
	
	private JsonExport serializer = new JsonExport();
	
	
	@Override
	protected void joiningConnection(String url,String adress) throws IOException {
		
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
			session = factory.createSession(player.getName(),"password",false,true,true,true, 0, "ID-"+player.getId());
			
		} catch (ActiveMQException e) {
			throw new IOException(e); 
		}
		
		try {
			session.start();
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}
	}
	
	
	@Override
	public void switchAddress(String adress) throws IOException {
		try {
			producer = session.createProducer(adress);
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}

		try {
			var cqc = createQueueConf(adress);
			session.createQueue(cqc);
			consumer = session.createConsumer(cqc.getName());
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}
	}
	

	private QueueConfiguration createQueueConf(String adress) {
		var cqc = new QueueConfiguration();
		cqc.setAddress(adress);
		cqc.setName("queue-"+player.getId());
		cqc.setDurable(true);
		cqc.setAutoCreated(true);
		cqc.setConfigurationManaged(true);
		cqc.setRoutingType(RoutingType.MULTICAST);
		cqc.setAutoCreateAddress(true);
		
		return cqc;
	}

	@Override
	public JsonMessage consume() throws IOException {
		
		ClientMessage msg;
		try {
			msg = consumer.receive();
			logger.debug("consume {}",msg);
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}
		
		if(msg==null)
			return null;
		
		return   serializer.fromJson(msg.getBodyBuffer().readString(),JsonMessage.class);
	}

	

	@Override
	public void sendMessage(JsonMessage obj) throws IOException {
		var message = session.createMessage(obj.getTypeMessage()==MSG_TYPE.TALK);
		var jsonMsg = serializer.toJson(obj);
		message.getBodyBuffer().writeString(jsonMsg);
		
		try {
			producer.send(message);
			logger.debug("send {}",obj);
		} catch (ActiveMQException e) {
			throw new IOException(e);
		}		
	}
	 
	

	@Override
	public void logout() throws IOException {
		try {
			sendMessage(new JsonMessage(player,"disconnect",Color.black,MSG_TYPE.DISCONNECT));
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
		return session!=null && !session.isClosed();
	}

	
	@Override
	public String getName() {
		return "ArtemisMQ";
	}

	@Override
	public boolean isEnable() {
		return true;
	}
	



}
