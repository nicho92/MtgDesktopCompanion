package org.magic.api.pricers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.api.mkm.exceptions.MkmException;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Article.ARTICLES_ATT;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.services.ArticleService;
import org.api.mkm.services.CartServices;
import org.api.mkm.services.ProductServices;
import org.api.mkm.tools.MkmAPIConfig;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.tools.InstallCert;

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
			props.put("MIN_CONDITION", "");
			props.put("COMMONCHECK", "false");
			props.put("MAX", "10");
			props.put("USER_ARTICLE", "false");
			props.put("AUTOMATIC_ADD_CARD_ALERT", "false");
		save();
		
		try{
		MkmAPIConfig.getInstance().init(
				props.getProperty("APP_ACCESS_TOKEN_SECRET").toString(),
    			props.getProperty("APP_ACCESS_TOKEN").toString(),
    			props.getProperty("APP_SECRET").toString(),
    			props.getProperty("APP_TOKEN").toString());
		}
		catch(MkmException e)
		{
			logger.error(e);
		}
		
    	}
    	
    	
    	try {
    		//if(!new File(confdir,props.getProperty("KEYSTORE_NAME")).exists())
    			InstallCert.install("www.mkmapi.eu");
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGControler.CONF_DIR,MTGControler.KEYSTORE_NAME).getAbsolutePath());
    	} catch (Exception e1) {
			logger.error(e1);
		}
     }
    
    public static void selectEditionCard(MagicCard mc,String edition)
    {
    	for(MagicEdition ed : mc.getEditions())
			if(ed.getSet().startsWith(edition))
			{
				mc.getEditions().add(0, ed);
				break;
			}
    }
	
	public static Product getProductFromCard(MagicCard mc,List<Product> list)
	{
		String edName=mc.getEditions().get(0).getSet();
		Product resultat = null;
		for(Product p : list)
		{
			if(mc.getEditions().get(0).getMkm_name()!=null)
				edName=mc.getEditions().get(0).getMkm_name();
			
			
			logger.debug("\""+edName + "\".startWith("+p.getExpansionName()+")"+StringUtils.getJaroWinklerDistance(edName, p.getExpansionName()) );
			
			if(edName.startsWith(p.getExpansionName()))
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
    	
    	logger.info(getName() + " looking for " + card +" " + me);
    	
    	if(props.getProperty("COMMONCHECK").equals("false") && me.getRarity().equalsIgnoreCase("Common"))
        {
        	MagicPrice mp = new MagicPrice();
        	mp.setCurrency("EUR");
        	mp.setValue(0.01);
        	mp.setSite(getName());
        	mp.setSeller("Not checked common");
        	lists.add(mp);
        	return lists;
        }
    	
    	
    	
       ProductServices pService = new ProductServices();
       Map<PRODUCT_ATTS,String> atts = new HashMap<Product.PRODUCT_ATTS, String>();
		atts.put(PRODUCT_ATTS.idGame, "1");
		atts.put(PRODUCT_ATTS.exact,props.getProperty("IS_EXACT"));
		
		
		if(!props.getProperty("LANGUAGE_ID").equals(""))
			atts.put(PRODUCT_ATTS.idLanguage,props.getProperty("LANGUAGE_ID"));

		
		if(props.getProperty("USER_ARTICLE").equals("false"))
		{
				Product p = getProductFromCard(card,pService.findProduct(card.getName(), atts));
				p = pService.getProductById(p.getIdProduct());
				MagicPrice mp = new MagicPrice();
				   mp.setSeller(String.valueOf(p.getExpansionName()));
				   mp.setValue(p.getPriceGuide().getLOW());
				   mp.setQuality("");
				   mp.setUrl("https://www.magiccardmarket.eu"+p.getWebsite());
				   mp.setSite("MagicCardMarket");
				   mp.setFoil(false);
				   mp.setCurrency("EUR");
				   mp.setLanguage(String.valueOf(p.getLocalization()));
				   lists.add(mp);
			
			
		}
		else
		{
			List<Product> list = pService.findProduct(card.getName(), atts);
			Product resultat = getProductFromCard(card, list);
			if(resultat==null)
			{
				logger.info(getName() + " found no item");
				return lists;
			}
			
			ArticleService aServ = new ArticleService();
			Map<ARTICLES_ATT,String> aatts = new HashMap<ARTICLES_ATT, String>();
			aatts.put(ARTICLES_ATT.start, "0");
			aatts.put(ARTICLES_ATT.maxResults, props.getProperty("MAX"));
			
			if(!props.getProperty("LANGUAGE_ID").equals(""))
			aatts.put(ARTICLES_ATT.idLanguage, props.getProperty("LANGUAGE_ID"));
			
			if(!props.getProperty("MIN_CONDITION").equals(""))
				aatts.put(ARTICLES_ATT.minCondition,props.getProperty("MIN_CONDITION"));
	
			
			List<Article> articles = aServ.find(resultat, aatts);
			logger.debug(getName() +" found "  + articles.size() +" items");
			
			for(Article a : articles)
			{
				MagicPrice mp = new MagicPrice();
						   mp.setSeller(String.valueOf(a.getSeller()));
						   mp.setValue(a.getPrice());
						   mp.setQuality(a.getCondition());
						   mp.setUrl("https://www.magiccardmarket.eu"+resultat.getWebsite());
						   mp.setSite("MagicCardMarket");
						   mp.setFoil(a.isFoil());
						   mp.setCurrency("EUR");
						   mp.setLanguage(a.getLanguage().toString());
						   mp.setShopItem(a);
						   
				lists.add(mp);
			}
		}
		
       
    }
    catch(Exception e) {
    	e.printStackTrace();
    	logger.error(e);
		
	} 
    
    return lists;
    }
    
 
	public String getName() {
		return "MagicCardMarket";
	}



	@Override
	public void alertDetected(final List<MagicPrice> p) {
		
		
		ThreadManager.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				if(p.size()>0)
				{
					if(props.getProperty("AUTOMATIC_ADD_CARD_ALERT").equals("true"))
					{
						CartServices cart = new CartServices();
						try {
							List<Article> list = new ArrayList<Article>();
							
							for(MagicPrice mp : p)
							{
								Article a = (Article)mp.getShopItem();
								a.setCount(1);
								list.add(a);
							}
							boolean res = cart.addArticles(list);
							logger.info("add " + list + " to card :"  + res);
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("Could not add " + p +" to cart");
						}
					}
				}
				
			}
		}, "addCart");

		
	}

	
}