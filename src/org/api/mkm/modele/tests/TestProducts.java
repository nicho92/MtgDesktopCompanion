package org.api.mkm.modele.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.modele.services.ProductServices;
import org.api.mkm.modele.tools.MkmAPIConfig;
import org.magic.api.pricers.impl.MagicCardMarketPricer;

public class TestProducts {

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		MagicCardMarketPricer pricer = new MagicCardMarketPricer();
		MkmAPIConfig.getInstance().init(pricer.getProperty("APP_ACCESS_TOKEN_SECRET").toString(),
										pricer.getProperty("APP_ACCESS_TOKEN").toString(),
										pricer.getProperty("APP_SECRET").toString(),
										pricer.getProperty("APP_TOKEN").toString());
		
		/*
		ArticleService artServices = new ArticleService();
		Map<ARTICLES_ATT, String> map = new HashMap<ARTICLES_ATT,String>();
		
		map.put(ARTICLES_ATT.idLanguage, "1");
		map.put(ARTICLES_ATT.start, "0");
		map.put(ARTICLES_ATT.maxResults, "5");
		
		
		List<Article> pcs = artServices.find("295893",map);
		for(Article a : pcs)
		{
			System.out.println(a.getPrice());
			System.out.println(a.getSeller());
		}
		
		*/
		
		
		ProductServices artServices = new ProductServices();
		Map<PRODUCT_ATTS, String> map = new HashMap<PRODUCT_ATTS,String>();
		
		map.put(PRODUCT_ATTS.idLanguage, "1");
		map.put(PRODUCT_ATTS.idGame, "1");
		map.put(PRODUCT_ATTS.start, "0");
		map.put(PRODUCT_ATTS.maxResults, "5");
		
		
		List<Product> pcs = artServices.find("Tarmog",map);
		
		for(Product p : pcs)
			System.out.println(BeanUtils.describe(p));
		
		
		

	}

}
