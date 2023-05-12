package org.magic.api.exports.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.XMLTools;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class MTGOXMLDeckExport extends AbstractCardExport {


	@Override
	public String getName() {
		return "MTGO XML";
	}

	@Override
	public String getFileExtension() {
		return ".dek";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();
		temp.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(System.lineSeparator())
		        .append("<Deck xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">").append(System.lineSeparator())
		        .append("<NetDeckID>0</NetDeckID>").append(System.lineSeparator())
				.append("<PreconstructedDeckID>0</PreconstructedDeckID>").append(System.lineSeparator());
		
		deck.getMain().entrySet().forEach(e->{
			temp.append("<Cards CatID=\"1\" Quantity=\"").append(e.getValue()).append("\" Sideboard=\"false\" Name=\"").append(e.getKey().getName()).append("\" Annotation=\"0\"/>").append(System.lineSeparator());
		});
		
		deck.getSideBoard().entrySet().forEach(e->{
			temp.append("<Cards CatID=\"1\" Quantity=\"").append(e.getValue()).append(" Sideboard=\"true\" Name=\"").append(e.getKey().getName()).append("\" Annotation=\"0\"/>").append(System.lineSeparator());
		});
		temp.append("</Deck>");
		
		
		FileTools.saveFile(dest, temp.toString());
	}
	
	
	public static void main(String[] args) throws IOException {
		new MTGOXMLDeckExport().importDeckFromFile(new File("D:\\Desktop\\Deck_-_Goblins.dek"));
	}
	

	@Override
	public MagicDeck importDeck(String f, String deckName) throws IOException {
		var deck = new MagicDeck();
			deck.setName(deckName);
			
			try {
				var b = XMLTools.createSecureXMLDocumentBuilder();
				var d = b.parse(new ByteArrayInputStream(f.getBytes()));
				var c = XMLTools.parseNodes(d, "//Cards");
				
				for(int i = 0; i < c.getLength();i++)
				{
					var obj = c.item(i);
					
					read(obj,deck);
					
				}
			} catch (SAXException| ParserConfigurationException | XPathExpressionException e) {
				throw new IOException(e);
			}
			
			return deck;
	}
	
	private void read(Node obj, MagicDeck deck) throws DOMException, IOException {
		try {
			
			var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(obj.getAttributes().getNamedItem("Name").getTextContent(), null, true).get(0);
			var qty = Integer.parseInt(obj.getAttributes().getNamedItem("Quantity").getTextContent());
			
			
			if(obj.getAttributes().getNamedItem("Sideboard").getTextContent().equalsIgnoreCase("false"))
			{
				deck.getMain().put(mc, qty);
			}
			else
			{
				deck.getSideBoard().put(mc, qty);
			}
			
			
		}
		catch(IndexOutOfBoundsException ioobe)
		{
			logger.error("no card found {}",obj.getAttributes().getNamedItem("Name"));
		}
		
		
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGOXMLDeckExport.class.getResource("/icons/plugins/mtgo.png"));
	}

}
