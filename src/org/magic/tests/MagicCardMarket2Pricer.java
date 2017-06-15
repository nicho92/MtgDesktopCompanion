package org.magic.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MagicCardMarket2Pricer extends AbstractMagicPricesProvider{
    
    private int _lastCode;
    private String _lastContent;
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
			props.put("GAME_ID", "1");
			props.put("ENCODING", "UTF-8");
			props.put("LANGUAGE_ID", "1");
			props.put("IS_EXACT", "false");
			props.put("WS_VERSION", "v2.0");
			props.put("CERT_SERV", "www.mkmapi.eu");
			props.put("URL", "https://www.mkmapi.eu/ws/v2.0/metaproducts/find");
			props.put("WEBSITE", "https://www.magiccardmarket.eu");
			props.put("REF_PRICE", "LOW");
			props.put("OAUTH_VERSION", "1.0");
			props.put("CRYPT", "HMAC-SHA1");
			props.put("REF_PRICE", "LOW");
			props.put("MAX", "5");
			props.put("COMMONCHECK", "false");
		save();
    	}
     }
  
    public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws IOException {
    	
    try{
    	lists = new ArrayList<MagicPrice>();
    	_lastCode = 0;
        _lastContent = "";
    
        	String url = props.getProperty("URL")+"?search=Tarmogoyf&idGame=1&idLanguage=1";
		   
		   String authorizationProperty = generateOAuthSignature(url,"GET");
		   HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		   					 connection.addRequestProperty("Authorization", authorizationProperty) ;
		   					 connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
				             connection.connect();
				             
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
    catch(Exception e) {
    	e.printStackTrace();
		logger.error(e);
		
	} 
    
    return lists;
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
    
    
    public String generateOAuthSignature(String link,String method) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException {

        String realm = props.getProperty("URL").toString();
        String oauth_version =  props.get("OAUTH_VERSION").toString() ;
        String oauth_consumer_key = props.get("APP_TOKEN").toString() ;
        String oauth_token = props.get("APP_ACCESS_TOKEN").toString() ;
        String oauth_signature_method = props.get("CRYPT").toString();
        String oauth_timestamp = ""+ (System.currentTimeMillis()/1000) ;
        String oauth_nonce = "" + System.currentTimeMillis() ;
        String encode = props.get("ENCODING").toString();
        
        String baseString = method+"&" + URLEncoder.encode(realm,encode) ;
       
        Map<String,String> headerParams = new TreeMap<String,String>();
        	headerParams.put("realm",realm);
        	headerParams.put("oauth_consumer_key",oauth_consumer_key);
        	headerParams.put("oauth_nonce",oauth_nonce) ;
			headerParams.put("oauth_signature_method",oauth_signature_method) ;
			headerParams.put("oauth_timestamp",oauth_timestamp) ;
			headerParams.put("oauth_token",oauth_token) ;
			headerParams.put("oauth_version",oauth_version) ;
        
        int index=link.indexOf("?");
	
        if(index>0)
		{
			String urlParams = link.substring(index);
			headerParams.putAll(parseQueryString(urlParams));;
		}

        System.out.println(headerParams);
        
        for(String k : headerParams.keySet())
        {
            if (k.equals("realm")==false)
            {
            	baseString +="&"+URLEncoder.encode(k,encode)+"="+URLEncoder.encode(headerParams.get(k),encode) ;
            }
        }
        
        System.out.println(baseString);
         
	    baseString += baseString ;
	    String signatureKey = URLEncoder.encode( props.get("APP_SECRET").toString(),encode) + "&" + URLEncoder.encode(props.get("APP_ACCESS_TOKEN_SECRET").toString(),encode) ;
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secret = new SecretKeySpec(signatureKey.getBytes(), mac.getAlgorithm());
        mac.init(secret);
        byte[] digest = mac.doFinal(baseString.getBytes());
        String oauth_signature = DatatypeConverter.printBase64Binary(digest); //Base64.encode(digest).trim() ;     
        
        headerParams.put("oauth_signature", oauth_signature);
      
        List<String> headerParamStrings = new ArrayList<String>();
        for (String s : headerParams.keySet())
        {
            headerParamStrings.add(s + "=\"" + headerParams.get(s) + "\"");
        }
        
        
        String authHeader = "OAuth "+join(headerParamStrings, ", ");
        
      
        System.out.println(authHeader);
        
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
	
	public String getName() {
		return "Magic Card Market 2";
	}

	
}