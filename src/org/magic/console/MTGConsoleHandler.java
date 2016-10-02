package org.magic.console;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.magic.server.MTGDesktopCompanionTCPServer;

public class MTGConsoleHandler extends IoHandlerAdapter
{
	ClassLoader classLoader = MTGDesktopCompanionTCPServer.class.getClassLoader();

	public static String[] att_cards ={"name","fullType", "rarity", "colors", "cost"};
	public static String[] att_set ={"id","set", "cardCount", "releaseDate", "block"};
	public static String[] att_cols = {"name"};

	public static String[] att_shop ={"name","description","price","shopName"};

	public static String[] att_shake={"name","ed","price","percentDayChange","priceDayChange"};
	
	
	
	@Override  
    public void sessionOpened(IoSession session) throws Exception {  
        session.write("Welcome to MTG Desktop Companion Server\r\n");
    }  
	
	public void sessionClosed(IoSession session) throws Exception {  
		System.out.println("client disconnection : " +session.getRemoteAddress() + " is Disconnection");  
  
    }  
	
    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        cause.printStackTrace();
    }
    
    public Command commandFactory(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException
   	{
   		String clazz = Character.toUpperCase(name.charAt(0)) + name.substring(1);
   		Class myCommand = classLoader.loadClass("org.magic.console.commands."+clazz);
           Command c = (Command)myCommand.newInstance();
           return c;
   	}
    
    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
    	System.out.println("message = " + message);
    	
    	if(message.toString().equals("cls")|| message.toString().equals("clear"))
		{
    		session.write("\033[2J");
		}
    	else
    	{
            String line = message.toString();
            String[] commandeLine = line.split(" ");
    		Command c = commandFactory(commandeLine[0]);
    		c.run(commandeLine,session);
    		c.quit();
    	}

    }
}