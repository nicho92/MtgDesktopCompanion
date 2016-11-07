package org.magic.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.magic.api.beans.MagicCardNames;
import org.magic.api.pricers.impl.MagicCardMarketPricer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


public class MkmWantList {

	HttpURLConnection connection;
	String authorizationProperty;
	MagicCardMarketPricer mkmPricer;
	
	
	 public static void main(String[] args) throws Exception {
		new MkmWantList();
	}
	 
	public MkmWantList() throws Exception {
		
	  mkmPricer = new MagicCardMarketPricer();
	  String link = "https://www.mkmapi.eu/ws/v1.1/wantslist";
	  				authorizationProperty = mkmPricer.generateOAuthSignature(link);
	  				connection = (HttpURLConnection) new URL(link).openConnection();
			        connection.addRequestProperty("Authorization", authorizationProperty) ;
			        connection.connect();
      
			        
	  List<WantList> list = getWantList();
	  
      List<Want> nodes2 = getWants(list.get(0));
      
      System.out.println("==========="+list.get(2));
      for(Want w : nodes2)
      {
    	  System.out.println(w.getProduct());
      }
	}
	
	public List<WantList> getWantList() throws Exception
	{
		  int _lastCode = connection.getResponseCode();
		  Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
		  XPath xpath = XPathFactory.newInstance().newXPath();
		  XPathExpression expr = xpath.compile("//wantslist");
		  Object result = expr.evaluate(d, XPathConstants.NODESET);
		  NodeList nodes = (NodeList) result;
		  List<WantList> list = new ArrayList<WantList>();
		  for (int i = 0; i < nodes.getLength(); i++) {
	    	  Element el = (Element) nodes.item(i);
	    	  String id = (el.getElementsByTagName("idWantsList").item(0).getTextContent());
	    	  String name= (el.getElementsByTagName("name").item(1).getTextContent());
	    	  list.add(new WantList(id, name));
	      }
	      return list;
	}
	
	public List<Want> getWants(WantList wl) throws Exception
	{
		String url="https://www.mkmapi.eu/ws/v1.1/wantslist/"+wl.getId();
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url);
        connection.addRequestProperty("Authorization", authorizationProperty) ;
        connection.connect();
        int _lastCode = connection.getResponseCode();
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
        NodeList res = d.getElementsByTagName("want");
        List<Want> ret = new ArrayList<Want>();
        for (int i = 0; i < res.getLength(); i++) {
      	  Want w = new Want();
      	  w.setProduct(getProduct(((Element)res.item(i)).getElementsByTagName("idProduct").item(0).getTextContent()));
      	  w.setQte(((Element)res.item(i)).getElementsByTagName("count").item(0).getTextContent());
      	  ret.add(w);
        }
        return ret;
        
        
	}
	
	public Product getProduct(String id) throws Exception
	{
		String url="https://www.mkmapi.eu/ws/v1.1/product/"+id;
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url);
        connection.addRequestProperty("Authorization", authorizationProperty) ;
        connection.connect();
        int _lastCode = connection.getResponseCode();
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
        
        XPath xpath = XPathFactory.newInstance().newXPath();
	    XPathExpression expr = xpath.compile("//product");
	    Element n = (Element)expr.evaluate(d, XPathConstants.NODE);
        
		Product p = new Product();
		
		p.setIdProduct(n.getElementsByTagName("idProduct").item(0).getTextContent());
		p.setNumber(n.getElementsByTagName("number").item(0).getTextContent());
		p.setRarity(n.getElementsByTagName("rarity").item(0).getTextContent());
		p.setExpension(n.getElementsByTagName("expansion").item(0).getTextContent());
		p.setWebSite(new URL("https://www.magiccardmarket.eu"+n.getElementsByTagName("website").item(0).getTextContent()));
		
		NodeList names = n.getElementsByTagName("name");
		for(int i =0;i<names.getLength();i++)
		{
			Element e = (Element)names.item(i);
			MagicCardNames aName = new MagicCardNames();
					aName.setLanguage(e.getElementsByTagName("languageName").item(0).getTextContent());
					aName.setName(e.getElementsByTagName("productName").item(0).getTextContent());
					p.getNames().add(aName);
					
					if(aName.getLanguage().startsWith("Engli"))
						p.setName(aName.getName());
		}
		return p;
	}
	
	static void prettyPrint(Document doc) throws IOException
	{
		OutputFormat format = new OutputFormat(doc);
        format.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(System.out, format);
        serializer.serialize(doc);
	}
}


class Product
{
	String idProduct;
	String name;
	List<MagicCardNames> names;
	URL webSite;
	String expension;
	String rarity;
	String number;
	
	@Override
	public String toString() {
		return getName();
	}
	
	public List<MagicCardNames> getNames() {
		return names;
	}

	public void setNames(List<MagicCardNames> names) {
		this.names = names;
	}

	public Product() {
		names=new ArrayList<MagicCardNames>();
	}
	
	public String getIdProduct() {
		return idProduct;
	}
	public void setIdProduct(String idProduct) {
		this.idProduct = idProduct;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public URL getWebSite() {
		return webSite;
	}
	public void setWebSite(URL webSite) {
		this.webSite = webSite;
	}
	public String getExpension() {
		return expension;
	}
	public void setExpension(String expension) {
		this.expension = expension;
	}
	public String getRarity() {
		return rarity;
	}
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	
	
}


class WantList
{
	String id;
	String name;
	
	public WantList() { }
	
	public WantList(String id,String name)
	{
		setId(id);
		setName(name);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	
}

class Want
{
	Product idProduct;
	String qte;

	public Product getProduct() {
		return idProduct;
	}
	public void setProduct(Product idProduct) {
		this.idProduct = idProduct;
	}
	public String getQte() {
		return qte;
	}
	public void setQte(String qte) {
		this.qte = qte;
	}
	
	@Override
	public String toString() {
		return getProduct() +" (" + getQte() +")";
	}
}
