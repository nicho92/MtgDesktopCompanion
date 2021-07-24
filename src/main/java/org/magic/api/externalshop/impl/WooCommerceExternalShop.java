package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.api.mkm.modele.Category;
import org.api.mkm.modele.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.services.MTGConstants;
import org.magic.tools.WooCommerceTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;

public class WooCommerceExternalShop extends AbstractExternalShop {

	
	private WooCommerce client;
	
	private void init()
	{
		if(client==null)
			client = WooCommerceTools.newClient(new WooCommerceExport().getProperties());
	}
	
	
	public static void main(String[] args) throws IOException {
		new WooCommerceExternalShop().listTransaction();
	}
	
	
	@Override
	public List<Transaction> loadTransaction() throws IOException{
		init();
		
		Map<String, String> parameters = new HashMap<>();
	    parameters.put("status", getString("any"));
	    
		List<JsonElement> obj = client.getAll(EndpointBaseType.ORDERS.getValue(),parameters);
		
		
		return new ArrayList<>();
	}


	@Override
	@SuppressWarnings("unchecked")
	public void createTransaction(Transaction t) throws IOException {
			init();
			
			Map<String,Object> content = new HashMap<>();
							   content.put("post", createOrder(t));
			
			Map<Object,Object> ret=  client.create(EndpointBaseType.ORDERS.getValue(),content);
			
			if(!ret.isEmpty() && ret.get("id") !=null)
			{
				logger.info(t + " created in " + getName() + " with id = " + ret.get("id"));
			}
			else
			{
				logger.error(ret);
			}
	}


	@Override
	@SuppressWarnings("unchecked")
	public int createProduct(Product p) throws IOException {
		init();
		
		Map<Object,Object> ret = client.create(EndpointBaseType.PRODUCTS.getValue(), toWooCommerceAttributs(p,null,78));
		
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
		return WooCommerceExport.WOO_COMMERCE;
	}
	

	@Override
	public List<Product> listProducts(String name) throws IOException {
		init();
		
		Map<String, String> productInfo = new HashMap<>();

		productInfo.put("search", name);
		
		@SuppressWarnings("unchecked")
		List<JsonObject> res = client.getAll(EndpointBaseType.PRODUCTS.getValue(),productInfo);
		List<Product> ret =  new ArrayList<>();
		
		
		res.forEach(element->{
			
			Product p = new Product();
			JsonObject obj = element.getAsJsonObject();
			p.setIdProduct(obj.get("id").getAsInt());
			p.setEnName(obj.get("name").getAsString());
			p.setIdGame(1);
			p.setLocalization(new ArrayList<>());
			
			JsonObject objCateg = obj.get("categories").getAsJsonArray().get(0).getAsJsonObject();
			Category c = new Category();
					 c.setIdCategory(objCateg.get("id").getAsInt());
					 c.setCategoryName(objCateg.get("name").getAsString());
			p.setCategory(c);
			
			
			JsonObject img = obj.get("images").getAsJsonArray().get(0).getAsJsonObject();
			p.setImage(img.get("src").getAsString());
			ret.add(p);
		});
		return ret;
	}
	
	@Override
	public void initDefault() {
		setProperty("CATEGORY","0");
	}
	
	private static Map<String, Object> toWooCommerceAttributs(Product product,String status, int idCategory)
	{
		Map<String, Object> productInfo = new HashMap<>();

		productInfo.put("name", product.getEnName());
		productInfo.put("type", "simple");
        productInfo.put("categories", WooCommerceTools.entryToJsonArray("id",String.valueOf(idCategory)));
        productInfo.put("status", status==null?"private":status);
        productInfo.put("images", WooCommerceTools.entryToJsonArray("src","https:"+product.getImage()));
		 
		return productInfo;
	}
	
	private static JSONObject createOrder(Transaction t)
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
			obj.put("date_paid", t.getDatePayment().getTime());
		}
		
		
		for(MTGStockItem st : t.getItems())
		{
			var line = new JSONObject();
				line.put("product_id", st.getTiersAppIds(WooCommerceExport.WOO_COMMERCE));
				line.put("quantity", st.getQte());
			items.put(line);
		}
		return obj;
	}



}
