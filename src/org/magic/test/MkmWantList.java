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

import org.magic.api.pricers.impl.MagicCardMarketPricer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class MkmWantList {

	HttpURLConnection connection;
	String authorizationProperty;
	
	 public static void main(String[] args) throws Exception {
		new MkmWantList();
	}
	 
	public MkmWantList() throws Exception {
	  String link = "https://www.mkmapi.eu/ws/v1.1/wantslist";
	  				authorizationProperty = new MagicCardMarketPricer().generateOAuthSignature(link);
	  				connection = (HttpURLConnection) new URL(link).openConnection();
			        connection.addRequestProperty("Authorization", authorizationProperty) ;
			        connection.connect();
      
	  int _lastCode = connection.getResponseCode();
     
	  Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
	  XPath xpath = XPathFactory.newInstance().newXPath();
	  XPathExpression expr = xpath.compile("//wantslist");
	  Object result = expr.evaluate(d, XPathConstants.NODESET);
	  NodeList nodes = (NodeList) result;
      for (int i = 0; i < nodes.getLength(); i++) {
    	  Element el = (Element) nodes.item(i);
    	  String id = (el.getElementsByTagName("idWantsList").item(0).getTextContent());
    	  String name= (el.getElementsByTagName("name").item(1).getTextContent());
      }
      
      List<Want> nodes2 = getProductsFromWantListID("1032260");
      
      for(Want w : nodes2)
      {
    	  System.out.println(getProduct(w.getIdProduct()));
      }
	}
	
	
	public List<Want> getProductsFromWantListID(String id) throws Exception
	{
		String url="https://www.mkmapi.eu/ws/v1.1/wantslist/"+id;
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = new MagicCardMarketPricer().generateOAuthSignature(url);
        connection.addRequestProperty("Authorization", authorizationProperty) ;
        connection.connect();
        int _lastCode = connection.getResponseCode();
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
       // prettyPrint(d);
        NodeList res = d.getElementsByTagName("want");
        List<Want> ret = new ArrayList<Want>();
        for (int i = 0; i < res.getLength(); i++) {
      	  Want w = new Want();
      	  w.setIdProduct(((Element)res.item(i)).getElementsByTagName("idProduct").item(0).getTextContent());
      	  w.setQte(((Element)res.item(i)).getElementsByTagName("count").item(0).getTextContent());
      	  ret.add(w);
        }
        return ret;
        
        
	}
	
	public String getProduct(String id) throws Exception
	{
		String url="https://www.mkmapi.eu/ws/v1.1/articles/"+id;
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = new MagicCardMarketPricer().generateOAuthSignature(url);
        connection.addRequestProperty("Authorization", authorizationProperty) ;
        connection.connect();
        int _lastCode = connection.getResponseCode();
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
     //   prettyPrint(d);
        return d.toString();
	}
	
	
	
	
	
	/*static void prettyPrint(Document doc) throws IOException
	{
		OutputFormat format = new OutputFormat(doc);
        format.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(System.out, format);
        serializer.serialize(doc);
	}*/
}





class Want
{
	String idProduct;
	String qte;
	
	
	public String getIdProduct() {
		return idProduct;
	}
	public void setIdProduct(String idProduct) {
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
		return getIdProduct() +" (" + getQte() +")";
	}
	
	
	
}
