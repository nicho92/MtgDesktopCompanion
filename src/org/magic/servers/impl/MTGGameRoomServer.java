package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.game.Player;

public class MTGGameRoomServer extends AbstractMTGServer{
 static final Logger logger = LogManager.getLogger(MTGGameRoomServer.class.getName());
 private IoAcceptor acceptor;
 private IoHandlerAdapter adapter = new IoHandlerAdapter() {
 		@Override
 		public void sessionCreated(IoSession session) throws Exception {
 			logger.debug("New Session " + session.getRemoteAddress());
 			session.write(props.getProperty("WELCOME_MESSAGE"));
 		}
 	 	
 	 	@Override
 		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
 	 		initPlayer(session); //refresh list users
 		}
 	 
 	 	@Override
 	 	public void messageReceived(IoSession session, Object message) throws Exception {
 	 		if(message instanceof Player)
 	 		{
 	 			Player p = (Player)message;
 	 			session.setAttribute("PLAYER", p);
 	 			sendRoomMessage(p + " is now connected");
 	 		}
 	 		else if(message instanceof MagicDeck)
 	 		{
 	 			Player p = (Player)session.getAttribute("PLAYER");
 	 			p.setDeck((MagicDeck)message);
 	 			session.setAttribute("PLAYER", p);
 	 		}
 	 		else if (message instanceof String)
 	 		{
 	 			sendRoomMessage(message);
 	 		}
 	 	}
 	 	

 	  @Override
 	    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
 	    {
 	      logger.error(cause);
 	      initPlayer(session);
 	    }
	};
	
	public void sendRoomMessage(Object message)
	{
		for(IoSession s : acceptor.getManagedSessions().values())
				s.write(message.toString());
	}
	
	
	public void initPlayer(IoSession session)
	{
		List<Player> list = new ArrayList<Player>();
			for(IoSession s : acceptor.getManagedSessions().values())
				list.add((Player)s.getAttribute("PLAYER"));
			
			session.write(list);
	}
	
	public MTGGameRoomServer() throws IOException {
		
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("SERVER-PORT", "18567");
			props.put("IDLE-TIME", "10");
			props.put("BUFFER-SIZE", "2048");
			props.put("AUTOSTART", "false");
			props.put("WELCOME_MESSAGE", "Welcome to my MTG Desktop Gaming Room");
			save();
		}
		
		
    	acceptor = new NioSocketAcceptor();
        acceptor.setHandler(adapter);
        //acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        acceptor.getSessionConfig().setReadBufferSize( Integer.parseInt(props.getProperty("BUFFER-SIZE")) );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, Integer.parseInt(props.getProperty("IDLE-TIME")) );
	}
 	
 	
	
	 public static void main(String[] args) throws Exception {
		 new MTGGameRoomServer().start();
	 }



	@Override
	public void start() throws Exception {
		 acceptor.bind( new InetSocketAddress(Integer.parseInt(props.getProperty("SERVER-PORT"))) );
		 logger.info("Server started on port " + props.getProperty("SERVER-PORT") +" ...");
		
	}



	@Override
	public void stop() throws Exception {
		logger.info("Server closed");
		acceptor.unbind();
	}



	@Override
	public boolean isAlive() {
		return acceptor.isActive();
	}



	@Override
	public boolean isAutostart() {
		return props.getProperty("AUTOSTART").equals("true");
	}



	@Override
	public String getName() {
		return "MTG Game Server";
	}

}
