package org.api.mkm.modele.services;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.api.mkm.modele.Expansion;
import org.api.mkm.modele.Link;
import org.api.mkm.modele.Localization;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.modele.ProductListFile;
import org.api.mkm.modele.Response;
import org.api.mkm.modele.tools.MkmAPIConfig;
import org.api.mkm.modele.tools.Tools;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

public class ProductServices {

	private AuthenticationServices auth;
	private XStream xstream;
	
	
	public ProductServices() {
		auth=MkmAPIConfig.getInstance().getAuthenticator();
		
		xstream = new XStream(new StaxDriver());
			XStream.setupDefaultSecurity(xstream);
	 		xstream.addPermission(AnyTypePermission.ANY);
	 		xstream.alias("response", Response.class);
	 		xstream.addImplicitCollection(Response.class,"product", Product.class);
	 		xstream.addImplicitCollection(Response.class,"links",Link.class);
	 		xstream.addImplicitCollection(Product.class,"links",Link.class);
	 		xstream.addImplicitCollection(ProductListFile.class,"links",Link.class);
	 		xstream.addImplicitCollection(Product.class,"localization",Localization.class);
	 		xstream.addImplicitCollection(Product.class,"reprint",Expansion.class);
	 		xstream.ignoreUnknownElements();
	}
	
	public void exportProductList(File f) throws IOException, InvalidKeyException, NoSuchAlgorithmException
	{
		String link="https://www.mkmapi.eu/ws/v2.0/productlist";
		xstream.alias("response", ProductListFile.class);
		
		
	    HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
			               connection.addRequestProperty("Authorization", auth.generateOAuthSignature(link,"GET")) ;
			               connection.connect() ;
		String xml= IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
		
		ProductListFile res = (ProductListFile)xstream.fromXML(xml);
		
		
		byte[] bytes = Base64.decodeBase64( res.getProductsfile());
		FileUtils.writeByteArrayToFile( f, bytes );
		
	}
	
	
	
	public List<Product> find(String name,Map<PRODUCT_ATTS,String> atts) throws InvalidKeyException, NoSuchAlgorithmException, IOException
	{
		String link = "https://www.mkmapi.eu/ws/v2.0/products/find?search="+name;
		
		if(atts.size()>0)
    	{
			link+="&";
    		List<String> paramStrings = new ArrayList<String>();
 	        for(PRODUCT_ATTS parameter:atts.keySet())
	             paramStrings.add(parameter + "=" + atts.get(parameter));
	        
 	        link+=Tools.join(paramStrings, "&");
    	}
		
	    HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
			               connection.addRequestProperty("Authorization", auth.generateOAuthSignature(link,"GET")) ;
			               connection.connect() ;
		String xml= IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
		Response res = (Response)xstream.fromXML(xml);
		return res.getProduct();
	}
	
	
	public Product getById(String idProduct) throws InvalidKeyException, NoSuchAlgorithmException, IOException
	{
    	String link = "https://www.mkmapi.eu/ws/v2.0/products/"+idProduct;
	    HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
			               connection.addRequestProperty("Authorization", auth.generateOAuthSignature(link,"GET")) ;
			               connection.connect() ;
		String xml= IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
		Response res = (Response)xstream.fromXML(xml);
		return res.getProduct().get(0);
	}
	
}
