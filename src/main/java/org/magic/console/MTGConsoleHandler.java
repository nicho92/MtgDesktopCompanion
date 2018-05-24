package org.magic.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.asciitable.impl.ASCIITableImpl;
import org.asciitable.impl.CollectionASCIITableAware;
import org.asciitable.spec.IASCIITableAware;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.exports.impl.JsonExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.ArgsLineParser;

import com.google.gson.Gson;

public class MTGConsoleHandler extends IoHandlerAdapter {

	protected static final String[] att_cards = { "name", "fullType", "editions[0].rarity", "colors", "cost" };
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
	
	private <T> String showList(List list, List<String> attributes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		IASCIITableAware asciiTableAware = new CollectionASCIITableAware<T>(list, attributes, attributes);
		new ASCIITableImpl(ps).printTable(asciiTableAware);

		return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}

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
			Object ret = c.run(commandeLine);
			if(ret instanceof List)
			{
				List l = (List)ret;
				if(!l.isEmpty())
				{
					if(l.get(0) instanceof MagicCard)
						session.write(showList(l, Arrays.asList(att_cards)));
					else if(l.get(0) instanceof MagicEdition)
						session.write(showList(l, Arrays.asList(att_set)));
					else if(l.get(0) instanceof CardShake)
						session.write(showList(l, Arrays.asList(att_shake)));
					else if(l.get(0) instanceof MagicCollection)
						session.write(showList(l, Arrays.asList(att_cols)));
				}
			}
			else
			{
				session.write(new Gson().toJson(ret));
			}
			
			c.quit();
			history.add(line);
		}

	}
}