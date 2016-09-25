package org.magic.console;

import java.io.PrintStream;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

public interface Command {

	CommandLineParser parser = new DefaultParser();

	PrintStream out = System.out;
	
	public void run(String[] array) throws Exception ;
	
	public void usage();
	
	public void quit();


}
