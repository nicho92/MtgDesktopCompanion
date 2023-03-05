package org.beta;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Currency;

import org.magic.api.beans.SealedStock;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class TransactionCreator {

		
	public static void main(String[] args) throws SQLException, IOException {
		
		MTGControler.getInstance().init();
	
		var c = MTG.getEnabledPlugin(MTGDao.class).getContactByEmail("");
		var s = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById("INV");
	//	var p = MTG.getEnabledPlugin(MTGSealedProvider.class).get(s, EnumItems.SET).get(0);
		
		var t = new Transaction();
		t.setContact(c);
		t.setTransporter("Colissimo");
		t.setCurrency(Currency.getInstance("EUR"));
		t.setShippingPrice(7.0);
		t.setStatut(TransactionStatus.IN_PROGRESS);
		t.setTypeTransaction(TransactionDirection.SELL);
		t.setSourceShopName("Facebook");
		
		
		
		
		for(String l : FileTools.readAllLines(new File("D:\\Desktop\\INV.csv")))
		{
			

			var number = l.split(";")[2];
			var priceNormal = UITools.parseDouble(l.split(";")[4].replace("\"", "").replace("?", ""));
			var priceFoil = UITools.parseDouble(l.split(";")[5].replace("\"", "").replace("?", ""));
			var foil = l.split(";")[3].equals("X");
			
			var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, s);
			
			
			var item = MTGControler.getInstance().getDefaultStock();
				item.setFoil(foil);
				item.setProduct(mc);
				
				if(foil)
					item.setPrice(priceFoil);
				else
					item.setPrice(priceNormal);
				
				
			t.getItems().add(item);
		}
		
//		
//		
//		
//		var item = new SealedStock();
//			item.setProduct(p);
//			item.setPrice(150.0);
//			item.setLanguage("French");
//			item.setQte(1);
//			item.setComment("86% 308 cartes");

		
	
			
			
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);
			
			System.exit(0);
	}	
	
}
