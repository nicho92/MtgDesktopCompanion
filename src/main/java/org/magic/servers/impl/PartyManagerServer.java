package org.magic.servers.impl;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.Jetty;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.URLTools;

public class PartyManagerServer extends AbstractMTGServer {

	
	private Server server;
	private static final String SERVER_PORT="SERVER_PORT";
	private static final String AUTOSTART="AUTOSTART";
	
	
	public PartyManagerServer() {
		super();
		server = new Server(getInt(SERVER_PORT));
		
		ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        
        handler.addServletWithMapping(PartyServlet.class,"/");
        
        
	}
	
	
	public static void main(String[] args) throws IOException {
		new PartyManagerServer().start();

	}

	
	@Override
	public void start() throws IOException {
		try {
			server.start();
			logger.info("Server start on port " + getInt(SERVER_PORT));
		} catch (Exception e) {
			throw new IOException(e);
		}

	}

	@Override
	public void stop() throws IOException {
		try {
			server.stop();
			logger.info("Server closed");
		} catch (Exception e) {
			throw new IOException(e);
		}

	}

	@Override
	public boolean isAlive() {

		if (server != null)
			return server.isRunning();
		else
			return false;
	}

	@Override
	public boolean isAutostart() {
		return getBoolean(AUTOSTART);
	}

	@Override
	public String description() {
		return "Party web screen";
	}

	@Override
	public String getName() {
		 return "Party manager";
	}

	
	@Override
	public String getVersion() {
		return Jetty.VERSION;
	}
	
	
	@Override
	public void initDefault() {
		setProperty(SERVER_PORT, "8082");
		setProperty(AUTOSTART, "false");

	}
	
	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_EVENTS;
	}
	
	public static class PartyServlet extends HttpServlet
	{
		protected static Logger logger = MTGLogger.getLogger(PartyServlet.class);

		@Override
        protected void doGet(HttpServletRequest request,HttpServletResponse response)
        {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(URLTools.HEADER_HTML);
            response.setCharacterEncoding(MTGConstants.DEFAULT_ENCODING.displayName());
            
            try {
            	response.getWriter().println("<h1>Tournaments</h1><br/>");
            	
			} catch (IOException e) {
				logger.error(e);
			}
        }
		
		
		
		
		
	}
	

}
