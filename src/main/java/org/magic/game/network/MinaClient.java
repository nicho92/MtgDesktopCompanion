package org.magic.game.network;

import java.awt.Color;
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
import org.magic.game.model.Player.STATE;
import org.magic.game.network.actions.ChangeDeckAction;
import org.magic.game.network.actions.ChangeStatusAction;
import org.magic.game.network.actions.JoinAction;
import org.magic.game.network.actions.ReponseAction;
import org.magic.game.network.actions.ReponseAction.CHOICE;
import org.magic.game.network.actions.RequestPlayAction;
import org.magic.game.network.actions.ShareDeckAction;
import org.magic.game.network.actions.SpeakAction;

public class MinaClient extends Observable {

   private IoConnector connector;
   private IoSession session;
   private Player p;
   
   private IoHandlerAdapter adapter = new IoHandlerAdapter() {
	 	
	   	public void messageReceived(IoSession session, Object m) throws Exception {
	   		
	   		if(m instanceof Long)
	   		{
	   			p.setId((Long)m); //get id from server.
	   		}
	   		else
	   		{
	   			setChanged();
	   			notifyObservers(m);
	   		}
		}
   };
   
   
   public Player getP() {
	return p;
}

   public IoSession getSession() {
		return session;
   }
	   
   public MinaClient(String server, int port) {
	   
	 p = new Player();
     connector = new NioSocketConnector();
     connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));  
     connector.setHandler(adapter);
     
     ConnectFuture connFuture = connector.connect(new InetSocketAddress(server, port));
     connFuture.awaitUninterruptibly();
     session = connFuture.getSession();
	}
   
	public void join()
	{
	   session.write(new JoinAction(p));
	}

	public void updateDeck(MagicDeck d) {
		p.setDeck(d);
		session.write(new ChangeDeckAction(p, d));
	}

	public void sendMessage(String text) {
		SpeakAction act = new SpeakAction(p,text);
		session.write(act);
	}
	
	public void sendDeck(MagicDeck d, Player to)
	{
		session.write(new ShareDeckAction(p, d,to));
	}
	
	public void sendMessage(String text,Color c) {
		SpeakAction act = new SpeakAction(p,text);
		act.setColor(c);
		session.write(act);
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

	public void changeStatus(STATE selectedItem) {
		p.setState(selectedItem);
		session.write(new ChangeStatusAction(p));
	}
	
}
