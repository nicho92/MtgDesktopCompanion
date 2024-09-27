package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.api.mkm.exceptions.MkmException;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Article.ARTICLES_ATT;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.services.ArticleService;
import org.api.mkm.services.CartServices;
import org.api.mkm.services.ProductServices;
import org.api.mkm.tools.MkmAPIConfig;
import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;

public class MagicCardMarketPricer2 extends AbstractPricesProvider  {

	private static final String IS_EXACT = "IS_EXACT";
	private static final String FALSE = "false";
	private static final String LANGUAGE_ID = "LANGUAGE_ID";
	private static final String FILTER_COUNTRY = "FILTER_COUNTRY";
	private static final String MIN_CONDITION = "MIN_CONDITION";

	private List<MTGPrice> lists;
	private boolean initied=false;

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	private void init()
	{


		try {
			MkmAPIConfig.getInstance().init(getAuthenticator().getTokensAsProperties());
			initied=true;
		} catch (MkmException e) {
			logger.error(e);
		}
	}


	public static Product getProductFromCard(MTGCard mc, List<Product> list) {

		if(list.size()==1)
			return list.get(0);

		String edName = mc.getEdition().getSet();
		Product resultat = null;

		if (mc.getEdition().getMkmName() != null)
			edName = mc.getEdition().getMkmName();


		if(mc.isExtraCard())
			edName +=": Extras";



		Integer mkmId=0;

		if(mc.getMkmId()!=null)
			mkmId=mc.getMkmId();

		for (Product p : list)
		{

			if (p.getCategoryName().equalsIgnoreCase("Magic Single") && (p.getExpansionName().equalsIgnoreCase(edName) || (p.getIdProduct()==mkmId && !mc.isExtraCard()))) {
				resultat = p;
				break;
			}
		}
		return resultat;
	}


	@Override
	public List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		if(!initied)
			init();

		try {
			lists = new ArrayList<>();

			logger.info("{} looking for {}/{}",getName(),card,card.getEdition());

			if (card.getRarity() != null && !getBoolean("COMMONCHECK") && card.getRarity()==EnumRarity.COMMON) {
				var mp = new MTGPrice();
					mp.setCardData(card);
					mp.setCurrency("EUR");
					mp.setValue(0.01);
					mp.setSite(getName());
					mp.setSeller("Not checked common");
					lists.add(mp);
					return lists;
				}

			var pService = new ProductServices();
			EnumMap<PRODUCT_ATTS, String> atts = new EnumMap<>(PRODUCT_ATTS.class);
			atts.put(PRODUCT_ATTS.idGame, "1");


			if(!getString(IS_EXACT).isEmpty())
				atts.put(PRODUCT_ATTS.exact, getString(IS_EXACT));

			if (!getString(LANGUAGE_ID).equals(""))
				atts.put(PRODUCT_ATTS.idLanguage, getString(LANGUAGE_ID));

			if (!getBoolean("USER_ARTICLE")) {
				var p = getProductFromCard(card, pService.findProduct(card.getName(), atts));

				if (p != null) {
					p = pService.getProductById(p.getIdProduct());
					var mp = new MTGPrice();
					mp.setSeller(String.valueOf(p.getExpansionName()));
					mp.setValue(p.getPriceGuide().getLOW());
					mp.setQuality("");
					mp.setUrl(MkmConstants.MKM_SITE_URL  + p.getWebsite());
					mp.setSellerUrl(MkmConstants.MKM_SITE_URL+"/fr/Magic/Users/"+mp.getSeller()+"/Offers/Singles");
					mp.setSite(getName());
					mp.setFoil(false);
					mp.setCurrency("EUR");
					mp.setLanguage(String.valueOf(p.getLocalization()));
					lists.add(mp);
				}

			}
			else {
				List<Product> list = pService.findProduct(card.getName(), atts);
				var resultat = getProductFromCard(card, list);
				if (resultat == null) {
					logger.info("{} found no product for {}",getName(),card);
					return lists;
				}

				var aServ = new ArticleService();
				EnumMap<ARTICLES_ATT, String> aatts = new EnumMap<>(ARTICLES_ATT.class);
				aatts.put(ARTICLES_ATT.start, "0");
				aatts.put(ARTICLES_ATT.maxResults, getString("MAX"));

				if (!getString(LANGUAGE_ID).equals(""))
					aatts.put(ARTICLES_ATT.idLanguage, getString(LANGUAGE_ID));

				if (!getString(MIN_CONDITION).equals(""))
					aatts.put(ARTICLES_ATT.minCondition, getString(MIN_CONDITION));

				List<Article> articles = aServ.find(resultat, aatts);


				for (Article a : articles)
				{
					var mp = new MTGPrice();
							mp.setSeller(String.valueOf(a.getSeller()));
							mp.setSellerUrl(MkmConstants.MKM_SITE_URL+"/fr/Magic/Users/"+mp.getSeller()+"/Offers/Singles");

							mp.setCountry(String.valueOf(a.getSeller().getAddress().getCountry()));
							mp.setValue(a.getPrice());
							mp.setQuality(a.getCondition());
							mp.setUrl(MkmConstants.MKM_SITE_URL + resultat.getWebsite());
							mp.setSite(getName());
							mp.setFoil(a.isFoil());
							mp.setCurrency("EUR");
							mp.setLanguage(a.getLanguage().toString());
							mp.setShopItem(a);

					if(StringUtils.isEmpty(getString(FILTER_COUNTRY)) || ArrayUtils.contains(getArray(FILTER_COUNTRY),mp.getCountry().toUpperCase()))
						lists.add(mp);

				}
			}

		} catch (Exception e) {
			logger.error("Error retrieving prices for {}",card, e);
			logger.error(e);

		}
		logger.info("{} found {} offers",getName(),lists.size());
		return lists;
	}

	@Override
	public String getName() {
		return MkmConstants.MKM_NAME;
	}

	@Override
	public void alertDetected(final List<MTGPrice> p) {
		if(!initied)
			init();

		ThreadManager.getInstance().executeThread( new MTGRunnable() {

			@Override
			protected void auditedRun() {
				if (!p.isEmpty() && getBoolean("AUTOMATIC_ADD_CARD_ALERT")) {
					var cart = new CartServices();
					try {
						List<Article> list = new ArrayList<>();

						for (MTGPrice mp : p) {
							Article a = (Article) mp.getShopItem();
							a.setCount(1);
							list.add(a);
						}
						boolean res = cart.addArticles(list);
						logger.info("add {} to card : {}",list,res);
					} catch (Exception e) {
						logger.error("Could not add {} to cart",p, e);
					}
				}

			}
		}, "addCart");
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {

		var map = new HashMap<String,MTGProperty>();
				
		map.put(LANGUAGE_ID, MTGProperty.newIntegerProperty("1", "1 - English, 2 - French, 3 - German,4 - Spanish, 5 - Italian,6 - Simplified Chinese,7 - Japanese, 8 - Portuguese, 9 - Russian,10 - Korean,11 - Traditional Chinese", 1, 11));
		map.put(IS_EXACT, new MTGProperty("","set to 1 f you want to search with the exact cardname","","1"));
		map.put(MIN_CONDITION, new MTGProperty("Minimum condition for filter.  (MT for Mint > NM for Near Mint > EX for Exellent > GD for Good > LP for Light Played > PL for Played > PO for Poor). Separated by comma", "","MT","NM","EX","EX","GD","LP","PL","PO"));
		map.put("COMMONCHECK", MTGProperty.newBooleanProperty(FALSE, "set to true if you want to check price of common rarity cards"));
		map.put("MAX", MTGProperty.newIntegerProperty("10","Max result to return",1,20));
		map.put("USER_ARTICLE", MTGProperty.newBooleanProperty(TRUE,"Set to true if you want to check prices from users stock. False will return average"));
		map.put("AUTOMATIC_ADD_CARD_ALERT", MTGProperty.newBooleanProperty(FALSE,"set to true if you want to automaticaly add product to your online cart when card is found"));
		map.put(FILTER_COUNTRY, new MTGProperty("EN,"+Locale.getDefault().getCountry()," Filter users country for results. Separated by comma"));

		return map;

	}

	@Override
	public String getVersion() {
		return MkmConstants.MKM_API_VERSION;
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return MkmConstants.mkmTokens();
	}



}