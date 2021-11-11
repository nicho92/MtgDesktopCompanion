package org.magic.api.graders.impl;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class MNTGrader extends AbstractGradersProvider {

	
	@Override
	public STATUT getStatut() {
		return STATUT.BUGGED;
	}
	
	@Override
	public Grading loadGrading(String identifier) throws IOException {
		
		URLToolsClient c = URLTools.newClient();
		
		
		HttpResponse el = RequestBuilder.build()
						.setClient(c)
						.url(getWebSite()+"/wp-admin/admin-ajax.php")
						.method(METHOD.POST)
						.addContent("mnt_verification_lookup", identifier)
						.addHeader(URLTools.REFERER, getWebSite()+"/verification-lookup/")
						.addHeader("x-requested-with", "XMLHttpRequest")
						.addHeader(":authority", "mntgrading.com")
						.addHeader(URLTools.ACCEPT, "*/*")
						.addHeader(URLTools.ORIGIN, getWebSite())
						.addHeader(URLTools.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")
						.addHeader("sec-fetch-dest", "empty")
						.addHeader("sec-fetch-mode","cors")
						.toResponse();
		
		logger.debug(el);
		
		logger.error("Blocked by captcha protection");
		UITools.browse(getWebSite()+"/verification-lookup");
		
		
		
		return null;
		
	}

	@Override
	public String getWebSite() {
		return "https://mntgrading.com/";
	}

	@Override
	public String getName() {
		return "MNT";
	}

}
