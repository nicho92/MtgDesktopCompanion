package org.magic.api.externalshop.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.api.mkm.exceptions.MkmException;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Category;
import org.api.mkm.modele.Game;
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
import org.magic.api.beans.Contact;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
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
		return new GameService().listCategories();
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
								  
								  item.setId(Integer.parseInt(art.get("idProduct")));
								  item.setProduct(product);
								  item.setQte(Integer.parseInt(art.get("Amount")));
								  item.setPrice(UITools.parseDouble(art.get("Price")));
								  item.setIdArticle(Integer.parseInt(art.get("idArticle")));
								  item.setComment(art.get("Comments"));
								  item.setLanguage(art.get("Language").equals("1")?"English":"French");
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
	public List<Product> listProducts(String name) throws IOException {
		init();
		Map<PRODUCT_ATTS, String> atts = new EnumMap<>(PRODUCT_ATTS.class);
		atts.put(PRODUCT_ATTS.idGame, getString(ID_GAME));
		return new ProductServices().findProduct(name, atts);
	}

	@Override
	protected void createTransaction(Transaction t) throws IOException {
		throw new IOException("Not enable to create orders in Mkm");
	}

	@Override
	public int createProduct(Product t,Category c) throws IOException {
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
			item.setId(article.getIdProduct());
			item.setLanguage(article.getLanguage().getLanguageName());
			item.setPrice(article.getPrice());
			item.setProduct(article.getProduct());
			item.setQte(article.getCount());
			item.setFoil(article.isFoil());
			item.setAltered(article.isAltered());
			item.setSigned(article.isSigned());
			item.getTiersAppIds().put(getName(), String.valueOf(article.getIdProduct()));
			item.setTypeStock(article.getProduct().getRarity()==null?EnumItems.SEALED:EnumItems.CARD);
			t.getItems().add(item);
		});
		return t;
	}
	
	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance().loadAccountsConfiguration();
		MTG.getPlugin(MkmConstants.MKM_NAME,MTGExternalShop.class).getStockById(null, 1250767098);
		
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



class MkmStockItem extends AbstractStockItem<LightProduct>
{
	private int idArticle;
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setProduct(LightProduct product) {
		this.product=product;
		setProductName(product.getEnName());
		edition= new MagicEdition("",product.getExpansion());
		if(product.getImage()!=null && product.getImage().startsWith("//"))
			url = "https:"+ product.getImage();
		else
			url=product.getImage();
	}
	
	public void setIdArticle(int idArticle) {
		this.idArticle = idArticle;
	}
	
	public int getIdArticle() {
		return idArticle;
	}
	
	
}


