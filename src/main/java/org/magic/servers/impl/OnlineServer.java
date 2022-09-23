package org.magic.servers.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.api.network.actions.AbstractNetworkAction;
import org.magic.api.network.actions.ChangeStatusAction;
import org.magic.api.network.actions.JoinAction;
import org.magic.api.network.actions.ListPlayersAction;
import org.magic.api.network.actions.SpeakAction;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;
import org.magic.services.MTGConstants;

public class OnlineServer extends AbstractMTGServer {

	private static final String SERVER_PORT = "SERVER-PORT";
	private static final String PLAYER = "PLAYER";
	private static final String MAX_CLIENT = "MAX_CLIENT";


	private IoAcceptor acceptor;
	private IoHandlerAdapter adapter = new IoHandlerAdapter() {

		private void playerUpdate(ChangeStatusAction act) {
			((Player) acceptor.getManagedSessions().get(act.getInitiator().getId()).getAttribute(PLAYER)).setState(act.getInitiator().getState());
		}

		private void join(IoSession session, JoinAction ja) {
			if (!getString(MAX_CLIENT).equals("0") && acceptor.getManagedSessions().size() >= getInt(MAX_CLIENT)) {
				session.write(new SpeakAction(null, "Number of users reached (" + getString(MAX_CLIENT) + ")"));
				session.closeOnFlush();
				return;
			}
			ja.getInitiator().setState(STATUS.CONNECTED);
			ja.getInitiator().setId(session.getId());
			ja.getInitiator().setOnlineConnectionDate(new Date());
			session.setAttribute(PLAYER, ja.getInitiator());
			execute(new SpeakAction(ja.getInitiator(), " is now connected"));
			session.write(session.getId());

			refreshPlayers(session);
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			logger.debug("New Session {} ",session.getRemoteAddress());
			session.write(new SpeakAction(null, getString("WELCOME_MESSAGE")));
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			refreshPlayers(session); // refresh list users
		}

		private void execute(AbstractNetworkAction act) {

			logger.debug("Send {} to {} ",act,acceptor.getManagedSessions().values());


			for (IoSession s : acceptor.getManagedSessions().values())
				s.write(act);
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			logger.info(message);
			if (message instanceof AbstractNetworkAction act) {
				switch (act.getAct()) {
					case JOIN:
						join(session, (JoinAction) act);
						break;
					case CHANGE_STATUS:
						playerUpdate((ChangeStatusAction) act);
						break;
					default:execute(act);
						break;
					}
			}
		}


		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			logger.error("error session", cause);
			refreshPlayers(session);
		}
	};

	@Override
	public String description() {
		return "Enable MTGCompanion users to share MTG datas";
	}



	public void refreshPlayers(IoSession session) {
		List<Player> list = new ArrayList<>();
		for (IoSession s : acceptor.getManagedSessions().values().stream().filter(s->session.getId() != ((Player) s.getAttribute(PLAYER)).getId()).toList()) {
			list.add((Player) s.getAttribute(PLAYER));
		}

		session.write(new ListPlayersAction(list));
	}

	public OnlineServer() throws IOException {
		acceptor = new NioSocketAcceptor();
		acceptor.setHandler(adapter);
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		acceptor.getSessionConfig().setReadBufferSize(getInt("BUFFER-SIZE"));
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,getInt("IDLE-TIME"));

	}

	@Override
	public void start() throws IOException {
		acceptor.bind(new InetSocketAddress(getInt(SERVER_PORT)));
		logger.info("Server started on port {}",getString(SERVER_PORT));
	}

	@Override
	public void stop() throws IOException {
		logger.info("Server closed");
		acceptor.unbind();
	}

	@Override
	public boolean isAlive() {
		return acceptor.isActive();
	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}

	@Override
	public String getName() {
		return "MTG Online Server";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(SERVER_PORT, "18567",
				 "IDLE-TIME", "10",
				 "BUFFER-SIZE", "2048",
				 "AUTOSTART", "false",
				 "WELCOME_MESSAGE", "Welcome to my MTG Online services",
				 MAX_CLIENT, "0");

	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_NETWORK;
	}

	@Override
	public String getVersion() {
		return "2.0.21";
	}

}
