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
   private IoSession session;
   
   public IoSession getSession() {
		return session;
   }
	   
   public MinaClient(String server, int port) {
     connector = new NioSocketConnector();
     connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));  
     connector.setHandler(this);
     
     ConnectFuture connFuture = connector.connect(new InetSocketAddress(server, port));
     connFuture.awaitUninterruptibly();
     session = connFuture.getSession();
	}       
   
   	
   	public void messageReceived(IoSession session, Object message) throws Exception {
   		
	}
   
	
	public void join(Player p)
	{
	   session.write(p);
	}
	
}
