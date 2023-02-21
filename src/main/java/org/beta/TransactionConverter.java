package org.beta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.magic.api.beans.SealedStock;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class TransactionConverter {

	
	private static List<Transaction> transactions;	
	
	public static void main(String[] args) throws SQLException, IOException {
		
		MTGControler.getInstance().init();
		MTG.getEnabledPlugin(MTGCardsProvider.class).init();
		var c = MTG.getEnabledPlugin(MTGDao.class).getContactByEmail("nicolas.pihen@gmail.com");
		var orders = MTG.getEnabledPlugin(MTGDao.class).listOrders().stream().filter(o->o.getType()==EnumItems.SET).toList();
		
		
		
		for(var order : orders)
		{
					var t = new Transaction();
					 t.setSourceShopId(order.getIdTransation());
					 t.setCurrency(order.getCurrency());
					 t.setDateCreation(order.getTransactionDate());
					 t.setDatePayment(order.getTransactionDate());
					 t.setDateSend(order.getTransactionDate());
					 t.setSourceShopName(order.getSource());
					 t.setShippingPrice(order.getShippingPrice());
					 t.setTypeTransaction(order.getTypeTransaction());
					 t.setMessage("Imported from orders");
					 t.setStatut(TransactionStatus.CLOSED);
					 t.setReduction(0);
					 t.setContact(c);
			
					 
					 for(String idset : order.getDescription().split(" "))
					  {
						  	 var set = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(idset.trim());
						  	 try { 
						  		var product = MTG.getEnabledPlugin(MTGSealedProvider.class).get(set, order.getType()).get(0);
						  		var stockItem = new SealedStock(product);
									 	  stockItem.setQte(1);
									 	  stockItem.setPrice(order.getItemPrice());
									 	  stockItem.setLanguage("French");
									 	  
									 	  System.out.println("\t"+stockItem.getQte() + " " + stockItem.getProduct().getTypeProduct() + " " + stockItem.getProduct().getEdition().getId() + " " +stockItem.getProduct().getExtra() + " " + stockItem.getLanguage()+ " " + stockItem.getPrice());
									 	  
									 	  t.getItems().add(stockItem);	 
						  	 }
						  	 catch(ArrayIndexOutOfBoundsException ex)
						  	 {
						  		 System.out.println("ERROR PRODUCT : " + set);
						  	 }
					  }
						
					 
							try {
								//TransactionService.saveTransaction(t, false);
								System.out.println("Transaction saved for order #"+order.getIdTransation() + " with " +t.getItems().size() + " items : " + UITools.formatDouble(t.total())) ;
								//MTG.getEnabledPlugin(MTGDao.class).deleteOrderEntry(order);
								
							} catch (Exception e) {
								e.printStackTrace();
							}
		}			
					System.exit(0);				
									
					}
				
}
	
