package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.Icon;

import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.magic.api.interfaces.abstracts.extra.AbstractWebServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.services.tools.FileTools;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		FileTools.saveFile(Paths.get(dest.getAbsolutePath(),"css","extra.css").toFile(), MTGControler.getInstance().getWebshopService().getWebConfig().getExtraCss());
	}

	@Override
	public void extraConfig() {
				var holderCss = new ServletHolder("extraCssServlet", new DefaultServlet() {
					private static final long serialVersionUID = 1L;
					@Override
					protected void doGet(HttpServletRequest request, HttpServletResponse response)throws  IOException {
						  response.setContentType(URLTools.HEADER_CSS+";charset="+MTGConstants.DEFAULT_ENCODING);
						  response.setStatus(HttpServletResponse.SC_OK);
						  try {
							  response.getWriter().println(MTGControler.getInstance().getWebshopService().getWebConfig().getExtraCss());
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
