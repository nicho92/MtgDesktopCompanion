package org.magic.services.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.AccountAuthenticator;
import org.magic.api.exports.impl.JsonExport;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.HttpMethod;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import com.icoderman.woocommerce.oauth.OAuthSignature;

public class WooCommerceTools {

	protected static Logger logger = MTGLogger.getLogger(WooCommerceTools.class);

	public static final String WOO_COMMERCE_NAME = "WooCommerce";
	private static final String WOO_COMMERCE_VERSION="V3";

	private static final String WEBSITE = "WEBSITE";
	private static final String CONSUMER_KEY = "CONSUMER_KEY";
	private static final String CONSUMER_SECRET = "CONSUMER_SECRET";


	private WooCommerceTools() {}

	public static List<String> generateKeysForWooCommerce()
	{
		return List.of(WEBSITE,CONSUMER_KEY,CONSUMER_SECRET);
	}

	public static WooCommerce newClient(AccountAuthenticator p)
	{
		return newClient(p.get(CONSUMER_KEY), p.get(CONSUMER_SECRET), p.get(WEBSITE), WOO_COMMERCE_VERSION);
	}
	
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
					MTGHttpClient c = URLTools.newClient();
					Map<String,String> header = new HashMap<>();
									   header.put(URLTools.CONTENT_TYPE, contentType);

				   var resp = c.doPut(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.PUT), new ByteArrayEntity(new JsonExport().toJson(object).getBytes(MTGConstants.DEFAULT_ENCODING)), header);


				   if(object.get("post")!=null)
					{
						resp = c.doPut(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.POST), new ByteArrayEntity(object.get("post").toString().getBytes(MTGConstants.DEFAULT_ENCODING)), header);
					}
				   var ret = c.toString(resp);

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

					HttpResponse resp = null;
					
					if(object.get("post")==null)
					{
						resp = c.doPost(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.POST), new ByteArrayEntity(new JsonExport().toJson(object).getBytes(MTGConstants.DEFAULT_ENCODING)), header);
					}
					else
					{
						resp = c.doPost(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.POST), new ByteArrayEntity(object.get("post").toString().getBytes(MTGConstants.DEFAULT_ENCODING)), header);
					}
					var obj = URLTools.toJson(c.toString(resp)).getAsJsonObject();
					obj.entrySet().forEach(e->map.put(e.getKey(), e.getValue()));
				} catch (Exception e) {
					logger.error(e);
				}


				return map;
			}
			
			@Override
			public List<JsonElement> getAll(String endpoint) {
				return getAll(endpoint, new HashMap<>());
			}
			
			
			@Override
			public List<JsonElement> getAll(String endpoint,Map<String, String> vars) {
			    var max = 100;
				var page=1;
				 	  vars.put("per_page", String.valueOf(max));
				 	  
				 List<JsonElement> returnList = new ArrayList<>();
				 List<JsonElement> list = readPaginate(endpoint,vars);
				
				 returnList.addAll(list);
				 while(list.size()>=max)
				 {
					 page=page+1;
					  vars.put("page", String.valueOf(page));
					  list=readPaginate(endpoint,vars);
					  returnList.addAll(list);
				 }
				
				 return returnList;

			}

			private List<JsonElement> readPaginate(String endpointBase, Map<String, String> params) {

				var url = String.format(API_URL_FORMAT, config.getUrl(), apiVersion, endpointBase);
				var signature = OAuthSignature.getAsQueryString(config, url, HttpMethod.GET, params);
				var securedUrl = String.format(URL_SECURED_FORMAT, url, signature);
			    List<JsonElement> ret = new ArrayList<>();
		        try
		        {

		        	var arr = URLTools.extractAsJson(securedUrl);
		        	for(JsonElement e : arr.getAsJsonArray())
		        		ret.add(e);

				} catch (Exception e) {
					logger.error(e);
				}
		        return ret;
			}

			@Override
			public Map<String,JsonElement> get(String endpointBase, int id) {
				var url = String.format(API_URL_ONE_ENTITY_FORMAT, config.getUrl(), apiVersion, endpointBase, id);
				var signature = OAuthSignature.getAsQueryString(config, url, HttpMethod.GET);
				var securedUrl = String.format(URL_SECURED_FORMAT, url, signature);
		        Map<String,JsonElement> map = new HashMap<>();
				
					var el = URLTools.extractAsJson(securedUrl).getAsJsonObject();
					el.entrySet().forEach(e->map.put(e.getKey(), e.getValue()));
					return map;

			}

			@Override
			public Map delete(String endpointBase, int id) {
				return new HashMap<>();
			}
			

			@Override
			public Map<String,JsonElement> batch(String endpointBase, Map<String, Object> object) {
				var url = String.format(API_URL_BATCH_FORMAT, config.getUrl(), apiVersion, endpointBase);
				MTGHttpClient c = URLTools.newClient();
				Map<String,String> header = new HashMap<>();
				  				   header.put(URLTools.CONTENT_TYPE, contentType);

				Map<String,JsonElement> ret = new HashMap<>();
				try {
					var resp = c.doPost(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.POST), new ByteArrayEntity(new JsonExport().toJson(object).getBytes(MTGConstants.DEFAULT_ENCODING)), header);
					var str = c.toString(resp);

					var obj = URLTools.toJson(str).getAsJsonObject();
					obj.entrySet().forEach(e->ret.put(e.getKey(), e.getValue()));
				} catch (IOException e) {
					logger.error("Error in batch",e);
				}

				return ret;
			}
		};
	}


	
	
	public static JsonObject createAttributes(String key ,String val,boolean visible)
	{
		if(val==null || val.equals("null"))
			createAttributes(key ,new String[] {""},visible);
		
		return createAttributes(key ,new String[] {val},visible);
	}

	public static JsonObject createAttributes(String key ,String[] val,boolean visible)
	{
					var obj = new JsonObject();
					   obj.addProperty("name", key);
					   obj.addProperty("visible", String.valueOf(visible));

					   var arr  =new JsonArray();
					   for(String s : val)
						   arr.add(s);

					   obj.add("options", arr);
		   return obj;
	}
	
	public static JsonArray entryToJsonArray(String string, String value) {

		var obj = new JsonObject();
		    obj.addProperty(string, value);

		var arr = new JsonArray();
		    arr.add(obj);

		return arr;
	}

	
	
	

}
