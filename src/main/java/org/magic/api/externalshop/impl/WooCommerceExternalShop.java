package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.api.mkm.modele.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.services.MTGConstants;
import org.magic.tools.MTG;
import org.magic.tools.WooCommerceTools;

import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;

public class WooCommerceExternalShop extends AbstractExternalShop {

	
	private WooCommerce client;
	
	private void init()
	{
		if(client==null)
			client = WooCommerceTools.newClient(new WooCommerceExport().getProperties());
	}
	
	
	@Override
	public List<Transaction> listTransaction() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@SuppressWarnings("unchecked")
	public void createTransaction(Transaction t)throws IOException {
			init();
			
			Map<String,Object> content = new HashMap<>();
							   content.put("post", createOrder(t));
			
			Map<Object,Object> ret=  client.create(EndpointBaseType.ORDERS.getValue(),content);
			logger.info(ret);
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
		// TODO Auto-generated method stub
		return null;
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
