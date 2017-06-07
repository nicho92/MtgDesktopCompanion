package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.pricers.impl.MagicCardMarketPricer;
import org.magic.services.MTGControler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


public class MKMOnlineWantListExport extends AbstractCardExport {

	HttpURLConnection connection;
	String authorizationProperty;
	MagicCardMarketPricer mkmPricer;
	static final Logger logger = LogManager.getLogger(MKMOnlineWantListExport.class.getName());

	public MKMOnlineWantListExport() throws Exception {
		super();
		mkmPricer = new MagicCardMarketPricer();

		if(!new File(confdir, getName()+".conf").exists()){
			props.put("QUALITY", "GD");
			props.put("DEFAULT_QTE", "1");
			props.put("LANGUAGES", "1,2");
			props.put("MAX_WANTLIST_SIZE", "150");
			save();
		}
	}

	public boolean removeWant(WantList li, List<Want> list) throws Exception
	{
		String url ="https://www.mkmapi.eu/ws/v1.1/wantslist/"+li.getId();
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url,"PUT");
		connection.addRequestProperty("Authorization", authorizationProperty) ;
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		connection.connect();
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

		StringBuffer temp = new StringBuffer();

		temp.append("<?xml version='1.0' encoding='UTF-8' ?>");
		temp.append("<request><action>remove</action>");

		for(Want w : list)
		{
			temp.append("<want>");
			temp.append("<idWant>"+w.getProduct().getIdProduct()+"</idWant>");
		}		    
		temp.append("</want>");

		out.write(temp.toString());
		out.close();

		return (connection.getResponseCode()>=200 || connection.getResponseCode()<300);


	}

	public boolean addWant(WantList li, List<Want> list) throws Exception
	{
		String url ="https://www.mkmapi.eu/ws/v1.1/wantslist/"+li.getId();
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url,"PUT");
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

			if(w.getWishPrice()!=null)
				temp.append("<wishPrice>"+w.getWishPrice()+"</wishPrice>");
			else
				temp.append("<wishPrice/>");
			temp.append("</product>");
		}		    

		temp.append("</request>");

		logger.debug("add wants to " + li.getName() + " : " + temp.toString());

		out.write(temp.toString());
		out.close();
		return (connection.getResponseCode()>=200 || connection.getResponseCode()<300);


	}

	public boolean removeWantList(WantList wl) throws Exception
	{
		String temp = "<?xml version='1.0' encoding='UTF-8' ?><request><wantslist><idGame>1</idGame><name>"+wl.getId()+"</name></wantslist></request>";
		String url ="https://www.mkmapi.eu/ws/v1.1/wantslist/"+wl.getId();
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url,"DELETE");
		connection.addRequestProperty("Authorization", authorizationProperty) ;
		connection.setDoOutput(true);
		connection.setRequestMethod("DELETE");
		connection.setRequestProperty( "charset", "utf-8");

		connection.connect();
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		out.write(temp.toString());
		out.close();
		return (connection.getResponseCode()>=200 || connection.getResponseCode()<300);

	}

	public WantList addWantList(String name) throws Exception
	{
		String temp = "<?xml version='1.0' encoding='UTF-8' ?><request><wantslist><idGame>1</idGame><name>"+name+"</name></wantslist></request>";
		String url ="https://www.mkmapi.eu/ws/v1.1/wantslist";
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url,"POST");
		connection.addRequestProperty("Authorization", authorizationProperty) ;
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty( "charset", "utf-8");

		connection.connect();
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		out.write(temp.toString());
		out.close();

		if(connection.getResponseCode()<200 || connection.getResponseCode()>=300)
			return null;


		InputStream in = connection.getInputStream();
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(in)));
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//wantslist[contains(name,'"+name+"')]");

		Object result = expr.evaluate(d, XPathConstants.NODE);
		Element nodes = (Element) result;
		String id = (nodes.getElementsByTagName("idWantsList").item(0).getTextContent());
		return new WantList(id,name);
	}

	public List<WantList> getWantList() throws Exception
	{
		String url="https://www.mkmapi.eu/ws/v1.1/wantslist";
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url,"GET");
		connection.addRequestProperty("Authorization", authorizationProperty) ;

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
		authorizationProperty = mkmPricer.generateOAuthSignature(url,"GET");
		connection.addRequestProperty("Authorization", authorizationProperty) ;
		connection.connect();
		int _lastCode = connection.getResponseCode();
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));

		NodeList res = d.getElementsByTagName("want");
		List<Want> ret = new ArrayList<Want>();
		for (int i = 0; i < res.getLength(); i++) {
			Want w = new Want();

			if(!((Element)res.item(i)).getElementsByTagName("type").item(0).getTextContent().equals("metaproduct"))
			{
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
		/*//TODO : correction
		try{
			if(mc.getEditions().get(0).getMkm_id()>0)
				return getProductById(String.valueOf(mc.getEditions().get(0).getMkm_id()));
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		*/
		String KEYWORD=URLEncoder.encode(mc.getName(),"UTF-8");
		String url ="https://www.mkmapi.eu/ws/v1.1/products/"+KEYWORD+"/1/1/true";
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url,"GET");
		connection.addRequestProperty("Authorization", authorizationProperty) ;
		connection.connect();
		logger.debug("Parsing :" + url);
		int _lastCode = connection.getResponseCode();
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
		return parseProductDocument(d,mc.getEditions().get(0));
	}

	private Product parseProductDocument(Document d,MagicEdition ed) throws Exception
	{
		//prettyPrint(d);
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = null;

		if(ed!=null)
		{
			if(ed.getMkm_name()!=null)
			{
				expr=xpath.compile("//product[contains(expansion,'"+ed.getMkm_name()+"')]");
			}
			else
			{
				expr=xpath.compile("//product[contains(expansion,'"+ed.getSet()+"')]");
			}
		}
		else
			expr=xpath.compile("//product");

		
		Element n = (Element)expr.evaluate(d, XPathConstants.NODE);
		
		
		
		Product p = new Product();

		try{
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
		}catch(Exception e)
		{
			return null;
		}
	}

	private void getSet() throws Exception
	{
		String url="https://www.mkmapi.eu/ws/v1.1/expansion/1";
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url,"GET");
		connection.addRequestProperty("Authorization", authorizationProperty) ;
		connection.connect();
		int _lastCode = connection.getResponseCode();
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
		// prettyPrint(d);
	}

	private Product getProductById(String id) throws Exception
	{
		String url="https://www.mkmapi.eu/ws/v1.1/product/"+id;
		connection = (HttpURLConnection) new URL(url).openConnection();
		authorizationProperty = mkmPricer.generateOAuthSignature(url,"GET");
		connection.addRequestProperty("Authorization", authorizationProperty) ;
		connection.connect();
		int _lastCode = connection.getResponseCode();
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream())));
		// prettyPrint(d);
		return parseProductDocument(d,null);
	}
	
	static void prettyPrint(Document doc) throws IOException
	{
		OutputFormat format = new OutputFormat(doc);
        format.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(System.out, format);
        serializer.serialize(doc);
	}

	@Override
	public MagicDeck importDeck(File f) throws Exception {


		MagicDeck d = new MagicDeck();
		d.setName(f.getName());
		WantList list = null;
		for(WantList l  : getWantList())
			if(l.getName().equalsIgnoreCase(d.getName()))
				list=l;

		if(list==null)
			throw new Exception(getName() + " can't import deck for " + f.getName());

		for(Want w : getWants(list))
		{
			
			d.getMap().put(MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", w.getProduct().getName(), null).get(0), w.getQte());
		}

		return d;
	}

	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		MagicDeck d = new MagicDeck();
		for(MagicCard mc : cards)
			d.getMap().put(mc, Integer.parseInt(props.getProperty("DEFAULT_QTE")));

		d.setName(f.getName());

		export(d, f);
	}

	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws Exception {
	
		List<Want> wants = new ArrayList<Want>();
		for(MagicCard mc : deck.getMap().keySet())
		{
			Product p = getProductByCard(mc);
			if(p!=null)
			{ 
				p.setExpension(mc.getEditions().get(0).getSet());
				Want w = new Want();
				w.setProduct(p);
				w.setQte(deck.getMap().get(mc));
				w.setFoil(false);
				w.setMinCondition(props.getProperty("QUALITY"));
				w.setAltered(false);
				w.setSigned(false);
				for(String s : props.getProperty("LANGUAGES").split(","))
					w.getLanguages().add(s);

				wants.add(w);
			}
		}
		
		int max = Integer.parseInt(props.getProperty("MAX_WANTLIST_SIZE"));
		if(wants.size()<=max)
		{
			WantList l= addWantList(deck.getName());
			logger.debug("Create " + l + " list with " + wants.size() + " items");
			addWant(l,wants);	
		}
		else //si max , alors on découpe par tranche
		{
			
			List<List<Want>> decoupes = ListUtils.partition(wants, max);
			
			for(int i=0;i<decoupes.size();i++)
			{
				WantList wl= addWantList(deck.getName()+"-"+(i+1));
				logger.debug("Create " + wl + " list with " + decoupes.get(i).size() + " items");
				addWant(wl, decoupes.get(i));
			}
			
		}
	}

	@Override
	public String getName() {
		return "MKM Online WantList";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MKMFileWantListExport.class.getResource("/res/mkm.png"));
	}




	public class Want
	{
		Product product;
		Double wishPrice;
		List<String> languages;
		String minCondition;
		boolean foil;
		boolean signed;
		boolean playset;
		boolean altered;


		public Want() {
			languages=new ArrayList<String>();
		}

		public Double getWishPrice() {
			return wishPrice;
		}
		public void setWishPrice(Double wishPrice) {
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

	public class WantList
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
			return getName();
		}


	}

	public class Product
	{
		String idProduct;
		String name;
		List<MagicCardNames> names;
		URL webSite;
		String expension;
		String rarity;
		String number;
		int idSet;

		public Product() {
			names = new ArrayList<MagicCardNames>();
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
		public List<MagicCardNames> getNames() {
			return names;
		}
		public void setNames(List<MagicCardNames> names) {
			this.names = names;
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
		public int getIdSet() {
			return idSet;
		}
		public void setIdSet(int idSet) {
			this.idSet = idSet;
		}
		public String toString() {
			return getName();
		}



	}


}

