package org.beta;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.magic.api.beans.OrderEntry;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class PhilibertShopper extends AbstractMagicShopper {

	private static final String BASE_URL="https://www.philibertnet.com/";
	
	public static void main(String[] args) throws IOException {
		new PhilibertShopper().listOrders();
	}
	
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		URLToolsClient c = URLTools.newClient();
		
		c.doGet(BASE_URL);
		
		HttpResponse s = RequestBuilder.build()
					  .method(METHOD.POST)
					  .url(BASE_URL+"/en/authentication")
					  .setClient(c)
					  .addContent("email", getString("LOGIN"))
					  .addContent("passwd",getString("PASSWORD"))
					  .addContent("back","order-history")
					  .addContent("SubmitLogin","")
					  .addHeader(":path", "/en/authentication")
					  .addHeader(":authority", "www.philibertnet.com")
					  .addHeader(":method", METHOD.POST.name())
					  .addHeader(URLTools.CONTENT_TYPE, "application/x-www-form-urlencoded")
					  .toResponse();
		
		
		
		
		
		logger.debug(s);
		
		
		
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void initDefault() {
		setProperty("LOGIN", "");
		setProperty("PASSWORD", "");
	}
	
	
	
	@Override
	public String getName() {
		return "Philibert";
	}

}
