package org.magic.api.shopping.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PriceMinisterShopper extends AbstractMagicShopper{

	static final Logger logger = LogManager.getLogger(PriceMinisterShopper.class.getName());

	public PriceMinisterShopper() {
		super();	
		
		if(!new File(confdir, getShopName()+".conf").exists()){
				props.put("LOGIN", "login");
				props.put("PASSWORD", "password");
				props.put("VERSION", "2015-07-05");
				props.put("CATEGORIE", "");
				props.put("URL", "https://ws.priceminister.com/listing_ssl_ws?action=listing&login=%LOGIN%&pwd=%PASSWORD%&version=%VERSION%&scope=%SCOPE%&nbproductsperpage=%NB_PRODUCT_PAGE%&kw=%KEYWORD%&nav=%CACTEGORIE%");
				props.put("SCOPE", "PRICING");
				props.put("NB_PRODUCT_PAGE", "20");
				props.put("USER_AGENT", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
				props.put("WEBSITE", "http://www.priceminister.com/");
				props.put("ENCODING", "UTF-8");
		save();
		}
		
	
		
	}
	
	
	public static void main(String[] args) {
		new PriceMinisterShopper().search("cartes magic");
	}
	
	@Override
	public List<ShopItem> search(String search) {
		List<ShopItem> list = new ArrayList<ShopItem>();
		try 
		{
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		String url = props.getProperty("URL")
					.replace("%LOGIN%", props.getProperty("LOGIN"))
					.replace("%PASSWORD%", props.getProperty("PASSWORD"))
					.replace("%VERSION%", props.getProperty("VERSION"))
					.replace("%SCOPE%", props.getProperty("SCOPE"))
					.replace("%NB_PRODUCT_PAGE%", props.getProperty("NB_PRODUCT_PAGE"))
					.replace("%CATEGORIE%",props.getProperty("CATEGORIE"))
					.replace("%KEYWORD%",URLEncoder.encode(search,props.getProperty("ENCODING")));
		
		 logger.debug("parsing item from " + url) ;
			
		 Document doc = dBuilder.parse(url);
		 doc.getDocumentElement().normalize();
		
				NodeList lst =  doc.getElementsByTagName("product");
				for (int temp = 0; temp < lst.getLength(); temp++) {
					 Node nNode = lst.item(temp);
					 if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			             Element e = (Element)nNode;
			             ShopItem it = new ShopItem();
			             it.setId(e.getElementsByTagName("productid").item(0).getTextContent());
						 it.setType(e.getElementsByTagName("topic").item(0).getTextContent());
						 it.setUrl(new URL(e.getElementsByTagName("url").item(0).getTextContent()));
						 
						 it.setImage(new URL(e.getElementsByTagName("image").item(0).getTextContent()));
						 it.setName(e.getElementsByTagName("headline").item(0).getTextContent());
						 it.setShopName(getShopName());
						 it.setPrice(Double.parseDouble(parsePrice((Element)e.getElementsByTagName("global").item(0))));
						 list.add(it);
						 
						
					}
					 
					
				}
		return list;
		 
		}
		catch(Exception e)
		{
			logger.error(e);
			e.printStackTrace();
		}
		 
		
		return list;
	}
	
	private String parsePrice(Element item) {
		try{
			String price = ((Element)item.getElementsByTagName("advertprice").item(0)).getElementsByTagName("amount").item(0).getTextContent();
			return price;
		}
		catch(Exception e)
		{
			logger.error(item);
			return "0.0";
		}
	}


	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    transformer.transform(new DOMSource(doc), 
	         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
	

	@Override
	public String getShopName() {
		return "PriceMinister";
	}

}
