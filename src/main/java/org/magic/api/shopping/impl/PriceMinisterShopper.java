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

import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PriceMinisterShopper extends AbstractMagicShopper{

	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	
	public PriceMinisterShopper() {
		super();	
		
		if(!new File(confdir, getName()+".conf").exists()){
				props.put("LOGIN", "login");
				props.put("PASS", "PASS");
				props.put("VERSION", "2015-07-05");
				props.put("CATEGORIE", "");
				props.put("URL", "https://ws.priceminister.com/listing_ssl_ws?action=listing");
				props.put("SCOPE", "PRICING");
				props.put("NB_PRODUCT_PAGE", "20");
				props.put("USER_AGENT", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
				props.put("WEBSITE", "http://www.priceminister.com/");
				props.put("ENCODING", "UTF-8");
		save();
		}
		
	
		
	}
	
	
	@Override
	public List<ShopItem> search(String search) {
		List<ShopItem> list = new ArrayList<>();
		try 
		{
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		StringBuilder url = new StringBuilder();
		
			url.append(props.getProperty("URL"))
			   .append("&login=").append(props.getProperty("LOGIN"))
			   .append("&pwd=").append(props.getProperty("PASS"))
			   .append("&version=").append(props.getProperty("VERSION"))
			   .append("&scope=").append(props.getProperty("SCOPE"))
			   .append("&nbproductsperpage=").append(props.getProperty("NB_PRODUCT_PAGE"))
			   .append("&kw=").append(URLEncoder.encode(search,props.getProperty("ENCODING")))
			   .append("&nav=").append(props.getProperty("CATEGORIE"));
		
		 logger.debug(getName() + " parsing item from " + url) ;
			
		 Document doc = dBuilder.parse(url.toString());
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
						 it.setShopName(getName());
						 it.setPrice(Double.parseDouble(parsePrice((Element)e.getElementsByTagName("global").item(0))));
						 list.add(it);
						 
						
					}
					 
					
				}
		logger.debug(getName() +" found " + list.size() +" items") ;
							
		return list;
		 
		}
		catch(Exception e)
		{
			logger.error("error in search " + search,e);
		}
		 
		
		return list;
	}
	
	private String parsePrice(Element item) {
		try{
			return ((Element)item.getElementsByTagName("advertprice").item(0)).getElementsByTagName("amount").item(0).getTextContent();
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
	public String getName() {
		return "PriceMinister";
	}

}
