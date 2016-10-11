package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MagicFactory;


public class ConsoleServer extends AbstractMTGServer{

    static final Logger logger = LogManager.getLogger(ConsoleServer.class.getName());

    IoAcceptor acceptor = new NioSocketAcceptor();
    
 	public static void main(String[] args) throws Exception {
		 
		MagicFactory.getInstance().getEnabledProviders().init();
		MagicFactory.getInstance().getEnabledDAO().init();
	
		ConsoleServer serv = new ConsoleServer();
		   		     serv.start();
	}
 	
 	public ConsoleServer() throws NumberFormatException, IOException {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("SERVER-PORT", "5152");
			props.put("IDLE-TIME", "10");
			props.put("BUFFER-SIZE", "2048");
			props.put("ENCODING", "UTF-8");
			props.put("AUTOSTART", "false");
			save();
		}
		
		if(props.getProperty("AUTOSTART").equalsIgnoreCase("true"))
			start();

	}
 	
 	

	@Override
	public void start() throws NumberFormatException, IOException {
		  acceptor = new NioSocketAcceptor();
 	      acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory(Charset.forName(props.getProperty("ENCODING")))));
          acceptor.getSessionConfig().setReadBufferSize( Integer.parseInt(props.getProperty("BUFFER-SIZE")) );
          acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, Integer.parseInt(props.getProperty("IDLE-TIME")) );
		  acceptor.setHandler( new MTGConsoleHandler() );
		  acceptor.bind( new InetSocketAddress(Integer.parseInt(props.getProperty("SERVER-PORT"))) );
		  logger.info("Server startup on port " + props.getProperty("SERVER-PORT"));
	}

	@Override
	public void stop() {
		acceptor.unbind();
		
	}

	@Override
	public boolean isAlive() {
		try{
		return acceptor.isActive();
		}catch(Exception e)
		{
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
		return props.getProperty("AUTOSTART").equals("true");
	}
}



