package org.magic.api.interfaces.abstracts.extra;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.Icon;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.Jetty;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.services.MTGConstants;
import org.magic.services.PluginRegistry;
import org.magic.services.network.URLTools;
import org.magic.tools.FileTools;

public abstract class AbstractWebServer extends AbstractMTGServer {

	private static final String REST_BACKEND_URI = "REST_BACKEND_URI";
	private static final String ALLOW_LIST_DIR = "ALLOW_LIST_DIR";
	private static final String AUTOSTART = "AUTOSTART";
	private static final String SERVER_PORT = "SERVER-PORT";
	private static final String SERVER_SSL_PORT = "SERVER-SSL-PORT";
	protected static final String REST_JS_FILENAME="rest-server.js";
	protected static final String JSON_SERVER_START = "JSONSERVER_START";
	private static final String SSL_ENABLED = "SSL_ENABLED";
	private static final String KEYSTORE_URI = "KEYSTORE_URI";
	private static final String KEYSTORE_PASS = "KEYSTORE_PASS";
	
	
	private Server server;
	private URL webRootLocation;
	protected ServletContextHandler ctx;
	
	protected abstract String getWebLocation();
	
	private void initServlet() {
		ctx = new ServletContextHandler();
		ctx.setContextPath("/");
		
		var holderPwd = new ServletHolder("mtg-web-ui", new DefaultServlet());
					  holderPwd.setInitParameter("resourceBase", webRootLocation.toString());
					  holderPwd.setInitParameter("dirAllowed", getString(ALLOW_LIST_DIR));

		var holderJs = new ServletHolder("mtg-js-file", new DefaultServlet() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
				  response.setContentType(URLTools.HEADER_HTML+";charset="+MTGConstants.DEFAULT_ENCODING);
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
		
		extraConfig();
		
		logger.trace(ctx.dump());
		server.setHandler(ctx);
		
		
	}

	public void exportWeb(File dest) throws IOException
	{
		
		FileTools.copyDirJarToDirectory(getWebLocation(), dest);
		logger.debug("copying " + getWebLocation() + " to " + dest);
		
		var js = Paths.get(dest.getAbsolutePath(),getWebLocation(),"dist","js",REST_JS_FILENAME).toFile();
		
		logger.debug("copying " + js + " to " + dest);
		
		FileTools.saveFile(js, "var restserver='" + getString(REST_BACKEND_URI) + "';");
	}
	

	@Override
	public String getVersion() {
		return Jetty.VERSION;
	}
	
	
	public void extraConfig()
	{
		//do nothing by default
	}
	
	
	
	public ServerConnector createHttpsConnector(HttpConfiguration httpConfig)
	{

		var httpsConfig = new HttpConfiguration(httpConfig);
        var src = new SecureRequestCustomizer();
        src.setStsMaxAge(2000);
        src.setStsIncludeSubDomains(true);
        httpsConfig.addCustomizer(src);
	
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(getFile(KEYSTORE_URI).getAbsolutePath());
        sslContextFactory.setKeyStorePassword(getString(KEYSTORE_PASS));
        
        var https = new ServerConnector(server,new SslConnectionFactory(sslContextFactory,HttpVersion.HTTP_1_1.asString()),new HttpConnectionFactory(httpsConfig));
        	https.setPort(getInt(SERVER_SSL_PORT));
        	https.setIdleTimeout(500000);
	     
	     
	     
        return https;
	}
	
	
	@Override
	public void start() throws IOException {
		try {

			server = new Server();
			Connector httpsConnector=null;
			
			
			var httpConfig = new HttpConfiguration();
				 httpConfig.setSecureScheme("https");
				 httpConfig.setSecurePort(getInt(SERVER_SSL_PORT));
				 httpConfig.setOutputBufferSize(32768);
			
		
			
			try(var http = new ServerConnector(server,new HttpConnectionFactory(httpConfig)))
			{
				 http.setPort(getInt(SERVER_PORT));
			     http.setIdleTimeout(30000);
			     server.setConnectors(new Connector[] {http});
			}
			
			

			 if(getBoolean(SSL_ENABLED)) {
				 httpsConnector = createHttpsConnector(httpConfig);
				server.setConnectors(ArrayUtils.add(server.getConnectors(), httpsConnector));    
			 }
			
	       
	        
	        
			
			webRootLocation = MTGConstants.class.getResource("/"+getWebLocation());
			if (webRootLocation == null) {
				throw new IllegalStateException("Unable to determine webroot URL location: " + webRootLocation);
			}

			
			initServlet();
			
			server.start();
			
			if(getBoolean(JSON_SERVER_START))
			{
				MTGServer jserv = PluginRegistry.inst().getPlugin(new JSONHttpServer().getName(), MTGServer.class);
				if(!jserv.isAlive())
					jserv.start();
			}
			
			
			logger.info("Server "+ getName() + "("+getVersion()+") start on port " + getInt(SERVER_PORT) + " @ " + webRootLocation);
		} catch (Exception e) {
			logger.error(e);
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
		return MTGConstants.ICON_WEBSITE_24;
	}
	
	@Override
	public boolean isAutostart() {
		return getBoolean(AUTOSTART);
	}
	
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		
		var m = new HashMap<String,String>();
		
		m.put(SERVER_PORT, "80");
		m.put(SERVER_SSL_PORT, "443");
		m.put(AUTOSTART, FALSE);
		m.put(ALLOW_LIST_DIR, FALSE);
		m.put(SSL_ENABLED, FALSE);
		m.put(KEYSTORE_URI, new File(MTGConstants.DATA_DIR,"jetty.jks").getAbsolutePath());
		m.put(KEYSTORE_PASS, "changeit");
		try {
			m.put(REST_BACKEND_URI, "http://"+InetAddress.getLocalHost().getHostAddress()+":8080");
		} catch (UnknownHostException e) {
			m.put(REST_BACKEND_URI, "http://localhost:8080");
		}
		m.put(JSON_SERVER_START,"true");
		
		return m;
	}
	

}
