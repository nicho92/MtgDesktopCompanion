package org.magic.servers.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.console.MTGConsoleHandler;

public class ConsoleServer extends AbstractMTGServer {

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	IoAcceptor acceptor = new NioSocketAcceptor();

	@Override
	public String description() {
		return "use mtg desktop companion via telnet connection";
	}


	public ConsoleServer() throws IOException {
		super();
	}

	@Override
	public void start() throws IOException {
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName(getString("ENCODING")))));
		acceptor.getSessionConfig().setReadBufferSize(Integer.parseInt(getString("BUFFER-SIZE")));
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, Integer.parseInt(getString("IDLE-TIME")));
		acceptor.setHandler(new MTGConsoleHandler());
		acceptor.bind(new InetSocketAddress(Integer.parseInt(getString("SERVER-PORT"))));
		logger.info("Server started on port " + getString("SERVER-PORT"));
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
		return "Console Server";
	}

	@Override
	public boolean isAutostart() {
		return getString("AUTOSTART").equals("true");
	}

	@Override
	public void initDefault() {
		setProperty("SERVER-PORT", "5152");
		setProperty("IDLE-TIME", "10");
		setProperty("BUFFER-SIZE", "2048");
		setProperty("ENCODING", "UTF-8");
		setProperty("AUTOSTART", "false");

	}

	@Override
	public String getVersion() {
		return "0.5";
	}
}
