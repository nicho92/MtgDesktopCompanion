package org.beta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.externalshop.impl.WooCommerceExternalShop;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.MTG;
import org.magic.services.tools.WooCommerceTools;

import com.google.gson.JsonObject;
import com.icoderman.woocommerce.EndpointBaseType;


public class FixingWoo {

	private static Logger logger = MTGLogger.getLogger(FixingWoo.class);
	public static void main(String[] args) throws SQLException, IOException {
		MTGControler.getInstance().init();
		
		var client = WooCommerceTools.newClient(new WooCommerceExport().getAuthenticator());
		
		var categs = new WooCommerceExternalShop().listCategories();
		
		for(var mcs : MTG.getEnabledPlugin(MTGDao.class).listStocks().stream().filter(mcs->mcs.getTiersAppIds(WooCommerceTools.WOO_COMMERCE_NAME)==null).toList())
		{
			var map = new HashMap<String,String>();
					map.put("search",mcs.getProduct().getName().replace(" ", "%20").replace("'", "%27").replace(",","%2C"));
					map.put("category",""+categs.stream().filter(p->p.getCategoryName().equalsIgnoreCase(mcs.getProduct().getCurrentSet().getSet())).findFirst().get().getIdCategory());
			
			List<JsonObject> objs = client.getAll(EndpointBaseType.PRODUCTS.getValue(), map);
			
			for(var obj : objs)
				save(mcs,obj);

		}
		
		System.exit(0);
		
		
	}

	private static void save(MTGCardStock mcs, JsonObject obj) throws SQLException {
		for(var je : obj.get("attributes").getAsJsonArray()) 
		{
			
			if(je.getAsJsonObject().get("name").getAsString().equals("mtg_comp_stock_id") && je.getAsJsonObject().get("options").getAsJsonArray().get(0).getAsString().equals(""+mcs.getId()))
			{
				mcs.getTiersAppIds().put(WooCommerceTools.WOO_COMMERCE_NAME,obj.get("id").getAsString());
				mcs.setUpdated(true);
				MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateCardStock(mcs);
				logger.info("saving {} with wooId={}", mcs.getId() ,mcs.getTiersAppIds(WooCommerceTools.WOO_COMMERCE_NAME));
				break;
			}
		}
		
		
	}

}
