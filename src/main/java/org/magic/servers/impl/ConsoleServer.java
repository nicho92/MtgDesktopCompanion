package org.magic.servers.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGConstants;

public class ConsoleServer extends AbstractMTGServer {

	private static final String SERVER_PORT = "SERVER-PORT";

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	IoAcceptor acceptor = new NioSocketAcceptor();

	@Override
	public String description() {
		return "use mtg desktop companion via telnet connection";
	}

	@Override
	public void start() throws IOException {
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TextLineCodecFactory(MTGConstants.DEFAULT_ENCODING)));
		acceptor.getSessionConfig().setReadBufferSize(getInt("BUFFER-SIZE"));
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, getInt("IDLE-TIME"));
		var handler = new MTGConsoleHandler();
		handler.setWelcomeMessage(getString("STARTUP_MESSAGE"));
		acceptor.setHandler(handler);
		acceptor.bind(new InetSocketAddress(getInt(SERVER_PORT)));
		logger.info("Server started on port " + getString(SERVER_PORT));
	}

	@Override
	public void stop() {
		acceptor.unbind();

	}

	@Override
	public boolean isAlive() {
		try {
			return acceptor.isActive();
		} catch (Exception e) {
			logger.error(e);
			return false;
		}

	}

	@Override
	public String getName() {
		return "Console";
	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}

	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(SERVER_PORT, "5152",
					"IDLE-TIME", "10",
					"BUFFER-SIZE", "2048",
					"AUTOSTART", "false",
					"STARTUP_MESSAGE", "Welcome to MTG Desktop Companion Server");
	}


}
