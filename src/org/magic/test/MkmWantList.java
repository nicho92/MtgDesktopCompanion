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

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;



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
	  
	  List<WantList> list = new ArrayList<WantList>();
	  
      for (int i = 0; i < nodes.getLength(); i++) {
    	 
    	  WantList wants = new WantList();
    	  
    	  Element el = (Element) nodes.item(i);
    	  String id = (el.getElementsByTagName("idWantsList").item(0).getTextContent());
    	  String name= (el.getElementsByTagName("name").item(1).getTextContent());
    	  
    	  wants.setId(id);
    	  wants.setName(name);
    	  wants.setWants(getWantsFromID(id));
    	  list.add(wants);
      }
     
      System.out.println(list);
      
      
	}
	
	
	public List<Want> getWantsFromID(String id) throws Exception
	{
		String url="https://www.mkmapi.eu/ws/v1.1/wantslist/"+id;
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = new MagicCardMarketPricer().generateOAuthSignature(url);
        connection.addRequestProperty("Authorization", authorizationProperty) ;
        connection.connect();
        int _lastCode = connection.getResponseCode();
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
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
	
	public Product getProduct(String id) throws Exception
	{
		String url="https://www.mkmapi.eu/ws/v1.1/articles/"+id;
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = new MagicCardMarketPricer().generateOAuthSignature(url);
        connection.addRequestProperty("Authorization", authorizationProperty) ;
        connection.connect();
        int _lastCode = connection.getResponseCode();
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
        NodeList res = d.getElementsByTagName("article");
        prettyPrint(d);
        		
        Product p = new Product();
        for (int i = 0; i < res.getLength(); i++) {
        	
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



class WantList
{
	String id;
	String name;
	List<Want> wants;
	
	public WantList() {
		wants = new ArrayList<Want>();
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
	public List<Want> getWants() {
		return wants;
	}
	public void setWants(List<Want> wants) {
		this.wants = wants;
	}
	
	@Override
	public String toString() {
		return getId() + " " + getName() +" (" + wants.size() +")";
	}
	
	
}

class Product
{

        int idProduct;
        int idMetaproduct;
        int countReprints;
        List<String> name;
        String expansion;
        
        public Product() {
			name = new ArrayList<String>();
		}

		public int getIdProduct() {
			return idProduct;
		}

		public void setIdProduct(int idProduct) {
			this.idProduct = idProduct;
		}

		public int getIdMetaproduct() {
			return idMetaproduct;
		}

		public void setIdMetaproduct(int idMetaproduct) {
			this.idMetaproduct = idMetaproduct;
		}

		public int getCountReprints() {
			return countReprints;
		}

		public void setCountReprints(int countReprints) {
			this.countReprints = countReprints;
		}

		public List<String> getName() {
			return name;
		}

		public void setName(List<String> name) {
			this.name = name;
		}

		public String getExpansion() {
			return expansion;
		}

		public void setExpansion(String expansion) {
			this.expansion = expansion;
		}
        
        
        
}

class Article
{
	
}


class Want
{
	String idProduct;
	String qte;
	List<String> languages;
	
	public Want() {
		languages = new ArrayList<String>();
	}
	
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

