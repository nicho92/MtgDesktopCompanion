package org.magic.game.network;

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
import org.magic.game.model.Player;
import org.magic.game.network.actions.ChangeDeckAction;
import org.magic.game.network.actions.JoinAction;
import org.magic.game.network.actions.ReponseAction;
import org.magic.game.network.actions.ReponseAction.CHOICE;
import org.magic.game.network.actions.RequestPlayAction;
import org.magic.game.network.actions.SpeakAction;

public class MinaClient extends Observable {

   private IoConnector connector;
   private IoSession session;
   private Player p;
   
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
	   this.p=p;
	   p.setId(session.getId());
	   session.write(new JoinAction(p));
	}

	public void updateDeck(MagicDeck d) {
		session.write(new ChangeDeckAction(p, d));
	}

	public void sendMessage(String text) {
		session.write(new SpeakAction(p,text));
	}
	
	public void logout()
	{
		session.closeOnFlush();
	}

	public void requestPlay(Player otherplayer) {
		
		session.write(new RequestPlayAction(p,otherplayer));
		
	}

	public void reponse(RequestPlayAction pa,CHOICE c)
	{
		session.write(new ReponseAction(pa, c));
	}
	
}
