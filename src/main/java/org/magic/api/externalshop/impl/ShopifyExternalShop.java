package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.apache.groovy.util.Maps;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.services.ProductFactory;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ShopifyExternalShop extends AbstractExternalShop {

	private static final String JSON = ".json";
	private static final String OPTION = "option";
	private static final String INVENTORY_QUANTITY = "inventory_quantity";
	private static final String FIRST_NAME = "first_name";
	private static final String TITLE = "title";
	private static final String LAST_NAME = "last_name";
	private static final String PRICE = "price";
	private static final String EMAIL = "email";
	private static final String FOIL_OPTION_NUMBER = "FOIL_OPTION_NUMBER";
	private static final String SET_OPTION_NUMBER = "SET_OPTION_NUMBER";
	private static final String X_SHOPIFY_ACCESS_TOKEN = "X-Shopify-Access-Token";
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String SUBDOMAIN = "SUBDOMAIN";
	private static final String MYSHOPIFY_COM_API_VERSION = ".myshopify.com/admin/api/2022-01/";
	private static final String CUSTOMER = "customer";
	private static final String PRODUCT = "product";
	private static final String VARIANT = "variant";
	private static final String ORDER = "order";
	private MTGHttpClient client = URLTools.newClient();



	private String getBaseUrl()
	{
		return "https://"+getAuthenticator().get(SUBDOMAIN)+MYSHOPIFY_COM_API_VERSION;
	}



	private Map<String, String> headers() {
		return Map.of(X_SHOPIFY_ACCESS_TOKEN, getAuthenticator().get(ACCESS_TOKEN),URLTools.ACCEPT, URLTools.HEADER_JSON,URLTools.CONTENT_TYPE,URLTools.HEADER_JSON);
	}





	private RequestBuilder build(String url,METHOD m) {
		return RequestBuilder.build()
				  .url(url)
				  .method(m)
				  .setClient(client)
				  .addHeaders(headers())
				  .addContent("limit","250");
	}


	private JsonObject readId(String entityName, Long id) throws IOException
	{
		var resp=build(getBaseUrl()+entityName.toLowerCase()+"s/"+id+JSON,METHOD.GET).execute();
		return URLTools.toJson(resp.getEntity().getContent()).getAsJsonObject().get(entityName).getAsJsonObject();
	}

	private JsonArray read(String entityName, Map<String,String> attributs) throws IOException
	{
		JsonArray arr = new JsonArray();
		var build=build(getBaseUrl()+entityName.toLowerCase()+"s.json",METHOD.GET);

		if(attributs!=null && !attributs.isEmpty())
			attributs.entrySet().forEach(e->build.addContent(e.getKey(), e.getValue()));

		var resp = build.execute();

		arr.addAll(URLTools.toJson(resp.getEntity().getContent()).getAsJsonObject().get(entityName+"s").getAsJsonArray());
		var next = URLTools.parseLinksHeader(resp.getFirstHeader("Link")).get("next");


		while(next!=null)
		{
			resp = build(next,METHOD.GET).execute();
			next = URLTools.parseLinksHeader(resp.getFirstHeader("Link")).get("next");
			arr.addAll(URLTools.toJson(resp.getEntity().getContent()).getAsJsonObject().get(entityName+"s").getAsJsonArray());
		}
		return arr;
	}



	private List<MTGStockItem> parseVariants(JsonObject obj) {

		var ret = new ArrayList<MTGStockItem>();

		for(JsonElement el : obj.get("variants").getAsJsonArray())
		{
				var it = ProductFactory.generateStockItem(parseProduct(obj));
									 try {
										 it.getProduct().setEdition(new MTGEdition(el.getAsJsonObject().get(OPTION+getString(SET_OPTION_NUMBER)).getAsString(), el.getAsJsonObject().get(OPTION+getString(SET_OPTION_NUMBER)).getAsString()));
									 }
									 catch(Exception e)
									 {
										logger.error("Error getting option{} for {} : {}",getString(SET_OPTION_NUMBER),obj,e);
									 }
									  it.setId(el.getAsJsonObject().get("id").getAsLong());
									  it.setPrice(el.getAsJsonObject().get(PRICE).getAsDouble());
									  it.setQte(el.getAsJsonObject().get(INVENTORY_QUANTITY).getAsInt());
									  it.setFoil(el.getAsJsonObject().get(OPTION+getString(FOIL_OPTION_NUMBER)).getAsString().toLowerCase().contains("foil"));
									  ret.add(it);
		}
		return ret;
	}


	//TODO change product type
	private MTGProduct parseProduct(JsonObject sp) {
		MTGProduct p = ProductFactory.createDefaultProduct(EnumItems.SEALED);
				   p.setProductId(sp.get("id").getAsLong());
				   p.setName(sp.get(TITLE).getAsString());
				   try{
					   p.setUrl(sp.get("image").getAsJsonObject().get("src").getAsString());
				   }
				   catch(Exception ise)
				   {
					   logger.error("{} has no url",p);
				   }
				   p.setTypeProduct(EnumItems.CARD);
				   notify(p);
		return p;
	}


	private Transaction parseTransaction(JsonObject obj) {

	var t = new Transaction();
		logger.debug(obj);
		
		t.setCurrency(obj.get("currency").getAsString());
		t.setDateCreation(UITools.parseGMTDate(obj.get("created_at").getAsString()));
		t.setContact(parseContact(obj.get(CUSTOMER).getAsJsonObject()));
		t.setDatePayment(UITools.parseGMTDate(obj.get("processed_at").getAsString()));
		t.setSourceShopName(getName());
		t.setSourceShopId(obj.get("id").getAsString());
		t.setId(obj.get("id").getAsInt());
		
		if(obj.get("note")!=null && !obj.get("note").isJsonNull())
			t.setMessage(obj.get("note").getAsString());


		if(obj.get("financial_status")!=null)
			t.setStatut(EnumTransactionStatus.PAID);


		obj.get("line_items").getAsJsonArray().forEach(je->{

			var item = je.getAsJsonObject();
			var it = ProductFactory.generateStockItem(parseProduct(item));

			  	try {
			  	it.setId(item.get("variant_id").getAsLong());
			  	}
			  	catch(Exception e)
			  	{
			  		logger.warn("No variant_id found for {}",it.getProduct());
			  	}

			  	it.setPrice(item.get(PRICE).getAsDouble());
			  	it.setQte(item.get("quantity").getAsInt());

			t.getItems().add(it);

		});



		return t;
	}





	private Contact parseContact(JsonObject obj) {
		var c = new Contact();
			c.setEmail(obj.get(EMAIL).getAsString());
			c.setName(!obj.get(FIRST_NAME).isJsonNull()?obj.get(FIRST_NAME).getAsString():"");
			c.setLastName(!obj.get(LAST_NAME).isJsonNull()?obj.get(LAST_NAME).getAsString():"");
			c.setActive(obj.get("state").getAsString().equalsIgnoreCase("enabled"));
			c.setId(obj.get("id").getAsInt());
			if(obj.get("default_address")!=null) {
				var addrObj = obj.get("default_address").getAsJsonObject();
				c.setAddress(addrObj.get("address1").getAsString());
				c.setCity(addrObj.get("city").getAsString());
				c.setZipCode(addrObj.get("zip").getAsString());
				c.setCountry(addrObj.get("country").getAsString());
				c.setTelephone(addrObj.get("phone").getAsString());
			}

		return c;

	}

	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {
		return StreamSupport.stream(read(PRODUCT,null).spliterator(), true)
							.filter(je->je.getAsJsonObject().get(TITLE).getAsString().toLowerCase().contains(name.toLowerCase()))
							.map(a->parseProduct(a.getAsJsonObject()))
							.toList();
	}

	@Override
	protected List<MTGStockItem> loadStock(String name) throws IOException {
		var ret = new ArrayList<MTGStockItem>();
		//need to load all products because title search is strict only
		var arr = StreamSupport.stream(read(PRODUCT,null).spliterator(), true)
				.filter(je->je.getAsJsonObject().get(TITLE).getAsString().toLowerCase().contains(name.toLowerCase()))
				.toList();

		for(JsonElement a: arr )
		{
			ret.addAll(parseVariants(a.getAsJsonObject()));
		}
		return ret;
	}



	@Override
	public Transaction getTransactionById(Long id) throws IOException {
		var obj= readId(ORDER,id);
		return parseTransaction(obj);
	}


	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		var arr = read(ORDER,null);
		var ret = new ArrayList<Transaction>();
		for(JsonElement je : arr)
		{
			ret.add(parseTransaction(je.getAsJsonObject()));
		}
		return ret;
	}


	@Override
	public List<Contact> listContacts() throws IOException {
		var arr = read(CUSTOMER,null);
		var ret = new ArrayList<Contact>();
		for(JsonElement je : arr)
		{
			ret.add(parseContact(je.getAsJsonObject()));
		}
		return ret;

	}

	@Override
	public List<Category> listCategories() throws IOException {
		return  StreamSupport.stream(read(PRODUCT,Maps.of("fields","product_type")).spliterator(),true).map(je->{
			var s = je.getAsJsonObject().get("product_type").getAsString();
			return new Category(s.hashCode(), s);
			}).distinct().toList();

	}

	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Long id) throws IOException {

		var obj= readId(VARIANT,id);

		var it = ProductFactory.generateStockItem(parseProduct(readId(PRODUCT,obj.get("product_id").getAsLong())));
		  it.setId(obj.get("id").getAsLong());
		  it.setPrice(obj.get(PRICE).getAsDouble());
		  it.setQte(obj.get(INVENTORY_QUANTITY).getAsInt());
		  it.setFoil(obj.get(OPTION+getString(FOIL_OPTION_NUMBER)).getAsString().toLowerCase().contains("foil"));
		  return it;

	}

	@Override
	public Contact getContactByEmail(String email) throws IOException {
		var arr = read(CUSTOMER,Maps.of(EMAIL, email));
		try {
			return parseContact(arr.getAsJsonArray().get(0).getAsJsonObject());
		}
		catch(IndexOutOfBoundsException  e)
		{
			return null;
		}

	}

	@Override
	public Integer saveOrUpdateContact(Contact c) throws IOException {
		var obj = new JsonObject();
		var prodobj = new JsonObject();
			obj.add(CUSTOMER, prodobj);
			prodobj.addProperty(FIRST_NAME, c.getName());
			prodobj.addProperty(LAST_NAME,c.getLastName());
			prodobj.addProperty(EMAIL, c.getEmail());
			prodobj.addProperty("phone", c.getTelephone());
			prodobj.addProperty("verified_email", false);
			var addresses = new JsonArray();
				var imageObj = new JsonObject();
					imageObj.addProperty("address1", c.getAddress());
					imageObj.addProperty("city", c.getCity());
					imageObj.addProperty("country", c.getCountry());
					imageObj.addProperty("zip", c.getZipCode());
					addresses.add(imageObj);
			prodobj.add("addresses", addresses);

			HttpResponse res =null;

			if(c.getId()<0)
			{
				res = client.doPost(getBaseUrl()+"customers.json",
												new StringEntity(obj.toString()),
												headers()
											);

				try {
					var content = URLTools.toJson(res.getEntity().getContent());
					c.setId(content.getAsJsonObject().get(CUSTOMER).getAsJsonObject().get("id").getAsInt());
					return c.getId();
				}
				catch(Exception e)
				{
					logger.error(e);
					return null;
				}
			}
			else
			{
				res = client.doPut(getBaseUrl()+"customers/"+c.getId()+JSON,
						new StringEntity(obj.toString()),
						headers()
					);


				try {
					var content = URLTools.toJson(res.getEntity().getContent());
					logger.info("return {}",content);
					return c.getId();
				}
				catch(Exception e)
				{
					logger.error(e);
					return null;
				}
			}
	}

	@Override
	protected void saveOrUpdateStock(List<MTGStockItem> it) throws IOException {
			HttpResponse res =null;

			for(MTGStockItem c : it)
			{

				var obj = new JsonObject();
				var objVariant = new JsonObject();
				obj.add(VARIANT, objVariant);

				objVariant.addProperty(PRICE, c.getPrice());
				objVariant.addProperty(OPTION+getString(FOIL_OPTION_NUMBER), c.isFoil());

				try {
					objVariant.addProperty(OPTION+getString(SET_OPTION_NUMBER), c.getProduct().getEdition().getSet());
				}catch(Exception e)
				{
					logger.error("no set found for {}",c);
				}
				objVariant.addProperty(INVENTORY_QUANTITY,c.getQte());


				if(c.getId()<0)
				{
					res = client.doPost(getBaseUrl()+"products/"+c.getProduct().getProductId()+"/variants.json",
													new StringEntity(obj.toString()),
													headers()
												);

					try {
						var content = URLTools.toJson(res.getEntity().getContent());
						logger.info("ret={}",content);
						c.setId(content.getAsJsonObject().get(VARIANT).getAsJsonObject().get("id").getAsInt());
					}
					catch(Exception e)
					{
						logger.error(e);
					}
				}
				else
				{
					res = client.doPut(getBaseUrl()+"variants/"+c.getId()+JSON,
							new StringEntity(obj.toString()),
							headers()
						);


					try {
						var content = URLTools.toJson(res.getEntity().getContent());
						logger.info("ret={}",content);
					}
					catch(Exception e)
					{
						logger.error(e);
					}
				}
			}
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
	public Long saveOrUpdateTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public void deleteTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Transaction> listTransactions(Contact c) throws IOException {
		return new ArrayList<>();
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
	public List<String> listAuthenticationAttributes() {
		return List.of(SUBDOMAIN, ACCESS_TOKEN) ;
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
			m.put(FOIL_OPTION_NUMBER, MTGProperty.newStringProperty("1","Foil category number"));
			m.put(SET_OPTION_NUMBER, MTGProperty.newStringProperty("2","set category number"));
			m.put("DEFAULT_VENDOR", MTGProperty.newStringProperty("MTGCompanion","default vendor name"));
		return m;
	}

}
