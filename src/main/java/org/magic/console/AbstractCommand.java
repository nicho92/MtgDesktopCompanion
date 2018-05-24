package org.magic.console;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class AbstractCommand implements Command {

	protected CommandLineParser parser = new DefaultParser();
	protected Options opts = new Options();
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected Gson json;
	
	public abstract void initOptions();
	
	
	
	
	public AbstractCommand() {
		json = new Gson();
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
		obj.addProperty("ret", string);
		return obj;
	}

	
	
	@Override
	public JsonElement usage() {
		HelpFormatter formatter = new HelpFormatter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter ps = new PrintWriter(baos);
		formatter.printHelp(ps, 50, getCommandName(), null, opts, 0, 0, null);
		ps.close();
		try {
			return toObject(baos.toString(MTGConstants.DEFAULT_ENCODING));
			
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			return toObject(e.getMessage());
		}
		
	}
	
	



	@Override
	public void quit() {
		// nothing to do

	}

}
