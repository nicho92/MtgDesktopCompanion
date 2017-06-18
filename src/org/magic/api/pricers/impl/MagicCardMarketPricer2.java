package org.magic.api.pricers.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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

public class MagicCardMarketPricer2 extends AbstractMagicPricesProvider{
    
   private List<MagicPrice> lists;
    
    static final Logger logger = LogManager.getLogger(MagicCardMarketPricer2.class.getName());

    public MagicCardMarketPricer2() {
    	super();
    	
    	
    	if(!new File(confdir, getName()+".conf").exists()){
	    	props.put("APP_TOKEN", "");
			props.put("APP_SECRET", "");
			props.put("APP_ACCESS_TOKEN", "");
			props.put("APP_ACCESS_TOKEN_SECRET", "");
			props.put("LANGUAGE_ID", "1");
			props.put("IS_EXACT", "false");
			props.put("COMMONCHECK", "false");
			props.put("MAX", "10");
			props.put("MODE_SELLER", "false");
		save();
		
		MkmAPIConfig.getInstance().init(
				props.getProperty("APP_ACCESS_TOKEN_SECRET").toString(),
    			props.getProperty("APP_ACCESS_TOKEN").toString(),
    			props.getProperty("APP_SECRET").toString(),
    			props.getProperty("APP_TOKEN").toString());
    	}
    	try {
    		//if(!new File(confdir,props.getProperty("KEYSTORE_NAME")).exists())
    			InstallCert.install("www.mkmapi.eu", MTGControler.KEYSTORE_NAME, MTGControler.KEYSTORE_PASS);
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGControler.CONF_DIR,MTGControler.KEYSTORE_NAME).getAbsolutePath());
    	} catch (Exception e1) {
			logger.error(e1);
		}
     }
    
    
	
	public Product getProductFromCard(MagicCard mc,List<Product> list)
	{
		String edName=mc.getEditions().get(0).getSet();
		Product resultat = null;
		for(Product p : list)
		{
			if(mc.getEditions().get(0).getMkm_name()!=null)
				edName=mc.getEditions().get(0).getMkm_name();
			
			if(p.getExpansionName().startsWith(edName))
			{
				resultat=p;
				break;
			}
		}
		return resultat;
		
	}

  
    public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws IOException {
    	try{	
    		
    		if(me==null)
    			me=card.getEditions().get(0);
    
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
		
		if(props.getProperty("MODE_SELLER").equals("false"))
		{
			for(Product p : list)
			{
				MagicPrice mp = new MagicPrice();
				   mp.setSeller(String.valueOf(p.getExpansionName()));
				   mp.setValue(p.getPriceGuide().getLOW());
				   mp.setQuality("");
				   mp.setUrl("https://www.magiccardmarket.eu"+p.getWebsite());
				   mp.setSite("magiccardmarket.eu");
				   mp.setFoil(false);
				   mp.setCurrency("EUR");
				   mp.setLanguage(null);
				   lists.add(mp);
			}
		}
		else
		{
		
		Product resultat = getProductFromCard(card, list);
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
					   mp.setLanguage(a.getLanguage().toString());
			lists.add(mp);
		}
		}
		
       
    }
    catch(Exception e) {
    	logger.error(e);
		
	} 
    
    return lists;
    }
    
 
	public String getName() {
		return "Magic Card Market 2";
	}

	
}