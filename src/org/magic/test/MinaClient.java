package org.magic.test;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.magic.game.Player;

public class MinaClient extends IoHandlerAdapter{

   private IoConnector connector;
   private  IoSession session;
 	

   public IoSession getSession() {
	return session;
}
   
   public MinaClient() {
     connector = new NioSocketConnector();
     connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));  
     connector.setHandler(this);
     ConnectFuture connFuture = connector.connect(new InetSocketAddress("localhost", MinaServer.PORT));
     connFuture.awaitUninterruptibly();
     session = connFuture.getSession();
	}       
	     
   
   @Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println(message);
	}
   
	
	public static void main(String[] args) throws InterruptedException {
		MinaClient client = new MinaClient();
		  
		    Player p = new Player();
		    p.setName("Nicho");
		    client.getSession().write(p);
			//client.connector.dispose(true);
			
			
			
	}
	
}
