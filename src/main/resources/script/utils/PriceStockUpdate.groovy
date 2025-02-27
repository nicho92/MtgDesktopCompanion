/**
* Update prices of your stock cards
*/


import org.magic.services.*;
import org.magic.services.tools.*;
import org.magic.api.interfaces.*;
import org.magic.api.exports.impl.*;
import org.magic.api.beans.*;
import java.io.File;
import org.magic.services.PluginRegistry;
import java.util.stream.*;
import java.util.*;

//////////////////////////////////MANUAL CONFIGURATION 

var pricerName = "Mkm";
var collectionName = "Library";
var editionId = "10E";
var dateBefore = new Date();


//////////////////////////////////////////////////////

var pricer = PluginRegistry.inst().getPlugin(pricerName,MTGPricesProvider.class);
var dao = PluginRegistry.inst().getEnabledPlugins(MTGDao.class);
	
	
	MTGEdition edition = null;
		
	if(!editionId.isEmpty())
		edition = PluginRegistry.inst().getEnabledPlugins(MTGCardsProvider.class).getSetById(editionId);


	System.out.println("Listing stock from " + collectionName + " / " + edition);
	
	
	
	
	dao.listStocks(new MTGCollection(collectionName), edition).stream().filter(mcs->mcs.getDateUpdate().before(dateBefore)).collect(Collectors.toList()).each{ mcs->
		try {
			var card = mcs.getProduct();
			System.out.println("getting price for " + card + " / StockId ="+ mcs.getId());
			var doubleValue = pricer.getSuggestedPrice(card,mcs.isFoil());
			mcs.setPrice(doubleValue);
			dao.saveOrUpdateStock(mcs);
		} catch (Exception e) {
			System.out.println(""+collection + ";" + newC.getEdition() + ";" + c + ";ERROR;" + e+"\n");
		} 
	 };



	System.out.println("end");

