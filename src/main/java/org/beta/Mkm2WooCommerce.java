package org.beta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.api.mkm.modele.LightArticle;
import org.api.mkm.modele.Order;
import org.api.mkm.services.OrderService;
import org.magic.api.beans.Contact;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.MTGLogger;
import org.magic.tools.UITools;

public class Mkm2WooCommerce {
	
	private static Logger logger = MTGLogger.getLogger(Mkm2WooCommerce.class);
    private static  Map<Integer,Integer>  conversions = new HashMap<>();
	
	
	
	public static void main(String[] args) throws IOException {
		List<Order> ordrs = new OrderService().listOrders(new File("C:\\Users\\Pihen\\Downloads\\Orders.Mkm.Bought.xml"));
		
		loadConversions();
		
		ordrs.forEach(o->{
			Transaction t = toTransaction(o);
//			System.out.println(t.getContact() + " " + t.getStatut() + " : " + UITools.roundDouble(t.total() + t.getShippingPrice())  + " "+t.getCurrency());
//			t.getItems().forEach(it->{
//				System.out.println("\t"+it.getProductName() + " " + it.getPrice() + " " + it.getTiersAppIds() + " " + it.getLanguage());
//			});

			if(t.getStatut()==TransactionStatus.NEW)
			{
				
				Map<Object, Object> ret =  new WooCommerceExport().sendOrder(t);
				System.out.println(ret);
			}
			System.exit(0);
			
		});
	}
	
	private static void loadConversions() throws IOException
	{
			conversions.clear();
			var list = Files.readAllLines(new File("C:\\Users\\Pihen\\Downloads\\conversions.csv").toPath());
			
			
			list.remove(0); // remove title
			
			list.forEach(s->{
				try {
					conversions.put(Integer.parseInt(s.split(",")[2]), Integer.parseInt(s.split(",")[3]));
				} catch (Exception e) {
					logger.error(s+"|"+e.getMessage());
				}
			});
	}
	

	private static Transaction toTransaction(Order o) {
		Transaction t = new Transaction();
		t.setTransporterShippingCode(null);
		t.setDateCreation(o.getState().getDateBought());
		t.setDatePayment(o.getState().getDatePaid());
		t.setDateSend(o.getState().getDateSent());
		t.setCurrency(o.getCurrencyCode());
		t.setMessage(String.valueOf(o.getIdOrder()));
		
		Contact c = new Contact();
				c.setName(o.getBuyer().getAddress().getName().split(" ")[0]);
				c.setLastName(o.getBuyer().getAddress().getName().split(" ")[1]);
				c.setAddress(o.getBuyer().getAddress().getStreet());
				c.setZipCode(o.getBuyer().getAddress().getZip());
				c.setCity(o.getBuyer().getAddress().getCity());
				c.setEmail(null);
				
		t.setContact(c);
		
		
		t.setShippingPrice(o.getShippingMethod().getPrice());
		t.setTransporterShippingCode(o.getTrackingNumber());
		
		
		if(t.getDateCreation()!=null)
			t.setStatut(TransactionStatus.NEW);
		
		if(t.getDatePayment()!=null)
			t.setStatut(TransactionStatus.PAID);

		if(t.getDateSend()!=null)
			t.setStatut(TransactionStatus.SENT);
		
	
		o.getArticle().forEach(article->{
			var item = new AbstractStockItem<LightArticle>() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public void setProduct(LightArticle c) {
					product=c;
					setProductName(c.getProduct().getEnName());
					edition= new MagicEdition("",c.getProduct().getExpansion());
					url = "https:"+ c.getProduct().getImage();
					setTypeStock(EnumItems.SEALED);
				}
			};
			
			item.setLanguage(article.getLanguage().getLanguageName());
			item.setPrice(article.getPrice());
			item.setProduct(article);
			item.setQte(article.getCount());
			item.getTiersAppIds().put("MagicCardMarket", String.valueOf(article.getIdArticle()));
			try {
				item.getTiersAppIds().put("WooCommerce", String.valueOf(conversions.get(article.getIdArticle())));
			} catch (Exception e) {
				logger.error("Error getting Woocomerce id for Mkm ArticleID="+article.getIdArticle() + " : " + e);
			}
			t.getItems().add(item);
		});
		return t;
	}

}
