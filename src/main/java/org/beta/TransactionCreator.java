package org.beta;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Currency;

import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class TransactionCreator {

		
	public static void main(String[] args) throws SQLException, IOException {
		
		MTGControler.getInstance().init();
	
		var c = MTG.getEnabledPlugin(MTGDao.class).getContactById(87);
		var t = MTG.getEnabledPlugin(MTGDao.class).getTransaction(290L);

			t.setContact(c);
			t.setTransporter("Mondial Relay");
			t.setCurrency(Currency.getInstance("EUR"));
			t.setShippingPrice(7.0);
			t.setStatut(TransactionStatus.CLOSED);
			t.setTypeTransaction(TransactionDirection.SELL);
			t.setSourceShopName("Facebook");
			t.setTransporterShippingCode("670301");
			t.setDateCreation(UITools.parseDate("18/11/2022", "dd/MM/yyyy"));
			t.setDatePayment(UITools.parseDate("19/11/2022", "dd/MM/yyyy"));
			t.setDateSend(UITools.parseDate("20/11/2022", "dd/MM/yyyy"));
			t.getItems().clear();
		
		for(String l : FileTools.readAllLines(new File("D:\\Desktop\\INV.csv")))
		{
			
			
			var number = l.split(";")[2];
			var priceNormal = UITools.parseDouble(l.split(";")[4].replace("\"", "").replace("?", ""));
			var priceFoil = UITools.parseDouble(l.split(";")[5].replace("\"", "").replace("?", ""));
			var foil = l.split(";")[3].equals("X");
			var set = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetByName(l.split(";")[1]);
			var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, set);
			
			
			
			var item = MTGControler.getInstance().getDefaultStock();
				item.setFoil(foil);
				item.setProduct(mc);
				item.setLanguage("French");
				item.setCondition(EnumCondition.GOOD);
				
				if(foil)
					item.setPrice(priceFoil);
				else
					item.setPrice(priceNormal);
				
				
			t.getItems().add(item);
		}
			
		//	MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);
			
			System.exit(0);
	}	
	
}
