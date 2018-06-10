package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;

public class WebManagerServer extends AbstractMTGServer {

	private static final String REST_BACKEND_URI = "REST_BACKEND_URI";
	private static final String ALLOW_LIST_DIR = "ALLOW_LIST_DIR";
	private static final String AUTOSTART = "AUTOSTART";
	private static final String SERVER_PORT = "SERVER-PORT";
	private static final String REST_JS_FILENAME="rest-server.js";
	private Server server;
	private URL webRootLocation;

	public static void main(String[] args) throws Exception {
		new WebManagerServer().start();
	}

	public WebManagerServer() {
		super();
		server = new Server(getInt(SERVER_PORT));

		webRootLocation = MTGConstants.WEBUI_LOCATION;
		if (webRootLocation == null) {
			throw new IllegalStateException("Unable to determine webroot URL location");
		}

		ServletContextHandler ctx = new ServletContextHandler();
		ctx.setContextPath("/");
		DefaultServlet defaultServlet = new DefaultServlet();
		ServletHolder holderPwd = new ServletHolder("default", defaultServlet);
		holderPwd.setInitParameter("resourceBase", webRootLocation.toString());
		holderPwd.setInitParameter("dirAllowed", getString(ALLOW_LIST_DIR));

		ctx.addServlet(holderPwd, "/*");
		server.setHandler(ctx);
	}

	@Override
	public void start() throws IOException {
		URL u = null;
		
		//TODO in jar file, we can't write file. So make code to read it from local directory
		try {
			u = this.getClass().getResource("/web-ui/dist/js/"+REST_JS_FILENAME);
			FileUtils.writeStringToFile(new File(u.toURI()), "var restserver='" + getString(REST_BACKEND_URI) + "';",MTGConstants.DEFAULT_ENCODING);
		} catch (Exception e) {
			logger.error("couldn't write js rest file " + u, e);
			logger.info("Create " + new File(".").getAbsolutePath()+"/"+REST_JS_FILENAME+". then, restart server");
			FileUtils.writeStringToFile(new File(".",REST_JS_FILENAME), "var restserver='" + getString(REST_BACKEND_URI) + "';",MTGConstants.DEFAULT_ENCODING);
			
		}

		try {
			server.start();
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
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty(SERVER_PORT, "80");
		setProperty(AUTOSTART, "false");
		setProperty(ALLOW_LIST_DIR, "false");
		setProperty(REST_BACKEND_URI, "http://localhost:8080");
	}

}
