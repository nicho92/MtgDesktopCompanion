package org.magic.servers.impl;

import java.io.File;
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
import org.magic.services.MTGControler;


public class ConsoleServer extends AbstractMTGServer{

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
    IoAcceptor acceptor = new NioSocketAcceptor();
    
    @Override
    public String description() {
    	return "use mtg desktop companion via telnet connection";
    }
    
    
 	public static void main(String[] args) throws Exception {
		 
		MTGControler.getInstance().getEnabledProviders().init();
		MTGControler.getInstance().getEnabledDAO().init();
	
		ConsoleServer serv = new ConsoleServer();
		   		      serv.start();
	}
 	
 	public ConsoleServer() throws IOException {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			setProperty("SERVER-PORT", "5152");
			setProperty("IDLE-TIME", "10");
			setProperty("BUFFER-SIZE", "2048");
			setProperty("ENCODING", "UTF-8");
			setProperty("AUTOSTART", "false");
			save();
		}
	}
 	

	@Override
	public void start() throws IOException  {
		  acceptor = new NioSocketAcceptor();
 	      acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory(Charset.forName(getProperty("ENCODING")))));
          acceptor.getSessionConfig().setReadBufferSize( Integer.parseInt(getProperty("BUFFER-SIZE")) );
          acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, Integer.parseInt(getProperty("IDLE-TIME")) );
		  acceptor.setHandler( new MTGConsoleHandler() );
		  acceptor.bind( new InetSocketAddress(Integer.parseInt(getProperty("SERVER-PORT"))) );
		  logger.info("Server started on port " + getProperty("SERVER-PORT"));
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
		return getProperty("AUTOSTART").equals("true");
	}
}



