package org.magic.servers.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.magic.api.dav.WebDavMTGFileResourceFactory;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;

import io.milton.servlet.MiltonFilter;
import io.milton.servlet.MiltonServlet;

public class WebDAVServer extends AbstractMTGServer {

	private static final String AUTOSTART = "AUTOSTART";
	private Server server;
	private static final String SERVER_PORT = "SERVER-PORT";
	public static String LOG = "user";
	public static String PAS = "password";
	
	public static void main(String[] args) throws IOException {
		new WebDAVServer().start();
	}
	
	@Override
	public void start() throws IOException {
		server = new Server(getInt(SERVER_PORT));
		
		
		LOG = getString("LOGIN");
		PAS = getString("PASS");
		
		
		ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
		ctx.setContextPath("/");
		
		ServletHandler handler = new ServletHandler();
		
		ctx.addServlet(new ServletHolder("default", new MiltonServlet()),"/");
		
		
		FilterHolder fh = handler.addFilterWithMapping(MiltonFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
					 fh.setInitParameter("resource.factory.class", WebDavMTGFileResourceFactory.class.getCanonicalName());
					 
					 
					 
		ctx.addFilter(fh, "/*", EnumSet.of(DispatcherType.REQUEST));
					 
		logger.trace(ctx.dump());
	
		ctx.setHandler(handler);
		server.setHandler(ctx);
		
		try {
			server.start();
			logger.info("Webdav start on port http://"+InetAddress.getLocalHost().getHostName() + ":"+getInt(SERVER_PORT));

		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public void stop() throws IOException {
		try {
			server.stop();
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
		return "WEBDAV access to collection";
	}

	@Override
	public String getName() {
		return "WebDAV";
	}
	
	@Override
	public void initDefault() {
		setProperty(SERVER_PORT, "8088");
		setProperty(AUTOSTART, "false");
		setProperty("LOGIN", LOG);
		setProperty("PASS", PAS);
	}

	
	@Override
	public String getVersion() {
		return "2.7.4.8";
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
}




