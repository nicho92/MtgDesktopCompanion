package org.magic.gui.game.network;

import java.net.InetSocketAddress;
import java.util.Observable;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.magic.api.beans.MagicDeck;
import org.magic.game.Player;

public class MinaClient extends Observable {

   private IoConnector connector;
   private IoSession session;
   
   private IoHandlerAdapter adapter = new IoHandlerAdapter() {
	 	
	   	public void messageReceived(IoSession session, Object m) throws Exception {
	   		setChanged();
	   		notifyObservers(m);
		}
   };
   
   
   public IoSession getSession() {
		return session;
   }
	   
   public MinaClient(String server, int port) {
	  
     connector = new NioSocketConnector();
     connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));  
     connector.setHandler(adapter);
     
     ConnectFuture connFuture = connector.connect(new InetSocketAddress(server, port));
     connFuture.awaitUninterruptibly();
     session = connFuture.getSession();
	}
   
	public void join(Player p)
	{
	   session.write(p);
	}

	public void updateDeck(MagicDeck d) {
		session.write(d);
	}

	public void sendMessage(String text) {
		session.write(text);
	}
	
	public void logout()
	{
		session.closeOnFlush();
	}
	
}
