package org.magic.api.exports.impl;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OCTGNDeckExport extends AbstractCardExport{

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	public OCTGNDeckExport() {
		super();
		

		if(!new File(confdir,  getName()+".conf").exists()){
			props.put("MAGIC_GAME_ID", "a6c8d2e8-7cd8-11dd-8f94-e62b56d89593");
			props.put("SLEEVE_ID", "0");
			props.put("SHARED", "False");
			save();
		}
	}
	
	public String getFileExtension()
	{
		return ".o8d";
	}
	
	public void export(MagicDeck deck , File dest) throws IOException
	{
		StringBuffer temp = new StringBuffer();
		
		temp.append("<?xml version='1.0' encoding='utf-8' standalone='yes'?>");
		temp.append("<deck game='"+getProperty("MAGIC_GAME_ID")+"' sleeveid='"+getProperty("SLEEVE_ID")+"' >");
		temp.append("<section name='Main' shared='"+getProperty("SHARED")+"'>");
		for(MagicCard mc : deck.getMap().keySet())
		{
			temp.append("<card qty='").append(deck.getMap().get(mc)).append("' id='"+mc.getId()+"'>").append(mc.getName()).append("</card>");
		}
		temp.append("</section>");
		temp.append("<section name='Sideboard' shared='"+getProperty("SHARED")+"'>");
		for(MagicCard mc : deck.getMapSideBoard().keySet())
		{
			temp.append("<card qty='").append(deck.getMapSideBoard().get(mc)).append("' id='"+mc.getId()+"'>").append(mc.getName()).append("</card>");
		}
		temp.append("</section>");
		
		
		temp.append("<notes><![CDATA["+deck.getDescription()+"]]></notes>");
		
		temp.append("</deck>");
		
		FileWriter out = new FileWriter(dest);
		out.write(temp.toString());
		out.close();
		
		
	}
	
	public static void main(String[] args) throws Exception {
		OCTGNDeckExport exp = new OCTGNDeckExport();
		
		exp.importDeck(new File("D:\\T\ufffdl\ufffdchargements\\The Deck by Brian Weissman.o8d"));
	}
	
	
	@Override
	public MagicDeck importDeck(File f) throws Exception {
		MagicDeck deck = new MagicDeck();
		deck.setName(f.getName().substring(0,f.getName().indexOf(".")));
		
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileReader(f)));
		XPath xpath = XPathFactory.newInstance().newXPath();
	    
		XPathExpression expr = xpath.compile("//section[@name='Main']/card");
	    NodeList result = (NodeList)expr.evaluate(d, XPathConstants.NODESET);
	    for(int i=0;i<result.getLength();i++)
	    {
	    	Node it = result.item(i);
	    	String name = it.getTextContent();
	    	String qte = it.getAttributes().getNamedItem("qty").getNodeValue();
	    	MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name, null).get(0);
	    	
	    	deck.getMap().put(mc, Integer.parseInt(qte));
	    }
	    
	    expr = xpath.compile("//section[@name='Sideboard']/card");
	    result = (NodeList)expr.evaluate(d, XPathConstants.NODESET);
	    for(int i=0;i<result.getLength();i++)
	    {
	    	Node it = result.item(i);
	    	String name = it.getTextContent();
	    	String qte = it.getAttributes().getNamedItem("qty").getNodeValue();
	    	MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name, null).get(0);
	    	
	    	deck.getMapSideBoard().put(mc, Integer.parseInt(qte));
	    }
	    
	    
		return deck;
	}
	
	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {

		
		StringBuffer temp = new StringBuffer();
		
		
		FileWriter out = new FileWriter(f);
		out.write(temp.toString());
		out.close();
		
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(OCTGNDeckExport.class.getResource("/res/octgn.png"));
	}
	

	@Override
	public String getName() {
		return "OCTGN";
	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws Exception {
		MagicDeck d = new MagicDeck();
		d.setName(f.getName());
		
		for(MagicCardStock mcs : stock)
		{
			d.getMap().put(mcs.getMagicCard(), mcs.getQte());
		}
		
		export(d, f);
		
	}

	@Override
	public List<MagicCardStock> importStock(File f) throws Exception {
		return importFromDeck(importDeck(f));
	}
	
}
