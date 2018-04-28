package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.collections4.ListUtils;
import org.api.mkm.exceptions.MkmException;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Localization;
import org.api.mkm.modele.MkmBoolean;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.modele.WantItem;
import org.api.mkm.modele.Wantslist;
import org.api.mkm.services.ProductServices;
import org.api.mkm.services.StockService;
import org.api.mkm.services.WantsService;
import org.api.mkm.tools.MkmAPIConfig;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.services.MTGControler;

public class MkmOnlineExport extends AbstractCardExport {

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	HttpURLConnection connection;
	MagicCardMarketPricer2 mkmPricer;
	EnumMap<PRODUCT_ATTS, String> atts;
	ProductServices pService;

	public MkmOnlineExport() throws MkmException {
		super();
		mkmPricer = new MagicCardMarketPricer2();

		try {
			MkmAPIConfig.getInstance().init(mkmPricer.getString("APP_ACCESS_TOKEN_SECRET"),
					mkmPricer.getString("APP_ACCESS_TOKEN"), mkmPricer.getString("APP_SECRET"),
					mkmPricer.getString("APP_TOKEN"));

			pService = new ProductServices();
			atts = new EnumMap<>(PRODUCT_ATTS.class);
			atts.put(PRODUCT_ATTS.exact, "true");
			atts.put(PRODUCT_ATTS.idGame, "1");
			atts.put(PRODUCT_ATTS.idLanguage, "1");
		} catch (MkmException e) {
			logger.error(e);
		}
	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		WantsService service = new WantsService();
		MagicDeck d = new MagicDeck();
		d.setName(f.getName());
		Wantslist list = null;
		for (Wantslist l : service.getWantList())
			if (l.getName().equalsIgnoreCase(d.getName()))
				list = l;

		if (list == null)
			throw new NullPointerException(getName() + " can't import deck for " + f.getName());

		service.loadItems(list);
		for (WantItem w : list.getItem()) {
			try {
				Product p = w.getProduct();

				if (p.getEnName().contains("(Version "))
					p.setEnName(p.getEnName().substring(0, p.getEnName().indexOf("(Version")));

				d.getMap().put(MTGControler.getInstance().getEnabledCardsProviders()
						.searchCardByCriteria("name", p.getEnName().trim(), null, true).get(0), w.getCount());
			} catch (Exception e) {
				logger.error("could not import " + w);
			}

		}

		return d;
	}

	@Override
	public void export(List<MagicCard> cards, File f) throws IOException {
		MagicDeck d = new MagicDeck();
		for (MagicCard mc : cards)
			d.getMap().put(mc, Integer.parseInt(getString("DEFAULT_QTE")));

		d.setName(f.getName());

		export(d, f);
	}

	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		WantsService wlService = new WantsService();
		List<WantItem> wants = new ArrayList<>();

		int c = 0;
		for (MagicCard mc : deck.getMap().keySet()) {

			Product p;

			p = MagicCardMarketPricer2.getProductFromCard(mc, pService.findProduct(mc.getName(), atts));

			if (p != null) {
				WantItem w = new WantItem();
				w.setProduct(p);
				w.setCount(deck.getMap().get(mc));
				w.setFoil(new MkmBoolean(false));
				w.setMinCondition(getString("QUALITY"));
				w.setAltered(new MkmBoolean(false));
				w.setType("product");
				w.setSigned(new MkmBoolean(false));
				for (String s : getString("LANGUAGES").split(","))
					w.getIdLanguage().add(Integer.parseInt(s));
				wants.add(w);
			} else {
				logger.debug("could not export " + mc);
			}

			setChanged();
			notifyObservers(c++);

		}

		int max = Integer.parseInt(getString("MAX_WANTLIST_SIZE"));
		if (wants.size() <= max) {
			Wantslist l = wlService.createWantList(deck.getName());
			logger.debug("Create " + l + " list with " + wants.size() + " items");
			wlService.addItem(l, wants);
		} else {

			List<List<WantItem>> decoupes = ListUtils.partition(wants, max);

			for (int i = 0; i < decoupes.size(); i++) {
				Wantslist wl = wlService.createWantList(deck.getName() + "-" + (i + 1));
				logger.debug("Create " + wl + " list with " + decoupes.get(i).size() + " items");
				wlService.addItem(wl, decoupes.get(i));
			}

		}

	}

	@Override
	public String getName() {
		return "MagicCardMarket";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MKMFileWantListExport.class.getResource("/icons/plugins/mkm.png"));
	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {

		if (!getString("STOCK_USE").equals("true")) {
			MagicDeck d = new MagicDeck();
			d.setName(f.getName());
			for (MagicCardStock mcs : stock) {
				d.getMap().put(mcs.getMagicCard(), mcs.getQte());
			}
			export(d, f);
		} else {

			StockService serv = new StockService();
			ProductServices prods = new ProductServices();
			EnumMap<PRODUCT_ATTS, String> enumAtts = new EnumMap<>(PRODUCT_ATTS.class);
			enumAtts.put(PRODUCT_ATTS.idGame, "1");
			enumAtts.put(PRODUCT_ATTS.exact, "true");

			List<Article> list = new ArrayList<>();
			for (MagicCardStock mcs : stock) {
				Product p = MagicCardMarketPricer2.getProductFromCard(mcs.getMagicCard(),
						prods.findProduct(mcs.getMagicCard().getName(), enumAtts));
				Article a = new Article();
				a.setAltered(mcs.isAltered());
				a.setSigned(mcs.isSigned());
				a.setCount(mcs.getQte());
				a.setFoil(mcs.isFoil());
				a.setPrice(mcs.getPrice());

				a.setCondition(convert(mcs.getCondition()));
				a.setLanguage(convertLang(mcs.getLanguage()));
				a.setProduct(p);
				a.setIdProduct(p.getIdProduct());
				list.add(a);
			}
			serv.addArticles(list);
		}
	}

	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {

		if (!getString("STOCK_USE").equals("true"))
			return importFromDeck(importDeck(f));

		StockService serv = new StockService();
		List<Article> list = serv.getStock();
		List<MagicCardStock> stock = new ArrayList<>();
		for (Article a : list) {
			MagicCardStock mcs = new MagicCardStock();
			mcs.setUpdate(true);
			mcs.setIdstock(-1);
			mcs.setComment(a.getComments());
			mcs.setLanguage(a.getLanguage().getLanguageName());
			mcs.setQte(a.getCount());
			mcs.setFoil(a.isFoil());
			mcs.setSigned(a.isSigned());
			mcs.setAltered(a.isAltered());
			mcs.setPrice(a.getPrice());
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders()
					.searchCardByCriteria("name", a.getProduct().getEnName(), null, true).get(0);
			MagicCardMarketPricer2.selectEditionCard(mc, a.getProduct().getExpansionName());

			mcs.setMagicCard(mc);
			mcs.setCondition(convert(a.getCondition()));
			stock.add(mcs);

		}
		return stock;
	}

	private Localization convertLang(String s) {
		Localization l = new Localization();
		l.setIdLanguage(1);
		l.setLanguageName(s);
		return l;
	}

	private String convert(EnumCondition condition) {
		switch (condition) {
		case MINT:
			return "MT";
		case NEAR_MINT:
			return "NM";
		case LIGHTLY_PLAYED:
			return "LP";
		case PLAYED:
			return "PL";
		case POOR:
			return "PO";
		}
		return null;
	}

	private EnumCondition convert(String condition) {
		switch (condition) {
		case "MT":
			return EnumCondition.MINT;
		case "NM":
			return EnumCondition.NEAR_MINT;
		case "EX":
			return EnumCondition.NEAR_MINT;
		case "GD":
			return EnumCondition.NEAR_MINT;
		case "LP":
			return EnumCondition.LIGHTLY_PLAYED;
		case "PL":
			return EnumCondition.PLAYED;
		case "PO":
			return EnumCondition.POOR;
		default:
			return null;
		}
	}

	@Override
	public void initDefault() {
		setProperty("QUALITY", "GD");
		setProperty("DEFAULT_QTE", "1");
		setProperty("LANGUAGES", "1,2");
		setProperty("MAX_WANTLIST_SIZE", "150");
		setProperty("STOCK_USE", "true");

	}

	@Override
	public String getVersion() {
		return "3.1";
	}
}
