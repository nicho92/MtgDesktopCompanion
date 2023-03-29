package org.beta;

import org.apache.activemq.artemis.api.core.client.ActiveMQClient;

public class ActiveMQCliennt {

	public static void main(String[] args) throws Exception {
		var locator = ActiveMQClient.createServerLocator("tcp://127.0.0.1:8081");
		
		try( 
			  var factory =  locator.createSessionFactory();
			  var session = factory.createSession();
   			  var producer = session.createProducer("example");
			  var consumer = session.createConsumer("example");
		    )
		{
			var message = session.createMessage(true);
			 message.getBodyBuffer().writeString("Hello");
			 producer.send(message);		
			
			session.start();
			var msgReceived = consumer.receive();

			System.out.println("message = " + msgReceived.getBodyBuffer().readString());

			
		}
	
	}

}
