package org.beta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.api.mkm.modele.LightArticle;
import org.api.mkm.modele.Order;
import org.api.mkm.modele.Product;
import org.api.mkm.services.OrderService;
import org.api.mkm.services.OrderService.STATE;
import org.api.mkm.tools.MkmAPIConfig;
import org.magic.api.beans.Contact;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.exports.impl.MkmOnlineExport;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.MTG;
import org.magic.tools.WooCommerceTools;

import com.icoderman.woocommerce.EndpointBaseType;

public class Mkm2WooCommerce {
	
	private static Logger logger = MTGLogger.getLogger(Mkm2WooCommerce.class);
    private List<ConverterItem>  conversions;
	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance();
		MkmAPIConfig.getInstance().init(new File("C:\\Users\\Nicolas\\.magicDeskCompanion\\pricers\\MagicCardMarket.conf"));
		
		Mkm2WooCommerce mkWoo= new Mkm2WooCommerce();
		mkWoo.loadConversionsFromFile(new File("C:\\Users\\Nicolas\\Google Drive\\conversions.csv"));
		mkWoo.listTransaction(STATE.paid).forEach(t->{
			
			System.out.println(t.getId() + " " + t.getContact() + " " + t.total() + " " + t.getCurrency());
			t.getItems().forEach(item->{
				System.out.println("\t"+item.getProductName() + " " + item.getTiersAppIds());
			});
		});
		
		
		
		
		
	}
	


	public Mkm2WooCommerce() throws IOException {
		conversions = new ArrayList<>();
	}
	
	public List<Transaction> listTransaction(STATE statut) throws IOException
	{
		//return new OrderService().listOrders(ACTOR.buyer,statut,null).stream().map(this::toTransaction).collect(Collectors.toList());
		return new OrderService().listOrders(new File("C:\\Users\\Nicolas\\Google Drive\\Orders.Mkm.Paid.xml")).stream().map(this::toTransaction).collect(Collectors.toList());
	}
	
	
	
	
	private void saveProduct(Product p) throws IOException {
		
		Map<Object,Object> ret =((WooCommerceExport)MTG.getPlugin(WooCommerceExport.WOO_COMMERCE, MTGCardsExport.class)).getWooCommerce().create(EndpointBaseType.PRODUCTS.getValue(), toWooCommerceAttributs(p,null,78));
		logger.debug(ret);
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
	
	
	private int getWoocommerceId(String lang, int idMkm)
	{
		return conversions.stream().filter(p->(p.getLang().equalsIgnoreCase(lang) && p.getIdMkmProduct()==idMkm)).findFirst().orElse(new ConverterItem()).getIdWoocommerceProduct();
	}
	
	
	private Map<String, Object> toWooCommerceAttributs(Product product,String status, int idCategory)
	{
		Map<String, Object> productInfo = new HashMap<>();

		productInfo.put("name", product.getEnName());
		productInfo.put("type", "simple");
        productInfo.put("categories", WooCommerceTools.entryToJsonArray("id",String.valueOf(idCategory)));
        productInfo.put("status", status==null?"private":status);
        productInfo.put("images", WooCommerceTools.entryToJsonArray("src","https:"+product.getImage()));
		 
		return productInfo;
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
				c.setLastName(o.getBuyer().getAddress().getName().split(" ")[0]);
				c.setName(o.getBuyer().getAddress().getName().split(" ")[1]);
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
				item.getTiersAppIds().put(WooCommerceExport.WOO_COMMERCE, String.valueOf(getWoocommerceId(article.getLanguage().getLanguageName(),article.getIdProduct())));
			} catch (Exception e) {
				logger.error("Error getting Woocomerce id for Mkm ProductId="+article.getIdProduct() + " : " + e);
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
