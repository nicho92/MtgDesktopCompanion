package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.abstracts.AbstractProduct;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;
import org.magic.tools.WooCommerceTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;

public class WooCommerceExternalShop extends AbstractExternalShop {

	
	private static final String PER_PAGE = "PER_PAGE";
	private static final String STATUS = "status";
	private static final String DATE_PAID = "date_paid";
	private WooCommerce client;
	
	private void init()
	{
		if(client==null)
			client = WooCommerceTools.newClient(getAuthenticator());
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return WooCommerceTools.generateKeysForWooCommerce();
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Category> listCategories() throws IOException {
		init();
		
		var params = new HashMap<String, String>();
		
		params.put("per_page", getString(PER_PAGE));
		
		
		List<JsonElement> res = client.getAll(EndpointBaseType.PRODUCTS_CATEGORIES.getValue(),params);
		 
		var ret = new ArrayList<Category>();
		 
		 res.forEach(je->{
			 
			 var objCateg = je.getAsJsonObject();
			 var c = new Category();
			 	 c.setIdCategory(objCateg.get("id").getAsInt());
			 	 c.setCategoryName(objCateg.get("name").getAsString());
			 
			 	ret.add(c);
		 });
		 
		 return ret;
		 
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<Transaction> loadTransaction() throws IOException{
		init();
		
		Map<String, String> parameters = new HashMap<>();
	    					parameters.put(STATUS, "any");
	    					parameters.put("per_page", getString(PER_PAGE));
	    List<JsonElement> res = client.getAll(EndpointBaseType.ORDERS.getValue(),parameters);
		
	    var ret = new ArrayList<Transaction>();
	   
	    for(JsonElement el : res)
	    {
	    	var obj = el.getAsJsonObject();
	    	var t = new Transaction();
	    				t.setCurrency(obj.get("currency").getAsString());
	    				t.setDateCreation(UITools.parseGMTDate(obj.get("date_created").getAsString()));
	    				t.setId(obj.get("id").getAsInt());
	    				t.setShippingPrice(obj.get("shipping_total").getAsDouble());
	    				t.setSourceShopName(getName());
	    				if(!obj.get(DATE_PAID).isJsonNull())
	    					t.setDatePayment(UITools.parseGMTDate(obj.get(DATE_PAID).getAsString()));
	    				
	    			
	    	t.setStatut(tostatus(obj.get(STATUS).toString()));
	   	
	    	var contactObj = obj.get("billing").getAsJsonObject();
	    	t.setContact(toContact(contactObj,obj.get("customer_id").getAsInt()));	
	    	
	    	
	    	t.setItems(toWooItems(obj.get("line_items").getAsJsonArray()));
	    	ret.add(t);
	    }
		return ret;
	}


	private List<MTGStockItem> toWooItems(JsonArray itemsArr) {
		
		var ret = new ArrayList<MTGStockItem>();
		
		for(JsonElement item : itemsArr)
    	{
    		
    		var entry = AbstractStockItem.generateDefault();
			
    		var objItem = item.getAsJsonObject();
    		entry.setId(objItem.get("product_id").getAsInt());
    		entry.setQte(objItem.get("quantity").getAsInt());
    		entry.setPrice(objItem.get("total").getAsDouble());
    		
    		var prod = AbstractProduct.createDefaultProduct();

			prod.setName(objItem.get("name").getAsString());
    		prod.setProductId(objItem.get("product_id").getAsInt());
    		prod.setUrl("");
    		entry.setProduct(prod);
    		entry.setLanguage(entry.getProduct().getName().toLowerCase().contains("français")?"French":"English");
    		entry.getTiersAppIds().put(getName(), String.valueOf(entry.getId()));
    		ret.add(entry);
    		
    	}
		
		return ret;
	}


	@Override
	@SuppressWarnings("unchecked")
	protected void createTransaction(Transaction t) throws IOException {
			init();
			
			Map<String,Object> content = new HashMap<>();
							   content.put("post", createOrder(t));
			
			Map<Object,Object> ret=  client.create(EndpointBaseType.ORDERS.getValue(),content);
			
			if(!ret.isEmpty() && ret.get("id") !=null)
			{
				t.setId(Integer.parseInt(ret.get("id").toString()));
				logger.info(t + " created in " + getName() + " with id = " +t.getId());
			}
			else
			{
				logger.error(ret);
			}
	}


	@Override
	@SuppressWarnings("unchecked")
	public int createProduct(MTGProduct p,Category c) throws IOException {
		init();
		
		Map<Object,Object> ret = client.create(EndpointBaseType.PRODUCTS.getValue(), toWooCommerceAttributs(p,null,c.getIdCategory()));
		
		if(!ret.isEmpty() && ret.get("id") !=null)
		{
			logger.info(p + " created in " + getName() + " with id = " + ret.get("id"));
			return Integer.parseInt(ret.get("id").toString());
		}
		else
		{
			logger.error(ret);
		}
		
		return -1;
	}

	@Override
	public String getName() {
		return WooCommerceTools.WOO_COMMERCE_NAME;
	}
	
	@Override
	public List<MTGStockItem> loadStock(String search) throws IOException {
		init();
		var ret = new ArrayList<MTGStockItem>();
		Map<String, String> parameters = new HashMap<>();
										 parameters.put("per_page", getString(PER_PAGE));
										 parameters.put("search", search.replace(" ", "%20"));
										 
		List<JsonObject> res = client.getAll(EndpointBaseType.PRODUCTS.getValue(),parameters);

		res.forEach(element->{
			var p = AbstractProduct.createDefaultProduct();
			JsonObject obj = element.getAsJsonObject();
	
			p.setProductId(obj.get("id").getAsInt());
			p.setName(obj.get("name").getAsString());
			
			JsonObject objCateg = obj.get("categories").getAsJsonArray().get(0).getAsJsonObject();
			Category c = new Category();
					 c.setIdCategory(objCateg.get("id").getAsInt());
					 c.setCategoryName(objCateg.get("name").getAsString());
			p.setCategory(c);
		
			
			JsonObject img = obj.get("images").getAsJsonArray().get(0).getAsJsonObject();
							p.setUrl(img.get("src").getAsString());
			
			
					var stockItem = AbstractStockItem.generateDefault();
					stockItem.setProduct(p);
					stockItem.setId(p.getProductId());
					try {
						stockItem.setPrice(obj.get("price").getAsDouble());
					}
					catch(Exception e)
					{
						stockItem.setPrice(0.0);	
					}
					
					try {
						stockItem.setQte(obj.get("stock_quantity").getAsInt());	
					}catch(Exception e)
					{
						stockItem.setQte(0);	
					}
				notify(stockItem);
				ret.add(stockItem);	
		});
		
		return ret;
		
	
	}

	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {
		init();
		
		Map<String, String> productInfo = new HashMap<>();

		productInfo.put("search", name.replace(" ", "%20"));
		
		@SuppressWarnings("unchecked")
		List<JsonObject> res = client.getAll(EndpointBaseType.PRODUCTS.getValue(),productInfo);
		
		List<MTGProduct> ret =  new ArrayList<>();
	
		res.forEach(element->{
			var p = parseProduct(element);
			notify(p);
			ret.add(p);
		});
		return ret;
	}
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(PER_PAGE,"50");
	}
	
	@Override
	public Integer saveOrUpdateContact(Contact c) throws IOException {
		var attributs = new HashMap<String, Object>();
		var objAddr = new JsonObject();
			  objAddr.addProperty("first_name", c.getName());
			  objAddr.addProperty("last_name", c.getLastName());
			  objAddr.addProperty("address_1", c.getAddress());
			  objAddr.addProperty("city", c.getCity());
			  objAddr.addProperty("country", c.getCountry());
			  objAddr.addProperty("postcode", c.getZipCode());
			  objAddr.addProperty("email", c.getZipCode());
			  objAddr.addProperty("phone", c.getTelephone());
			  
			  attributs.put("billing", objAddr);
			  attributs.put("shipping", objAddr);
			  attributs.put("first_name", c.getName());
			  attributs.put("last_name", c.getLastName());
			  attributs.put("email", c.getEmail());
			  
			  if(c.getId()>0)
				  {
				  	client.update(EndpointBaseType.CUSTOMERS.getValue(),c.getId(), attributs);
				  }
			  else
			  {
				  var ret=  client.create(EndpointBaseType.CUSTOMERS.getValue(), attributs);
				  c.setId(Integer.parseInt(ret.get("id").toString()));
			  }
			 	return c.getId();
	}

	@Override
	public Contact getContactByEmail(String email) throws IOException {
			throw new IOException("Not Implemented");
	}

	@Override
	public int saveOrUpdateTransaction(Transaction t) throws IOException {
		if(t.getId()>0)
		{
			Map<String,Object> content = new HashMap<>();
			   content.put("post", createOrder(t));

			 client.update(EndpointBaseType.ORDERS.getValue(),t.getId(),content);
		}
		else
		{
			createTransaction(t);
		}
		
		return t.getId();
	}

	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Integer id) throws IOException {
		return null;
	}

	@Override
	public void saveOrUpdateStock(EnumItems typeStock, MTGStockItem stock) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Contact> listContacts() throws IOException {
		init();
		
		var params = new HashMap<String,String>();
		params.put("per_page", getString(PER_PAGE));
		List<JsonObject> res = client.getAll(EndpointBaseType.CUSTOMERS.getValue(),params);
		var ret = new ArrayList<Contact>();
		 
		res.forEach(obj->{
			var contact = toContact(obj, obj.get("id").getAsInt());
			ret.add(contact);
		});
		 return ret;
	}

	@Override
	public void deleteContact(Contact contact) throws IOException {
		init();
		client.delete(EndpointBaseType.CUSTOMERS.getValue(), contact.getId());
	}

	@Override
	public void deleteTransaction(Transaction t) throws IOException {
		init();
		client.delete(EndpointBaseType.ORDERS.getValue(), t.getId());
		
	}

	@Override
	public Transaction getTransactionById(int parseInt) throws IOException {
		init();
		var ret = client.get(EndpointBaseType.ORDERS.getValue(), parseInt);
		var t = new Transaction();
			t.setId(parseInt);
			t.setContact(toContact(new JsonExport().toJsonElement(ret.get("billing")).getAsJsonObject(), Integer.parseInt(ret.get("customer_id").toString())));
			t.setStatut(tostatus(ret.get("status").toString()));
			t.setItems(toWooItems(new JsonExport().toJsonArray(ret.get("line_items"))));
			t.setCurrency(ret.get("currency").toString());
		return t;
	}
	
	
	private JSONObject createOrder(Transaction t)
	{
		var obj = new JSONObject();
		var items = new JSONArray();
		
		var contact = new JSONObject();
				   contact.put("first_name", t.getContact().getName());
				   contact.put("last_name", t.getContact().getLastName());
				   contact.put("country", t.getContact().getCountry());
				   contact.put("email", t.getContact().getEmail());
				   contact.put("phone", t.getContact().getTelephone());
				   contact.put("address_1", t.getContact().getAddress());
				   contact.put("city", t.getContact().getCity());
				   contact.put("postcode", t.getContact().getZipCode());
				   
		obj.put("billing", contact);
		obj.put("shipping", contact);
		obj.put("line_items", items);
		obj.put("set_paid", t.getStatut().equals(TransactionStatus.PAID));
		obj.put("created_via", MTGConstants.MTG_APP_NAME);
		
		if(t.getPaymentProvider()!=null)
		{
			obj.put("payment_method_title", t.getPaymentProvider().name());
			obj.put(DATE_PAID, t.getDatePayment().getTime());
		}
		
		
		for(MTGStockItem st : t.getItems())
		{
			var line = new JSONObject();
				line.put("product_id", st.getTiersAppIds(WooCommerceTools.WOO_COMMERCE_NAME));
				line.put("quantity", st.getQte());
			items.put(line);
		}
		return obj;
	}
	
	
	private MTGProduct parseProduct(JsonObject element) {
			
			MTGProduct p = AbstractProduct.createDefaultProduct();
			JsonObject obj = element.getAsJsonObject();
			p.setProductId(obj.get("id").getAsInt());
			p.setName(obj.get("name").getAsString());
			
			
			JsonObject objCateg = obj.get("categories").getAsJsonArray().get(0).getAsJsonObject();
			Category c = new Category();
					 c.setIdCategory(objCateg.get("id").getAsInt());
					 c.setCategoryName(objCateg.get("name").getAsString());
			p.setCategory(c);
			
			JsonObject img = obj.get("images").getAsJsonArray().get(0).getAsJsonObject();
			p.setUrl(img.get("src").getAsString());
			return p;
		
	}

	
	private Map<String, Object> toWooCommerceAttributs(MTGProduct product,String status, int idCategory)
	{
		Map<String, Object> productInfo = new HashMap<>();

		productInfo.put("name", product.getName());
		productInfo.put("type", "simple");
        productInfo.put("categories", WooCommerceTools.entryToJsonArray("id",String.valueOf(idCategory)));
        productInfo.put(STATUS, status==null?"private":status);
        productInfo.put("images", WooCommerceTools.entryToJsonArray("src",product.getUrl().startsWith("//")?"https:"+product.getUrl():product.getUrl()));
		 
		return productInfo;
	}
	
	private TransactionStatus tostatus(String status) {
		switch(status)
		{
			case "pending" : return TransactionStatus.NEW;
			case "processing" : return TransactionStatus.IN_PROGRESS;
			case "on-hold" : return TransactionStatus.PAYMENT_WAITING;
			case "completed": return TransactionStatus.CLOSED;
			case "cancelled": return TransactionStatus.CANCELED;
			case "failed": return TransactionStatus.CANCELED;
			case "pre-ordered":return TransactionStatus.PRE_ORDERED;
			case "lpc_transit": return TransactionStatus.SENT;
			case "lpc_delivered": return TransactionStatus.DELIVRED;
			case "lpc_ready_to_ship" : return TransactionStatus.PAID;
			case "refunded" : return TransactionStatus.CANCELED;
			default : {
				
				logger.debug(status + " is unknow");
				return TransactionStatus.IN_PROGRESS;
			}
		}
	}

	private Contact toContact(JsonObject contactObj, int id) {
		
		var c = new Contact();
			c.setId(id);
			
			try {
				if(contactObj.get("first_name")!=null)
					c.setName(contactObj.get("first_name").getAsString());
				
				if(contactObj.get("last_name")!=null)
					c.setLastName(contactObj.get("last_name").getAsString());
				
				if(contactObj.get("address_1")!=null)
					c.setAddress(contactObj.get("address_1").getAsString());
				
				if(contactObj.get("postcode")!=null)
					c.setZipCode(contactObj.get("postcode").getAsString());
				
				if(contactObj.get("city")!=null)
					c.setCity(contactObj.get("city").getAsString());
				
				if(contactObj.get("country")!=null)
					c.setCountry(contactObj.get("country").getAsString());
				
				if(contactObj.get("email")!=null)
					c.setEmail(contactObj.get("email").getAsString());
				
				if(contactObj.get("phone")!=null)
					c.setTelephone(contactObj.get("phone").getAsString());
				
				
			c.setEmailAccept(false);
			}
			catch(Exception e)
			{
				logger.error(e);
			}
		
		return c;

	}

	
}


