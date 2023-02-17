package org.beta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.services.tools.MTG;

public class TransactionConverter {

	public static void main(String[] args) throws SQLException {
		
		MTGControler.getInstance().init();
		MTG.getEnabledPlugin(MTGCardsProvider.class).init();
		
		
		
		var listId = MTG.getEnabledPlugin(MTGDao.class).listOrders().stream().filter(o->o.getType()==EnumItems.CARD).map(o->o.getIdTransation()).collect(Collectors.toList());
		var c = MTG.getEnabledPlugin(MTGDao.class).getContactByEmail("nicolas.pihen@gmail.com");
		
		
		for(String id : listId) 
		{
			
			var items = MTG.getEnabledPlugin(MTGDao.class).listOrdersByIdTransaction(id);
			var order = items.get(0);
			
			var t = new Transaction();
					 t.setSourceShopId(order.getIdTransation());
					 t.setCurrency(order.getCurrency());
					 t.setDateCreation(order.getTransactionDate());
					 t.setDatePayment(order.getTransactionDate());
					 t.setDateSend(order.getTransactionDate());
					 t.setSourceShopName(order.getSource());
					 t.setShippingPrice(order.getShippingPrice());
					 t.setTypeTransaction(order.getTypeTransaction());
					 t.setStatut(TransactionStatus.CLOSED);
					 t.setContact(c);
			
			items.forEach(o->{
				
					 try {
						 var card = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(o.getDescription().trim(), o.getEdition(), true).get(0);
						 var stockItem = new MagicCardStock();
						 	  stockItem.setProduct(card);
						 	  stockItem.setPrice(o.getItemPrice());
						 	  t.getItems().add(stockItem);
					 }
					 catch(IndexOutOfBoundsException e)
					 {
						System.out.println("No card found for " + o.getDescription() + "|" + o.getEdition() + "|"+id);
					 }
					 catch(IOException e)
					 {
						System.out.println("Exception  for " + o.getDescription() + "|" + o.getEdition() + "|"+id);
					 }
			});
			
			 try {
					TransactionService.saveTransaction(t, false);
					System.out.println("Transaction created for order #"+id) ;
				} catch (IOException e) {
					e.printStackTrace();
				}
				
		}
		
	}
	
	
}
