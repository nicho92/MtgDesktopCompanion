package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class EchoMTGExport extends AbstractCardExport {

	private String authToken=null;
	public static final String BASE_URL="https://api.echomtg.com";
	private MTGHttpClient client;


	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("EMAIL","PASSWORD");
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.ONLINE;
	}
	
	
	@Override
	public boolean needFile() {
		return false;
	}

	
	private void connect()
	{
		client = URLTools.newClient();

		var con = RequestBuilder.build().post()
				 .url(BASE_URL+"/api/user/auth/")
				 .addContent("email", getAuthenticator().get("EMAIL"))
				 .addContent("password", getAuthenticator().get("PASSWORD"))
				 .addHeader(URLTools.ACCEPT, "*/*")
				 .addHeader(URLTools.ACCEPT_ENCODING, "gzip, deflate, br")
				 .setClient(client)
				 .toJson();
		authToken=con.getAsJsonObject().get("token").getAsString();
	}

	@Override
	public String getFileExtension() {
		return "";
	}

	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		if(client==null)
			connect();


		stock.forEach(entry->{

					var list = RequestBuilder.build().post()
							 .url(BASE_URL+"/api/inventory/add/")
							 .addContent("auth", authToken)
							 .addContent("mid",entry.getProduct().getMultiverseid())
							 .addContent("quantity", String.valueOf(entry.getQte()))
							 .addContent("condition", aliases.getConditionFor(this, entry.getCondition()))
							 .addContent("foil", entry.isFoil()?"1":"0")
							 .setClient(client)
							 .toJson();
					
					
					logger.debug(list);
					
					if(list.getAsJsonObject().get("status").getAsString().equalsIgnoreCase("error"))
						logger.error("error loading {}: {}",entry.getProduct(),list.getAsJsonObject().get("message").getAsString());
					else
					{
						logger.debug("export: {}",list.getAsJsonObject().get("message").getAsString());
						entry.getTiersAppIds().put(getName(), list.getAsJsonObject().get("inventory_id").getAsString());
						entry.setUpdated(true);
						
					}
					notify(entry.getProduct());
		});
	}

	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		if(client==null)
			connect();


		  var list = RequestBuilder.build().get()
				 .url(BASE_URL+"/api/inventory/view/")
				 .addContent("auth", authToken)
				 .addContent("start", "0")
				 .addContent("limit", "100")
				 .setClient(client)
				 .toJson();


		var arr = list.getAsJsonObject().get("items").getAsJsonArray();
		var ret = new ArrayList<MTGCardStock>();
		arr.forEach(element -> {
			var ob = element.getAsJsonObject();
			MTGEdition ed =null;
			try {
				ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(ob.get("set_code").getAsString());
			} catch (Exception e) {
				logger.error("error with {}",ob,e);
			}


			try {
				List<MTGCard> cards = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(ob.get("name").getAsString(), ed, true);
				
				var mcs = MTGControler.getInstance().getDefaultStock();
				mcs.setProduct(cards.get(0));
				
				mcs.setCondition(aliases.getReversedConditionFor(this, element.getAsJsonObject().get("condition").getAsString(), EnumCondition.NEAR_MINT));
				mcs.setLanguage(element.getAsJsonObject().get("lang").getAsString());
				mcs.getTiersAppIds().put(getName(), element.getAsJsonObject().get("inventory_id").getAsString());
				mcs.setPrice(element.getAsJsonObject().get("current_price").getAsDouble());
				mcs.setFoil(element.getAsJsonObject().get("foil").getAsInt()==1);
				
				ret.add(mcs);
				notify(mcs.getProduct());
			} catch (IOException e) {
				logger.error(e);
			}




		});
		return ret;
	}
	

	@Override
	public String getName() {
		return "EchoMTG";
	}


	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}


}
