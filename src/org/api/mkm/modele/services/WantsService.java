package org.api.mkm.modele.services;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.api.mkm.modele.Link;
import org.api.mkm.modele.Localization;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Response;
import org.api.mkm.modele.WantItem;
import org.api.mkm.modele.Wantslist;
import org.api.mkm.modele.tools.MkmAPIConfig;
import org.magic.api.exports.impl.MKMOnlineWantListExport.Want;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

public class WantsService {

	private AuthenticationServices auth;
	private XStream xstream;
	
	
	public WantsService() {
		auth=MkmAPIConfig.getInstance().getAuthenticator();
		
		xstream = new XStream(new StaxDriver());
			XStream.setupDefaultSecurity(xstream);
	 		xstream.addPermission(AnyTypePermission.ANY);
	 		xstream.alias("response", Response.class);
	 		xstream.addImplicitCollection(Response.class,"wantslist", Wantslist.class);
	 		xstream.addImplicitCollection(Wantslist.class,"item", WantItem.class);
	 		xstream.addImplicitCollection(Wantslist.class,"links", Link.class);
	 		xstream.addImplicitCollection(Product.class,"localization",Localization.class);
	 		xstream.addImplicitCollection(WantItem.class,"idLanguage",Integer.class);
		 		
	 		xstream.addImplicitCollection(Response.class,"links",Link.class);
	 		xstream.ignoreUnknownElements();
	}
	
	public boolean removeWant(Wantslist li, List<WantItem> list) throws Exception
	{
		String link ="https://www.mkmapi.eu/ws/v1.1/wantslist/"+li.getIdWantslist();

    	HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
				            connection.addRequestProperty("Authorization", auth.generateOAuthSignature(link,"PUT")) ;
				       		connection.setDoOutput(true);
				    		connection.setRequestMethod("PUT");
				    		connection.connect();

		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		StringBuffer temp = new StringBuffer();
		temp.append("<?xml version='1.0' encoding='UTF-8' ?>");
		temp.append("<request><action>deleteItem</action>");
		for(WantItem w : list)
		{
			temp.append("<want>");
			temp.append("<idWant>"+w.getProduct().getIdProduct()+"</idWant>");
		}		    
		temp.append("</want>");

		out.write(temp.toString());
		out.close();
		return (connection.getResponseCode()>=200 || connection.getResponseCode()<300);
	}
	
	public List<Wantslist> getWantList() throws InvalidKeyException, NoSuchAlgorithmException, IOException
	{
    	String link = "https://www.mkmapi.eu/ws/v2.0/wantslist";
    	
    	HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
			               connection.addRequestProperty("Authorization", auth.generateOAuthSignature(link,"GET")) ;
			               connection.connect() ;
		String xml= IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
		Response res = (Response)xstream.fromXML(xml);
		return res.getWantslist();
	}
	
	public Wantslist createWantList(String name) throws IOException, InvalidKeyException, NoSuchAlgorithmException
	{
		String link = "https://www.mkmapi.eu/ws/v2.0/wantslist";
		String temp = "<?xml version='1.0' encoding='UTF-8' ?><request><wantslist><idGame>1</idGame><name>"+name+"</name></wantslist></request>";
		
		HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
        				  connection.addRequestProperty("Authorization", auth.generateOAuthSignature(link,"POST")) ;
        				  connection.setDoOutput(true);
        				  connection.setRequestMethod("POST");
        				  connection.setRequestProperty( "charset", "utf-8");
        				  connection.connect() ;
        				  OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        				  out.write(temp.toString());
        				  out.close();
        				  
        	if(connection.getResponseCode()>=200 || connection.getResponseCode()<300)
        	{
        		String xml= IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
        		Response res = (Response)xstream.fromXML(xml);
        		return res.getWantslist().get(0);
        	}
        	
        	
      	return null;
	}
	
	public boolean deleteWantList(Wantslist l) throws IOException, InvalidKeyException, NoSuchAlgorithmException
	{
		String link = "https://www.mkmapi.eu/ws/v2.0/wantslist/"+l.getIdWantslist();
		String temp = "<?xml version='1.0' encoding='UTF-8' ?><request><wantslist><idGame>1</idGame><name>"+l.getIdWantslist()+"</name></wantslist></request>";
		
		HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
        				  connection.addRequestProperty("Authorization", auth.generateOAuthSignature(link,"DELETE")) ;
        				  connection.setDoOutput(true);
        				  connection.setRequestMethod("DELETE");
        				  connection.setRequestProperty( "charset", "utf-8");
        				  connection.connect() ;
        				  OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        				  out.write(temp.toString());
        				  out.close();
      	return connection.getResponseCode()==200;
	}
	
	public void loadItems(Wantslist wl) throws InvalidKeyException, NoSuchAlgorithmException, IOException
	{
    	String link = "https://www.mkmapi.eu/ws/v2.0/wantslist/"+wl.getIdWantslist();
    	
    	HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
			               connection.addRequestProperty("Authorization", auth.generateOAuthSignature(link,"GET")) ;
			               connection.connect() ;
		String xml= IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
		Response res = (Response)xstream.fromXML(xml);
		wl.setItem(res.getWantslist().get(0).getItem());
	}
	
	
}
