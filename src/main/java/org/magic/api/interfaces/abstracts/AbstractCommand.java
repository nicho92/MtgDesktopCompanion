package org.magic.api.interfaces.abstracts;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.MTGConsoleHandler;
import org.magic.console.TextResponse;
import org.magic.services.MTGConstants;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class AbstractCommand extends AbstractMTGPlugin implements MTGCommand {

	protected CommandLineParser parser = new DefaultParser();
	protected Options opts = new Options();
	protected JsonExport json;
	protected MTGConsoleHandler handler;

	public void initOptions()
	{
		opts.addOption("?", "help", false, " : help for command");
	}

	protected AbstractCommand() {
		json = new JsonExport();
		initOptions();
	}

	@Override
	public void setHandler(MTGConsoleHandler handler) {
		this.handler=handler;
	}


	@Override
	public String toString() {
		return getCommandName();
	}

	@Override
	public String getCommandName() {
		return getClass().getSimpleName();
	}


	protected JsonElement toObject(String string) {
		var obj = new JsonObject();
		obj.addProperty("result", string);
		return obj;
	}



	@Override
	public AbstractResponse usage() {
		var formatter = new HelpFormatter();
		var baos = new ByteArrayOutputStream();
		var ps = new PrintWriter(baos);
		formatter.printHelp(ps, 50, getCommandName(), null, opts, 0, 0, null);
		ps.close();
		try {
			return new TextResponse(baos.toString(MTGConstants.DEFAULT_ENCODING.displayName()));
		} catch (UnsupportedEncodingException e) {
			return new TextResponse(e.getMessage());
		}

	}


	@Override
	public String getName() {
		return getCommandName();
	}

	 @Override
	public PLUGINS getType() {
		return PLUGINS.COMMAND;
	}


	 @Override
	public Icon getIcon() {
		return new ImageIcon(AbstractCommand.class.getResource("/icons/plugins/console.png"));
	}


	@Override
	public void quit() {
		// nothing to do
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

	@Override
	public int hashCode() {
		return (getType()+getName()).hashCode();
	}

}
