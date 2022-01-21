package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.abstracts.extra.AbstractProduct;
import org.magic.api.interfaces.abstracts.extra.AbstractStockItem;
import org.magic.services.MTGControler;

import com.shopify.ShopifySdk;
import com.shopify.model.ShopifyProduct;

public class ShopifyExternalShop extends AbstractExternalShop {

	public static void main(String[] args) throws IOException {
		MTGControler.getInstance().loadAccountsConfiguration();
		new ShopifyExternalShop().listProducts("Absorb in Aether").forEach(System.out::println);;
		
	}

	private ShopifySdk build;
	

	private ShopifySdk builder()
	{
		
		if(build==null)
			build = ShopifySdk.newBuilder().withSubdomain(getAuthenticator().get("SUBDOMAIN")).withAccessToken(getAuthenticator().get("ACCESS_TOKEN")).build();
		
		return build;
	}
	
	
	
	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {
		
		Stream<ShopifyProduct> s=null;
		if(name!=null)
		{
			s = builder().getProducts().values().stream().filter(sp->sp.getTitle().toLowerCase().contains(name.toLowerCase()));
		}
		else
		{
			s = builder().getProducts().values().stream();
		}
		
		return s.map(sp->{
			var prod = parseProduct(sp);
			notify(prod);
			return prod;
			
		}).toList();
	}

	private MTGProduct parseProduct(ShopifyProduct sp) {
		
		MTGProduct p = AbstractProduct.createDefaultProduct();
				   p.setName(sp.getTitle());
				   Long l = Long.parseLong(sp.getId());
				   p.setProductId(l.intValue());
				   p.setCategory(new Category(sp.getProductType().hashCode(),sp.getProductType()));
				   p.setUrl(sp.getImage().getSource());
		return p;
	}

	

	@Override
	protected List<MTGStockItem> loadStock(String search) throws IOException {
		var ret = new ArrayList<MTGStockItem>();
		
		builder().getProducts().values().stream().filter(sp->sp.getTitle().toLowerCase().contains(search.toLowerCase())).forEach(sp->{
			sp.getVariants().forEach(sv->{
				AbstractStockItem<MTGProduct> st =AbstractStockItem.generateDefault();
				Long l = Long.parseLong(sv.getId());
				logger.info(l);
				st.setId(l.intValue());
				st.setQte(sv.getInventoryQuantity().intValue());
				st.setPrice(sv.getPrice().doubleValue());
				st.setProduct(parseProduct(sp));
				
				logger.info(sv.getOption1());
				logger.info(sv.getOption2());
				logger.info(sv.getOption3());
				
				
				ret.add(st);
			});
		});
		
		return ret;
	}

	@Override
	protected void saveOrUpdateStock(List<MTGStockItem> it) throws IOException {
		// TODO Auto-generated method stub

	}
	
	
	@Override
	public int createProduct(MTGProduct t, Category c) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("SUBDOMAIN", "ACCESS_TOKEN") ;
	}
	
	
	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Integer id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> listCategories() throws IOException {
		return builder().getProducts().values().stream().map(sp->{
			return sp.getProductType();
		}).distinct().map(s->new Category(s.hashCode(), s)).toList();
	}

	@Override
	public Integer saveOrUpdateContact(Contact c) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact getContactByEmail(String email) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Contact> listContacts() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteContact(Contact contact) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Contact getContactByLogin(String login, String passw) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int saveOrUpdateTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Transaction getTransactionById(int parseInt) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> listTransactions(Contact c) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean enableContact(String token) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return "Shopify";
	}

	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


}
