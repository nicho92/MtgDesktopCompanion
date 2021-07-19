package org.magic.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

import com.google.gson.JsonElement;
import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.HttpMethod;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import com.icoderman.woocommerce.oauth.OAuthSignature;

public class WooCommerceTools {

	protected static Logger logger = MTGLogger.getLogger(WooCommerceTools.class);

	private WooCommerceTools() {}
	
	
	public static WooCommerce newClient(String key, String secret, String website,String version)
	{
		return new WooCommerce() {
			
			private OAuthConfig config = new OAuthConfig(website,key,secret);
			private ApiVersionType apiVersion = ApiVersionType.valueOf(version);
			private static final String API_URL_FORMAT = "%s/wp-json/wc/%s/%s";
		    private static final String API_URL_BATCH_FORMAT = "%s/wp-json/wc/%s/%s/batch";
		    private static final String API_URL_ONE_ENTITY_FORMAT = "%s/wp-json/wc/%s/%s/%d";
		    private static final String URL_SECURED_FORMAT = "%s?%s";
			private final String contentType=URLTools.HEADER_JSON +"; charset="+MTGConstants.DEFAULT_ENCODING.name();
		    
		    
			@Override
			public Map<String,JsonElement> update(String endpointBase, int id, Map<String, Object> object) {
				Map<String,JsonElement> map = new HashMap<>();
				try {
					var url = String.format(API_URL_ONE_ENTITY_FORMAT, config.getUrl(), apiVersion, endpointBase,id);
					URLToolsClient c = URLTools.newClient();
					Map<String,String> header = new HashMap<>();
									   header.put(URLTools.CONTENT_TYPE, contentType);
									   
					String ret = c.doPut(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.PUT), new ByteArrayEntity(new JsonExport().toJson(object).getBytes(MTGConstants.DEFAULT_ENCODING)), header);
					
					var obj = URLTools.toJson(ret).getAsJsonObject();
					obj.entrySet().forEach(e->map.put(e.getKey(), e.getValue()));
				} catch (IOException e) {
					logger.error(e);
				}
				
				
				return map;
			}
		
			@Override
			public Map<String,JsonElement> create(String endpointBase, Map<String, Object> object) {
				
				Map<String,JsonElement> map = new HashMap<>();
				try {
					var url = String.format(API_URL_FORMAT, config.getUrl(), apiVersion, endpointBase);
					var c = URLTools.newClient();
					Map<String,String> header = new HashMap<>();
									   header.put(URLTools.CONTENT_TYPE, contentType);
									   
					var ret = "";	
									   
					if(object.get("post")==null)				   
					{
						ret = c.doPost(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.POST), new ByteArrayEntity(new JsonExport().toJson(object).getBytes(MTGConstants.DEFAULT_ENCODING)), header);
					}
					else
					{
						ret = c.doPost(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.POST), new ByteArrayEntity(object.get("post").toString().getBytes(MTGConstants.DEFAULT_ENCODING)), header);
					}
					
					var obj = URLTools.toJson(ret).getAsJsonObject();
					obj.entrySet().forEach(e->map.put(e.getKey(), e.getValue()));
				} catch (Exception e) {
					logger.error(e);
				}
				
				
				return map;
			}
			
			
			@Override
			public List<JsonElement> getAll(String endpointBase, Map<String, String> params) {
				var url = String.format(API_URL_FORMAT, config.getUrl(), apiVersion, endpointBase);
				var signature = OAuthSignature.getAsQueryString(config, url, HttpMethod.GET, params);
				var securedUrl = String.format(URL_SECURED_FORMAT, url, signature);
		        List<JsonElement> ret = new ArrayList<>();
		        try {
					for(JsonElement e : URLTools.extractJson(securedUrl).getAsJsonArray())
						ret.add(e);
		
				} catch (IOException e) {
					logger.error(e);
					return ret;
				}
		        return ret;
			}
			
			@Override
			public Map<String,JsonElement> get(String endpointBase, int id) {
				var url = String.format(API_URL_ONE_ENTITY_FORMAT, config.getUrl(), apiVersion, endpointBase, id);
				var signature = OAuthSignature.getAsQueryString(config, url, HttpMethod.GET);
				var securedUrl = String.format(URL_SECURED_FORMAT, url, signature);
		        Map<String,JsonElement> map = new HashMap<>();
				try {
					var el = URLTools.extractJson(securedUrl).getAsJsonObject();
					el.entrySet().forEach(e->map.put(e.getKey(), e.getValue()));
					return map;
				       
				} catch (IOException e) {
					logger.error(e);
				}
		        return map;
			}
			
			@Override
			public Map delete(String endpointBase, int id) {
				return null;
			}
			
			
			@Override
			public Map<String,JsonElement> batch(String endpointBase, Map<String, Object> object) {
				var url = String.format(API_URL_BATCH_FORMAT, config.getUrl(), apiVersion, endpointBase);
				URLToolsClient c = URLTools.newClient();
				Map<String,String> header = new HashMap<>();
				  				   header.put(URLTools.CONTENT_TYPE, contentType);
					 
				Map<String,JsonElement> ret = new HashMap<>();
				try {
					String str = c.doPost(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.POST), new ByteArrayEntity(new JsonExport().toJson(object).getBytes(MTGConstants.DEFAULT_ENCODING)), header);
					var obj = URLTools.toJson(str).getAsJsonObject();
					obj.entrySet().forEach(e->ret.put(e.getKey(), e.getValue()));
				} catch (IOException e) {
					logger.error("Error in batch",e);
				}    
			
				return ret;
			}
		};
	}

	
	public static JSONObject createOrder(Transaction t)
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
		obj.put("created_via", "MtgCompanion");
		
		for(MTGStockItem st : t.getItems())
		{
			var line = new JSONObject();
				line.put("product_id", st.getTiersAppIds("WooCommerce"));
				line.put("quantity", st.getQte());
			items.put(line);
		}
		return obj;
	}
	
	
	
}
