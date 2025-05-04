package org.magic.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.technical.WebShopConfig;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class WebShopService {

	private static final String DELIVERY_KEY = "delivery";
	private static final String PAYMENTS_KEY = "payments";
	private static final String CONFIG_KEY = "config";
	private static final String PRODUCTS_KEY = "products";
	private static final String BANQ_KEYS = "banqAccount";

	
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private File configFile;
	
	public WebShopService() throws IOException {
		configFile = new File(MTGConstants.CONF_DIR, MTGConstants.CONF_WEBSHOP_FILENAME);
		if (!configFile.exists())
			try {
				logger.info("{} file doesn't exist. creating one from default file",configFile);
				FileTools.copyURLToFile(getClass().getResource("/data/default-shop.json"),configFile);
				logger.info("webshop conf file created");
			} catch (IOException e1) {
				logger.error(e1);
			}
	}
	
	public WebShopConfig getWebConfig()
	{
		var conf = new WebShopConfig();
		
		JsonObject jsonData;
		try {
			jsonData = FileTools.readJson(configFile).getAsJsonObject();
		} catch (IOException e) {
			logger.error("error reading {}",configFile,e);
			return conf;
		}
		
			conf.setAboutText(jsonData.get(CONFIG_KEY).getAsJsonObject().get("aboutText").getAsString());
			conf.setBannerText(jsonData.get(CONFIG_KEY).getAsJsonObject().get("bannerText").getAsString());
			conf.setBannerTitle(jsonData.get(CONFIG_KEY).getAsJsonObject().get("bannerTitle").getAsString());
			conf.setSiteTitle(jsonData.get(CONFIG_KEY).getAsJsonObject().get("siteTitle").getAsString());
			conf.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
			conf.setMaxLastProduct(jsonData.get(CONFIG_KEY).getAsJsonObject().get("maxLastProductSlide").getAsInt());
			conf.setProductPagination(jsonData.get(CONFIG_KEY).getAsJsonObject().get("productPaginationSlide").getAsInt());
			conf.setPercentReduction(jsonData.get(CONFIG_KEY).getAsJsonObject().get("percentReduction").getAsDouble());
			conf.setGoogleAnalyticsId(jsonData.get(CONFIG_KEY).getAsJsonObject().get("ganalyticsId").getAsString());
			conf.setEnableGed(jsonData.get(CONFIG_KEY).getAsJsonObject().get("enableGed").getAsBoolean());
			conf.setExtraCss(jsonData.get(CONFIG_KEY).getAsJsonObject().get("extracss").getAsString());
			conf.setAutomaticValidation(jsonData.get(CONFIG_KEY).getAsJsonObject().get("autoValidation").getAsBoolean());
			conf.setWebsiteUrl(jsonData.get(CONFIG_KEY).getAsJsonObject().get("websiteUrl").getAsString());
			conf.setSealedEnabled(jsonData.get(CONFIG_KEY).getAsJsonObject().get("sealedEnabled").getAsBoolean());

			conf.setAutomaticProduct(jsonData.get(CONFIG_KEY).getAsJsonObject().get(PRODUCTS_KEY).getAsJsonObject().get("autoSelection").getAsBoolean());
			
			for(var s : jsonData.get(CONFIG_KEY).getAsJsonObject().get("collections").getAsJsonArray())
				conf.getCollections().add(new MTGCollection(s.getAsString()));

			for(var s : jsonData.get(CONFIG_KEY).getAsJsonObject().get("needCollections").getAsJsonArray())
				conf.getNeedcollections().add(new MTGCollection(s.getAsString()));

			for(var s : jsonData.get(CONFIG_KEY).getAsJsonObject().get("slides").getAsJsonArray())
		       conf.getSlidesLinksImage().add(s.getAsString());

			conf.setPaypalClientId(jsonData.get(PAYMENTS_KEY).getAsJsonObject().get("paypalclientId").getAsString());

			conf.setBic(jsonData.get(PAYMENTS_KEY).getAsJsonObject().get(BANQ_KEYS).getAsJsonObject().get("bic").getAsString());
			conf.setIban(jsonData.get(PAYMENTS_KEY).getAsJsonObject().get(BANQ_KEYS).getAsJsonObject().get("iban").getAsString());

			try {
				conf.setPaypalSendMoneyUri(new URI(jsonData.get(PAYMENTS_KEY).getAsJsonObject().get("paypalSendMoneyUri").getAsString()));
			} catch (URISyntaxException _) {
				conf.setPaypalSendMoneyUri(null);
			}
	
			conf.setAverageDeliveryTime(jsonData.get(DELIVERY_KEY).getAsJsonObject().get("deliveryDay").getAsInt());
			conf.setShippingRules( jsonData.get(DELIVERY_KEY).getAsJsonObject().get("shippingRules").getAsString());
				
		
			try {

				if(conf.isAutomaticProduct())
					conf.setTopProduct(TransactionService.getBestProduct());
				else
					conf.setTopProduct( MTG.getEnabledPlugin(MTGDao.class).getStockById(jsonData.get(CONFIG_KEY).getAsJsonObject().get(PRODUCTS_KEY).getAsJsonObject().get("top").getAsLong()));
			}
			catch(Exception _)
			{
				logger.warn("No top product selected for webshop");
			}

		

			var id = jsonData.get(CONFIG_KEY).getAsJsonObject().get("contact").getAsInt();

			Contact contact = new Contact();
			try {
				contact = MTG.getEnabledPlugin(MTGDao.class).getContactById(id);
			} catch (NumberFormatException | SQLException _) {
				logger.error("No contact found with id = {}",id);
			}
			conf.setContact(contact);

		return conf;
	}


	public void saveWebConfig(WebShopConfig wsc) {
		
		var obj = new JsonObject();
		
		var jsonconfig = new JsonObject();
			obj.add(CONFIG_KEY , jsonconfig);
			
		var jsondelivery = new JsonObject();
			 obj.add(DELIVERY_KEY , jsondelivery);
		
		 var productConf = new JsonObject();
			 jsonconfig.add(PRODUCTS_KEY, productConf);	  
		
		var paymentConf = new JsonObject();
			obj.add(PAYMENTS_KEY, paymentConf);
			 
			 
			 
			 jsonconfig.addProperty("siteTitle",wsc.getSiteTitle());
			 jsonconfig.addProperty("bannerTitle",wsc.getBannerTitle());
			 jsonconfig.addProperty("bannerText",wsc.getBannerText());
			 jsonconfig.addProperty("aboutText",wsc.getAboutText());
			 jsonconfig.addProperty("websiteUrl",wsc.getWebsiteUrl());
			 jsonconfig.addProperty("enableGed",wsc.isEnableGed());
			 jsonconfig.addProperty("extracss",wsc.getExtraCss());
			 jsonconfig.addProperty("maxLastProductSlide",wsc.getMaxLastProduct());
			 jsonconfig.addProperty("autoValidation",wsc.isAutomaticValidation());
			 jsonconfig.addProperty("ganalyticsId",wsc.getGoogleAnalyticsId());
			 jsonconfig.addProperty("sealedEnabled",wsc.isSealedEnabled());
			 jsonconfig.addProperty("percentReduction",wsc.getPercentReduction());
			 jsonconfig.addProperty("contact",wsc.getContact().getId());
			 jsonconfig.addProperty("productPaginationSlide", wsc.getProductPagination());
			 
			 
			var arrSlides = new JsonArray();
			wsc.getSlidesLinksImage().forEach(arrSlides::add);
			jsonconfig.add("slides", arrSlides);
		
			var arrNeeds = new JsonArray();
			wsc.getNeedcollections().forEach(c->arrNeeds.add(c.getName()));
			jsonconfig.add("needCollections", arrNeeds);
			
			var arrCols = new JsonArray();
			wsc.getCollections().forEach(c->arrCols.add(c.getName()));
			jsonconfig.add("collections", arrCols);
			
			productConf.addProperty("autoSelection",wsc.isAutomaticProduct());
			
			if(wsc.getTopProduct().getId()!=null)
				productConf.addProperty("top", wsc.getTopProduct().getId());
			
			jsondelivery.addProperty("shippingRules", wsc.getShippingRules());
			jsondelivery.addProperty("deliveryDay",wsc.getAverageDeliveryTime());

			
			var objBanq = new JsonObject();
    				objBanq.addProperty("bic",wsc.getBic());
					objBanq.addProperty("iban",wsc.getIban());
			paymentConf.add(BANQ_KEYS, objBanq);

					
		paymentConf.addProperty("paypalclientId",wsc.getPaypalClientId());
		paymentConf.addProperty("paypalSendMoneyUri",wsc.getSetPaypalSendMoneyUri().toString());
		
		
		try {
			FileTools.saveFile(configFile,obj.toString());
		} catch (IOException e) {
			logger.error("error saving {} file ",configFile.getAbsolutePath(),e);
		}
		
	}


}
