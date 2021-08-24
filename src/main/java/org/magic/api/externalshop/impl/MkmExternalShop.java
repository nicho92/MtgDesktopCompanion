package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.api.mkm.exceptions.MkmException;
import org.api.mkm.modele.Category;
import org.api.mkm.modele.LightProduct;
import org.api.mkm.modele.Order;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.services.GameService;
import org.api.mkm.services.OrderService;
import org.api.mkm.services.OrderService.ACTOR;
import org.api.mkm.services.OrderService.STATE;
import org.api.mkm.services.ProductServices;
import org.api.mkm.tools.MkmAPIConfig;
import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.Contact;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.AccountsManager;

public class MkmExternalShop extends AbstractExternalShop {
	
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
	protected List<Transaction> loadTransaction()  {
		init();
		try {
			return new OrderService().listOrders(ACTOR.valueOf(getString("ACTOR")),STATE.valueOf(getString("STATE")),null).stream().map(this::toTransaction).collect(Collectors.toList());
		} catch (IOException e) {
			logger.error(e);
			return new ArrayList<>();
		}
		
	}

	@Override
	public List<Product> listProducts(String name) throws IOException {
		init();
		Map<PRODUCT_ATTS, String> atts = new EnumMap<>(PRODUCT_ATTS.class);
		atts.put(PRODUCT_ATTS.idGame, "1");
		return new ProductServices().findProduct(name, atts);
	}

	@Override
	public void createTransaction(Transaction t) throws IOException {
		throw new IOException("Not enable to create orders in Mkm");

	}

	@Override
	public int createProduct(Product t,Category c) throws IOException {
		throw new IOException("Not enable to create product in Mkm");
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
		
		Contact c = new Contact();
				c.setLastName(o.getBuyer().getAddress().getName().split(" ")[0]);
				c.setName(o.getBuyer().getAddress().getName().split(" ")[1]);
				c.setAddress(o.getBuyer().getAddress().getStreet());
				c.setZipCode(o.getBuyer().getAddress().getZip());
				c.setCity(o.getBuyer().getAddress().getCity());
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
			item.getTiersAppIds().put(getName(), String.valueOf(article.getIdProduct()));
			t.getItems().add(item);
		});
		return t;
	}
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("STATE", STATE.paid.name(),"ACTOR", ACTOR.seller.name());
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return MkmConstants.mkmTokens();
	}


}



class MkmStockItem extends AbstractStockItem<LightProduct>
{
	private static final long serialVersionUID = 1L;
	@Override
	public void setProduct(LightProduct product) {
		this.product=product;
		setProductName(product.getEnName());
		edition= new MagicEdition("",product.getExpansion());
		url = "https:"+ product.getImage();
		setTypeStock(EnumItems.SEALED);
	}
}


