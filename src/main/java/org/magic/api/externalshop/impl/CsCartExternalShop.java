package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.abstracts.AbstractProduct;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.services.MTGControler;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.tools.CryptoUtils;

import com.google.gson.JsonObject;

public class CsCartExternalShop extends AbstractExternalShop {

	private static final String API_USERS = "api/users";
	private static final String API_PRODUCTS = "api/products";
	private static final String API_ORDERS = "api/orders";
	private static final String CONTACT_TYPE="C";
	
	private MTGHttpClient client;
	
	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance().loadAccountsConfiguration();
		
		var cscart = new CsCartExternalShop();
		
		cscart.listProducts("").forEach(System.out::println);
		
		System.exit(0);
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	public CsCartExternalShop() {
		client = URLTools.newClient();
	}
	
	private RequestBuilder getBuilder(String endpoint,METHOD m) {
		
		var auth= "Basic " + CryptoUtils.toBase64((getAuthenticator().get("EMAIL")+":"+getAuthenticator().get("API_KEY")).getBytes());
		
		if(!StringUtils.isEmpty(getAuthenticator().get("COMPANY_ID")))
			endpoint = "/vendors/"+getAuthenticator().get("COMPANY_ID")+"/"+endpoint;
		
		
		return RequestBuilder.build().setClient(client).method(m)
		 .url(getAuthenticator().get("WEBSITE")+"/"+endpoint)
		 .addHeader(URLTools.AUTHORIZATION, auth)
		 .addHeader(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("WEBSITE","EMAIL","API_KEY","COMPANY_ID");
	}
	
	
	@Override
	public List<Category> listCategories() throws IOException {
		var list = new ArrayList<Category>();
		var ret = getBuilder("api/categories",METHOD.GET).toJson();
		
		ret.getAsJsonObject().get("categories").getAsJsonArray().forEach(je->{
			var jo = je.getAsJsonObject();
			list.add(new Category(jo.get("category_id").getAsInt(),jo.get("category").getAsString()));
		});
		return list;
	}
	
	@Override
	public List<Contact> listContacts() throws IOException {
		var list = new ArrayList<Contact>();
		var ret = getBuilder(API_USERS,METHOD.GET).addContent("user_type", CONTACT_TYPE).toJson();
		
		ret.getAsJsonObject().get("users").getAsJsonArray().forEach(je->list.add(buildContact(je.getAsJsonObject())));
		return list;
	}
	
	@Override
	public Contact getContactByEmail(String email) throws IOException {
		var ret = getBuilder(API_USERS,METHOD.GET).addContent("user_type", CONTACT_TYPE).addContent("email",email).toJson();
		return buildContact(ret.getAsJsonObject().get("users").getAsJsonArray().get(0).getAsJsonObject());
	}


	@Override
	public Contact getContactByLogin(String login, String passw) throws IOException {
		var ret = getBuilder(API_USERS,METHOD.GET).addContent("user_type", CONTACT_TYPE).addContent("user_login",login).toJson();
		return buildContact(ret.getAsJsonObject().get("users").getAsJsonArray().get(0).getAsJsonObject());
	}

	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {
		var list = new ArrayList<MTGProduct>();
		var build = getBuilder(API_PRODUCTS,METHOD.GET);
		
		if(!StringUtils.isEmpty(name))
			build.addContent("pname",name);
		
		var ret = build.toJson();
		
		ret.getAsJsonObject().get("products").getAsJsonArray().forEach(je->list.add(buildProduct(je.getAsJsonObject())));
		
		return list;
	}
	
	@Override
	public Transaction getTransactionById(Long id) throws IOException {
		var ret = getBuilder(API_ORDERS+"/"+id,METHOD.GET).toJson();
		return buildTransaction(ret.getAsJsonObject());
	}
	
	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		var list = new ArrayList<Transaction>();
		var ret = getBuilder(API_ORDERS,METHOD.GET).toJson();
		ret.getAsJsonObject().get("orders").getAsJsonArray().forEach(je->list.add(buildTransaction(je.getAsJsonObject())));
		return list;
	}


	@Override
	public List<Transaction> listTransactions(Contact c) throws IOException {
		var list = new ArrayList<Transaction>();
		var ret = getBuilder(API_ORDERS,METHOD.GET).addContent("user_id", String.valueOf(c.getId())).toJson();
		ret.getAsJsonObject().get("orders").getAsJsonArray().forEach(je->list.add(buildTransaction(je.getAsJsonObject())));
		return list;
	}


	private MTGProduct buildProduct(JsonObject jo) {
		var product = AbstractProduct.createDefaultProduct();
			  product.setProductId(jo.get("product_id").getAsLong());
			  product.setName(jo.get("product").getAsString());
			  try {
			  product.setUrl(jo.get("main_pair").getAsJsonObject().get("detailed").getAsJsonObject().get("image_path").getAsString());
			  }
			  catch(Exception e)
			  {
				  logger.error("error getting image url for {}",product.getName());
			  }
		return product;
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
		t.setId(jo.get("order_id").getAsLong());
		t.setDateCreation(new Date(jo.get("timestamp").getAsLong()));
		
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
			default : t.setStatut(EnumTransactionStatus.IN_PROGRESS);break;
		}
		
		
		return t;
	}
	
	
	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Long id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected List<MTGStockItem> loadStock(String search) throws IOException {
		// TODO Auto-generated method stub
		return null;
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
