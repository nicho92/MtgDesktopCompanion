package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ListUtils;
import org.api.mkm.exceptions.MkmException;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Inserted;
import org.api.mkm.modele.LightArticle;
import org.api.mkm.modele.Localization;
import org.api.mkm.modele.MkmBoolean;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.modele.WantItem;
import org.api.mkm.services.ProductServices;
import org.api.mkm.services.StockService;
import org.api.mkm.services.WantsService;
import org.api.mkm.tools.MkmAPIConfig;
import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.gui.components.dialog.MkmWantListChooserDialog;
import org.magic.services.MTGControler;

public class MkmOnlineExport extends AbstractCardExport {

	private static final String STOCK_USE = "STOCK_USE";
	private static final String MAX_WANTLIST_SIZE = "MAX_WANTLIST_SIZE";
	private static final String LANGUAGES = "LANGUAGES";
	private static final String QUALITY = "QUALITY";


	private EnumMap<PRODUCT_ATTS, String> atts;
	private ProductServices pService;

	private boolean init=false;

	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.ONLINE;
	}

	@Override
	public boolean needFile() {
		return false;
	}

	@Override
	public boolean needDialogForDeck(MODS mod) {
		return true;
	}

	@Override
	public boolean needDialogForStock(MODS mod) {
		return !(getBoolean(STOCK_USE));
	}



	private void init()
	{
		try {
			MkmAPIConfig.getInstance().init(getAuthenticator().getTokensAsProperties());

			pService = new ProductServices();
			atts = new EnumMap<>(PRODUCT_ATTS.class);
			atts.put(PRODUCT_ATTS.exact, "true");
			atts.put(PRODUCT_ATTS.idGame, "1");
			atts.put(PRODUCT_ATTS.idLanguage, "1");
			init=true;
		} catch (MkmException e) {
			logger.error(e);
			init=false;
		}
	}


	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		return importDeckFromFile(null);
	}

	@Override
	public MTGDeck importDeckFromFile(File f) throws IOException {

		if(!init)
			init();

		var service = new WantsService();
		var d = new MTGDeck();
		d.setName("import mkm");

		var diag = new MkmWantListChooserDialog();
		diag.setVisible(true);
		var list = diag.getSelectedWantList();

		if (list == null)
			throw new NullPointerException(getName() + " can't import deck for " + f);

		service.loadItems(list);
		for (WantItem w : list.getItem()) {
			try {
				var p = w.getProduct();
				if (p.getEnName().contains("(Version "))
					p.setEnName(p.getEnName().substring(0, p.getEnName().indexOf("(Version")));

				List<MTGCard> cards = getEnabledPlugin(MTGCardsProvider.class).searchCardByName( p.getEnName().trim(), null, true);
				MTGCard mc = cards.stream().filter(c->c.getEdition().getSet().equalsIgnoreCase(p.getExpansionName())).findAny().orElse(cards.get(0));
				notify(mc);
				d.getMain().put(mc, w.getCount());

			} catch (Exception _) {
				logger.error("could not import {}",w);
			}
		}

		return d;
	}


	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		if(!init)
			init();


		var wlService = new WantsService();
		List<WantItem> wants = new ArrayList<>();

		for (MTGCard mc : deck.getMain().keySet())
		{
			Integer p = null;
			try
			{
				if(mc.getMkmId()!=null)
				{
					p = mc.getMkmId();
				}
				else
				{
					List<Product> list = pService.findProduct(mc.getName(), atts);
					if(!list.isEmpty())
					{
						logger.debug("found multiple product for {} : {}",mc,list.size());
						var prod = MagicCardMarketPricer2.getProductFromCard(mc,list);

						if(prod!=null)
							p=prod.getIdProduct();
					}
				}
			}
			catch(Exception ex)
			{
				logger.error("could not export {}",mc,ex);
				p=null;
			}

			if (p != null) {
				var w = new WantItem();
				w.setIdProduct(p);
				w.setCount(deck.getMain().get(mc));
				w.setFoil(new MkmBoolean(false));
				w.setMinCondition(getString(QUALITY));
				w.setAltered(new MkmBoolean(false));
				w.setType("product");
				w.setSigned(new MkmBoolean(false));
				for (String s : getArray(LANGUAGES))
					w.getIdLanguage().add(Integer.parseInt(s));

				wants.add(w);
			} else {
				logger.debug("could not find product for {} ({}) ",mc,mc.getEdition());
			}
			notify(mc);
		}

		int max = getInt(MAX_WANTLIST_SIZE);
		if (wants.size() <= max) {
			var l = wlService.createWantList(deck.getName());
			logger.debug("Create {} list with {} items id={}" ,l,wants.size(),l.getIdWantsList());
			wlService.addItem(l, wants);
		} else {

			List<List<WantItem>> decoupes = ListUtils.partition(wants, max);

			for (var i = 0; i < decoupes.size(); i++) {
				var wl = wlService.createWantList(deck.getName() + "-" + (i + 1));
				logger.debug("Create {} list with {} items ",wl ,decoupes.get(i).size());
				wlService.addItem(wl, decoupes.get(i));
			}

		}

	}

	@Override
	public String getName() {
		return MkmConstants.MKM_NAME;
	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		if(!init)
			init();

		if (!getBoolean(STOCK_USE)) {
			var d = new MTGDeck();
			d.setName("export");
			for (MTGCardStock mcs : stock) {
				d.getMain().put(mcs.getProduct(), mcs.getQte());
			}
			exportDeck(d, f);
		}

		else {

			var serv = new StockService();
			var prods = new ProductServices();
			EnumMap<PRODUCT_ATTS, String> enumAtts = new EnumMap<>(PRODUCT_ATTS.class);
			enumAtts.put(PRODUCT_ATTS.idGame, "1");
			enumAtts.put(PRODUCT_ATTS.exact, "true");

			for (MTGCardStock mcs : stock)
			{

				Product p = null;
				try {
					p = MagicCardMarketPricer2.getProductFromCard(mcs.getProduct(),prods.findProduct(mcs.getProduct().getName(), enumAtts));
				}
				catch(Exception e)
				{
					logger.error("Error getting mkm product for {} {} : {}" ,mcs.getProduct(),mcs.getProduct().getEdition(),e.getMessage());
					logger.trace(e);
				}



				if(p==null)
				{
					logger.error("No product found for {} {}",mcs.getProduct(),mcs.getProduct().getEdition());
				}
				else
				{

					var a = new Article();
						a.setAltered(mcs.isAltered());
						a.setSigned(mcs.isSigned());
						a.setCount(mcs.getQte());
						a.setFoil(mcs.isFoil());
						a.setPrice(mcs.getValue().doubleValue());
						a.setComments(mcs.getComment());
						a.setCondition(aliases.getConditionFor(this, mcs.getCondition()));
						a.setLanguage(convertLang(mcs.getLanguage()));
						a.setProduct(p);
						a.setIdProduct(p.getIdProduct());

						if(mcs.getTiersAppIds().get(getName())!=null)
						{
							try {
								var id = mcs.getTiersAppIds().get(getName());
								a.setIdArticle(Integer.parseInt(id));
								logger.debug("Item {} is present for {} with id={} and idProduct={}",mcs,getName(),id,p.getIdProduct());
								serv.updateArticles(a);
							}
							catch(Exception e)
							{
								logger.error("Error updating {}", mcs,e);
							}
						}
						else
						{

								try
								{
									Inserted retour  = serv.addArticle(a);
									if(!retour.isSuccess())
									{
										logger.error(retour.getError());
									}
									else
									{
										mcs.getTiersAppIds().put(getName(), String.valueOf(retour.getIdArticle().getIdArticle()));
										mcs.setUpdated(true);
									}

								}
								catch(Exception e)
								{
									logger.error(e);
								}

						}

				}


				notify(mcs.getProduct());

			}

		}
	}

	@Override
	public List<MTGCardStock> importStockFromFile(File f) throws IOException {
		if(!init)
			init();

		if (!getBoolean(STOCK_USE))
			return importFromDeck(importDeckFromFile(f));

		var serv = new StockService();
		List<LightArticle> list = serv.getStock();
		List<MTGCardStock> stock = new ArrayList<>();

		if(list==null)
			return new ArrayList<>();


		for (LightArticle a : list) {
			var mcs = MTGControler.getInstance().getDefaultStock();
			mcs.setUpdated(true);
			mcs.setId(-1);
			mcs.setComment(a.getComments());
			try{
				mcs.setLanguage(a.getLanguage().getLanguageName());
			}
			catch(Exception _)
			{
				logger.error("Error getting langage for {}", a);
			}


			mcs.setQte(a.getCount());
			mcs.setFoil(a.isFoil());
			mcs.setSigned(a.isSigned());
			mcs.setAltered(a.isAltered());
			mcs.setPrice(a.getPrice());
			List<MTGCard> cards = getEnabledPlugin(MTGCardsProvider.class).searchCardByName( a.getProduct().getEnName(), null, true);

			MTGCard mc = cards.stream().filter(c->c.getEdition().getSet().equalsIgnoreCase(a.getProduct().getExpansion())).findAny().orElse(cards.get(0));



			mcs.setProduct(mc);
			mcs.setCondition(aliases.getReversedConditionFor(this, a.getCondition(), EnumCondition.NEAR_MINT));
			mcs.getTiersAppIds().put(getName(), String.valueOf(a.getIdArticle()));
			stock.add(mcs);
			notify(mcs.getProduct());

		}
		return stock;
	}

	private Localization convertLang(String s) {
		var l = new Localization();
		l.setIdLanguage(1);
		l.setLanguageName(s);
		return l;
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of(QUALITY, new MTGProperty("GD","Default quality of uploaded card","MT","NM","EX","GD","LP","PL","PO"),
								LANGUAGES, MTGProperty.newIntegerProperty("1", "1 - English, 2 - French, 3 - German,4 - Spanish, 5 - Italian,6 - Simplified Chinese,7 - Japanese, 8 - Portuguese, 9 - Russian,10 - Korean,11 - Traditional Chinese", 1, 11),
								MAX_WANTLIST_SIZE, MTGProperty.newIntegerProperty("150","Max size of want list",2,150),
								STOCK_USE, MTGProperty.newBooleanProperty("true","i don't remember the use XD"));

	}

	@Override
	public String getVersion() {
		return MkmConstants.MKM_API_GIT_VERSION;
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return MkmConstants.mkmTokens();
	}






}
