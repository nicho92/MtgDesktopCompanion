package org.magic.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.ArgsLineParser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.vandermeer.asciitable.AsciiTable;

public class MTGConsoleHandler extends IoHandlerAdapter {

	public static final String[] att_cards = { "name", "fullType", "editions[0].rarity", "colors", "cost" };
	protected static final String[] att_set = { "id", "set", "cardCount", "releaseDate", "block" };
	protected static final String[] att_cols = { "name" };
	protected static final String[] att_shake = { "name", "ed", "price", "percentDayChange", "priceDayChange" };

	private Logger logger = MTGLogger.getLogger(this.getClass());

	private List<String> history;

	public MTGConsoleHandler() {
		history = new ArrayList<>();
	}

	public List<String> getHistory() {
		return history;
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.write("Welcome to MTG Desktop Companion Server\r\n");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.debug("client disconnection : " + session.getRemoteAddress() + " is Disconnection");

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		if (session.getCurrentWriteMessage() != null) {
			session.write(cause + "\n");
			logger.error(cause);
		}
	}

	public static Command commandFactory(String name) throws ClassNotFoundException, InstantiationException,IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String clazz = StringUtils.capitalize(name);
		Class myCommand = MTGConsoleHandler.class.getClassLoader().loadClass(MTGConstants.COMMANDS_PACKAGE +"." + clazz);
		return (Command) myCommand.getDeclaredConstructor().newInstance();
	}
	
	public String showList(JsonArray list) {
		List<String> attrs = new ArrayList<>();
		
		attrs.add("name");
		attrs.add("cost");
		attrs.add("id");
		attrs.add("power");
		attrs.add("toughness");
		
		AsciiTable at = new AsciiTable();
		at.getContext().setWidth(250);
		at.addRule();
		at.addRow(attrs);
		at.addRule();
		for(int i=0;i<list.size();i++)
		{
			JsonObject obj = list.get(i).getAsJsonObject();
			
			ArrayList<String> values = new ArrayList<>();
			for(String k : attrs)
			{
				if(obj.get(k)!=null)
					if(!obj.get(k).isJsonArray())
						values.add(obj.get(k).getAsString());
					else
						values.add(obj.get(k).toString());
				else
					values.add("");
			}
			
			at.addRow(values);
		}
	
		return at.render();
	}
	
	/*
	
	public String showList(List list, List<String> attributes) {
		AsciiTable at = new AsciiTable();
		at.addRule();
		at.addRow(attributes);
		at.addRule();
		for(Object mc : list)
		{
			Map<String, String> map;
			try {
				map = BeanUtils.describe(mc);
				List<String> attrs = new ArrayList<>();
				for(String s : attributes)
				{
					if(map.get(s)!=null)
						attrs.add(map.get(s));
					else
						attrs.add("");
				}
				at.addRow(attrs);
			} catch (Exception e) {
				logger.error("error for " + mc,e);
			} 
			
		}
			
	
		return at.render();
	}*/

	@Override
	public void messageReceived(IoSession session, Object message)throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException,InvocationTargetException, NoSuchMethodException {
		logger.debug("message="+message);
		if (message == null)
			return;

		if (message.toString().equals("cls") || message.toString().equals("clear")) {
			session.write("\033[2J");
		} else {
			String line = message.toString();
			String[] commandeLine = ArgsLineParser.translateCommandline(line);
			Command c = commandFactory(commandeLine[0]);
			logger.debug("message="+line + " commandLine="+Arrays.asList(commandeLine) + " Command="+c);
			JsonElement ret = c.run(commandeLine);
			if(ret.isJsonArray())
			{
				session.write(showList(ret.getAsJsonArray()));
			}
			else
			{
				session.write(ret);
			}
			
			c.quit();
			history.add(line);
		}

	}
}