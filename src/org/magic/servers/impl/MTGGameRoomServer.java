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
import org.magic.gui.game.network.actions.AbstractGamingAction;
import org.magic.gui.game.network.actions.ChangeDeckAction;
import org.magic.gui.game.network.actions.JoinAction;
import org.magic.gui.game.network.actions.ListPlayersAction;
import org.magic.gui.game.network.actions.PlayAction;
import org.magic.gui.game.network.actions.SpeakAction;

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
 	 		refreshPlayers(session); //refresh list users
 		}
 	 
 	 	@Override
 	 	public void messageReceived(IoSession session, Object message) throws Exception {
 	 		
 	 		if(message instanceof AbstractGamingAction)
 	 		{
 	 			AbstractGamingAction act = (AbstractGamingAction)message;
 	 			switch (act.getAct()) {
 	 				case PLAY: play(session,(PlayAction)act);break;
 	 				case JOIN: join(session, (JoinAction)act);break;
 	 				case CHANGE_DECK: changeDeck(session,(ChangeDeckAction)act);break;
 	 				case SPEAK: speak((SpeakAction)act);break;	
 	 				default:break;
				}
 	 		}
 	 	}
 	 	

 	  @Override
 	    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
 	    {
 	      //cause.printStackTrace();
 		  logger.error(cause);
 	      refreshPlayers(session);
 	    }
	};
	
	public void speak(SpeakAction sa)
	{
		for(IoSession s : acceptor.getManagedSessions().values())
				s.write(sa);
	}
	
	private void join(IoSession session, JoinAction ja)
	{
		Player p = ja.getPlayer();
			session.setAttribute("PLAYER",p);
			p.setId(session.getId());
			speak(new SpeakAction(p, " is now connected"));
	}
	
	
	protected void changeDeck(IoSession session, ChangeDeckAction cda) {
			Player p = (Player)session.getAttribute("PLAYER");
			p.setDeck(cda.getDeck());
			session.setAttribute("PLAYER", p);
		
	}

	protected void play(IoSession session, PlayAction p) {
		for(IoSession s : acceptor.getManagedSessions().values())
			if(p.getP2().getId()==s.getId())
				s.write(p);
		
	}

	

	public void refreshPlayers(IoSession session)
	{
		List<Player> list = new ArrayList<Player>();
			for(IoSession s : acceptor.getManagedSessions().values())
				list.add((Player)s.getAttribute("PLAYER"));
			
			session.write(new ListPlayersAction(list));
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
