package org.magic.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MagicFactory;


public class MTGDesktopCompanionTCPServer {

    static final Logger logger = LogManager.getLogger(MTGDesktopCompanionTCPServer.class.getName());

 	public static void main(String[] args) throws Exception {
		 
		MagicFactory.getInstance().getEnabledProviders().init();
		MagicFactory.getInstance().getEnabledDAO().init();
		
		IoAcceptor acceptor = new NioSocketAcceptor();
	        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory(Charset.forName( "UTF-8" ))));
	        acceptor.setHandler( new MTGConsoleHandler() );
	        acceptor.getSessionConfig().setReadBufferSize( 2048 );
	        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
	        acceptor.bind( new InetSocketAddress(Integer.parseInt(MagicFactory.getInstance().get("console-port"))) );
	        logger.info("Server startup on port " + MagicFactory.getInstance().get("console-port"));
	}
}



