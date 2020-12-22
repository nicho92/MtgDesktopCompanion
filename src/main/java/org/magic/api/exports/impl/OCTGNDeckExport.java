package org.magic.api.exports.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

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
import org.magic.tools.FileTools;
import org.magic.tools.XMLTools;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class OCTGNDeckExport extends AbstractCardExport {

	private static final String SHARED = "SHARED";

	public String getFileExtension() {
		return ".o8d";
	}

	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();

		temp.append("<?xml version='1.0' encoding='").append(MTGConstants.DEFAULT_ENCODING).append("' standalone='yes'?>");
		temp.append("<deck game='" + getString("MAGIC_GAME_ID") + "' sleeveid='" + getString("SLEEVE_ID") + "' >");
		temp.append("<section name='Main' shared='" + getString(SHARED) + "'>");
		for (MagicCard mc : deck.getMain().keySet()) {
			temp.append("<card qty='").append(deck.getMain().get(mc)).append("' id='" + mc.getId() + "'>")
					.append(mc.getName()).append("</card>");
			
			notify(mc);
		}
		temp.append("</section>");
		temp.append("<section name='Sideboard' shared='" + getString(SHARED) + "'>");
		for (MagicCard mc : deck.getSideBoard().keySet()) {
			temp.append("<card qty='").append(deck.getSideBoard().get(mc)).append("' id='" + mc.getId() + "'>")
					.append(mc.getName()).append("</card>");
			notify(mc);
		}
		temp.append("</section>");

		temp.append("<notes><![CDATA[" + deck.getDescription() + "]]></notes>");

		temp.append("</deck>");

		FileTools.saveFile(dest, temp.toString());

	}

	@Override
	public MagicDeck importDeck(String f,String dname) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(dname);

		try (StringReader sr = new StringReader(f)){
			Document d = XMLTools.createSecureXMLFactory().newDocumentBuilder().parse(new InputSource(sr));
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//section[@name='Main']/card");
			NodeList result = (NodeList) expr.evaluate(d, XPathConstants.NODESET);
			for (int i = 0; i < result.getLength(); i++) {
				Node it = result.item(i);
				String name = it.getTextContent();
				String qte = it.getAttributes().getNamedItem("qty").getNodeValue();
				MagicCard mc = getEnabledPlugin(MTGCardsProvider.class)
						.searchCardByName( name, null, true).get(0);

				deck.getMain().put(mc, Integer.parseInt(qte));
				notify(mc);
			}

			expr = xpath.compile("//section[@name='Sideboard']/card");
			result = (NodeList) expr.evaluate(d, XPathConstants.NODESET);
			for (int i = 0; i < result.getLength(); i++) {
				Node it = result.item(i);
				String name = it.getTextContent();
				String qte = it.getAttributes().getNamedItem("qty").getNodeValue();
				MagicCard mc = getEnabledPlugin(MTGCardsProvider.class)
						.searchCardByName( name, null, true).get(0);

				deck.getSideBoard().put(mc, Integer.parseInt(qte));
				notify(mc);
			}
			return deck;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}


	@Override
	public String getName() {
		return "OCTGN";
	}


	@Override
	public void initDefault() {
		setProperty("MAGIC_GAME_ID", "a6c8d2e8-7cd8-11dd-8f94-e62b56d89593");
		setProperty("SLEEVE_ID", "0");
		setProperty(SHARED, "False");

	}


}
