package org.beta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import org.magic.api.exports.impl.MkmOnlineExport;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class Mkm2WooCommerce {
	
	private static Logger logger = MTGLogger.getLogger(Mkm2WooCommerce.class);
    private List<ConverterItem>  conversions;
	
	public static void main(String[] args) throws IOException, SQLException {
		MTGControler.getInstance();
		MTG.getEnabledPlugin(MTGDao.class).init();
		
		new Mkm2WooCommerce().debugTransactions();
	}
	
	public Mkm2WooCommerce() throws IOException {
		conversions = new ArrayList<>();
		loadConversionsFromFile(new File("C:\\Users\\Nicolas\\Google Drive\\conversions.csv"));
	}
	
	
	private void debugTransactions() throws IOException {
		listTransaction().forEach(t->{
			logger.info(t.getContact() + " "+  UITools.roundDouble(t.total()) +" " + t.getCurrency().getSymbol());
			for(MTGStockItem it : t.getItems())
			{
				logger.info(it.getProductName() + " " + it.getLanguage() + " " + it.getTiersAppIds());
			}
		});
		
	}

	public void saveTransactions()
	{
		
			try {
				
				for(Transaction t : listTransaction())
					MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);
			} catch (Exception e) {
				logger.error(e);
			} 
		
		
	}
	
	public List<Transaction> listTransaction() throws IOException
	{
		//PROD return new OrderService().listOrders(ACTOR.buyer,STATE.paid,null).stream().map(Mkm2WooCommerce::toTransaction).collect(Collectors.toList())
		return new OrderService().listOrders(new File("C:\\Users\\Nicolas\\Google Drive\\Orders.Mkm.Bought.xml")).stream().map(this::toTransaction).collect(Collectors.toList());
	}
	
	
	public void loadConversionsFromFile(File f) throws IOException
	{
			conversions.clear();
			var list = Files.readAllLines(f.toPath());
			list.remove(0); // remove title
			list.forEach(s->{
				
				var arr = s.split(";");
				
				try {
					conversions.add(new ConverterItem(arr[0],Integer.parseInt(arr[3]) ,Integer.parseInt(arr[2]), arr[1]));
				} catch (Exception e) {
					logger.error(s+"|"+e.getMessage());
				}
			});
	}
	
	
	public int getWoocommerceId(String lang, int idMkm)
	{
		return conversions.stream().filter(p->(p.getLang().equalsIgnoreCase(lang) && p.getIdMkmProduct()==idMkm)).findFirst().orElse(new ConverterItem()).getIdWoocommerceProduct();
	}
	

	private Transaction toTransaction(Order o) {
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
			item.getTiersAppIds().put(new MkmOnlineExport().getName(), String.valueOf(article.getIdProduct()));
			try {
				item.getTiersAppIds().put(new WooCommerceExport().getName(), String.valueOf(getWoocommerceId(article.getLanguage().getLanguageName(),article.getIdProduct())));
			} catch (Exception e) {
				logger.error("Error getting Woocomerce id for Mkm ArticleID="+article.getIdArticle() + " : " + e);
			}
			t.getItems().add(item);
		});
		return t;
	}

}

class ConverterItem
{
	private String name;
	private int idMkmProduct;
	private int idWoocommerceProduct;
	private String lang;
	
	public ConverterItem() {
		
	}
	
	public ConverterItem(String name, int idMkmProduct, int idWoocommerceProduct, String lang) {
		this.name = name;
		this.idMkmProduct = idMkmProduct;
		this.idWoocommerceProduct = idWoocommerceProduct;
		this.lang = lang;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIdMkmProduct() {
		return idMkmProduct;
	}
	public void setIdMkmProduct(int idMkmProduct) {
		this.idMkmProduct = idMkmProduct;
	}
	public int getIdWoocommerceProduct() {
		return idWoocommerceProduct;
	}
	public void setIdWoocommerceProduct(int idWoocommerceProduct) {
		this.idWoocommerceProduct = idWoocommerceProduct;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
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
