package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.Icon;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.Jetty;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.services.PluginRegistry;
import org.magic.tools.FileTools;

public class WebManagerServer extends AbstractMTGServer {

	private static final String REST_BACKEND_URI = "REST_BACKEND_URI";
	private static final String ALLOW_LIST_DIR = "ALLOW_LIST_DIR";
	private static final String AUTOSTART = "AUTOSTART";
	private static final String SERVER_PORT = "SERVER-PORT";
	private static final String REST_JS_FILENAME="rest-server.js";
	private static final String JSON_SERVER_START = "JSONSERVER_START";
	
	private Server server;
	private URL webRootLocation;

	public WebManagerServer() {
		super();
		server = new Server(getInt(SERVER_PORT));

		webRootLocation = MTGConstants.class.getResource("/"+MTGConstants.WEBUI_LOCATION);
		if (webRootLocation == null) {
			throw new IllegalStateException("Unable to determine webroot URL location");
		}

		ServletContextHandler ctx = new ServletContextHandler();
		ctx.setContextPath("/");
		
		ServletHolder holderPwd = new ServletHolder("mtg-web-ui", new DefaultServlet());
					  holderPwd.setInitParameter("resourceBase", webRootLocation.toString());
					  holderPwd.setInitParameter("dirAllowed", getString(ALLOW_LIST_DIR));

		ServletHolder holderJs = new ServletHolder("mtg-js-file", new DefaultServlet() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
				  response.setContentType("text/html;charset="+MTGConstants.DEFAULT_ENCODING);
				  response.setStatus(HttpServletResponse.SC_OK);
				  try { 
					  response.getWriter().println("var restserver='" + getString(REST_BACKEND_URI) + "';");  
				  }
				  catch(Exception e)
				  {
					  response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					  logger.error(e);
				  }
			}
		});
	
		ctx.addServlet(holderJs,"/dist/js/"+REST_JS_FILENAME);
		ctx.addServlet(holderPwd, "/*");
		logger.trace(ctx.dump());
		server.setHandler(ctx);
	}
	
	public void exportWeb(File dest) throws IOException
	{
		
		FileTools.copyDirJarToDirectory(MTGConstants.WEBUI_LOCATION, dest);
		logger.debug("copying " + MTGConstants.WEBUI_LOCATION + " to " + dest);
		
		File js = Paths.get(dest.getAbsolutePath(),MTGConstants.WEBUI_LOCATION,"dist","js",REST_JS_FILENAME).toFile();
		
		logger.debug("copying " + js + " to " + dest);
		
		FileTools.saveFile(js, "var restserver='" + getString(REST_BACKEND_URI) + "';");
	}
	

	@Override
	public String getVersion() {
		return Jetty.VERSION;
	}
	
	
	@Override
	public void start() throws IOException {
		try {
			server.start();
			
			if(getBoolean(JSON_SERVER_START))
			{
				MTGServer jserv = PluginRegistry.inst().getPlugin(new JSONHttpServer().getName(), MTGServer.class);
				if(!jserv.isAlive())
					jserv.start();
			}
			
			
			logger.info("Server start on port " + getInt(SERVER_PORT) + " @ " + webRootLocation);
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
	public Icon getIcon() {
		return MTGConstants.ICON_WEBSITE;
	}
	
	@Override
	public boolean isAutostart() {
		return getBoolean(AUTOSTART);
	}

	@Override
	public String description() {
		return "Web server front end";
	}

	@Override
	public String getName() {
		return "Web UI Server";
	}

	@Override
	public void initDefault() {
		setProperty(SERVER_PORT, "80");
		setProperty(AUTOSTART, "false");
		setProperty(ALLOW_LIST_DIR, "false");
		try {
			setProperty(REST_BACKEND_URI, "http://"+InetAddress.getLocalHost().getHostAddress()+":8080");
		} catch (UnknownHostException e) {
			setProperty(REST_BACKEND_URI, "http://localhost:8080");
		}
		setProperty(JSON_SERVER_START,"true");
	}
	

}
