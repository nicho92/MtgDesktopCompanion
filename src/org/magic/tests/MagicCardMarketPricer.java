package org.magic.tests;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Article.ARTICLES_ATT;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.services.ArticleService;
import org.api.mkm.services.ProductServices;
import org.api.mkm.tools.MkmAPIConfig;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MTGControler;
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
	    	props.put("APP_TOKEN", "");
			props.put("APP_SECRET", "");
			props.put("APP_ACCESS_TOKEN", "");
			props.put("APP_ACCESS_TOKEN_SECRET", "");
			props.put("LANGUAGE_ID", "1");
			props.put("IS_EXACT", "false");
			props.put("REF_PRICE", "LOW");
			props.put("COMMONCHECK", "false");
			props.put("MAX", "10");
		save();
    	}
    	try {
    		//if(!new File(confdir,props.getProperty("KEYSTORE_NAME")).exists())
    			InstallCert.install("www.mkmapi.eu", MTGControler.KEYSTORE_NAME, MTGControler.KEYSTORE_PASS);
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGControler.CONF_DIR,MTGControler.KEYSTORE_NAME).getAbsolutePath());
    	} catch (Exception e1) {
			logger.error(e1);
		}
    	
    	
    	
     }
  
    public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws IOException {
    	try{	
    		MkmAPIConfig.getInstance().init(props.getProperty("APP_ACCESS_TOKEN_SECRET").toString(),
    			props.getProperty("APP_ACCESS_TOKEN").toString(),
    			props.getProperty("APP_SECRET").toString(),
    			props.getProperty("APP_TOKEN").toString());
    
    	lists = new ArrayList<MagicPrice>();
    	
    	if(props.getProperty("COMMONCHECK").equals("false") && me.getRarity().equalsIgnoreCase("Common"))
        {
        	MagicPrice mp = new MagicPrice();
        	mp.setCurrency("EUR");
        	mp.setValue(0.10);
        	mp.setSite(getName());
        	mp.setSeller("Not checked common");
        	lists.add(mp);
        	return lists;
        }
       ProductServices pService = new ProductServices();
       Map<PRODUCT_ATTS,String> atts = new HashMap<Product.PRODUCT_ATTS, String>();
		atts.put(PRODUCT_ATTS.idGame, "1");
		atts.put(PRODUCT_ATTS.idLanguage, props.getProperty("LANGUAGE_ID"));
		atts.put(PRODUCT_ATTS.exact,props.getProperty("IS_EXACT"));
		
		List<Product> list = pService.find(card.getName(), atts);
		Product resultat = null;
		String edName=me.getSet();
		for(Product p : list)
		{
			if(me.getMkm_name()!=null)
				edName=me.getMkm_name();
			
			if(p.getExpansionName().equalsIgnoreCase(edName))
			{
				resultat=p;
				break;
			}
			
		}
		
		if(resultat==null)
		{
			logger.debug(getName() + " found no item");
			return lists;
		}
		
    	
		ArticleService aServ = new ArticleService();
		Map<ARTICLES_ATT,String> aatts = new HashMap<ARTICLES_ATT, String>();
		aatts.put(ARTICLES_ATT.start, "0");
		aatts.put(ARTICLES_ATT.maxResults, props.getProperty("MAX"));
		
		List<Article> articles = aServ.find(resultat, aatts);
		
		for(Article a : articles)
		{
			MagicPrice mp = new MagicPrice();
					   mp.setSeller(String.valueOf(a.getSeller()));
					   mp.setValue(a.getPrice());
					   mp.setQuality(a.getCondition());
					   mp.setUrl("https://www.magiccardmarket.eu"+resultat.getWebsite());
					   mp.setSite("magiccardmarket.eu");
					   mp.setFoil(a.isFoil());
					   mp.setCurrency("EUR");
			lists.add(mp);
		}
		
       
    }
    catch(Exception e) {
    	logger.error(e);
		
	} 
    
    return lists;
    }
    
    public String generateOAuthSignature(String link,String method) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {

        String realm = link ;
        String oauth_version =  props.get("OAUTH_VERSION").toString() ;
        String oauth_consumer_key = props.get("APP_TOKEN").toString() ;
        String oauth_token = props.get("APP_ACCESS_TOKEN").toString() ;
        String oauth_signature_method = props.get("CRYPT").toString();
        String oauth_timestamp = ""+ (System.currentTimeMillis()/1000) ;
        String oauth_nonce = "" + System.currentTimeMillis() ;
        
       
        String baseString = method+"&" + URLEncoder.encode(link,props.get("ENCODING").toString()) + "&" ;
        
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
	
	
	public String getName() {
		return "Magic Card Market";
	}

	
}