package org.magic.console;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.magic.servers.impl.ConsoleServer;
import org.magic.tools.MTGLogger;

public class MTGConsoleHandler extends IoHandlerAdapter
{
	ClassLoader classLoader = ConsoleServer.class.getClassLoader();

	public static String[] att_cards ={"name","fullType", "editions[0].rarity", "colors", "cost"};
	public static String[] att_set ={"id","set", "cardCount", "releaseDate", "block"};
	public static String[] att_cols = {"name"};
	public static String[] att_shop ={"name","description","price","shopName"};
	public static String[] att_shake={"name","ed","price","percentDayChange","priceDayChange"};
	
	Logger logger = MTGLogger.getLogger(this.getClass());

	private List<String> history;
	    
	public MTGConsoleHandler() {
		history = new ArrayList<String>();
	}    
	
	
	public List<String> getHistory()
	{
		return history;
	}
	

	@Override  
    public void sessionOpened(IoSession session) throws Exception {  
        session.write("Welcome to MTG Desktop Companion Server\r\n");
    }  
	
	public void sessionClosed(IoSession session) throws Exception {  
		logger.debug("client disconnection : " +session.getRemoteAddress() + " is Disconnection");  
  
    }  
	
    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
    	if(session.getCurrentWriteMessage()!=null)
    	{
    	session.write(cause+"\n");
    	logger.error(cause);
    	}
    }
    
    public Command commandFactory(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException
   	{
   		String clazz = Character.toUpperCase(name.charAt(0)) + name.substring(1);
   		Class myCommand = classLoader.loadClass("org.magic.console.commands."+clazz);
        return (Command)myCommand.newInstance();
   	}
    
    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
    	logger.debug("message = " + message);
    	
    	if(message==null)
    		return;
    	
    	if(message.toString().equals("cls")|| message.toString().equals("clear"))
		{
    		session.write("\033[2J");
		}
    	else
    	{
            String line = message.toString();
            String[] commandeLine = line.split(" ");
    		Command c = commandFactory(commandeLine[0]);
    		c.run(commandeLine,session,this);
    		c.quit();
    		history.add(line);
    	}

    }
}