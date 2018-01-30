package org.magic.console;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public abstract class AbstractCommand implements Command {

	protected CommandLineParser parser = new DefaultParser();
	protected Options opts = new Options();


}
