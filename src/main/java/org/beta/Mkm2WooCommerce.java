package org.beta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.api.mkm.modele.LightArticle;
import org.api.mkm.modele.Product;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.externalshop.impl.MkmExternalShop;
import org.magic.api.externalshop.impl.WooCommerceExternalShop;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.FileTools;

public class Mkm2WooCommerce {
	
	private static final String SEPARATOR = ";";
	private static Logger logger = MTGLogger.getLogger(Mkm2WooCommerce.class);
    private List<ConverterItem>  conversions;
	private MTGExternalShop mkm;
	private MTGExternalShop woo;
	private File conversionFile;
	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance();
		
		Mkm2WooCommerce mkWoo= new Mkm2WooCommerce();
	
		mkWoo.loadConversions(new File("C:\\Users\\Pihen\\Downloads\\conversions.csv"));
		
		mkWoo.testTransaction();
		mkWoo.testProduct();
	}


	public Mkm2WooCommerce() throws IOException {
		conversions = new ArrayList<>();
		mkm = new MkmExternalShop();
		woo = new WooCommerceExternalShop();
	}
	
	private void testProduct()
	{
		try {
			Product p =mkm.listProducts("Adventures in the Forgotten Realms Gift Fat Pack Bundle").get(0);
			exportProduct(p);
			
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	private void testTransaction()
	{
		
		
		try {
			Transaction t = mkm.listTransaction().get(0);
			woo.createTransaction(t);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		
		
	}
	
	
	public List<Transaction> listTransaction() throws IOException
	{
		List<Transaction> list = mkm.listTransaction();
		
		list.forEach(t->
			t.getItems().forEach(item->
				item.getTiersAppIds().put(woo.getName(),String.valueOf(getWoocommerceId(item.getLanguage(),Integer.parseInt(item.getTiersAppIds().get(mkm.getName())))))
			)
		);
		return list;
	}
	
	
	private int exportProduct(Product p) throws IOException {
		int ret = woo.createProduct(p);
		updateConversions(new ConverterItem(p.getEnName(), p.getIdProduct(), ret, "English"));
		return ret;
	}
	
	

	private void updateConversions(ConverterItem c) {
		
		
		String s = c.getName()+SEPARATOR+c.getLang()+SEPARATOR+c.getIdWoocommerceProduct()+SEPARATOR+c.getIdMkmProduct();
		try {
			FileTools.appendLine(conversionFile, s);
		} catch (IOException e) {
			logger.error(e);
		}
		conversions.add(c);
	}


	public void loadConversions(File f) throws IOException
	{
			this.conversionFile = f;
			conversions.clear();
			var list = Files.readAllLines(f.toPath());
			list.remove(0); // remove title
			list.forEach(s->{
				
				var arr = s.split(SEPARATOR);
				
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
