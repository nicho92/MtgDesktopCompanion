package org.magic.api.pricers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.InstallCert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MagicCardMarketPricer extends AbstractMagicPricesProvider{
    
    private int _lastCode;
    private String _lastContent;
    private List<MagicPrice> lists;
    
    static final Logger logger = LogManager.getLogger(MagicCardMarketPricer.class.getName());

    public MagicCardMarketPricer() {
    	super();
    	
    	
    	if(!new File(confdir, getName()+".conf").exists()){
    	props.put("APP_TOKEN", "YwlAAFW7VTANkQ0N");
		props.put("APP_SECRET", "");
		props.put("APP_ACCESS_TOKEN", "bARV5r0kGsqELWQyFdpYhVVeadzfqbQk");
		props.put("APP_ACCESS_TOKEN_SECRET", "");
		props.put("GAME_ID", "1");
		props.put("ENCODING", "UTF-8");
		props.put("LANGUAGE_ID", "1");
		props.put("IS_EXACT", "false");
		props.put("WS_VERSION", "v1.1");
		props.put("CERT_SERV", "www.mkmapi.eu");
		props.put("URL", "https://www.mkmapi.eu/ws/%VERSION%/products/%KEYWORD%/%GAME%/%LANG%/%IS_EXACT%");
		props.put("WEBSITE", "https://www.magiccardmarket.eu");
		props.put("KEYSTORE_PASS", "changeit");
		props.put("KEYSTORE_NAME", "jssecacerts");
		props.put("REF_PRICE", "LOW");
		props.put("OAUTH_VERSION", "1.0");
		props.put("CRYPT", "HMAC-SHA1");
		props.put("REF_PRICE", "LOW");
		props.put("MAX", "5");
		save();
    	}
    	
    	try {
    		//if(!new File(confdir,props.getProperty("KEYSTORE_NAME")).exists())
    			InstallCert.install(props.getProperty("CERT_SERV"), props.getProperty("KEYSTORE_NAME"), props.getProperty("KEYSTORE_PASS"));
    	    
    		System.setProperty("javax.net.ssl.trustStore",new File(confdir,props.getProperty("KEYSTORE_NAME")).getAbsolutePath());
    		
		} catch (Exception e1) {
			logger.error(e1);
		}
    	
    	
    	
     }
  
    public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws IOException {
    	
    try{
       	
    	
    	
    	lists = new ArrayList<MagicPrice>();
    	_lastCode = 0;
        _lastContent = "";
    
        String url = props.getProperty("URL");
		   url = url.replaceAll("%GAME%", props.get("GAME_ID").toString());
		   url = url.replaceAll("%LANG%", props.get("LANGUAGE_ID").toString());
		   url = url.replaceAll("%IS_EXACT%", props.get("IS_EXACT").toString());
		   url = url.replaceAll("%VERSION%", props.get("WS_VERSION").toString());
	
		   String KEYWORD=card.getName();
		   props.put("KEYWORD", KEYWORD);
		   
		   
		   KEYWORD=URLEncoder.encode(KEYWORD,props.getProperty("ENCODING"));
		   
		   String link=url.replaceAll("%KEYWORD%", KEYWORD);
		   
		   logger.info(getName() +" looking for price : " + link);
		   
		   String authorizationProperty = generateOAuthSignature(link);
		     HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
				              connection.addRequestProperty("Authorization", authorizationProperty) ;
				              connection.connect() ;
            
				              
           _lastCode = connection.getResponseCode();
           logger.debug(getName() + " response :  " + _lastCode);
           
           BufferedReader rd = new BufferedReader(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream()));  
           StringBuffer sb = new StringBuffer();  
           String line;  
           while ((line = rd.readLine()) != null) {  
               sb.append(line);  
           }
           rd.close();
           _lastContent = sb.toString();
            
           if (200 == _lastCode) 
           {
        	  completeListFromXML(_lastContent);
           }
           else	   
           {
                logger.error(_lastContent);
           }
           
           if(lists.size()>Integer.parseInt(props.get("MAX").toString()))
  			 if(Integer.parseInt(props.get("MAX").toString())>-1)
  				 return lists.subList(0, Integer.parseInt(props.get("MAX").toString()));
    }
    catch(SSLHandshakeException e)
    {
    	logger.error(e);
    	
    } catch (NoSuchAlgorithmException|InvalidKeyException e) {
		logger.error(e);
	} 
    return lists;
    }
    
    private String generateOAuthSignature(String link) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {

        String realm = link ;
        String oauth_version =  props.get("OAUTH_VERSION").toString() ;
        String oauth_consumer_key = props.get("APP_TOKEN").toString() ;
        String oauth_token = props.get("APP_ACCESS_TOKEN").toString() ;
        String oauth_signature_method = props.get("CRYPT").toString();
        String oauth_timestamp = ""+ (System.currentTimeMillis()/1000) ;
        String oauth_nonce = "" + System.currentTimeMillis() ;
        
       
        String baseString = "GET&" + URLEncoder.encode(link,props.get("ENCODING").toString()) + "&" ;
        
        String paramString = "oauth_consumer_key=" + URLEncoder.encode(oauth_consumer_key,props.get("ENCODING").toString()) + "&" +
                             "oauth_nonce=" + URLEncoder.encode(oauth_nonce,props.get("ENCODING").toString()) + "&" +
                             "oauth_signature_method=" + URLEncoder.encode(oauth_signature_method,props.get("ENCODING").toString()) + "&" +
                             "oauth_timestamp=" + URLEncoder.encode(oauth_timestamp,props.get("ENCODING").toString()) + "&" +
                             "oauth_token=" + URLEncoder.encode(oauth_token,props.get("ENCODING").toString()) + "&" +
                             "oauth_version=" + URLEncoder.encode(oauth_version,props.get("ENCODING").toString()) ;
        
        
        baseString += URLEncoder.encode(paramString,props.get("ENCODING").toString()) ;
       
        String signingKey = URLEncoder.encode( props.get("APP_SECRET").toString(),props.get("ENCODING").toString()) + "&" + URLEncoder.encode(props.get("APP_ACCESS_TOKEN_SECRET").toString(),props.get("ENCODING").toString()) ;
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secret = new SecretKeySpec(signingKey.getBytes(), mac.getAlgorithm());
        mac.init(secret);
        byte[] digest = mac.doFinal(baseString.getBytes());
        
        
        String oauth_signature = DatatypeConverter.printBase64Binary(digest); //Base64.encode(digest).trim() ;     
        
        String authorizationProperty = 
                "OAuth " +
                "realm=\"" + realm + "\", " + 
                "oauth_version=\"" + oauth_version + "\", " +
                "oauth_timestamp=\"" + oauth_timestamp + "\", " +
                "oauth_nonce=\"" + oauth_nonce + "\", " +
                "oauth_consumer_key=\"" + oauth_consumer_key + "\", " +
                "oauth_token=\"" + oauth_token + "\", " +
                "oauth_signature_method=\"" + oauth_signature_method + "\", " +
                "oauth_signature=\"" + oauth_signature + "\"" ;
        
        
        return authorizationProperty;
	}

	private void completeListFromXML(String xmlContent) {
    	
    	logger.debug(xmlContent);
    	try {
    		
    		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xmlContent)));
    		NodeList nodes =d.getElementsByTagName("product");
    		
    		 XPath xpath = XPathFactory.newInstance().newXPath();
    		    XPathExpression expr = xpath.compile("//response/product/category/idCategory");
    		    Object result = expr.evaluate(d, XPathConstants.NODESET);
    		    NodeList ids = (NodeList) result;
    		
    		for(int i = 0; i<nodes.getLength();i++)
    		{
    		
    			Node n = nodes.item(i);
    			Element e =  (Element) n.getChildNodes();
    			
    			if(Integer.parseInt(ids.item(i).getTextContent())==1)
    			{
    			MagicPrice mp = new MagicPrice();
    			
					mp.setUrl(props.getProperty("WEBSITE")+e.getElementsByTagName("website").item(0).getTextContent());
					mp.setSite(getName());
					mp.setCurrency("EUR");
					mp.setSeller(e.getElementsByTagName("expansion").item(0).getTextContent());
					mp.setValue(Double.parseDouble(((Element)e.getElementsByTagName("priceGuide").item(0)).getElementsByTagName(props.getProperty("REF_PRICE")).item(0).getTextContent()));
				lists.add(mp);
    			}
    		}
    		 logger.info(getName() +" found " + lists.size() + " items");
    	
		} catch (Exception e) {
			 logger.error(e);
		} 
    	
	}
	@Override
	public String getName() {
		return "Magic Card Market";
	}

	
}