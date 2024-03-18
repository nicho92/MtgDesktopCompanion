package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumPaymentProvider;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.services.MTGConstants;
import org.magic.services.ProductFactory;
import org.magic.services.tools.UITools;
import org.magic.services.tools.WooCommerceTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;

public class WooCommerceExternalShop extends AbstractExternalShop {


	private static final String ADDRESS_1 = "address_1";
	private static final String LINE_ITEMS = "line_items";
	private static final String PRODUCT_ID = "product_id";
	private static final String PRICE = "price";
	private static final String PHONE = "phone";
	private static final String EMAIL = "email";
	private static final String POSTCODE = "postcode";
	private static final String LAST_NAME = "last_name";
	private static final String IMAGES = "images";
	private static final String FIRST_NAME = "first_name";
	private static final String COUNTRY = "country";
	private static final String CATEGORIES = "categories";
	private static final String BILLING = "billing";
	private static final String STOCK_QUANTITY = "stock_quantity";
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


	@Override
	public List<Category> listCategories() throws IOException {
		init();

		var list = new ArrayList<Category>();
        List<JsonElement> ret = client.getAll(EndpointBaseType.PRODUCTS_CATEGORIES.getValue());
		 ret.forEach(je->{
			 var objCateg = je.getAsJsonObject();
			 var c = new Category();
			 	 c.setIdCategory(objCateg.get("id").getAsInt());
			 	 c.setCategoryName(objCateg.get("name").getAsString());

			 	list.add(c);
		 });
		
		 return list;

	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<Transaction> loadTransaction() throws IOException{
		init();

		Map<String, String> parameters = new HashMap<>();
	    					parameters.put(STATUS, "any");
	    List<JsonElement> res = client.getAll(EndpointBaseType.ORDERS.getValue(),parameters);

	    var ret = new ArrayList<Transaction>();

	    for(JsonElement el : res)
	    {
	    	var obj = el.getAsJsonObject();
	    	
	    	logger.debug("reading {}",obj);
	    	
	    	var t = new Transaction();
	    				t.setCurrency(obj.get("currency").getAsString());
	    				if(!obj.get("date_created").isJsonNull())
	    					t.setDateCreation(UITools.parseGMTDate(obj.get("date_created").getAsString()));
	    				
	    				if(!obj.get(DATE_PAID).isJsonNull())
	    					t.setDatePayment(UITools.parseGMTDate(obj.get(DATE_PAID).getAsString()));

	    				
	    				
	    				t.setShippingPrice(obj.get("shipping_total").getAsDouble());
	    				t.setSourceShopName(getName());
	    				t.setSourceShopId(obj.get("id").getAsString());
	    				t.setId(obj.get("id").getAsInt());

	    				if(obj.get("payment_method")!=null)
		    				switch(obj.get("payment_method").toString())
		    				{
		    					case "bacs":t.setPaymentProvider(EnumPaymentProvider.BANK_TRANSFERT);break;
		    					case "PayPal":t.setPaymentProvider(EnumPaymentProvider.PAYPAL);break;
		    					default :t.setPaymentProvider(EnumPaymentProvider.VISA);break;
		    				}

	    	t.setStatut(tostatus(obj.get(STATUS).toString()));

	    	var contactObj = obj.get(BILLING).getAsJsonObject();
	    	t.setContact(toContact(contactObj,obj.get("customer_id").getAsInt()));


	    	t.setItems(toWooItems(obj.get(LINE_ITEMS).getAsJsonArray()));
	    	ret.add(t);
	    }
		return ret;
	}

	private List<MTGStockItem> toWooItems(JsonArray itemsArr) {

		var ret = new ArrayList<MTGStockItem>();

		for(JsonElement item : itemsArr)
    	{

			var objItem = item.getAsJsonObject();
			
			var prod = ProductFactory.createDefaultProduct(EnumItems.SEALED);
    		prod.setCategory(null);
			prod.setName(objItem.get("name").getAsString());
    		prod.setProductId(objItem.get(PRODUCT_ID).getAsLong());
    		prod.setUrl("");
			
    		var entry = ProductFactory.generateStockItem(prod);

    		
    		entry.setId(objItem.get(PRODUCT_ID).getAsInt());
    		entry.setQte(objItem.get("quantity").getAsInt());
    		entry.setPrice(objItem.get("total").getAsDouble());
    		entry.setSku(objItem.get("sku").getAsString());
    		entry.setLanguage(entry.getProduct().getName().toLowerCase().contains("français")?"French":"English");
    		entry.getTiersAppIds().put(getName(), String.valueOf(entry.getId()));
    		ret.add(entry);
    	}
		return ret;
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
										 parameters.put("search", search.replace(" ", "%20").replace("'", "%27").replace(",","%2C"));

		List<JsonObject> res = client.getAll(EndpointBaseType.PRODUCTS.getValue(),parameters);

		res.forEach(element->{
		
			var obj = element.getAsJsonObject();
			
			

			var objCateg = obj.get(CATEGORIES).getAsJsonArray().get(0).getAsJsonObject();
			var c = new Category();
					 c.setIdCategory(objCateg.get("id").getAsInt());
					 c.setCategoryName(objCateg.get("name").getAsString());
			var p = ProductFactory.createDefaultProduct(parseType(c));
			p.setCategory(c);
			p.setProductId(obj.get("id").getAsLong());
			p.setName(obj.get("name").getAsString());
		

			try {
				var img = obj.get(IMAGES).getAsJsonArray().get(0).getAsJsonObject();
							p.setUrl(img.get("src").getAsString());
			}
			catch(Exception e)
			{
				//do nothing.. no image found
			}

					var stockItem = ProductFactory.generateStockItem(p);
					stockItem.setId(p.getProductId());


					if(List.of(EnumItems.BOOSTER,EnumItems.CONSTRUCTPACK,EnumItems.BOX,EnumItems.FATPACK,EnumItems.PRERELEASEPACK,EnumItems.BUNDLE, EnumItems.SEALED).contains(p.getTypeProduct()))
							stockItem.setCondition(EnumCondition.SEALED);
					else if(List.of(EnumItems.SET,EnumItems.LOTS).contains(p.getTypeProduct()))
						stockItem.setCondition(EnumCondition.OPENED);
					else if(List.of(EnumItems.CARD).contains(p.getTypeProduct()))
						stockItem.setCondition(EnumCondition.NEAR_MINT);

					try {
						stockItem.setPrice(obj.get(PRICE).getAsDouble());
					}
					catch(Exception e)
					{
						stockItem.setPrice(0.0);
					}

					try {
						stockItem.setSku(obj.get("sku").getAsString());
					}
					catch(Exception e)
					{
							//do nothing
					}

					try {
						stockItem.setQte(obj.get(STOCK_QUANTITY).getAsInt());
					}catch(Exception e)
					{
						stockItem.setQte(0);
					}
				notify(stockItem);
				ret.add(stockItem);
		});

		return ret;


	}

	private EnumItems parseType(Category c) {
		for(String s : getArray("MAP_CATEG_TYPE"))
		{
			var arr = s.split("=");

			if(arr.length==2 && arr[1].equals(String.valueOf(c.getIdCategory())))
			{
				return EnumItems.valueOf(arr[0]);
			}
		}

		logger.warn("No EnumItems map found for {}",c);

		return EnumItems.SEALED;
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

		var temp = new StringBuilder();
		for(EnumItems it : EnumItems.values())
		{
			temp.append(it.name()).append("=").append("").append(",");
		}

		return Map.of("MAP_CATEG_TYPE",temp.toString().substring(0, temp.toString().length()-1));
	}

	@Override
	public Integer saveOrUpdateContact(Contact c) throws IOException {
		var attributs = new HashMap<String, Object>();
		var objAddr = new JsonObject();
			  objAddr.addProperty(FIRST_NAME, c.getName());
			  objAddr.addProperty(LAST_NAME, c.getLastName());
			  objAddr.addProperty(ADDRESS_1, c.getAddress());
			  objAddr.addProperty("city", c.getCity());
			  objAddr.addProperty(COUNTRY, c.getCountry());
			  objAddr.addProperty(POSTCODE, c.getZipCode());
			  objAddr.addProperty(EMAIL, c.getZipCode());
			  objAddr.addProperty(PHONE, c.getTelephone());

			  attributs.put(BILLING, objAddr);
			  attributs.put("shipping", objAddr);
			  attributs.put(FIRST_NAME, c.getName());
			  attributs.put(LAST_NAME, c.getLastName());
			  attributs.put(EMAIL, c.getEmail());

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
	public Long saveOrUpdateTransaction(Transaction t) throws IOException {
		init();
		if(t.getId()>0)
		{
			Map<String,Object> content = new HashMap<>();
			   content.put("post", createOrder(t));

			 client.update(EndpointBaseType.ORDERS.getValue(),t.getId().intValue(),content);
		}
		else
		{
			Map<String,Object> content = new HashMap<>();
							   content.put("post", createOrder(t));

			@SuppressWarnings("unchecked")
			Map<Object,Object> ret=  client.create(EndpointBaseType.ORDERS.getValue(),content);

			if(!ret.isEmpty() && ret.get("id") !=null)
			{
				t.setId(Integer.parseInt(ret.get("id").toString()));
				logger.info("{} created in {} with id={}",t,getName(),t.getId());
			}
			else
			{
				logger.error(ret);
			}
		}

		return t.getId();
	}

	@Override
	public MTGStockItem getStockById( EnumItems typeStock,Long id) throws IOException {
		return loadStock("").stream().filter(mcsi->mcsi.getId().equals(id)).findFirst().orElseThrow();
	}

	@Override
	public void saveOrUpdateStock(List<MTGStockItem> stock) throws IOException {
		init();
		Map<String, Object> vars = new HashMap<>();
		for(MTGStockItem it : stock)
		{
			vars.put(PRICE, String.valueOf(it.getPrice()));
			vars.put("regular_price", String.valueOf(it.getPrice()));
			vars.put(STOCK_QUANTITY, it.getQte());
			
			if(it.getId()>-1)
			{
				var ret = client.update(EndpointBaseType.PRODUCTS.getValue(),it.getId().intValue(),vars );
			    logger.debug("ret={}",ret);
			}
			else
			{
				
			}
	    
			it.setUpdated(false);
		}
	}

	@Override
	public List<Contact> listContacts() throws IOException {
		init();

		List<JsonObject> res = client.getAll(EndpointBaseType.CUSTOMERS.getValue());
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
		client.delete(EndpointBaseType.ORDERS.getValue(), t.getId().intValue());

	}

	@Override
	public Transaction getTransactionById(Long parseInt) throws IOException {
		init();
		var ret = client.get(EndpointBaseType.ORDERS.getValue(), parseInt.intValue());

		var t = new Transaction();
			t.setId(parseInt);
			t.setContact(toContact(new JsonExport().toJsonElement(ret.get(BILLING)).getAsJsonObject(), Integer.parseInt(ret.get("customer_id").toString())));
			t.setStatut(tostatus(ret.get(STATUS).toString()));
			t.setItems(toWooItems(new JsonExport().toJsonArray(ret.get(LINE_ITEMS))));
			t.setCurrency(ret.get("currency").toString().replace("\"", ""));
			t.setDateCreation(UITools.parseGMTDate(ret.get("date_created").toString()));
			t.setDatePayment(ret.get(DATE_PAID).getClass()!=JsonNull.class?UITools.parseGMTDate(ret.get(DATE_PAID).toString()):null);
			t.setDateSend(ret.get("date_completed").getClass()!=JsonNull.class?UITools.parseGMTDate(ret.get("date_completed").toString()):null);


			switch(ret.get("payment_method").toString())
			{
				case "bacs":t.setPaymentProvider(EnumPaymentProvider.BANK_TRANSFERT);break;
				case "PayPal":t.setPaymentProvider(EnumPaymentProvider.PAYPAL);break;
				default :t.setPaymentProvider(EnumPaymentProvider.VISA);break;
			}

		return t;
	}


	private JSONObject createOrder(Transaction t)
	{
		var obj = new JSONObject();
		var items = new JSONArray();

		var contact = new JSONObject();
				   contact.put(FIRST_NAME, t.getContact().getName());
				   contact.put(LAST_NAME, t.getContact().getLastName());
				   contact.put(COUNTRY, t.getContact().getCountry());
				   contact.put(EMAIL, t.getContact().getEmail());
				   contact.put(PHONE, t.getContact().getTelephone());
				   contact.put(ADDRESS_1, t.getContact().getAddress());
				   contact.put("city", t.getContact().getCity());
				   contact.put(POSTCODE, t.getContact().getZipCode());

		obj.put(BILLING, contact);
		obj.put("shipping", contact);
		obj.put(LINE_ITEMS, items);
		obj.put("set_paid", t.getStatut().equals(EnumTransactionStatus.PAID));
		obj.put("created_via", MTGConstants.MTG_APP_NAME);

		if(t.getPaymentProvider()!=null)
		{
			obj.put("payment_method_title", t.getPaymentProvider().name());
			obj.put(DATE_PAID, t.getDatePayment().getTime());
		}


		for(MTGStockItem st : t.getItems())
		{
			var line = new JSONObject();
				line.put(PRODUCT_ID, st.getTiersAppIds(WooCommerceTools.WOO_COMMERCE_NAME));
				line.put("quantity", st.getQte());
			items.put(line);
		}
		return obj;
	}


	private MTGProduct parseProduct(JsonObject element) {

			var obj = element.getAsJsonObject();
			

			var objCateg = obj.get(CATEGORIES).getAsJsonArray().get(0).getAsJsonObject();
			var c = new Category();
					 c.setIdCategory(objCateg.get("id").getAsInt());
					 c.setCategoryName(objCateg.get("name").getAsString());
					 
					 var p = ProductFactory.createDefaultProduct(parseType(c));		 
			p.setCategory(c);
			p.setProductId(obj.get("id").getAsLong());
			p.setName(obj.get("name").getAsString());

			var img = obj.get(IMAGES).getAsJsonArray().get(0).getAsJsonObject();
			p.setUrl(img.get("src").getAsString());
			return p;

	}

	private EnumTransactionStatus tostatus(String status) {

		status = status.replace("\"", "");

		switch(status)
		{
			case "pending" : return EnumTransactionStatus.NEW;
			case "processing" : return EnumTransactionStatus.IN_PROGRESS;
			case "on-hold" : return EnumTransactionStatus.PAYMENT_WAITING;
			case "completed": return EnumTransactionStatus.CLOSED;
			case "cancelled": return EnumTransactionStatus.CANCELED;
			case "failed": return EnumTransactionStatus.CANCELED;
			case "pre-ordered":return EnumTransactionStatus.PRE_ORDERED;
			case "lpc_transit": return EnumTransactionStatus.SENT;
			case "lpc_delivered": return EnumTransactionStatus.DELIVRED;
			case "lpc_ready_to_ship" : return EnumTransactionStatus.PAID;
			case "refunded" : return EnumTransactionStatus.CANCELED;
			default : {
				logger.debug("{} is unknow",status);
				return EnumTransactionStatus.IN_PROGRESS;
			}
		}
	}

	private Contact toContact(JsonObject contactObj, int id) {

		var c = new Contact();
			c.setId(id);

			try {
				if(contactObj.get(FIRST_NAME)!=null)
					c.setName(contactObj.get(FIRST_NAME).getAsString());

				if(contactObj.get(LAST_NAME)!=null)
					c.setLastName(contactObj.get(LAST_NAME).getAsString());

				if(contactObj.get(ADDRESS_1)!=null)
					c.setAddress(contactObj.get(ADDRESS_1).getAsString());

				if(contactObj.get(POSTCODE)!=null)
					c.setZipCode(contactObj.get(POSTCODE).getAsString());

				if(contactObj.get("city")!=null)
					c.setCity(contactObj.get("city").getAsString());

				if(contactObj.get(COUNTRY)!=null)
					c.setCountry(contactObj.get(COUNTRY).getAsString());

				if(contactObj.get(EMAIL)!=null)
					c.setEmail(contactObj.get(EMAIL).getAsString());

				if(contactObj.get(PHONE)!=null)
					c.setTelephone(contactObj.get(PHONE).getAsString());


			c.setEmailAccept(false);
			}
			catch(Exception e)
			{
				logger.error(e);
			}

		return c;

	}

	@Override
	public Contact getContactByLogin(String login, String passw) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> listTransactions(Contact c) throws IOException {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public boolean enableContact(String token) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}


}


