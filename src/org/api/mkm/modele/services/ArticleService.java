package org.api.mkm.modele.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Article.ARTICLES_ATT;
import org.api.mkm.modele.Link;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Response;
import org.api.mkm.modele.User;
import org.api.mkm.modele.tools.MkmAPIConfig;
import org.api.mkm.modele.tools.Tools;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

public class ArticleService {

	private AuthenticationServices auth;
	private XStream xstream;
	
	
	public ArticleService() {
		auth=MkmAPIConfig.getInstance().getAuthenticator();
		
		xstream = new XStream(new StaxDriver());
			XStream.setupDefaultSecurity(xstream);
	 		xstream.addPermission(AnyTypePermission.ANY);
	 		xstream.alias("response", Response.class);
	 		xstream.addImplicitCollection(Response.class,"article", Article.class);
	 		xstream.addImplicitCollection(Response.class,"links",Link.class);
	 		xstream.ignoreUnknownElements();
	}
	
	public List<Article> find(User u,Map<ARTICLES_ATT,String> atts) throws InvalidKeyException, NoSuchAlgorithmException, IOException
	{
		//https://www.mkmapi.eu/ws/v2.0/users/karmacrow/articles?start=0&maxResults=100
		return null;
	}
	
	
	public List<Article> find(Product p,Map<ARTICLES_ATT,String> atts) throws InvalidKeyException, NoSuchAlgorithmException, IOException
	{
		return find(p.getIdProduct(),atts);
	}
	
	
	public List<Article> find(String idProduct,Map<ARTICLES_ATT,String> atts) throws InvalidKeyException, NoSuchAlgorithmException, IOException
	{
    	String link = "https://www.mkmapi.eu/ws/v2.0/articles/"+idProduct;
    	
    	if(atts.size()>0)
    	{
    		link+="?";
    		List<String> paramStrings = new ArrayList<String>();
 	        for(ARTICLES_ATT parameter:atts.keySet())
	             paramStrings.add(parameter + "=" + atts.get(parameter));
	        
 	        link+=Tools.join(paramStrings, "&");
    		
    	}
    
	    HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
			               connection.addRequestProperty("Authorization", auth.generateOAuthSignature(link,"GET")) ;
			               connection.connect() ;
		String xml= IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
		Response res = (Response)xstream.fromXML(xml);
		return res.getArticle();
	}
	
}
