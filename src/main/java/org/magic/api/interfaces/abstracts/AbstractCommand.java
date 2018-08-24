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
import org.magic.console.CommandResponse;
import org.magic.services.MTGConstants;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class AbstractCommand extends AbstractMTGPlugin implements MTGCommand {

	protected CommandLineParser parser = new DefaultParser();
	protected Options opts = new Options();
	protected JsonExport json;
	
	
	public abstract void initOptions();
	
	
	public AbstractCommand() {
		json = new JsonExport();
		initOptions();
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
		JsonObject obj = new JsonObject();
		obj.addProperty("result", string);
		return obj;
	}

	
	
	@Override
	public CommandResponse<?> usage() {
		HelpFormatter formatter = new HelpFormatter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter ps = new PrintWriter(baos);
		formatter.printHelp(ps, 50, getCommandName(), null, opts, 0, 0, null);
		ps.close();
		try {
			return new CommandResponse(String.class,null,toObject(baos.toString(MTGConstants.DEFAULT_ENCODING)));
			
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			return new CommandResponse(String.class,null,toObject(e.getMessage()));
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

}
