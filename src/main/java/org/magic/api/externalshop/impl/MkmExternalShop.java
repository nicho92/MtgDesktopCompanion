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
import org.api.mkm.exceptions.MkmException;
import org.api.mkm.modele.Game;
import org.api.mkm.modele.LightArticle;
import org.api.mkm.modele.LightProduct;
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
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.abstracts.AbstractProduct;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.MTGConstants;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class MkmExternalShop extends AbstractExternalShop {
	
	private static final String ID_GAME = "ID_GAME";
	private boolean initied=false;

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
	}
	
	@Override
	public List<Category> listCategories() throws IOException {
		return new GameService().listCategories().stream().map(c->{
			return new Category(c.getIdCategory(), c.getCategoryName());
			
		}).toList();
	}
	
	
	@Override
	public List<MTGStockItem> loadStock(String search) throws IOException {
		
			var ret = new ArrayList<MTGStockItem>();
		
			Game g = new Game();
			g.setIdGame(getInt(ID_GAME));
		
			var serv = new StockService();
			
			File temp = new File(MTGConstants.DATA_DIR, "mkm_temp_card.csv"); 
			File temp2 = new File(MTGConstants.DATA_DIR, "mkm_temp_sealed.csv"); 
			
			
			serv.exportStock(temp,getInt(ID_GAME),false);
			serv.exportStock(temp2,getInt(ID_GAME),true);
			
			
			
			for(File f : new File[] {temp,temp2})
				try(CSVParser p = CSVFormat.Builder.create().setDelimiter(";").setHeader().build().parse(new FileReader(f))  )
				{
					p.iterator().forEachRemaining(art->{
		
						if(art.get("English Name").toLowerCase().contains(search.toLowerCase()) || art.get("Exp. Name").toLowerCase().contains(search.toLowerCase())) {
						
							var item = new MkmStockItem();
				
							var product = new LightProduct();
								  product.setIdGame(1);
								  product.setLocName(art.get("Local Name"));
								  product.setExpansion(art.get("Exp. Name"));
								  product.setEnName(art.get("English Name"));
								  try {
									  item.setFoil(!art.get("Foil?").isEmpty());
									  item.setSigned(!art.get("Signed?").isEmpty());
									  item.setAltered(!art.get("Altered?").isEmpty());
								  }
								catch(IllegalArgumentException e)
								{
									//do nothing
								}
								  
								  item.setId(Integer.parseInt(art.get("idArticle")));
								  item.setProduct(toProduct(product,Integer.parseInt(art.get("idProduct"))));
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
									  logger.error("No language for code =" + art.get("Language"));
								  }
								  ret.add(item);
								  
								  notify(item);
						}
					});
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
		return new ProductServices().findProduct(name, atts).stream().map(p->toProduct(p)).toList();
	}

	

	@Override
	protected void createTransaction(Transaction t) throws IOException {
		
		logger.info(getName() + " will only update his stock from this transation");
		var mkmStockService = new StockService();
	
		t.getItems().stream().map(it -> {
			if(it.getTiersAppIds(getName())==null)
			{
				logger.warn(it.getProduct() + " is not synchronized with " + getName());
				return null;
			}
			else
			{
				return parse(it);
			}
			
		}).filter(Objects::nonNull).toList().forEach(art->{

			try {
				mkmStockService.changeQte(art, 0);
			} catch (IOException e) {
				logger.error(e);
			}
				
		});
		
		
		
		
	}
	
	private LightArticle parse(MTGStockItem it) {
		var ret = new LightArticle();
		ret.setIdArticle(it.getId());
		ret.setIdProduct(Integer.parseInt(it.getProduct().getProductId()));
		
		return ret;
	}

	@Override
	public int createProduct(MTGProduct t,Category c) throws IOException {
		throw new IOException("Not able to create product in Mkm");
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
		Transaction t = new Transaction();
							t.setId(o.getIdOrder());
							t.setTransporterShippingCode(null);
							t.setDateCreation(o.getState().getDateBought());
							t.setDatePayment(o.getState().getDatePaid());
							t.setDateSend(o.getState().getDateSent());
							t.setCurrency(o.getCurrencyCode());
							t.setMessage(o.getNote());
							t.setSourceShopName(getName());
							
		Contact c = new Contact();
				c.setLastName(o.getBuyer().getAddress().getName().split(" ")[0]);
				c.setName(o.getBuyer().getAddress().getName().split(" ")[1]);
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
			t.setStatut(TransactionStatus.NEW);
		
		if(t.getDatePayment()!=null)
			t.setStatut(TransactionStatus.PAID);

		if(t.getDateSend()!=null)
			t.setStatut(TransactionStatus.SENT);
		
	
		o.getArticle().forEach(article->{
			var item = new MkmStockItem();
			item.setId(article.getIdArticle());
			item.setLanguage(article.getLanguage().getLanguageName());
			item.setPrice(article.getPrice());
			item.setProduct(toProduct(article.getProduct(),article.getIdProduct()));
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
		
		product.setEnName(p.getEnName());
		product.setExpansion(p.getExpansionName());
		product.setImage(p.getImage());
		product.setRarity(p.getRarity());
		
		MTGProduct prod=  toProduct(product, p.getIdProduct());
		prod.setCategory(new Category(0,p.getCategoryName()));
		
		return prod;
		
	}
	
	
	private MTGProduct toProduct(LightProduct product, int idProduct) {
		var p = AbstractProduct.createDefaultProduct();
		p.setName(product.getEnName());
		p.setProductId(String.valueOf(idProduct));
		
		try {
		p.setEdition(MTG.getEnabledPlugin(MTGCardsProvider.class).getSetByName(product.getExpansion()));
		}
		catch(Exception e)
		{
			p.setEdition(new MagicEdition("set",product.getExpansion()));	
		}
			
		if(product.getImage()!=null && product.getImage().startsWith("//"))
			p.setUrl("https:"+ product.getImage());
		else
			p.setUrl(product.getImage());
		
		if(product.getRarity()==null)
			p.setTypeProduct(EnumItems.SEALED);
		else
			p.setTypeProduct(EnumItems.CARD);
				
				
		return p;
		
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("STATE", STATE.paid.name(),
				"ACTOR", ACTOR.seller.name(),
				ID_GAME,"1");
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
	public int saveOrUpdateTransaction(Transaction t) throws IOException {
		return -1;
	}

	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Integer id) throws IOException {
			return null;
	}

	@Override
	public void saveOrUpdateStock(EnumItems typeStock, MTGStockItem stock) throws IOException {
	}

	@Override
	public List<Contact> listContacts() throws IOException {
		return listTransaction().stream().map(Transaction::getContact).distinct().toList();
	}

	@Override
	public void deleteContact(Contact contact) throws IOException {
		throw new IOException("contacts can't be deleted");

	}
}


class MkmStockItem extends AbstractStockItem<MTGProduct>
{
	private static final long serialVersionUID = 1L;
	
}

