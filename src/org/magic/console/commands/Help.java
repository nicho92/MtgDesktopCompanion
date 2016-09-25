package org.magic.console.commands;

import java.io.File;
import java.net.URL;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.magic.console.Command;


public class Help implements Command {
	Options opts;

	public Help()
	{
		opts = new Options();
		Option o = new Option("c","class name");
			o.setOptionalArg(true);
			opts.addOption(o);
	}
	
	@Override
	public void run(String[] array) throws Exception {
		 String pkg = Help.class.getPackage().getName();
		 String relPath = pkg.replace('.', '/');
		 URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		 File[] lstFile = new File(resource.toURI()).listFiles();
		 
		for(int i=0;i<lstFile.length;i++)
		{
			Class myCommand = ClassLoader.getSystemClassLoader().loadClass(pkg+"."+lstFile[i].getName().replaceAll(".class", "").trim());
	        Command c = (Command)myCommand.newInstance();
			c.usage();
		}
		 
		 
	}

	@Override
	public void usage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub
		
	}
}
