package org.magic.api.externalshop.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.ArrayUtils;
import org.api.mkm.exceptions.MkmException;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Game;
import org.api.mkm.modele.LightArticle;
import org.api.mkm.modele.LightProduct;
import org.api.mkm.modele.Localization;
import org.api.mkm.modele.Order;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.services.GameService;
import org.api.mkm.services.OrderService;
import org.api.mkm.services.OrderService.ACTOR;
import org.api.mkm.services.OrderService.STATE;
import org.api.mkm.services.ProductServices;
import org.api.mkm.services.StockService;
import org.api.mkm.tools.MkmAPIConfig;
import org.api.mkm.tools.MkmConstants;
import org.api.mkm.tools.Tools;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.services.MTGConstants;
import org.magic.services.ProductFactory;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class MkmExternalShop extends AbstractExternalShop {

	private static final String ID_GAME = "ID_GAME";
	private boolean initied=false;
	private StockService mkmStockService;


	private void init()
	{
		if(!initied) {
			try {
				MkmAPIConfig.getInstance().init(getAuthenticator().getTokensAsProperties());
				initied=true;
			} catch (MkmException e) {
				logger.error(e);
			}
		}
		mkmStockService = new StockService();
	}


	@Override
	public List<Category> listCategories() throws IOException {
		return new GameService().listCategories().stream().map(c->
			new Category(c.getIdCategory(), c.getCategoryName())
		).toList();
	}


	private List<File> loadFiles() throws IOException
	{

	
		var temp = new File(MTGConstants.DATA_DIR, "mkm_temp_card.csv");
		var temp2 = new File(MTGConstants.DATA_DIR, "mkm_temp_sealed.csv");

		var g = new Game();
		g.setIdGame(getInt(ID_GAME));

		mkmStockService.exportStock(temp,getInt(ID_GAME),false);
		mkmStockService.exportStock(temp2,getInt(ID_GAME),true);

		return List.of(temp,temp2);

	}


	@Override
	public List<MTGStockItem> loadStock(String search) throws IOException {
			init();
			var ret = new ArrayList<MTGStockItem>();
			for(File f : loadFiles())
				try(CSVParser p = CSVFormat.Builder.create().setDelimiter(";").setHeader().get().parse(new FileReader(f))  )
				{
					p.iterator().forEachRemaining(art->{

						if(art.get("English Name").toLowerCase().contains(search.toLowerCase()) || art.get("Exp. Name").toLowerCase().contains(search.toLowerCase()) ||art.get("idArticle").equalsIgnoreCase(search.toLowerCase())) {

							var product = new LightProduct();
								  product.setIdGame(1);
								  product.setLocName(art.get("Local Name"));
								  product.setExpansion(art.get("Exp. Name"));
								  product.setEnName(art.get("English Name"));
								  product.setIdProduct(Integer.parseInt(art.get("idProduct")));
								  product.setRarity("");
								  product.setImage("");
								  
									var item = ProductFactory.generateStockItem(toProduct(product));
								  
								  try {
									  item.setFoil(!art.get("Foil?").isEmpty());
									  item.setSigned(!art.get("Signed?").isEmpty());
									  item.setAltered(!art.get("Altered?").isEmpty());
									  item.setCondition(aliases.getReversedConditionFor(this,art.get("Condition"),EnumCondition.NEAR_MINT));
								   }
									catch(IllegalArgumentException e)
									{
										item.setCondition(EnumCondition.SEALED);
										product.setRarity(null);
									}

								  item.setQte(Integer.parseInt(art.get("Amount")));
								  item.setPrice(UITools.parseDouble(art.get("Price")));
								  item.setId(Integer.parseInt(art.get("idArticle")));
								  item.setComment(art.get("Comments"));
								  try {
									  var loc = Tools.listLanguages().get(Integer.parseInt(art.get("Language"))-1);
									  item.setLanguage(loc.getLanguageName());
								  }
								  catch(Exception e)
								  {
									  logger.error("No language for code ={}",art.get("Language"));
								  }
								  ret.add(item);

								  notify(item);
						}
					});
				}
			catch(Exception e)
			{
				logger.error(e);
			}
		return ret;
	}


	@Override
	protected List<Transaction> loadTransaction()  {
		init();

		var ret = new ArrayList<Transaction>();
		try {
			var serv = new OrderService();

			for(String t: getArray("STATE"))
				ret.addAll(serv.listOrders(ACTOR.valueOf(getString("ACTOR")),STATE.valueOf(t),1).stream().map(this::toTransaction).toList());

			return ret;
		} catch (IOException e) {
			logger.error(e);
			return ret;
		}
	}

	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {
		init();
		Map<PRODUCT_ATTS, String> atts = new EnumMap<>(PRODUCT_ATTS.class);
		atts.put(PRODUCT_ATTS.idGame, getString(ID_GAME));
		return new ProductServices().findProduct(name, atts).stream().map(this::toProduct).toList();
	}

	@Override
	public Long saveOrUpdateTransaction(Transaction t) throws IOException {
		init();
		var stocks= new ArrayList<LightArticle>();
		for(File f : loadFiles())
		{
			stocks.addAll(mkmStockService.readStockFile(f, getInt(ID_GAME)));
		}

		logger.info("{} will only update his stock from this transation",getName());
		logger.debug("{} loaded {} items",getName(),stocks.size());


		t.getItems().stream().map(it -> {
			if(it.getTiersAppIds(getName())==null)
			{
				logger.warn("{} is not synchronized with {}",it.getProduct(),getName());
				return null;
			}
			else
			{
				return parse(it);
			}

		}).filter(Objects::nonNull).toList().forEach(art->{
			var articles = stocks.stream().filter(pl->pl.getIdProduct()==art.getIdProduct()).toList();
			if(articles.size()>1)
			{
				logger.warn("Found multiple Articles : {}",articles);
			}
			else if(articles.isEmpty())
			{
				logger.warn("Article {} not found in stock",art.getIdArticle());
			}
			else
			{
				try {
					mkmStockService.changeQte(art, -art.getCount());
				} catch (IOException e) {
					logger.error(e);
				}
			}
		});
		return t.getId();
	}




	private LightArticle parse(MTGStockItem it) {
		var ret = new LightArticle();

		ret.setIdArticle(it.getId().intValue());


		ret.setIdProduct(it.getProduct().getProductId().intValue());
		ret.setLanguage(Tools.listLanguages().stream().filter(l->l.getLanguageName().equalsIgnoreCase(it.getLanguage())).findFirst().orElse(new Localization(1, it.getLanguage())));
		ret.setCount(it.getQte());
		return ret;
	}

	@Override
	public String getVersion() {
		return MkmConstants.MKM_API_VERSION;
	}

	@Override
	public String getName() {
		return MkmConstants.MKM_NAME;
	}

	private Transaction toTransaction(Order o) {
		var t = new Transaction();
							t.setId(o.getIdOrder());
							t.setTransporterShippingCode(null);
							t.setDateCreation(o.getState().getDateBought());
							t.setDatePayment(o.getState().getDatePaid());
							t.setDateSend(o.getState().getDateSent());
							t.setCurrency(o.getCurrencyCode());
							t.setMessage(o.getNote());
							t.setSourceShopName(getName());
							t.setSourceShopId(String.valueOf(o.getIdOrder()));
		var c = new Contact();

				var name = o.getBuyer().getAddress().getName();

				if(name.indexOf(" ")>1)
				{
					c.setLastName(name.substring(0, name.indexOf(" ")).trim());
					c.setName(name.substring(name.indexOf(" ")).trim());
				}
				else
				{
					c.setLastName(name.trim());
					c.setName("");
				}

				c.setAddress(o.getBuyer().getAddress().getStreet());
				c.setZipCode(o.getBuyer().getAddress().getZip());
				c.setCity(o.getBuyer().getAddress().getCity());
				c.setId(o.getBuyer().getIdUser());
				c.setEmailAccept(false);
				c.setEmail(null);

		t.setContact(c);
		t.setShippingPrice(o.getShippingMethod().getPrice());
		t.setTransporterShippingCode(o.getTrackingNumber());


		if(t.getDateCreation()!=null)
			t.setStatut(EnumTransactionStatus.NEW);

		if(t.getDatePayment()!=null)
			t.setStatut(EnumTransactionStatus.PAID);

		if(t.getDateSend()!=null)
			t.setStatut(EnumTransactionStatus.SENT);


		o.getArticle().forEach(article->{
			
			
			
			
			var item = ProductFactory.generateStockItem(toProduct(article.getProduct()));
			item.setId(article.getIdArticle());
			item.setLanguage(article.getLanguage().getLanguageName());
			item.setPrice(article.getPrice());
			item.getProduct().setProductId(Long.valueOf(article.getIdProduct()));

			if(article.getCondition()!=null)
				item.setCondition(aliases.getReversedConditionFor(this, article.getCondition(), EnumCondition.NEAR_MINT));

			item.setQte(article.getCount());
			item.setFoil(article.isFoil());
			item.setAltered(article.isAltered());
			item.setSigned(article.isSigned());
			item.getTiersAppIds().put(getName(), String.valueOf(article.getIdProduct()));
			t.getItems().add(item);
		});
		return t;
	}


	private MTGProduct toProduct(Product p) {
		var product = new LightProduct();

		product.setIdProduct(p.getIdProduct());
		product.setEnName(p.getEnName());
		product.setExpansion(p.getExpansionName());
		product.setImage(p.getImage());
		product.setRarity(p.getRarity());

		MTGProduct prod=  toProduct(product);
		prod.setCategory(new Category(0,p.getCategoryName()));


		return prod;

	}


	private MTGProduct toProduct(LightProduct product) {
		var p = ProductFactory.createDefaultProduct(product.getRarity()==null?EnumItems.SEALED:EnumItems.CARD);
		p.setName(product.getEnName());
		p.setProductId(Long.valueOf(product.getIdProduct()));
		try {
		p.setEdition(MTG.getEnabledPlugin(MTGCardsProvider.class).getSetByName(product.getExpansion()));
		}
		catch(Exception e)
		{
			p.setEdition(new MTGEdition("set",product.getExpansion()));
		}

		if(product.getImage()!=null && product.getImage().startsWith("//"))
			p.setUrl("https:"+ product.getImage());
		else
			p.setUrl(product.getImage());

		return p;

	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
			m.put("STATE",new MTGProperty("paid", "filter states of orders to import. Separated by comma", ArrayUtils.toStringArray(STATE.values())));
			m.put("ACTOR", new MTGProperty("seller", "filter orders you bought or sell ", ArrayUtils.toStringArray(ACTOR.values())));
			m.put(ID_GAME, MTGProperty.newIntegerProperty("1","set ID Game. let 1 for MTG",1,21));
		
			return m;
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return MkmConstants.mkmTokens();
	}

	@Override
	public Integer saveOrUpdateContact(Contact c) throws IOException {
		throw new IOException("contacts can't be updated");
	}

	@Override
	public Contact getContactByEmail(String email) throws IOException {
		throw new IOException("contacts can't be found by email");
	}

	@Override
	public MTGStockItem getStockById(EnumItems typeStock,Long id) throws IOException {
			return loadStock(String.valueOf(id)).stream().findAny().orElse(null);
	}


	@Override
	public void saveOrUpdateStock(List<MTGStockItem> stocks) throws IOException {
		init();
		var transformed = stocks.stream().map(it->{
			var art = new Article();
			art.setIdArticle(it.getId().intValue());
			art.setIdProduct(it.getProduct().getProductId().intValue());
			art.setPrice(it.getValue().doubleValue());
			art.setCondition(aliases.getConditionFor(this,it.getCondition()));
			art.setFoil(it.isFoil());
			art.setSigned(it.isSigned());
			art.setAltered(it.isAltered());
			return art;
		}).toList();




		var retour = mkmStockService.updateArticles(transformed);
		stocks.forEach(mtg->mtg.setUpdated(retour.stream().map(LightArticle::getIdArticle).noneMatch(i-> i.intValue() == mtg.getId())));

		stocks.stream().filter(it->!it.getQte().equals(itemsBkcp.get(it).getKey())).forEach(it->{

			int changeQty = (it.getQte()-itemsBkcp.get(it).getKey());
			try {

				var ret = new LightArticle();
					  ret.setIdArticle(it.getId().intValue());
					  ret.setIdProduct(it.getProduct().getProductId().intValue());
					  ret.setCount(it.getQte());
					  logger.debug("{} new = {} old={} = {}",it,it.getQte(),itemsBkcp.get(it).getKey(),changeQty);
					  mkmStockService.changeQte(ret, changeQty);
			} catch (IOException e) {
				logger.error(e);
			}

		});


	}



	@Override
	public List<Contact> listContacts() throws IOException {
		return listTransaction().stream().map(Transaction::getContact).distinct().toList();
	}

	@Override
	public void deleteContact(Contact contact) throws IOException {
		throw new IOException("contacts can't be deleted");

	}


	@Override
	public void deleteTransaction(Transaction t) throws IOException {
		throw new IOException("Transaction can't be deleted");


	}


	@Override
	public Transaction getTransactionById(Long parseInt) throws IOException {
		var serv = new OrderService();
		return toTransaction(serv.getOrderById(parseInt.intValue()));
	}


	@Override
	public Contact getContactByLogin(String login, String passw) throws IOException {
		throw new IOException("get Contact by login is not implemented");
	}


	@Override
	public List<Transaction> listTransactions(Contact c) throws IOException {
		return listTransaction().stream().filter(t->t.getContact().getId()==c.getId()).toList();
	}


	@Override
	public boolean enableContact(String token) throws IOException {
		logger.warn("not authorized");
		return false;
	}
}


