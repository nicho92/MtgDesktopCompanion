package org.magic.console;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.magic.api.interfaces.MTGCommand;
import org.magic.services.MTGLogger;
import org.magic.services.PluginRegistry;
import org.magic.tools.ArgsLineParser;

public class MTGConsoleHandler extends IoHandlerAdapter {


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

	private MTGCommand commandFactory(String name) {
		return PluginRegistry.inst().newInstance("org.magic.api.commands.impl."+StringUtils.capitalize(name));
	}
	
	
	@Override
	public void messageReceived(IoSession session, Object message)throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException,InvocationTargetException, NoSuchMethodException {
		if (message == null)
			return;

		if (message.toString().equals("cls") || message.toString().equals("clear")) {
			session.write("\033[2J");
		} else {
			String line = message.toString();
			String[] commandeLine = ArgsLineParser.translateCommandline(line);
			MTGCommand c = commandFactory(commandeLine[0]);
			
			if(c==null)
			{
				session.write("Command not found");
			}
			else
			{
				
				logger.debug("message="+line + " commandLine="+Arrays.asList(commandeLine) + " Command="+c);
				CommandResponse<MTGCommand> ret = c.run(commandeLine);
				session.write(ret);
				c.quit();
				history.add(line);
				
			}
		}

	}

}