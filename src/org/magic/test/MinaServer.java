package org.magic.test;

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
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.magic.game.Player;

public class MinaServer extends IoHandlerAdapter {

 public static final int PORT = 18567;
 
 	private List<Player> players;
 
 	  @Override
 	    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
 	    {
 	        cause.printStackTrace();
 	    }
 
 	public MinaServer() throws IOException {

 		players= new ArrayList<Player>();
 		
 	      IoAcceptor acceptor = new NioSocketAcceptor();
 	        acceptor.setHandler(this);
 	        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
 	        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
 	        //acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));
 	        acceptor.getSessionConfig().setReadBufferSize( 2048 );
 	        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
 	        acceptor.bind( new InetSocketAddress(PORT) );
 	        
 	        System.out.println("Server started...");
	}
 	
 	@Override
	public void sessionCreated(IoSession session) throws Exception {
 		
 		System.out.println("Session created");
	}
 	
 	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
 		System.out.println( "IDLE " + session.getIdleCount( status ));
	}
 
 	@Override
 	public void messageReceived(IoSession session, Object message) throws Exception {
 		
 		if(message instanceof Player)
 		{
 			Player p = (Player)message;
 			players.add(p);
 			System.out.println("connexion de " + p.getName() );
 			session.write(players);
 		}
 		
 	}
 	
	
	 public static void main(String[] args) throws IOException {
		 new MinaServer();
	 }

}
