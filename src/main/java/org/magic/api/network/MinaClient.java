package org.magic.api.network;

import java.awt.Color;
import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.api.network.actions.ChangeStatusAction;
import org.magic.api.network.actions.JoinAction;
import org.magic.api.network.actions.SpeakAction;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATE;
import org.utils.patterns.observer.Observable;

public class MinaClient  extends Observable implements MTGNetworkClient {

	private IoConnector connector;
	private IoSession session;
	private Player p;

	private IoHandlerAdapter adapter = new IoHandlerAdapter() {
		@Override
		public void messageReceived(IoSession session, Object m) throws Exception {

			if (m instanceof Long l) {
				p.setId(l); // get id from server.
			} else {
				setChanged();
				notifyObservers(m);
			}
		}
	};

	@Override
	public Player getPlayer() {
		return p;
	}

	private IoSession getSession() {
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

	@Override
	public void join() {
		session.write(new JoinAction(p));
	}

	@Override
	public void sendMessage(String text) {
		var act = new SpeakAction(p, text);
		session.write(act);
	}

	@Override
	public void sendMessage(String text, Color c) {
		var act = new SpeakAction(p, text);
		act.setColor(c);
		session.write(act);
	}

	@Override
	public void logout() {
		session.closeOnFlush();
	}

	@Override
	public void changeStatus(STATE selectedItem) {
		p.setState(selectedItem);
		session.write(new ChangeStatusAction(p));
	}

	@Override
	public boolean isActive() {
		if(getSession()==null)
			return false;
		
		return getSession().isActive();
	}
	
}
