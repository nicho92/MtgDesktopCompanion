package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.FileTools;
import org.magic.tools.XMLTools;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CocatriceDeckExport extends AbstractCardExport {

	private static final String DEFAULT_PRICE = "DEFAULT_PRICE";
	private static final String VERSION = "VERSION";

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	public String getFileExtension() {
		return ".cod";
	}

	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();
		String endZoneTag = "</zone>";

		temp.append("<?xml version='1.0' encoding='").append(MTGConstants.DEFAULT_ENCODING).append("'?>");
		temp.append("<cockatrice_deck version='" + getString(VERSION) + "'>");
		temp.append("<deckname>").append(deck.getName()).append("</deckname>");
		temp.append("<comments>").append(deck.getDescription()).append("</comments>");
		temp.append("<zone name='main'>");
		for (MagicCard mc : deck.getMain().keySet()) {
			temp.append("<card number='").append(deck.getMain().get(mc))
					.append("' price='" + getString(DEFAULT_PRICE) + "' name=\"").append(mc.getName()).append("\"/>");
			notify(mc);
		}
		temp.append(endZoneTag);
		temp.append("<zone name='side'>");
		for (MagicCard mc : deck.getSideBoard().keySet()) {
			temp.append("<card number='").append(deck.getSideBoard().get(mc))
					.append("' price='" + getString(DEFAULT_PRICE) + "' name=\"").append(mc.getName()).append("\"/>");
			notify(mc);
		}
		temp.append(endZoneTag);
		temp.append("</cockatrice_deck>");

		FileTools.saveFile(dest, temp.toString());

	}
	
	
	
	

	@Override
	public MagicDeck importDeck(String f,String n) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(n);
		try {
			Document d = XMLTools.createSecureXMLFactory().newDocumentBuilder().parse(new InputSource(new StringReader(f)));
			XPath xpath = XPathFactory.newInstance().newXPath();

			XPathExpression expr = xpath.compile("//cockatrice_deck/deckname");
			NodeList result = (NodeList) expr.evaluate(d, XPathConstants.NODESET);

			deck.setName(result.item(0).getTextContent());

			expr = xpath.compile("//cockatrice_deck/comments");
			result = (NodeList) expr.evaluate(d, XPathConstants.NODESET);
			deck.setDescription(result.item(0).getTextContent());

			expr = xpath.compile("//cockatrice_deck/zone[contains(@name,'main')]/card");
			result = ((NodeList) expr.evaluate(d, XPathConstants.NODESET));
			for (int i = 0; i < result.getLength(); i++) {
				String name = result.item(i).getAttributes().getNamedItem("name").getTextContent();
				Integer qte = Integer.parseInt(result.item(i).getAttributes().getNamedItem("number").getTextContent());
				MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( name, null, true).get(0);
				deck.getMain().put(mc, qte);
				notify(mc);
			}
			expr = xpath.compile("//cockatrice_deck/zone[contains(@name,'side')]/card");
			result = ((NodeList) expr.evaluate(d, XPathConstants.NODESET));
			for (int i = 0; i < result.getLength(); i++) {
				String name = result.item(i).getAttributes().getNamedItem("name").getTextContent();
				Integer qte = Integer.parseInt(result.item(i).getAttributes().getNamedItem("number").getTextContent());
				MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( name, null, true).get(0);
				deck.getSideBoard().put(mc, qte);
				notify(mc);
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
		return deck;
	}

	@Override
	public String getName() {
		return "Cockatrice";
	}



	@Override
	public void initDefault() {
		setProperty(VERSION, "1.0");
		setProperty(DEFAULT_PRICE, "0");

	}

	@Override
	public String getVersion() {
		return getProperty(VERSION, "2.0");
	}

}
