package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.Icon;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.magic.api.interfaces.abstracts.extra.AbstractWebServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.tools.FileTools;

public class ShoppingServer extends AbstractWebServer {
	@Override
	public String description() {
		return "Transaction web page";
	}

	@Override
	public String getName() {
		return "WebShop";
	}

	@Override
	public void exportWeb(File dest) throws IOException
	{
		super.exportWeb(dest);
		FileTools.saveFile(Paths.get(dest.getAbsolutePath(),"css","extra.css").toFile(), MTGControler.getInstance().getWebConfig().getExtraCss());
	}
	
	@Override
	public void extraConfig() {
				var holderCss = new ServletHolder("extraCssServlet", new DefaultServlet() {
					private static final long serialVersionUID = 1L;
					@Override
					protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
						  response.setContentType(URLTools.HEADER_CSS+";charset="+MTGConstants.DEFAULT_ENCODING);
						  response.setStatus(HttpServletResponse.SC_OK);
						  try { 
							  response.getWriter().println(MTGControler.getInstance().getWebConfig().getExtraCss());  
						  }
						  catch(Exception e)
						  {
							  response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							  logger.error(e);
						  }
					}
				});
			
				ctx.addServlet(holderCss,"/css/extra.css");
		
		
		
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
