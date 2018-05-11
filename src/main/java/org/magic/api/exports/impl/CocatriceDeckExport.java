package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CocatriceDeckExport extends AbstractCardExport {

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	public CocatriceDeckExport() {
		super();
	}

	public String getFileExtension() {
		return ".cod";
	}

	public void export(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();
		int c = 0;
		String endZoneTag = "</zone>";

		temp.append("<?xml version='1.0' encoding='").append(MTGConstants.DEFAULT_ENCODING).append("'?>");
		temp.append("<cockatrice_deck version='" + getString("VERSION") + "'>");
		temp.append("<deckname>").append(deck.getName()).append("</deckname>");
		temp.append("<comments>").append(deck.getDescription()).append("</comments>");
		temp.append("<zone name='main'>");
		for (MagicCard mc : deck.getMap().keySet()) {
			temp.append("<card number='").append(deck.getMap().get(mc))
					.append("' price='" + getString("DEFAULT_PRICE") + "' name=\"").append(mc.getName()).append("\"/>");
			setChanged();
			notifyObservers(c++);
		}
		temp.append(endZoneTag);
		temp.append("<zone name='side'>");
		for (MagicCard mc : deck.getMapSideBoard().keySet()) {
			temp.append("<card number='").append(deck.getMapSideBoard().get(mc))
					.append("' price='" + getString("DEFAULT_PRICE") + "' name=\"").append(mc.getName()).append("\"/>");
		}
		temp.append(endZoneTag);
		temp.append("</cockatrice_deck>");

		FileUtils.writeStringToFile(dest, temp.toString(), MTGConstants.DEFAULT_ENCODING);

	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		MagicDeck deck = new MagicDeck();
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new FileReader(f)));
			XPath xpath = XPathFactory.newInstance().newXPath();

			XPathExpression expr = xpath.compile("//cockatrice_deck/deckname");
			NodeList result = (NodeList) expr.evaluate(d, XPathConstants.NODESET);

			deck.setName(result.item(0).getTextContent());

			expr = xpath.compile("//cockatrice_deck/comments");
			result = (NodeList) expr.evaluate(d, XPathConstants.NODESET);
			deck.setDescription(result.item(0).getTextContent());

			expr = xpath.compile("//cockatrice_deck/zone[contains(@name,'main')]/card");
			result = ((NodeList) expr.evaluate(d, XPathConstants.NODESET));
			int c = 0;
			for (int i = 0; i < result.getLength(); i++) {
				String name = result.item(i).getAttributes().getNamedItem("name").getTextContent();
				Integer qte = Integer.parseInt(result.item(i).getAttributes().getNamedItem("number").getTextContent());
				deck.getMap().put(MTGControler.getInstance().getEnabledCardsProviders()
						.searchCardByCriteria("name", name, null, true).get(0), qte);
				setChanged();
				notifyObservers(c++);
			}
			expr = xpath.compile("//cockatrice_deck/zone[contains(@name,'side')]/card");
			result = ((NodeList) expr.evaluate(d, XPathConstants.NODESET));
			for (int i = 0; i < result.getLength(); i++) {
				String name = result.item(i).getAttributes().getNamedItem("name").getTextContent();
				Integer qte = Integer.parseInt(result.item(i).getAttributes().getNamedItem("number").getTextContent());
				deck.getMapSideBoard().put(MTGControler.getInstance().getEnabledCardsProviders()
						.searchCardByCriteria("name", name, null, true).get(0), qte);
				setChanged();
				notifyObservers(c++);
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
		return deck;
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(CocatriceDeckExport.class.getResource("/icons/plugins/cockatrice_logo.png"));
	}

	@Override
	public String getName() {
		return "Cockatrice";
	}


	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		return importFromDeck(importDeck(f));
	}

	@Override
	public void initDefault() {
		setProperty("VERSION", "1.0");
		setProperty("DEFAULT_PRICE", "0");

	}

	@Override
	public String getVersion() {
		return getProperty("VERSION", "2.0");
	}

}
