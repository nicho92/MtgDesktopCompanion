package org.magic.servers.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.Icon;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.magic.api.interfaces.abstracts.AbstractWebServer;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;
import org.magic.tools.URLTools;

public class ShoppingServer extends AbstractWebServer {

	private static final String EXTRA_CSS_FILE = "EXTRA_CSS_FILE";

	@Override
	public String description() {
		return "Transaction web page";
	}

	@Override
	public String getName() {
		return "WebShop";
	}

	@Override
	public void extraConfig() {
		if(getFile(EXTRA_CSS_FILE)!=null) 
		{
			
			try {
				String content  =FileTools.readFile(getFile(EXTRA_CSS_FILE));
			
				var holderCss = new ServletHolder("ExtraCssServlet", new DefaultServlet() {
					private static final long serialVersionUID = 1L;
					@Override
					protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
						  response.setContentType(URLTools.HEADER_CSS+";charset="+MTGConstants.DEFAULT_ENCODING);
						  response.setStatus(HttpServletResponse.SC_OK);
						  try { 
							  response.getWriter().println(content);  
						  }
						  catch(Exception e)
						  {
							  response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							  logger.error(e);
						  }
					}
				});
			
				ctx.addServlet(holderCss,"/css/extra.css");
			} catch (IOException e1) {
				logger.error(e1);
			}
		}
		
		
	}
	
	
	@Override
	public void initDefault() {
		super.initDefault();
		setProperty(EXTRA_CSS_FILE, "");
	}
	
	@Override
	protected String getWebLocation() {
		return MTGConstants.WEBSHOP_LOCATION;
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_SHOP;
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
}
