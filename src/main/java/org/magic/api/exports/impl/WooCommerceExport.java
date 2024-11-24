package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.ListUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.externalshop.impl.WooCommerceExternalShop;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.api.providers.impl.ScryFallProvider;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;
import org.magic.services.tools.WooCommerceTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;

public class WooCommerceExport extends AbstractCardExport {

	private static final String MTG_COMP_STOCK_ID = "mtg_comp_stock_id";
	private static final String UPDATE = "update";
	private static final String CREATE = "create";
	private static final String CARD_LANG_DESCRIPTION = "CARD_LANG_DESCRIPTION";
	private static final String STOCK_MANAGEMENT = "STOCK_MANAGEMENT";
	private static final String CATEGORY_ID = "CATEGORY_ID";
	public static final String DEFAULT_STATUT = "DEFAULT_STATUT";
	private  static final String BATCH_THRESHOLD = "BATCH_THRESHOLD";
	private  static final String BATCH_SIZE = "BATCH_SIZE";
	private static final String CATEGORY_EDITION_MAPPING ="CATEGORY_EDITION_MAPPING";
	
	private WooCommerce wooCommerce;
	private List<Category> categs = new ArrayList<>();
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.ONLINE;
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
		return "";
	}

	private void init()
	{
	    wooCommerce = WooCommerceTools.newClient(getAuthenticator());
	}
	
	public WooCommerce getWooCommerce() {
		if(wooCommerce==null)
			init();

		return wooCommerce;
	}

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		init();

		List<MTGCardStock> stocks= new ArrayList<>();
		Map<String, String> productInfo = new HashMap<>();
		        productInfo.put("category", getString(CATEGORY_ID));
		        
		List<JsonElement> ret = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), productInfo);

		for(JsonElement e : ret)
		{
			try {
				var id = String.valueOf(e.getAsJsonObject().get("id").getAsInt());
				var st = MTG.getEnabledPlugin(MTGDao.class).getStockWithTiersID(getName(), id);

				if(st==null)
				{
					logger.debug("stock not found with id={}. Create new one",id);
					st = MTGControler.getInstance().getDefaultStock();
					st.getTiersAppIds().put(getName(), id);

					var mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(e.getAsJsonObject().get("name").getAsString(), null, true).stream().findFirst().orElse(null);
					if(mc!=null)
					{
						st.setProduct(mc);
					}
					else
					{
						logger.debug("{} is not found",e.getAsJsonObject().get("name").getAsString());
						continue;
					}

				}
				else
				{
					logger.debug("Found MTGCompanion Stock={} with {} id={}",st.getId(),getName(),id);
					st.setUpdated(true);
				}

				st.setPrice(e.getAsJsonObject().get("price").getAsDouble());
				st.setQte(e.getAsJsonObject().get("stock_quantity").getAsInt());

				stocks.add(st);

				notify(st.getProduct());

			} catch (Exception e1) {
				logger.error("error importStock",e1);
			}



		}
		return stocks;
	}



	@SuppressWarnings("unchecked")
	@Override
	public void exportStock(List<MTGCardStock> stocks, File f) throws IOException {

		init();
		
		 if(getBoolean(CATEGORY_EDITION_MAPPING))
		    {
				try {
					categs = new WooCommerceExternalShop().listCategories();
					logger.info("Loading {} categories", categs.size());
				} catch (IOException e) {
					logger.error("error getting categories",e);
				}
		    }
		
		
		if(stocks.size()>getInt(BATCH_THRESHOLD))
		{
			batchExport(ListUtils.partition(stocks, getInt(BATCH_SIZE)));
			return;
		}


		for(MTGCardStock st : stocks)
		{

			Map<String, Object> productInfo=build(st);

	        Map<String,JsonElement> ret;

				if(st.getTiersAppIds(getName())!=null)
				{
					logger.debug("{} is already present in {}. Update it",st.getProduct(),getName());
					ret = wooCommerce.update(EndpointBaseType.PRODUCTS.getValue(),(int)Double.parseDouble(st.getTiersAppIds().get(getName())),productInfo);
					if(ret.isEmpty())
					{
						logger.info("No update for {}-{}. Create it",st,st.getProduct());
						ret = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(), productInfo);

					}
				}
				else
				{
					logger.debug("{} is not present in {}",st.getProduct(),getName());
					ret = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(), productInfo);
				}

				if(ret.isEmpty() || ret.get("id")==null)
				{
					logger.error("No export for {}-{}:{}",st,st.getProduct(),ret);
				}
				else
				{
					st.getTiersAppIds().put(getName(), ret.get("id").getAsString());
					st.setUpdated(true);
				}

				notify(st.getProduct());
		}
	}



	public Map<String, Object> build(MTGStockItem st) {
		Map<String, Object> productInfo = new HashMap<>();


		if(st.getProduct()==null)
			return productInfo;

		if(st.getTiersAppIds().get(getName())!=null)
			productInfo.put("id", st.getTiersAppIds(getName()));

		if(st.getProduct().getTypeProduct()==EnumItems.CARD)
			productInfo.put("name", toForeign(st.getProduct()).getName());
		else
			productInfo.put("name", st.getProduct().getName());
		
        productInfo.put("type", "simple");
        productInfo.put("regular_price", String.valueOf(st.getPrice()));
        productInfo.put("price", String.valueOf(st.getPrice()));
           		
        if(getBoolean(CATEGORY_EDITION_MAPPING))
        {
        	Optional<Category> opt =categs.stream().filter(c->c.getCategoryName().equalsIgnoreCase(st.getProduct().getEdition().getSet())).findFirst();
        	if(opt.isPresent())
        	{
        		productInfo.put("categories", WooCommerceTools.entryToJsonArray("id",String.valueOf(opt.get().getIdCategory())));	
        	}
        	else
        	{
        		Map<String, Object> categoryMap = new HashMap<>();
        		try { 
	        		var categ = new Category();
	        			  categ.setCategoryName(st.getProduct().getEdition().getSet());
	        		categoryMap.put("name", categ.getCategoryName());
					categoryMap.put("slug", st.getProduct().getEdition().getId());
					Map<String,JsonElement> ret = wooCommerce.create(EndpointBaseType.PRODUCTS_CATEGORIES.getValue(), categoryMap);
					categ.setIdCategory(ret.get("id").getAsInt());
					categs.add(categ);
	        		logger.warn("Can't find category named {}. create new one",st.getProduct().getEdition().getSet());
	        		productInfo.put("categories", WooCommerceTools.entryToJsonArray("id",""+categ.getIdCategory()));
        		}
        		catch(Exception e)
        		{
        			logger.error("can't create Category with {} : {}", categoryMap, e.getMessage());
        		}
        	}
        }
        else
        {
        	productInfo.put("categories", WooCommerceTools.entryToJsonArray("id",getString(CATEGORY_ID)));	
        }
        
        productInfo.put("description",desc(st.getProduct()));
        productInfo.put("enable_html_description", "true");
        productInfo.put("status", getString(DEFAULT_STATUT));

        if(getBoolean(STOCK_MANAGEMENT)) {
        	productInfo.put("manage_stock", getString(STOCK_MANAGEMENT));
        	productInfo.put("stock_quantity", String.valueOf(st.getQte()));
        }


        	try {
        		
        		productInfo.put("images", WooCommerceTools.entryToJsonArray("src",new ScryFallProvider().getJsonFor(st.getProduct()).get("image_uris").getAsJsonObject().get("normal").getAsString()));
        	}catch(Exception e)
        	{
        		logger.error("error getting image for {} : {}",st.getProduct(),e.getMessage());
        	}


      		var arr = new JsonArray();
      				  arr.add(WooCommerceTools.createAttributes("mtg_comp_collection", String.valueOf(st.getMagicCollection()),false));
					  arr.add(WooCommerceTools.createAttributes("mtg_comp_foil", String.valueOf(st.isFoil()),true));
					  arr.add(WooCommerceTools.createAttributes("mtg_comp_condition", st.getCondition().name(),true));
					  arr.add(WooCommerceTools.createAttributes("mtg_comp_altered", String.valueOf(st.isAltered()),true));
					  arr.add(WooCommerceTools.createAttributes("mtg_comp_signed", String.valueOf(st.isSigned()),true));
					  arr.add(WooCommerceTools.createAttributes("mtg_comp_language", st.getLanguage(),true));
					  arr.add(WooCommerceTools.createAttributes("mtg_comp_comment", st.getComment()!=null?st.getComment():"",true));
					  arr.add(WooCommerceTools.createAttributes("mtg_comp_setCode", st.getProduct().getEdition().getId(),true));
					  arr.add(WooCommerceTools.createAttributes("mtg_comp_setName", st.getProduct().getEdition().getSet(),true));
					  arr.add(WooCommerceTools.createAttributes(MTG_COMP_STOCK_ID,String.valueOf(st.getId()),false));
					  arr.add(WooCommerceTools.createAttributes("mtg_comp_type",st.getProduct().getTypeProduct().name(),true));
					  productInfo.put("attributes", arr);

      	return productInfo;
	}

	private void batchExport(List<List<MTGCardStock>> partition) {



		for(List<MTGCardStock> stocks : partition) {
			Map<String,Object> params = new HashMap<>();



			List<MTGCardStock> creates = stocks.stream().filter(st->st.getTiersAppIds().get(getName())==null).toList();


			params.put(CREATE, creates.stream().map(this::build).toList());
			params.put(UPDATE, stocks.stream().filter(st->st.getTiersAppIds().get(getName())!=null).map(this::build).toList());


			Map<String,JsonElement> ret = wooCommerce.batch(EndpointBaseType.PRODUCTS.getValue(), params);

			logger.debug(ret);

			if(ret.get(CREATE)!=null) {
				var arrRet = ret.get(CREATE).getAsJsonArray();

				for(var i=0;i<arrRet.size();i++)
				{
					var obj = arrRet.get(i).getAsJsonObject();

					try {
							if(obj.get("id").getAsInt()==0)
							{
								logger.error("Error for {}:{}",creates.get(i),obj );
							}
							else
							{
								creates.get(i).getTiersAppIds().put(getName(), String.valueOf(obj.get("id").getAsInt()));
								creates.get(i).setUpdated(true);
								logger.debug("Update {}", creates.get(i).getId());
							}
					}
					catch(Exception e)
					{
						logger.error("error updating at {} : {}, Error:{}",i,obj,e.getMessage());
					}


				}
			}else
			{
				logger.warn("no return ....");
			}

			if(ret.get(UPDATE)!=null)
			{
				logger.debug("Update done");
			}


			for(MTGCardStock st : stocks)
				notify(st.getProduct());

		}

	}

	

	private String desc(MTGProduct prod) {
		
		if(prod.getTypeProduct()==EnumItems.CARD)
		{
		MTGCard mc2 = toForeign((MTGCard)prod);
		var build =new StringBuilder();
		build.append("<html>").append(mc2).append("<br/>").append(mc2.getFullType()).append("<br/>").append(mc2.getText()).append("</html>");
		return build.toString();
		}
		else
		{
			return prod.toString();	
		}
		
	}

	private MTGCard toForeign(@Nonnull MTGCard mc) {
		MTGCard mc2 ;

		if(!getString(CARD_LANG_DESCRIPTION).isEmpty())
			mc2 = mc.toForeign(mc.getForeignNames().stream().filter(fn->fn.getLanguage().equalsIgnoreCase(getString(CARD_LANG_DESCRIPTION))).findFirst().orElse(null));
		else
			mc2=mc;

		if(mc2==null )
			return mc;

		return mc2;
	}


	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		var d = new MTGDeck();
		d.setName(name);

		for(MTGCardStock st : importStock(f))
		{
			d.getMain().put(st.getProduct(), st.getQte());
		}
		return d;
	}


	@Override
	public String getName() {
		return WooCommerceTools.WOO_COMMERCE_NAME;
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
				m.put(CATEGORY_EDITION_MAPPING, MTGProperty.newBooleanProperty("true", "create a category for each card's set"));
				m.put(CATEGORY_ID, new MTGProperty("", "Woocommerce category id you want to import"));
				m.put(DEFAULT_STATUT, new MTGProperty("private", "select default status of the exported product","private","public"));
				m.put(STOCK_MANAGEMENT,MTGProperty.newBooleanProperty("true", "enable (or note) the stock management of the product"));
				m.put(CARD_LANG_DESCRIPTION, new MTGProperty("English","Choose lang for the card's data"));
				m.put(BATCH_THRESHOLD,MTGProperty.newIntegerProperty("50", "items threshold when api will use the batch endpoint", 2, -1));
				m.put(BATCH_SIZE, MTGProperty.newIntegerProperty("75", "number of items by batch if threshold is reached", 50, 100));
				
		return m;
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return WooCommerceTools.generateKeysForWooCommerce();
	}


}
