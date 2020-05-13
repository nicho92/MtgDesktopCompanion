package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.ByteArrayEntity;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.pictures.impl.MythicSpoilerPicturesProvider;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.HttpMethod;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import com.icoderman.woocommerce.oauth.OAuthSignature;

public class WooCommerceExport extends AbstractCardExport {

	private static final String CATEGORY_ID = "CATEGORY_ID";
	private static final String CONSUMER_KEY = "CONSUMER_KEY";
	private static final String CONSUMER_SECRET = "CONSUMER_SECRET";
	private static final String DEFAULT_STATUT = "DEFAULT_STATUT";
	private  WooCommerce wooCommerce;
	
	
	public static void main(String[] args) throws IOException {
		
		MagicCardStock stock = new MagicCardStock();
		
		MagicEdition ed = new MagicEdition("ISD","Innistrad");
					 ed.setMultiverseid("235597");
		MagicCard mc = new MagicCard();
				  mc.setName("Liliana of the veil");
				  mc.getSupertypes().add("Legendary");
				  mc.getTypes().add("PlanesWalker");
				  mc.getSubtypes().add("Liliana");
				  mc.getEditions().add(ed);
				  mc.setScryfallId("ac506c17-adc8-49c6-9d8d-43db7cb1ec9d");
				  mc.setText("+1: Each player discards a card.\n" + 
				  		"-2: Target player sacrifices a creature.\n" + 
				  		"-6: Separate all permanents target player controls into two piles. That player sacrifices all permanents in the pile of his or her choice.");				  
		stock.setMagicCard(mc);
		stock.setQte(4);
		stock.setCondition(EnumCondition.NEAR_MINT);
		stock.setPrice(99.99);
		stock.setFoil(true);
		
		
	//	new WooCommerceExport().exportStock(List.of(stock), null);
		new WooCommerceExport().importStock(null);
	}
	
	@Override
	public boolean needFile() {
		return false;
	}
	
	@Override
	public boolean needDialogForDeck(MODS mod) {
		return false;
	}
	
	 @Override
	public boolean needDialogForStock(MODS mod) {
		return false;
	}
	
	@Override
	public String getFileExtension() {
		return null;
	}
	
	private void init()
	{
		 wooCommerce = new WooCommerce() {
			
			private OAuthConfig config = new OAuthConfig(getString("WEBSITE"), getString(CONSUMER_KEY), getString(CONSUMER_SECRET));
			private ApiVersionType apiVersion = ApiVersionType.V3;
			private static final String API_URL_FORMAT = "%s/wp-json/wc/%s/%s";
		    private static final String API_URL_BATCH_FORMAT = "%s/wp-json/wc/%s/%s/batch";
		    private static final String API_URL_ONE_ENTITY_FORMAT = "%s/wp-json/wc/%s/%s/%d";
		    private static final String URL_SECURED_FORMAT = "%s?%s";
			
			@Override
			public Map<String,JsonElement> update(String endpointBase, int id, Map<String, Object> object) {
				Map<String,JsonElement> map = new HashMap<>();
				try {
					String url = String.format(API_URL_ONE_ENTITY_FORMAT, config.getUrl(), apiVersion, endpointBase,id);
					URLToolsClient c = URLTools.newClient();
					Map<String,String> header = new HashMap<>();
									   header.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);

					String ret = c.doPut(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.PUT), new ByteArrayEntity(new Gson().toJson(object).getBytes()), header);
					
					JsonObject obj = URLTools.toJson(ret).getAsJsonObject();
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
					String url = String.format(API_URL_FORMAT, config.getUrl(), apiVersion, endpointBase);
					URLToolsClient c = URLTools.newClient();
					Map<String,String> header = new HashMap<>();
									   header.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);

					String ret = c.doPost(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.POST), new ByteArrayEntity(new Gson().toJson(object).getBytes()), header);
					
					JsonObject obj = URLTools.toJson(ret).getAsJsonObject();
					obj.entrySet().forEach(e->map.put(e.getKey(), e.getValue()));
				} catch (IOException e) {
					logger.error(e);
				}
				
				
				return map;
			}
			
			
			@Override
			public List<JsonElement> getAll(String endpointBase, Map<String, String> params) {
				String url = String.format(API_URL_FORMAT, config.getUrl(), apiVersion, endpointBase);
		        String signature = OAuthSignature.getAsQueryString(config, url, HttpMethod.GET, params);
		        String securedUrl = String.format(URL_SECURED_FORMAT, url, signature);
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
				String url = String.format(API_URL_ONE_ENTITY_FORMAT, config.getUrl(), apiVersion, endpointBase, id);
		        String signature = OAuthSignature.getAsQueryString(config, url, HttpMethod.GET);
		        String securedUrl = String.format(URL_SECURED_FORMAT, url, signature);
		        Map<String,JsonElement> map = new HashMap<>();
				try {
					JsonObject el = URLTools.extractJson(securedUrl).getAsJsonObject();
					el.entrySet().forEach(e->map.put(e.getKey(), e.getValue()));
					return map;
				       
				} catch (IOException e) {
					logger.error(e);
				}
		        return map;
			}
			
			@Override
			public Map delete(String endpointBase, int id) {
				// TODO Auto-generated method stub
				return null;
			}
			
			
			@Override
			public Map<String,JsonElement> batch(String endpointBase, Map<String, Object> object) {
				String url = String.format(API_URL_BATCH_FORMAT, config.getUrl(), apiVersion, endpointBase);
				URLToolsClient c = URLTools.newClient();
				Map<String,String> header = new HashMap<>();
								   header.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
				Map<String,JsonElement> ret = new HashMap<>();
				try {
					String str = c.doPost(url+"?"+OAuthSignature.getAsQueryString(config, url, HttpMethod.POST), new ByteArrayEntity(new Gson().toJson(object).getBytes()), header);
					
					JsonObject obj = URLTools.toJson(str).getAsJsonObject();
					obj.entrySet().forEach(e->ret.put(e.getKey(), e.getValue()));
					
					
				} catch (IOException e) {
					logger.error(e);
				}    
			
				return ret;
			}
		};
	}
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		if(wooCommerce==null)
			init();
		
		List<MagicCardStock> stocks= new ArrayList<>();
		Map<String, String> productInfo = new HashMap<>();
		        productInfo.put("category", getString(CATEGORY_ID));
		
		List<JsonElement> ret=  wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), productInfo);
		
		logger.debug(ret);
		
		return stocks;
		
	}
	
	

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		if(wooCommerce==null)
			init();
	
		for(MagicCardStock st : stock) 
		{
					Map<String, Object> productInfo = new HashMap<>();
								        productInfo.put("name", st.getMagicCard().getName());
								        productInfo.put("type", "simple");
								        productInfo.put("regular_price", String.valueOf(st.getPrice()));
								        productInfo.put("categories", toJson("id",getString(CATEGORY_ID)));
								        productInfo.put("description",desc(st.getMagicCard()));
								        productInfo.put("short_description", st.getMagicCard().getName()+"-"+st.getCondition());
								        productInfo.put("enable_html_description", "true");
								        productInfo.put("stock_quantity", String.valueOf(st.getQte()));
								        productInfo.put("status", getString(DEFAULT_STATUT));
								        productInfo.put("images", toJson("src",new MythicSpoilerPicturesProvider().generateUrl(st.getMagicCard(), null).toString()));
			        Map<String,JsonElement> ret = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(), productInfo);
		
			        if(ret.isEmpty())
			        	logger.error("No export for " + st + "-" + st.getMagicCard());
					        
					notify(st.getMagicCard());
		}
	}
	
	private String desc(MagicCard mc) {
		StringBuilder build =new StringBuilder();
		build.append("<html>").append(mc.getName()).append("<br/>").append(mc.getFullType()).append("<br/>").append(mc.getText()).append("</html>");
		return build.toString();
	}

	private JsonArray toJson(String string, String value) {

		JsonObject obj = new JsonObject();
				   obj.addProperty(string, value);
				   
		JsonArray arr = new JsonArray();
				  arr.add(obj);
				   
				  return arr;
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		MagicDeck d = new MagicDeck();
		d.setName(name);
		
		for(MagicCardStock st : importStock(f))
		{
			d.getMain().put(st.getMagicCard(), st.getQte());
		}
		return d;
	}
	

	@Override
	public String getName() {
		return "WooCommerce";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void initDefault() {
		setProperty("WEBSITE", "https://");
		setProperty(CONSUMER_KEY, "");
		setProperty(CONSUMER_SECRET, "");
		setProperty(CATEGORY_ID, "");
		setProperty(DEFAULT_STATUT, "private");
		
	}
	
	

}
