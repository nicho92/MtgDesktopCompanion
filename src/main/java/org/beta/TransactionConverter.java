package org.beta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.SealedStock;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class TransactionConverter {

	
	private static List<Transaction> transactions;	
	
	public static void main(String[] args) throws SQLException, IOException {
		
		MTGControler.getInstance().init();
		MTG.getEnabledPlugin(MTGCardsProvider.class).init();
		var c = MTG.getEnabledPlugin(MTGDao.class).getContactByEmail("nicolas.pihen@gmail.com");
		var orders = MTG.getEnabledPlugin(MTGDao.class).listOrders().stream().filter(o->o.getType()==EnumItems.SET ).toList();
		transactions = MTG.getEnabledPlugin(MTGDao.class).listTransactions();
		
		
		for(var order : orders)
		{
					Transaction t = getTransactionFor(order);
					 t.setContact(c);
					  for(String idset : order.getDescription().split(" "))
					  {
						  	 try {

							  		if(idset.startsWith("THP"))
							  			order.setType(EnumItems.CONSTRUCTPACK);
							  		
							  		
						  		var product = MTG.getEnabledPlugin(MTGSealedProvider.class).get(new MagicEdition(idset), order.getType()).get(0);

						  		
						  		var stockItem = new SealedStock(product);
									 	 try {
									 		 stockItem.setQte( Integer.parseInt(order.getDescription().substring(0,order.getDescription().indexOf(' ')))  );
									 	 }
									 	 catch(NumberFormatException ex)
									 	 {
									 		 stockItem.setQte(1);
									 	 }
									 	  stockItem.setPrice(order.getItemPrice()/stockItem.getQte());
									 	  stockItem.setLanguage("French");
									 	  
									 	  System.out.println("\t"+stockItem.getQte() + " " + stockItem.getProduct().getTypeProduct() + " " + stockItem.getProduct().getEdition().getId() + " " +stockItem.getProduct().getExtra() + " " + stockItem.getLanguage()+ " " + stockItem.getPrice());
									 	  
									 	  t.getItems().add(stockItem);	 
						  	 }
						  	 catch(ArrayIndexOutOfBoundsException ex)
						  	 {
						  		 System.out.println("ERROR PRODUCT : " + order.getEdition() + " " + order.getType());
						  	 }
					  }
						
					 
							try {
								TransactionService.saveTransaction(t, false);
								System.out.println("Transaction saved for order #"+order.getIdTransation() + " with " +t.getItems().size() + " items : " + UITools.formatDouble(t.total())) ;
								MTG.getEnabledPlugin(MTGDao.class).deleteOrderEntry(order);
								
							} catch (Exception e) {
								e.printStackTrace();
							}
		}			
					System.exit(0);				
									
					}

	private static Transaction getTransactionFor(OrderEntry order) {
		
		var opt = transactions.stream().filter(t->t.getSourceShopId().equals(order.getIdTransation())).findFirst();
		
		if(opt.isPresent())
		{
			System.out.println("Found " + order.getIdTransation());
			return opt.get();
		}
		
		
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
		 
		 return t;
		
	}
				
}
	
