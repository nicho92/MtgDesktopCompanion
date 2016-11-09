package org.magic.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.pricers.impl.MagicCardMarketPricer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
      List<Want> nodes2 = getWants(list.get(2));
     
      System.out.println("==========="+list.get(2));
      for(Want w : nodes2)
      {
    	  System.out.println(w.getProduct() + " " + w.getLanguages());
      }
	}
	
	public void addWant(WantList li, List<Want> list) throws Exception
	{
		String url ="https://www.mkmapi.eu/ws/v1.1/wantslist/"+li.getId();
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url);
		connection.addRequestProperty("Authorization", authorizationProperty) ;
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		connection.connect();
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		
		StringBuffer temp = new StringBuffer();
		
		temp.append("<?xml version='1.0' encoding='UTF-8' ?>");
		temp.append("<request><action>add</action>");
		    
		for(Want w : list)
		{
			temp.append("<product>");
			temp.append("<idProduct>"+w.getProduct().getIdProduct()+"</idProduct>");
			temp.append("<count>"+w.getQte()+"</count>");
			
			for(String s : w.getLanguages())
				temp.append("<idLanguage>"+s+"</idLanguage>");
			
			temp.append("<minCondition>"+w.getMinCondition()+"</minCondition>");
			temp.append("<wishPrice>"+w.getWishPrice()+"</wishPrice>");
			temp.append("</product>");
		}		    
		    
		temp.append("</request>");

		
		
		out.write(temp.toString());
		out.close();
        
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
	    	  int qte = Integer.parseInt(el.getElementsByTagName("itemCount").item(0).getTextContent());
	    	  list.add(new WantList(id, name,qte));
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
        
        prettyPrint(d);
        
        NodeList res = d.getElementsByTagName("want");
        List<Want> ret = new ArrayList<Want>();
        for (int i = 0; i < res.getLength(); i++) {
      	  Want w = new Want();
	      	  w.setProduct(getProductById(((Element)res.item(i)).getElementsByTagName("idProduct").item(0).getTextContent()));
	      	  w.setQte(Integer.parseInt(((Element)res.item(i)).getElementsByTagName("count").item(0).getTextContent()));
	      	  w.setFoil((parseBool(((Element)res.item(i)).getElementsByTagName("isFoil").item(0).getTextContent())));
	      	  w.setSigned((parseBool(((Element)res.item(i)).getElementsByTagName("isSigned").item(0).getTextContent())));
	      	  w.setPlayset((parseBool(((Element)res.item(i)).getElementsByTagName("isPlayset").item(0).getTextContent())));
	      	  w.setAltered((parseBool(((Element)res.item(i)).getElementsByTagName("isAltered").item(0).getTextContent())));
	      	  w.setMinCondition(((Element)res.item(i)).getElementsByTagName("minCondition").item(0).getTextContent());
	      	NodeList names = ((Element)res.item(i)).getElementsByTagName("langName");
			for(int j =0;j<names.getLength();j++)
			{
				Element e = (Element)names.item(j);
				w.getLanguages().add(e.getTextContent());
			}
      	  
      	  ret.add(w);
        }
        wl.setCardCount(res.getLength());
        return ret;
	}
	
	private boolean parseBool(String b) {
		if(b.equalsIgnoreCase("Y"))
			return true;
		
		return false;
	}

	public Product getProductByCard(MagicCard mc) throws Exception
	{
		String url ="https://www.mkmapi.eu/ws/v1.1/products/"+mc.getName()+"/1/1/false";
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url);
        connection.addRequestProperty("Authorization", authorizationProperty) ;
        connection.connect();
        int _lastCode = connection.getResponseCode();
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
        return parseProductDocument(d);
	}
	
	private Product parseProductDocument(Document d) throws XPathExpressionException, MalformedURLException, DOMException
	{
		    XPath xpath = XPathFactory.newInstance().newXPath();
		    XPathExpression expr = xpath.compile("//product");
		    Element n = (Element)expr.evaluate(d, XPathConstants.NODE);
	        
			Product p = new Product();
					p.setIdProduct(n.getElementsByTagName("idProduct").item(0).getTextContent());
					p.setNumber(n.getElementsByTagName("number").item(0).getTextContent());
					p.setRarity(n.getElementsByTagName("rarity").item(0).getTextContent());
					p.setExpension(n.getElementsByTagName("expansion").item(0).getTextContent());
					p.setWebSite(new URL("https://www.magiccardmarket.eu"+n.getElementsByTagName("website").item(0).getTextContent()));
					p.setIdSet(Integer.parseInt(n.getElementsByTagName("expIcon").item(0).getTextContent()));
			NodeList names = n.getElementsByTagName("name");
			for(int i =0;i<names.getLength();i++)
			{
				Element e = (Element)names.item(i);
				MagicCardNames aName = new MagicCardNames();
							   aName.setLanguage(e.getElementsByTagName("languageName").item(0).getTextContent());
							   aName.setName(e.getElementsByTagName("productName").item(0).getTextContent());
					p.getNames().add(aName);
						
					if(aName.getLanguage().toLowerCase().startsWith("english"))
						p.setName(aName.getName());
			}
			return p;
	}
	
	public Product getProductById(String id) throws Exception
	{
		String url="https://www.mkmapi.eu/ws/v1.1/product/"+id;
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url);
        connection.addRequestProperty("Authorization", authorizationProperty) ;
        connection.connect();
        int _lastCode = connection.getResponseCode();
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
        return parseProductDocument(d);
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
	int idSet;
	
	
	public int getIdSet() {
		return idSet;
	}

	public void setIdSet(int idSet) {
		this.idSet = idSet;
	}

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
	int qte;
	
	public WantList() { }
	
	public void setCardCount(int length) {
		this.qte=length;
	}
	
	public int getCardCount()
	{
		return qte;
	}

	public WantList(String id,String name,int qte)
	{
		setId(id);
		setName(name);
		setCardCount(qte);;
	}
	
	public WantList(String id,String name)
	{
		setId(id);
		setName(name);
		qte=0;
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
		return getName() +" ("+getCardCount()+")";
	}
	
	
}

class Want
{
	Product product;
	double wishPrice;
	List<String> languages;
	String minCondition;
	boolean foil;
	boolean signed;
	boolean playset;
	boolean altered;
	
	
	public Want() {
		languages=new ArrayList<String>();
	}
	
	public double getWishPrice() {
		return wishPrice;
	}
	public void setWishPrice(double wishPrice) {
		this.wishPrice = wishPrice;
	}
	public List<String> getLanguages() {
		return languages;
	}
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	public String getMinCondition() {
		return minCondition;
	}
	public void setMinCondition(String minCondition) {
		this.minCondition = minCondition;
	}
	public boolean isFoil() {
		return foil;
	}
	public void setFoil(boolean foil) {
		this.foil = foil;
	}
	public boolean isSigned() {
		return signed;
	}
	public void setSigned(boolean signed) {
		this.signed = signed;
	}
	public boolean isPlayset() {
		return playset;
	}
	public void setPlayset(boolean playset) {
		this.playset = playset;
	}
	public boolean isAltered() {
		return altered;
	}
	public void setAltered(boolean altered) {
		this.altered = altered;
	}

	int qte;

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product idProduct) {
		this.product = idProduct;
	}
	public int getQte() {
		return qte;
	}
	public void setQte(int qte) {
		this.qte = qte;
	}
	
	@Override
	public String toString() {
		return getProduct() +" (" + getQte() +")";
	}
}
