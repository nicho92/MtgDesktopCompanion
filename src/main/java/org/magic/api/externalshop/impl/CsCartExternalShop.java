package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumPaymentProvider;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.TCache;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.services.ProductFactory;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.tools.CryptoUtils;

import com.google.gson.JsonObject;

public class CsCartExternalShop extends AbstractExternalShop {

	private static final String USERS = "users";
	private static final String PRODUCTS = "products";
	private static final String ORDER_ID = "order_id";
	private static final String USER_TYPE = "user_type";
	private static final String API_VENDORS = "api/vendors/";
	private static final String COMPANY_ID = "COMPANY_ID";

	private static final String API_USERS = "api/users";
	private static final String API_PRODUCTS = "api/products";
	private static final String API_ORDERS = "api/orders";
	
	private static final String CONTACT_TYPE="C";
	
	private static final String ID_CATEG = "ID_CATEG_";
	private static final String ID_PAY = "ID_PAYMENT_";
	
	private TCache<Category> cacheCateg = new TCache<>("categ");

	private MTGHttpClient client;
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	public CsCartExternalShop() {
		client = URLTools.newClient();
	}
	

	private EnumItems parseCategory(Category category) {
		for(var e : getProperties().entrySet())
		{
			if(e.getValue().toString().equalsIgnoreCase(""+category.getIdCategory()))
			{
				return EnumItems.valueOf(e.getKey().toString().replace(ID_CATEG, ""));
			}
		}
		
		return EnumItems.SEALED;
	}
	
	
	private RequestBuilder getBuilder(String endpoint,METHOD m) {
		
		var auth= "Basic " + CryptoUtils.toBase64((getAuthenticator().get("EMAIL")+":"+getAuthenticator().get("API_KEY")).getBytes());
		
		
		return RequestBuilder.build().setClient(client).method(m)
		 .url(getAuthenticator().get("WEBSITE")+"/"+endpoint)
		 .addHeader(URLTools.AUTHORIZATION, auth)
		 .addHeader(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		for(var k : EnumItems.values())
			m.put(ID_CATEG+k.name(), new MTGProperty("", "default id category for "+k));
		
		for(var k : EnumPaymentProvider.values())
			m.put(ID_PAY+k.name(), new MTGProperty("", "default id payment for "+k));
				
		return m;
	}
	

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("WEBSITE","EMAIL","API_KEY",COMPANY_ID);
	}
	
	
	@Override
	public List<Category> listCategories() throws IOException {
		var ret = getBuilder("api/categories",METHOD.GET).toJson();
		
		ret.getAsJsonObject().get("categories").getAsJsonArray().forEach(je->{
			var jo = je.getAsJsonObject();
			var c = new Category(jo.get("category_id").getAsInt(),jo.get("category").getAsString());
			cacheCateg.put(c.getIdCategory(), c);
		});
		return cacheCateg.values();
	}
	
	@Override
	public List<Contact> listContacts() throws IOException {
		var list = new ArrayList<Contact>();
		var ret = getBuilder(API_USERS,METHOD.GET).addContent(USER_TYPE, CONTACT_TYPE).toJson();
		
		ret.getAsJsonObject().get(USERS).getAsJsonArray().forEach(je->list.add(buildContact(je.getAsJsonObject())));
		return list;
	}


	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {
		var list = new ArrayList<MTGProduct>();
		var build = getBuilder(API_PRODUCTS,METHOD.GET);
		
		if(!StringUtils.isEmpty(name))
		{
			build.addContent("pname","Y");
			build.addContent("q",name);
		}
		
		var ret = build.toJson();
		
		ret.getAsJsonObject().get(PRODUCTS).getAsJsonArray().forEach(je->list.add(buildProduct(je.getAsJsonObject())));
		
		return list;
	}
	
	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		var list = new ArrayList<Transaction>();
		var ret = getBuilder(API_VENDORS+getAuthenticator().get(COMPANY_ID)+"/orders",METHOD.GET).toJson();
		ret.getAsJsonObject().get("orders").getAsJsonArray().forEach(je->{
			try {
				list.add(getTransactionById(je.getAsJsonObject().get(ORDER_ID).getAsLong()));
			} catch (IOException e) {
				logger.error("Error getting transaction {}",je.getAsJsonObject().get(ORDER_ID));
			}
		});
		return list;
	}

	@Override
	public List<Transaction> listTransactions(Contact c) throws IOException {
		var list = new ArrayList<Transaction>();
		var ret = getBuilder(API_ORDERS,METHOD.GET).addContent("user_id", String.valueOf(c.getId())).toJson();
		ret.getAsJsonObject().get("orders").getAsJsonArray().forEach(je->{
			try {
				list.add(getTransactionById(je.getAsJsonObject().get(ORDER_ID).getAsLong()));
			} catch (IOException e) {
				logger.error("Error getting transaction {}",je.getAsJsonObject().get(ORDER_ID));
			}
		});
		return list;
	}

	
	@Override
	protected List<MTGStockItem> loadStock(String search) throws IOException {
		var endpoint = API_VENDORS+getAuthenticator().get(COMPANY_ID)+"/products/";
		
		var build = getBuilder(endpoint, METHOD.GET);
		
		if(!StringUtils.isEmpty(search))
		{
				build.addContent("pname","Y");
				build.addContent("q",search);
		}
		
		var list = new ArrayList<MTGStockItem>();
		
		build.toJson().getAsJsonObject().get(PRODUCTS).getAsJsonArray().forEach(je->list.add(buildStockItem(je.getAsJsonObject())));
		return list;
	}

	private MTGProduct buildProduct(JsonObject jo) {

		jo = getBuilder(API_PRODUCTS+"/"+jo.get("product_id").getAsLong(), METHOD.GET).toJson().getAsJsonObject();
		
		
		var product = ProductFactory.createDefaultProduct(EnumItems.SEALED);
			  product.setProductId(jo.get("product_id").getAsLong());
			  product.setName(jo.get("product").getAsString());
			  try {
				  product.setUrl(jo.get("main_pair").getAsJsonObject().get("detailed").getAsJsonObject().get("image_path").getAsString());
			  }
			  catch(Exception e)
			  {
				  logger.error("error getting image url for {}",product.getName());
			  }
			  
			  try {
				product.setCategory(getCategoryById(jo.get("main_category").getAsInt()));
			} catch (IOException e) {
				  logger.error("error getting category for {}",product.getName());
			}
			 
			  product.setTypeProduct(parseCategory(product.getCategory()));
				
			  
		return product;
	}



	private MTGStockItem buildStockItem(JsonObject req) {
		var product = buildProduct(req.getAsJsonObject());
		var item = ProductFactory.generateStockItem(product);
			  try {
				  item.setLanguage(req.get("lang_code").getAsString());
			  }
			  catch(Exception e)
			  {
				 //do nothing
			  }

			 item.setPrice(req.get("price").getAsDouble());
			 item.setQte(req.get("amount").getAsInt());
			 
			 item.getTiersAppIds().put(getName(), String.valueOf(item.getProduct().getProductId()));
			 return item;
	}


	private Contact buildContact(JsonObject jo) {
		var c = new Contact();
		 c.setId(jo.get("user_id").getAsInt());
		 c.setActive(jo.get("status").getAsString().equalsIgnoreCase("A"));
		 c.setLastName(jo.get("lastname").getAsString());
		 c.setName(jo.get("firstname").getAsString());
		 c.setEmail(jo.get("email").getAsString());
		 c.setTelephone(jo.get("phone").getAsString());
		 return c;
	}
	
	
	private Transaction buildTransaction(JsonObject jo)
	{
		var t = new Transaction();
		t.setSourceShopId(jo.get(ORDER_ID).getAsString());
		t.setSourceShopName(getName());
		t.setDateCreation(new Date(jo.get("timestamp").getAsLong()*1000));
		t.setContact(buildContact(getBuilder(API_USERS+"/"+jo.get("issuer_id").getAsInt(),METHOD.GET).toJson().getAsJsonObject()));
		t.setId(jo.get(ORDER_ID).getAsInt());
		
		if(jo.get("notes").isJsonNull())
			t.setMessage(jo.get("notes").getAsString());
		
		
		if(!jo.get("payment_method").isJsonNull())
		{
			var objP = jo.get("payment_method").getAsJsonObject();
			
			if(objP.get("description").getAsString().toLowerCase().contains("visa"))
				t.setPaymentProvider(EnumPaymentProvider.VISA);
			
		}
		
		
		if(!jo.get("shipping").isJsonNull())
		{
			t.setShippingPrice(jo.get("shipping").getAsJsonArray().get(0).getAsJsonObject().get("rate_info").getAsJsonObject().get("base_rate").getAsDouble());
		
			try {
				
			var ship =  getBuilder("api/shipments", METHOD.GET).addContent(ORDER_ID,jo.get(ORDER_ID).getAsString()).toJson().getAsJsonObject().get("shipments").getAsJsonArray().get(0).getAsJsonObject();
				t.setDateSend(new Date(ship.get("shipment_timestamp").getAsLong()*1000));
				t.setTransporterShippingCode(ship.get("tracking_number").getAsString());
				t.setTransporter(ship.get("carrier_info").getAsJsonObject().get("name").getAsString());
				t.setStatut(EnumTransactionStatus.SENT);
			}
			catch(Exception e)
			{
				logger.error("Error getting shipping informations", e);
			}
		}
		
		switch(jo.get("status").getAsString())
		{
			case "P" : t.setStatut(EnumTransactionStatus.IN_PROGRESS);break;
			case "C" : t.setStatut(EnumTransactionStatus.CLOSED);break;
			case "O" : t.setStatut(EnumTransactionStatus.NEW);break;
			case "F" : t.setStatut(EnumTransactionStatus.REFUSED);break;
			case "D" : t.setStatut(EnumTransactionStatus.REFUSED);break;
			case "B" : t.setStatut(EnumTransactionStatus.CANCELATION_ASK);break;
			case "I" : t.setStatut(EnumTransactionStatus.CANCELED);break;
			case "Y" : t.setStatut(EnumTransactionStatus.IN_PROGRESS);break;
			default : break;
		}
		
		
		
		jo.get(PRODUCTS).getAsJsonObject().entrySet().forEach(e->t.getItems().add(buildStockItem(e.getValue().getAsJsonObject())));
		
		
		
		return t;
	}
	
	@Override
	public Transaction getTransactionById(Long id) throws IOException {
		var ret = getBuilder(API_ORDERS+"/"+id,METHOD.GET).toJson();
		return buildTransaction(ret.getAsJsonObject());
	}
	
	
	@Override
	public Contact getContactByEmail(String email) throws IOException {
		var ret = getBuilder(API_USERS,METHOD.GET).addContent(USER_TYPE, CONTACT_TYPE).addContent("email",email).toJson();
		return buildContact(ret.getAsJsonObject().get(USERS).getAsJsonArray().get(0).getAsJsonObject());
	}


	@Override
	public Contact getContactByLogin(String login, String passw) throws IOException {
		var ret = getBuilder(API_USERS,METHOD.GET).addContent(USER_TYPE, CONTACT_TYPE).addContent("user_login",login).toJson();
		return buildContact(ret.getAsJsonObject().get(USERS).getAsJsonArray().get(0).getAsJsonObject());
	}
	
	@Override
	public Category getCategoryById(Integer id) throws IOException {
			

			try {
				return cacheCateg.get(String.valueOf(id),new Callable<Category>() {
					
					@Override
					public Category call() throws Exception {
						var ret = getBuilder("api/categories/"+id,METHOD.GET).toJson();
						var jo = ret.getAsJsonObject();
						return new Category(jo.get("category_id").getAsInt(),jo.get("category").getAsString());

					}
				});
			} catch (ExecutionException e) {
				throw new IOException(e);
			}
		
			
			
		
	}
	
	
	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Long id) throws IOException {
			var endpoint = API_VENDORS+getAuthenticator().get(COMPANY_ID)+"/products/"+id;
			var req = getBuilder(endpoint, METHOD.GET).toJson().getAsJsonObject();
		return buildStockItem(req);
	}



	@Override
	protected void saveOrUpdateStock(List<MTGStockItem> it) throws IOException {
		// TODO Auto-generated method stub

	}


	@Override
	public Integer saveOrUpdateContact(Contact c) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	
	@Override
	public void deleteContact(Contact contact) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Long saveOrUpdateTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub

	}

	
	@Override
	public boolean enableContact(String token) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return "CS Cart";
	}

	

}
