package org.api.mkm.modele.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.api.mkm.modele.Product;
import org.apache.commons.beanutils.BeanUtils;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Article.ARTICLES_ATT;
import org.api.mkm.modele.Link;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.modele.WantItem;
import org.api.mkm.modele.Wantslist;
import org.api.mkm.modele.services.ArticleService;
import org.api.mkm.modele.services.ProductServices;
import org.api.mkm.modele.services.WantsService;
import org.api.mkm.modele.tools.MkmAPIConfig;
import org.magic.api.exports.impl.MKMOnlineWantListExport.WantList;
import org.magic.api.pricers.impl.MagicCardMarketPricer;

public class TestProducts {

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		MagicCardMarketPricer pricer = new MagicCardMarketPricer();
		MkmAPIConfig.getInstance().init(pricer.getProperty("APP_ACCESS_TOKEN_SECRET").toString(),
										pricer.getProperty("APP_ACCESS_TOKEN").toString(),
										pricer.getProperty("APP_SECRET").toString(),
										pricer.getProperty("APP_TOKEN").toString());
		
		
		WantsService wanServices = new WantsService();
		
		Wantslist wl= wanServices.getWantList().get(0);
		wanServices.loadItems(wl);
		
		for(Link link : wl.getLinks())
			System.out.println(BeanUtils.describe(link));
		
		for(WantItem it : wl.getItem())
		{
			System.out.println(it.getProduct());
		}
		
		wanServices.createWantList("TOTOF");
		
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
		
		/*
		ProductServices prodServices = new ProductServices();
		ArticleService artServices = new ArticleService();
		Map<ARTICLES_ATT, String> map = new HashMap<ARTICLES_ATT,String>();
		
		
		Product p = prodServices.getById("15145");
		
		map.put(ARTICLES_ATT.start, "0");
		map.put(ARTICLES_ATT.maxResults,"20");
		map.put(ARTICLES_ATT.idLanguage, "1");
		map.put(ARTICLES_ATT.minCondition, "NM");
		
		System.out.println(p.getPriceGuide().getAVG());
		
		List<Article> arts = artServices.find(p, map);
		
		for(Article art : arts)
		{
			art.setProduct(p);
			System.out.println(BeanUtils.describe(art));
		}
		*/

	}

}
