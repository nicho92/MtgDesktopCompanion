package org.beta;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Currency;

import org.magic.api.beans.SealedStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.enums.TransactionPayementProvider;
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
	
		//var c = MTG.getEnabledPlugin(MTGDao.class).getContactById(84);
		var t = MTG.getEnabledPlugin(MTGDao.class).getTransaction(305L);
		/*	t.setContact(c);
			t.setTransporter("Mondial Relay");
			t.setCurrency(Currency.getInstance("EUR"));
			t.setShippingPrice(7.0);
			t.setStatut(TransactionStatus.IN_PROGRESS);
			t.setTypeTransaction(TransactionDirection.SELL);
			t.setSourceShopName("Facebook");
			t.setTransporterShippingCode("");
			t.setPaymentProvider(TransactionPayementProvider.PAYPAL);
			t.setDateCreation(UITools.parseDate("09/03/2023", "dd/MM/yyyy"));
			t.setDatePayment(UITools.parseDate("09/03/2023", "dd/MM/yyyy"));
			t.setDateSend(UITools.parseDate("13/03/2022", "dd/MM/yyyy"));
			t.getItems().clear();
		*/
//		for(String l : FileTools.readAllLines(new File("D:\\Desktop\\INV.csv")))
//		{
//			
//			
//			var number = l.split(";")[2];
//			var priceNormal = UITools.parseDouble(l.split(";")[4].replace("\"", "").replace("?", ""));
//			var priceFoil = UITools.parseDouble(l.split(";")[5].replace("\"", "").replace("?", ""));
//			var foil = l.split(";")[3].equals("X");
//			var set = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetByName(l.split(";")[1]);
//			var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, set);
//			
//			
//			
//			var item = MTGControler.getInstance().getDefaultStock();
//				item.setFoil(foil);
//				item.setProduct(mc);
//				item.setLanguage("French");
//				item.setCondition(EnumCondition.NEAR_MINT);
//				
//				if(foil)
//					item.setPrice(priceFoil);
//				else
//					item.setPrice(priceNormal);
//				
//				
//			t.getItems().add(item);
//		}
		
		
		for(String setId : new String[] {"WAR", "UMA", "M19", "GRN","RNA", "MH1","C19"})
		{
			var sealedProduct = MTG.getEnabledPlugin(MTGSealedProvider.class).get(MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(setId), EnumItems.SET).get(0);
			var st = new SealedStock(sealedProduct);
			st.setQte(329);
			st.getProduct().setTypeProduct(EnumItems.LOTS);
			st.setCondition(EnumCondition.OPENED);
			st.setPrice(0.03);
			t.getItems().add(st);
			
		}
		
		
		
			
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);
			
			System.exit(0);
	}	
	
}
