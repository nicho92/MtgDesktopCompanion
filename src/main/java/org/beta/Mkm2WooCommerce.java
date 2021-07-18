package org.beta;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.api.mkm.modele.LightArticle;
import org.api.mkm.modele.Order;
import org.api.mkm.services.OrderService;
import org.magic.api.beans.Contact;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.abstracts.AbstractStockItem;

public class Mkm2WooCommerce {

	public static void main(String[] args) throws IOException {
		OrderService serv = new OrderService();
		List<Order> ordrs = serv.listOrders(new File("C:\\Users\\Nicolas\\Google Drive\\Orders.Mkm.Bought.xml"));
		
		ordrs.forEach(o->{
			Transaction t = toTransaction(o);
			System.out.println(t.getContact() + " " + t.getStatut() + " : " + t.total() + " (Shipp :" + t.getShippingPrice() + ") "+t.getCurrency() + " : " + (t.total() + t.getShippingPrice()) + " "+t.getCurrency());
			t.getItems().forEach(it->{
				System.out.println("\t"+it.getProductName() + " " + it.getPrice());
			});
			
			if(t.getStatut()==TransactionStatus.NEW)
			{
				
				Map<Object, Object> ret =  new WooCommerceExport().sendOrder(t);
				System.out.println(ret);
				
			}
			
			//only first
			System.exit(0);
			
		});
	}

	private static Transaction toTransaction(Order o) {
		Transaction t = new Transaction();
		t.setTransporterShippingCode(null);
		t.setDateCreation(o.getState().getDateBought());
		t.setDatePayment(o.getState().getDatePaid());
		t.setDateSend(o.getState().getDateSent());
		t.setCurrency(o.getCurrencyCode());
		t.setId(o.getIdOrder());
		
		Contact c = new Contact();
				c.setName(o.getBuyer().getAddress().getName().split(" ")[0]+"------TEST");
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
		
		
		t.setTransporter("");
		
		
		o.getArticle().forEach(article->{
			var item = new AbstractStockItem<LightArticle>() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public void setProduct(LightArticle c) {
					product=c;
					setProductName(c.getProduct().getEnName());
					edition= new MagicEdition("",c.getProduct().getExpansion());
					url = "https:"+ c.getProduct().getImage();
					//setTypeStock(EnumItems.CARD);
				}
				
			};
		
			item.setPrice(article.getPrice());
			item.setProduct(article);
			item.setQte(article.getCount());
			item.getTiersAppIds().put("MagicCardMarket", String.valueOf(article.getIdArticle()));
			item.getTiersAppIds().put("WooCommerce", "-1");
			t.getItems().add(item);
		});
		return t;
	}

}
