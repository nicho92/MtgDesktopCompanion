package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGDesktopCompanionControler;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CocatriceDeckExport extends AbstractCardExport{

	
	public CocatriceDeckExport() {
		super();
		

		if(!new File(confdir,  getName()+".conf").exists()){
			props.put("VERSION", "1.0");
			props.put("DEFAULT_PRICE", "0");
			save();
		}
	}
	
	public String getFileExtension()
	{
		return ".cod";
	}
	
	public void export(MagicDeck deck , File dest) throws IOException
	{
		StringBuffer temp = new StringBuffer();
		
		temp.append("<?xml version='1.0' encoding='UTF-8'?>");
		temp.append("<cockatrice_deck version='"+getProperty("VERSION")+"'>");
		temp.append("<deckname>").append(deck.getName()).append("</deckname>");
		temp.append("<comments>").append(deck.getDescription()).append("</comments>");
		temp.append("<zone name='main'>");
		for(MagicCard mc : deck.getMap().keySet())
		{
			temp.append("<card number='").append(deck.getMap().get(mc)).append("' price='"+getProperty("DEFAULT_PRICE")+"' name=\"").append(mc.getName()).append("\"/>");
		}
		temp.append("</zone>");
		temp.append("<zone name='side'>");
		for(MagicCard mc : deck.getMapSideBoard().keySet())
		{
			temp.append("<card number='").append(deck.getMapSideBoard().get(mc)).append("' price='"+getProperty("DEFAULT_PRICE")+"' name=\"").append(mc.getName()).append("\"/>");
		}
		temp.append("</zone>");
		
		
		
		temp.append("</cockatrice_deck>");
		
		FileWriter out = new FileWriter(dest);
		out.write(temp.toString());
		out.close();
		
		
	}
	
	@Override
	public MagicDeck importDeck(File f) throws Exception {
		MagicDeck deck = new MagicDeck();
		
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileReader(f)));
		XPath xpath = XPathFactory.newInstance().newXPath();
	    
		XPathExpression expr = xpath.compile("//cockatrice_deck/deckname");
	    NodeList result = (NodeList)expr.evaluate(d, XPathConstants.NODESET);
	    
	    deck.setName(result.item(0).getTextContent());
	    
	    expr = xpath.compile("//cockatrice_deck/comments");
	    result = (NodeList)expr.evaluate(d, XPathConstants.NODESET);
	    deck.setDescription(result.item(0).getTextContent());
	    	    
	    expr = xpath.compile("//cockatrice_deck/zone[contains(@name,'main')]/card");
	    result = ((NodeList)expr.evaluate(d, XPathConstants.NODESET));
	    for(int i = 0;i<result.getLength();i++)
		{
			String name = result.item(i).getAttributes().getNamedItem("name").getTextContent();
			Integer qte = Integer.parseInt(result.item(i).getAttributes().getNamedItem("number").getTextContent());
			deck.getMap().put(MTGDesktopCompanionControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name, null).get(0), qte);
		}
	    expr = xpath.compile("//cockatrice_deck/zone[contains(@name,'side')]/card");
		result = ((NodeList)expr.evaluate(d, XPathConstants.NODESET));
		for(int i = 0;i<result.getLength();i++)
		{
			String name = result.item(i).getAttributes().getNamedItem("name").getTextContent();
			Integer qte = Integer.parseInt(result.item(i).getAttributes().getNamedItem("number").getTextContent());
			deck.getMapSideBoard().put(MTGDesktopCompanionControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name, null).get(0), qte);
		}
		return deck;
	}
	
	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {

		
		StringBuffer temp = new StringBuffer();
		
		temp.append("<?xml version='1.0' encoding='UTF-8'?>");
		temp.append("<cockatrice_deck version='"+getProperty("VERSION")+"'>");
		temp.append("<deckname>search</deckname>");
		temp.append("<comments>Result of MTG Companion search</comments>");
		temp.append("<zone name='main'>");
		for(MagicCard mc : cards)
		{
			temp.append("<card number='1' price='"+getProperty("DEFAULT_PRICE")+"' name=\"").append(mc.getName()).append("\"/>");
		}
		temp.append("</zone>");
		
		temp.append("</cockatrice_deck>");
		
		FileWriter out = new FileWriter(f);
		out.write(temp.toString());
		out.close();
		
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(CocatriceDeckExport.class.getResource("/res/cockatrice_logo.png"));
	}
	

	@Override
	public String getName() {
		return "Cockatrice";
	}
	
}
