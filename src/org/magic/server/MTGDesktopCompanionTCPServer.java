package org.magic.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.magic.api.beans.MagicCard;
import org.magic.services.MagicFactory;


public class MTGDesktopCompanionTCPServer {


	 private static final int PORT = 8081;

	public static void main(String[] args) throws Exception {
		 
		MagicFactory.getInstance().getEnabledProviders().init();
		MagicFactory.getInstance().getEnabledDAO().init();
		
		IoAcceptor acceptor = new NioSocketAcceptor();
	        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory()));
	        acceptor.setHandler( new TimeServerHandler() );
	        acceptor.getSessionConfig().setReadBufferSize( 2048 );
	        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
	        acceptor.bind( new InetSocketAddress(PORT) );
	}

}
class TimeServerHandler extends IoHandlerAdapter
{
	
	@Override  
    public void sessionOpened(IoSession session) throws Exception {  
  
        System.out.println("client connection : " + session.getRemoteAddress());  
        session.write("client started");  
        session.write("Hello World!"); 
  
    }  
	
	public void sessionClosed(IoSession session) throws Exception {  
		  
        System.out.println("client disconnection : " +session.getRemoteAddress() + " is Disconnection");  
  
    }  
	
    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        cause.printStackTrace();
    }
    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
    	
    	System.out.println("message recived "  + message);
        String str = message.toString();
        if( str.trim().equalsIgnoreCase("quit") ) {
            session.closeNow();
            return;
        }
        
        if(str.trim().startsWith("search"))
        {
        	String[] cmd = str.trim().split(" ");
        	
        	String command = cmd[0];
        	String att = cmd[1];
        	String value = cmd[2];
        	
        	List<MagicCard> cards = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria(att, value, null);
        	session.write( cards);
        }
        
    }
    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
    {
        //System.out.println( "IDLE " + session.getIdleCount( status ));
    }
}

