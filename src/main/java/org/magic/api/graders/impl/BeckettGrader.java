package org.magic.api.graders.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.AccountsManager;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.network.URLToolsClient;
import org.magic.services.network.RequestBuilder.METHOD;

public class BeckettGrader extends AbstractGradersProvider {

	
	@Override
	public String getWebSite() {
		return "https://www.beckett.com";
	}

	@Override
	public Grading loadGrading(String identifier) throws IOException {
		
		URLToolsClient c = URLTools.newClient();
		
		String urlLogin = getWebSite()+"/login?utm_content=bkthp&utm_term=login";
		String urlCheking = getWebSite()+"/grading/card-lookup";
		
		Document d = RequestBuilder.build().url(urlLogin).setClient(c).method(METHOD.GET).toHtml();
		String token = d.select("input[name='login_token']").first().attr("value");
		
		
			d=RequestBuilder.build().url(urlLogin).setClient(c).method(METHOD.POST)
						  .addContent("redirect_url", getWebSite()+"/account")
						  .addContent("login_token", token)
						  .addContent("email",getAuthenticator().getLogin())
						  .addContent("password", getAuthenticator().getPassword())
						  .toHtml();
			
		boolean	connected = !d.getElementsByTag("title").html().equalsIgnoreCase("Member Login");
	
			
		if(!connected)
			throw new IOException("Error when login to website");
		
		
			d=RequestBuilder.build().url(urlCheking).setClient(c).method(METHOD.GET)
					.addContent("item_type", "BGS")
					.addContent("item_id", identifier)
					 .toHtml();
			
			Element table = d.select("table.cardDetail").first();
			
			if(table==null)
				return null;
				
				
				
			Elements trs=table.select("tr");
			var grad = new Grading();
			grad.setGraderName(getName());
			grad.setNumberID(identifier);
			grad.setUrlInfo(getWebSite()+"?item_id="+identifier);
			
			trs.forEach(tr->{
				if(tr.text().startsWith("Centering"))
					grad.setCentering(Double.parseDouble(tr.text().replace("Centering Grade : ","").trim()));
				
				if(tr.text().startsWith("Corner"))
					grad.setCorners(Double.parseDouble(tr.text().replace("Corner Grade : ","").trim()));
				
				if(tr.text().startsWith("Edges"))
					grad.setEdges(Double.parseDouble(tr.text().replace("Edges Grade : ","").trim()));
				
				if(tr.text().startsWith("Surfaces"))
					grad.setSurface(Double.parseDouble(tr.text().replace("Surfaces Grade : ","").trim()));

				if(tr.text().startsWith("Final"))
					grad.setGradeNote(Double.parseDouble(tr.text().replace("Final Grade : ","").trim()));
				
				if(tr.text().startsWith("Date"))
				{
					try {
						grad.setGradeDate(new SimpleDateFormat("EEEEE, MMMMM dd, yyyy",Locale.US).parse(tr.text().replace("Date Graded : ","").trim()));
					}
					catch(ParseException e)
					{
						logger.error(e);
					}
				}
				
				
			});
		return grad;
	}
	
	@Override
	public String getName() {
		return "BGS";
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}
}
