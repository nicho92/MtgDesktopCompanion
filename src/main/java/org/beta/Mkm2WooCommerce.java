package org.beta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.api.mkm.modele.LightArticle;
import org.api.mkm.modele.Order;
import org.api.mkm.services.OrderService;
import org.magic.api.beans.Contact;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.MTGLogger;
import org.magic.tools.UITools;

public class Mkm2WooCommerce {
	
	private static Logger logger = MTGLogger.getLogger(Mkm2WooCommerce.class);
    private static  Map<Integer,Integer>  conversions = new HashMap<>();
	
	public static void main(String[] args) throws IOException, SQLException {
		loadConversions(new File("C:\\Users\\Pihen\\Downloads\\conversions.csv"),2,3);
		listTransaction().forEach(t->{
			logger.info(t.getId() + " " + t.getContact() + " "+  UITools.roundDouble(t.total()) +" " + t.getCurrency().getSymbol());
		});
	}
	
	public static List<Transaction> listTransaction() throws IOException
	{
		//PROD return new OrderService().listOrders(ACTOR.buyer,STATE.paid,null).stream().map(Mkm2WooCommerce::toTransaction).collect(Collectors.toList())
		return new OrderService().listOrders(new File("C:\\Users\\Pihen\\Downloads\\Orders.Mkm.Paid.xml")).stream().map(Mkm2WooCommerce::toTransaction).collect(Collectors.toList());
	}
	
	
	public static void loadConversions(File f,int columnIdArticle, int columnIdWoocommerce) throws IOException
	{
			conversions.clear();
			var list = Files.readAllLines(f.toPath());
			list.remove(0); // remove title
			list.forEach(s->{
				try {
					conversions.put(Integer.parseInt(s.split(",")[columnIdArticle]), Integer.parseInt(s.split(",")[columnIdWoocommerce]));
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
		t.setMessage(o.getNote());
		
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
			var item = new MkmStockItem();
			item.setId(article.getIdProduct());
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


class MkmStockItem extends AbstractStockItem<LightArticle>
{
	private static final long serialVersionUID = 1L;

	@Override
	public void setProduct(LightArticle product) {
		this.product=product;
		setProductName(product.getProduct().getEnName());
		edition= new MagicEdition("",product.getProduct().getExpansion());
		url = "https:"+ product.getProduct().getImage();
		setTypeStock(EnumItems.SEALED);
	}

}
