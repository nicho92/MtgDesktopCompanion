package org.magic.console;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;


public class MTGConsole {

	public static Map<String,Object> ENV=new HashMap<String, Object>();
	
	
	
	ClassLoader classLoader = MTGConsole.class.getClassLoader();
	
	public static void main(String[] args) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				 new MTGConsole();
				
			}
		}).start();
	}
	
	public MTGConsole()  {
		
		
		Console io = new Console();
				io.setTitle("MTG Console");
				io.setSize(1024,500);
				io.setLocationRelativeTo(null);
				io.setVisible(true);
				io.println("Welcome to MTG Console. Type help for commands",Color.CYAN);
				
		//Console.io.print(getPrompt());
		Command c = null;
		String line ="";
		while(line !="quit")
		{	
			try {
				io.print(getPrompt());
				line = io.nextLine();
				CommandLineParser parser = new DefaultParser();
				String[] commandeLine = line.split(" ");
					
					if(commandeLine[0].equals("cls")|| commandeLine[0].equals("clear"))
					{
						io.clear();
					}
					else
					{	
						c = commandFactory(commandeLine[0]);
						c.run(commandeLine);
						c.quit();
					}
					io.addHistory(line);
					
					
		    } catch (Exception e) {
		    	handleException(e,c);
		    	io.print(getPrompt());
		    	line = io.nextLine();
		    } 
			
		}
			
	}
	
	private void handleException(Exception e, Command c) {
		if(c!=null)
		{
			e.printStackTrace();
			c.usage();
		}
	}
	
	private String getPrompt()
	{
		return " $>";
	}
	
	
	

	public Command commandFactory(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		String clazz = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		Class myCommand = classLoader.loadClass("org.magic.console.commands."+clazz);
        Command c = (Command)myCommand.newInstance();
        
        return c;
	}
	
	
}
