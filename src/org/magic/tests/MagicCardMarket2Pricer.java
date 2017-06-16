package org.magic.tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Link;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

public class MagicCardMarket2Pricer extends AbstractMagicPricesProvider{
    
    private int _lastCode;
    private List<MagicPrice> lists;
    
    static final Logger logger = LogManager.getLogger(MagicCardMarket2Pricer.class.getName());

    
    public static void main(String[] args) throws IOException {
    	MagicCardMarket2Pricer manager = new MagicCardMarket2Pricer();
    	manager.getPrice(null,null);
	}
    
    
    public MagicCardMarket2Pricer() {
    	super();
    	if(!new File(confdir, getName()+".conf").exists()){
	    	props.put("APP_TOKEN", "");
			props.put("APP_SECRET", "");
			props.put("APP_ACCESS_TOKEN", "");
			props.put("APP_ACCESS_TOKEN_SECRET", "");
			save();
    	}
     }
  
    public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws IOException {
    	 try{
    		 XStream xstream = new XStream(new StaxDriver());
    				XStream.setupDefaultSecurity(xstream);
    		 		xstream.addPermission(AnyTypePermission.ANY);
    		 		xstream.alias("article", Article.class);
    		 		xstream.alias("response", List.class);
    		 		xstream.alias("links", Link.class);
    		 		xstream.ignoreUnknownElements();
    		 		
    		 //   String link = "https://www.mkmapi.eu/ws/v2.0/products/find?search=Tarmogoyf&idGame=1&idLanguage=1";
    		 	String link = "https://www.mkmapi.eu/ws/v2.0/articles/15145?idLanguage=1&idGame=1&start=0&maxResults=10";//
		    	String authorizationProperty = generateOAuthSignature(link,"GET");
			    HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
					               connection.addRequestProperty("Authorization", authorizationProperty) ;
					               connection.connect() ;
				_lastCode = connection.getResponseCode();
				System.out.println(_lastCode);
				String xml= IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
				//xml = xml.replaceAll("<response>", "").replaceAll("</response>", "");
				System.out.println(xml);
				List<Article> res = (List<Article>)xstream.fromXML(xml,new Article());
				
				for(Object a : res)
				{
					System.out.println(a);
				}
				
    	 }
    	 catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
		return lists;
    	
    }
    
    static void prettyPrint(Document doc,PrintStream os) throws IOException
	{
		OutputFormat format = new OutputFormat(doc);
        format.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(os, format);
        serializer.serialize(doc);
	}
    
    private Map<String,String> parseQueryString(String query)
    {
        Map<String,String> queryParameters = new TreeMap<String, String>();
        
        String[] querySegments = query.split("&");
        for (String segment : querySegments)
        {
            String[] parts = segment.split("=");
            if (parts.length > 0)
            {
                String key = parts[0].replaceAll("\\?", " ").trim();
                String val = parts[1].trim();
                queryParameters.put(key, val);
            }
        }
        return queryParameters;
    }
    
    
    public String generateOAuthSignature(String url,String method) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException{
    	
    	 Map<String,String> headerParams = new HashMap<String,String>();
         Map<String,String> encodedParams = new TreeMap<String, String>();
         int index = url.indexOf("?");
    	 String accessSecret = props.getProperty("APP_ACCESS_TOKEN_SECRET");
         String accessToken = props.getProperty("APP_ACCESS_TOKEN");
         String appSecret = props.getProperty("APP_SECRET");
         String appToken = props.getProperty("APP_TOKEN");
         String signatureMethod = "HMAC-SHA1";
         String version = "1.0";
         String encode="UTF-8";
         String nonce="" + System.currentTimeMillis();
         String timestamp=""+ (System.currentTimeMillis()/1000);
         String baseUri = (index>0?url.substring(0,index):url);
         String signatureKey = URLEncoder.encode(appSecret,encode) + "&" + URLEncoder.encode(accessSecret,encode);
         
         headerParams = new TreeMap<String, String>();
         headerParams.put("oauth_consumer_key", appToken);
         headerParams.put("oauth_token", accessToken);
         headerParams.put("oauth_nonce", nonce);
         headerParams.put("oauth_timestamp", timestamp);
         headerParams.put("oauth_signature_method", signatureMethod);
         headerParams.put("oauth_version", version);
         headerParams.put("realm", baseUri);
         
         
         String baseString = method.toUpperCase()
                 + "&"
                 + URLEncoder.encode(baseUri, encode)
                 + "&";
         
         if (index > 0)
         {
             String urlParams = url.substring(index+1);
             Map<String,String> args = parseQueryString(urlParams);

             for (String k : args.keySet())
                 headerParams.put(k, args.get(k));
         }
         
         for (String k : headerParams.keySet())
         {
             if (false == k.equalsIgnoreCase("realm"))
             {
                 encodedParams.put(URLEncoder.encode(k,encode), URLEncoder.encode(headerParams.get(k),encode));
             }
         }
         
         List<String> paramStrings = new ArrayList<String>();
        
         for(String parameter:encodedParams.keySet())
             paramStrings.add(parameter + "=" + encodedParams.get(parameter));
         
         String paramString = URLEncoder.encode(join(paramStrings, "&"),encode);
         
         baseString += paramString;
         
         Mac mac = Mac.getInstance("HmacSHA1");
         SecretKeySpec secret = new SecretKeySpec(signatureKey.getBytes(), mac.getAlgorithm());
         mac.init(secret);
         byte[] digest = mac.doFinal(baseString.getBytes());
         String oAuthSignature = DatatypeConverter.printBase64Binary(digest);    
         headerParams.put("oauth_signature", oAuthSignature);
         
         List<String> headerParamStrings = new ArrayList<String>();
    
         for(String parameter:headerParams.keySet())
             headerParamStrings.add(parameter + "=\"" + headerParams.get(parameter) + "\"");
         
         String authHeader = "OAuth " + join(headerParamStrings,", ");
     	return authHeader;
    }

    
    private String join(Collection<?> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
              break;                  
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }
    
	public String getName() {
		return "Magic Card Market 2";
	}

	
}