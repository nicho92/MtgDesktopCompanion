package org.beta;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.Jetty;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;

import io.milton.servlet.MiltonFilter;
import io.milton.servlet.MiltonServlet;

public class WebDAVServer extends AbstractMTGServer {

	private Server server;
	private static final String SERVER_PORT = "SERVER-PORT";

	
	public static void main(String[] args) throws IOException {
		new WebDAVServer().start();
	}
	
	@Override
	public void start() throws IOException {
		server = new Server(getInt(SERVER_PORT));
	
		ServletContextHandler ctx = new ServletContextHandler();
		ctx.setContextPath("/");
	
		ServletHolder holderDav = new ServletHolder("default", new MiltonServlet());
		ctx.addServlet(holderDav,"/");
		ctx.addFilter(MiltonFilter.class, "/*",EnumSet.of(DispatcherType.REQUEST));
		
		logger.trace(ctx.dump());
		server.setHandler(ctx);
		
		try {
			server.start();
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
		return server.isRunning();
	}

	@Override
	public boolean isAutostart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "WebDAV Server";
	}
	
	@Override
	public void initDefault() {
		setProperty(SERVER_PORT, "8088");
		setProperty("AUTOSTART", "false");
	}

	
	@Override
	public String getVersion() {
		return Jetty.VERSION;
	}
}
