package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATE;
import org.magic.game.network.actions.AbstractNetworkAction;
import org.magic.game.network.actions.ChangeDeckAction;
import org.magic.game.network.actions.ChangeStatusAction;
import org.magic.game.network.actions.JoinAction;
import org.magic.game.network.actions.ListPlayersAction;
import org.magic.game.network.actions.ReponseAction;
import org.magic.game.network.actions.RequestPlayAction;
import org.magic.game.network.actions.ShareDeckAction;
import org.magic.game.network.actions.SpeakAction;
import org.magic.services.MTGLogger;

public class MTGGameRoomServer extends AbstractMTGServer{
 
	private IoAcceptor acceptor;
	private IoHandlerAdapter adapter = new IoHandlerAdapter() {
 		
		private void playerUpdate(ChangeStatusAction act) {
			((Player)acceptor.getManagedSessions().get(act.getPlayer().getId()).getAttribute("PLAYER")).setState(act.getPlayer().getState());	
		}
		

		private void sendDeck(IoSession session, ShareDeckAction act) {
			acceptor.getManagedSessions().get(act.getTo().getId()).write(act);
		}

		
		private void join(IoSession session, JoinAction ja)
		{
			if(!props.getProperty("MAX_CLIENT").equals("0")&&acceptor.getManagedSessions().size()>=Integer.parseInt(props.getProperty("MAX_CLIENT")))
			{
					session.write(new SpeakAction(null,"Number of users reached (" + props.getProperty("MAX_CLIENT") +")"));
					session.closeOnFlush();
					return;
			}
			ja.getPlayer().setState(STATE.CONNECTED);
			ja.getPlayer().setId(session.getId());
			session.setAttribute("PLAYER",ja.getPlayer());
			speak(new SpeakAction(ja.getPlayer(), " is now connected"));
			session.write(session.getId());
			
			refreshPlayers(session);
		}
		
		@Override
 		public void sessionCreated(IoSession session) throws Exception {
 			logger.debug("New Session " + session.getRemoteAddress());
 			session.write(new SpeakAction(null, props.getProperty("WELCOME_MESSAGE")));
 		}
 	 	
 	 	@Override
 		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
 	 		refreshPlayers(session); //refresh list users
 		}
 	 
 	 	@Override
 	 	public void messageReceived(IoSession session, Object message) throws Exception {
 	 		logger.info(message);
 	 		if(message instanceof AbstractNetworkAction)
 	 		{
 	 			AbstractNetworkAction act = (AbstractNetworkAction)message;
 	 			switch (act.getAct()) {
 	 				case REQUEST_PLAY: requestGaming((RequestPlayAction)act);break;
 	 				case RESPONSE: response((ReponseAction)act);break;
 	 				case JOIN: join(session, (JoinAction)act);break;
 	 				case CHANGE_DECK: changeDeck(session,(ChangeDeckAction)act);break;
 	 				case SPEAK: speak((SpeakAction)act);break;	
 	 				case CHANGE_STATUS:playerUpdate((ChangeStatusAction)act);break;
 	 				case SHARE:sendDeck(session,(ShareDeckAction)act);break;
 	 				default:break;
				}
 	 		}
 	 	}
 	 	
 	 	private void response(ReponseAction act) {
 			IoSession s = acceptor.getManagedSessions().get(act.getRequest().getRequestPlayer().getId());
 			IoSession s2 = acceptor.getManagedSessions().get(act.getRequest().getAskedPlayer().getId());
 			s.write(act);
 			s2.write(act);
 		}
 		

 	 	@Override
 	    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
 	    {
 	      MTGLogger.printStackTrace(cause);
 		  logger.error(cause);
 	      refreshPlayers(session);
 	    }
	};
	
	
 	@Override
    public String description() {
    	return "Enable local player room server";
    }
	
	public void speak(SpeakAction sa)
	{
		for(IoSession s : acceptor.getManagedSessions().values())
			s.write(sa);
	}
	

	
	
	
	protected void changeDeck(IoSession session, ChangeDeckAction cda) {
			Player p = (Player)session.getAttribute("PLAYER");
			p.setDeck(cda.getDeck());
			session.setAttribute("PLAYER", p);
		
	}

	protected void requestGaming(RequestPlayAction p) {
		IoSession s = acceptor.getManagedSessions().get(p.getAskedPlayer().getId());
		s.write(p);
		
	}


	
	public void refreshPlayers(IoSession session)
	{
		List<Player> list = new ArrayList<>();
			for(IoSession s : acceptor.getManagedSessions().values())
			{
				if(session.getId()!=((Player)s.getAttribute("PLAYER")).getId())
					list.add((Player)s.getAttribute("PLAYER"));
			}
			
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
			props.put("MAX_CLIENT", "0");
			save();
		}
    	acceptor = new NioSocketAcceptor();
        acceptor.setHandler(adapter);
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

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

}
