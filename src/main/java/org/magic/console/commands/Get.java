package org.magic.console.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;
import org.magic.console.AbstractCommand;

public class Get extends AbstractCommand {

	
	public static void main(String[] args) 
	{
		
		String command ="coucou --n test emrak -s Rise of Eldrazi -c";
		
		String[] split=command.split(" ");
		
		for(int i=0;i<split.length;i++)
		{
			String arg=split[i];
			if(!arg.startsWith("-"))
			{	
				StringBuilder temp=new StringBuilder();
				int y=i;
				while(y<split.length && !split[y].startsWith("-"))
					temp.append(split[y++]).append(" ");
				
				arg=temp.toString().trim();
				
				
			}
			else
			{
				System.out.println(arg);
			}
			
			
		}
		
		
		//new Get().run(command.split(" "));
	}
	
	
	public Get() {
		
		opts.addOption("n", "name", true, "get Card by name");
		opts.addOption("s", "set", true, "show all sets");
		opts.addOption("?", "help", false, "help for command");
	}
	
	@Override
	public Object run(String[] array) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException
	{	
		 CommandLine line = parser.parse(opts, array);
				System.out.println(line.getOptionValue("s"));
		return null;
	}

	

	@Override
	public String getCommandName() {
		return "get";
	}

}
