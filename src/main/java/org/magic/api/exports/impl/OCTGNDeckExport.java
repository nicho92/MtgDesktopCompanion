package org.magic.api.exports.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;
import org.magic.tools.XMLTools;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class OCTGNDeckExport extends AbstractCardExport {

	private static final String SHARED = "SHARED";

	@Override
	public String getFileExtension() {
		return ".o8d";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();

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
		var deck = new MagicDeck();
		deck.setName(dname);

		try (var sr = new StringReader(f)){
			var d = XMLTools.createSecureXMLDocumentBuilder().parse(new InputSource(sr));
			var xpath = XPathFactory.newInstance().newXPath();
			var expr = xpath.compile("//section[@name='Main']/card");
			var result = (NodeList) expr.evaluate(d, XPathConstants.NODESET);
			for (var i = 0; i < result.getLength(); i++) {
				var it = result.item(i);
				var name = it.getTextContent();
				var qte = it.getAttributes().getNamedItem("qty").getNodeValue();
				var mc = getEnabledPlugin(MTGCardsProvider.class)
						.searchCardByName( name, null, true).get(0);

				deck.getMain().put(mc, Integer.parseInt(qte));
				notify(mc);
			}

			expr = xpath.compile("//section[@name='Sideboard']/card");
			result = (NodeList) expr.evaluate(d, XPathConstants.NODESET);
			for (var i = 0; i < result.getLength(); i++) {
				var it = result.item(i);
				var name = it.getTextContent();
				var qte = it.getAttributes().getNamedItem("qty").getNodeValue();
				var mc = getEnabledPlugin(MTGCardsProvider.class)
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
	public Map<String, String> getDefaultAttributes() {

		var m = super.getDefaultAttributes();
		m.put("MAGIC_GAME_ID", "a6c8d2e8-7cd8-11dd-8f94-e62b56d89593");
		m.put("SLEEVE_ID", "0");
		m.put(SHARED, "False");

		return m;

	}


}
