package org.beta;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.magic.api.beans.MTGSealedProduct.EXTRA;
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
	
	public static void main(String[] args) throws SQLException {
		
		MTGControler.getInstance().init();
		MTG.getEnabledPlugin(MTGCardsProvider.class).init();
		
		var itemType=EnumItems.CONSTRUCTPACK;
		
		transactions=MTG.getEnabledPlugin(MTGDao.class).listTransactions();
		
		var listId = MTG.getEnabledPlugin(MTGDao.class).listOrders().stream().filter(o->o.getType()==itemType).map(o->o.getIdTransation()).distinct().collect(Collectors.toList());
		var c = MTG.getEnabledPlugin(MTGDao.class).getContactByEmail("nicolas.pihen@gmail.com");
		
		
		for(String id : listId) 
		{
			var items = MTG.getEnabledPlugin(MTGDao.class).listOrdersByIdTransaction(id).stream().filter(o->o.getType()==itemType).toList();
			var error = false;
			
			if(!items.isEmpty())
			{
				var t = getTransactionFor(items.get(0));
						 t.setContact(c);
				
						for(var order : items) 
						{
							
							 try {
								 
								 EXTRA extra = null;
							
//								 if(order.getDescription().contains("Collector"))
//								 		 extra= EXTRA.COLLECTOR;
//								  else  if(order.getDescription().contains("VIP"))
//							 		 extra= EXTRA.VIP;
//								  else  if(order.getDescription().contains("d'extension") || order.getDescription().contains("Set"))
//								 		 extra= EXTRA.SET;
//								  else  if(order.getDescription().contains("Draft"))
//								 		 extra= EXTRA.DRAFT;
//								  else  if(order.getDescription().contains("Gift"))
//								 		 extra= EXTRA.GIFT;
								 
								  var lang = "English";
								  if(order.getDescription().contains("French") || order.getDescription().contains("Français")  || order.getDescription().contains("FR"))
									  lang="French";
								  
								  
							 var item = MTG.getEnabledPlugin(MTGSealedProvider.class).get(order.getEdition(), order.getType(),extra).get(0);  
							 var stockItem = new SealedStock(item);
								 	  int qty = 1 ;
								 	  
								 	  try {
								 		  qty = Integer.parseInt(order.getDescription().substring(0, order.getDescription().indexOf(" ")).trim());
								 	  }
								 	  catch(Exception e)
								 	  {
								 		  qty=1;
								 	  }
								 	  
									  if(order.getDescription().contains("Duel"))
											  stockItem.setComment("Duel Deck");
									  else if(order.getDescription().contains("Planeswalker"))
										  stockItem.setComment("Planeswalker Deck");
									  else if(order.getDescription().contains("Premium"))
										  stockItem.setComment("Premium Deck");
									  
								 	  stockItem.setQte(qty);
								 	  stockItem.setPrice(order.getItemPrice()/qty);
								 	  stockItem.getProduct().setExtra(extra==null?EXTRA.DRAFT:extra);
								 	  stockItem.setLanguage(lang);
								 	  System.out.println("\t"+stockItem.getQte() + " " + stockItem.getProduct().getTypeProduct() + " " + stockItem.getProduct().getEdition().getId() + " " +stockItem.getProduct().getExtra() + " " + stockItem.getLanguage()+ " " + stockItem.getPrice());
								 	  
								 	  t.getItems().add(stockItem);
							 }
							 catch(IndexOutOfBoundsException e)
							 {
								System.out.println("\tNo item found for " + order.getDescription() + "|" + order.getEdition().getId() + "|"+id);
								error=true;
							 }
					}
				
						
				if(!error)
				{
					try {
					//	TransactionService.saveTransaction(t, false);
						System.out.println("Transaction saved for order #"+id + " with " +items.size() + " items : " + UITools.formatDouble(t.total())) ;
					//	MTG.getEnabledPlugin(MTGDao.class).deleteOrderEntry(items);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
				{
					System.out.println("Error for Order#"+id);
				}
				 
			}
			else
			{
				System.out.println("No items found for #"+ id);
			}
		
				
		}
		System.exit(0);
	}

	private static Transaction getTransactionFor(OrderEntry order) throws SQLException {

		
		var opt = transactions.stream().filter(t->t.getSourceShopId().equals(order.getIdTransation())).findFirst();
		
		if(opt.isPresent())
		{
			return opt.get();
		}
		else
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
			 t.setFeePercent(0);
			 t.setMessage("Imported from orders");
			 t.setStatut(TransactionStatus.CLOSED);
			 return t;
		}
	}
	
	
}
