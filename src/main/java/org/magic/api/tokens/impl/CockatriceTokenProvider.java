package org.magic.api.tokens.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractTokensProvider;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CockatriceTokenProvider extends AbstractTokensProvider {

	private static final String CARD_REVERSE_RELATED = "//card[reverse-related=\"";
	private static final String COLOR = "color";

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	private Document document;
	private XPath xPath;

	private void init()
	{
		try {
			document = URLTools.extractAsXml(getString("URL"));
			xPath = XPathFactory.newInstance().newXPath();
		} catch (Exception e) {
			logger.error(e);
		}
	}


	@Override
	public boolean isTokenizer(MTGCard mc) {

		if(xPath==null)
			init();

		String expression = CARD_REVERSE_RELATED + mc.getName() + "\"][not(contains(name,'Emblem'))]";
		logger.trace("looking for token : {}",expression);
		try {
			var nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			return (nodeList.getLength() > 0);
		} catch (XPathExpressionException _) {
			return false;
		}
	}

	@Override
	public boolean isEmblemizer(MTGCard mc) {

		if(mc.isEmblem())
			return false;


		if(xPath==null)
			init();

		String expression = CARD_REVERSE_RELATED + mc.getName() + "\"][contains(name,'Emblem')]";

		logger.trace("looking for emblem : {}",expression);
		try {
			var nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			return (nodeList.getLength() > 0);
		} catch (XPathExpressionException _) {
			return false;
		}
	}


	@Override
	public List<MTGCard> listTokensFor(MTGEdition ed) throws IOException {

		if(xPath==null)
			init();


		String expression = "//card[set=\'" + ed.getId() + "']";
		logger.debug("Expression ={}",expression);
		try {
			var nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

			var ret = new ArrayList<MTGCard>();

			for(var i = 0; i<nodeList.getLength();i++)
				ret.add(build((Element)nodeList.item(i), ed));

			return ret;
		}
		catch(Exception e)
		{
			logger.error(e);
		}

		return new ArrayList<>();
	}


	@Override
	public MTGCard generateEmblemFor(MTGCard mc) throws IOException {

		if(xPath==null)
			init();

		String expression = CARD_REVERSE_RELATED + mc.getName() + "\"][contains(name,'Emblem')]";
		logger.debug(expression);
		try {
			var nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			return build( ((Element)nodeList.item(0)),mc.getEdition());

		} catch (XPathExpressionException e) {
			logger.error("Erreur XPath", e);
			return null;
		}
	}


	@Override
	public MTGCard generateTokenFor(MTGCard mc) {

		if(xPath==null)
			init();


		String expression = CARD_REVERSE_RELATED + mc.getName() + "\"][not(contains(name,'emblem'))]";
		try {
			var nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			var value = (Element) nodeList.item(0);
			return build(value, mc.getEdition());
		} catch (XPathExpressionException e) {
			logger.error("erreur generate token for {}",mc, e);
			return null;
		} catch (IOException e) {
			logger.error("getSetById error {}", mc, e);
			return null;
		}
	}


	private MTGCard build(Element value, MTGEdition ed) throws IOException {


		var tok = new MTGCard();

		tok.setCmc(0);
		tok.setName(value.getElementsByTagName("name").item(0).getTextContent().replaceAll("\\(Emblem\\)", "").replaceAll("\\(Token\\)", "").trim());
		String types = value.getElementsByTagName("type").item(0).getTextContent();


		EnumLayout layout = types.startsWith("Emblem")?EnumLayout.EMBLEM:EnumLayout.TOKEN;

		tok.getSupertypes().add(layout.toPrettyString());

		tok.getSubtypes().add(types.substring(types.indexOf("\u2014") + 1));

		tok.setLayout(layout);
		tok.getEditions().add(getEnabledPlugin(MTGCardsProvider.class).getSetById(ed.getId()));

		if(layout==EnumLayout.EMBLEM)
			tok.setNumber("E");
		else
			tok.setNumber("T");

		if (value.getElementsByTagName(COLOR).item(0) != null) {
			var c = EnumColors.colorByCode(value.getElementsByTagName(COLOR).item(0).getTextContent());
			tok.getColors().add(c);
			tok.getColorIdentity().add(c);
		}

		if (types.toLowerCase().contains("legendary"))
			tok.getSupertypes().add("Legendary");

		if (types.toLowerCase().contains("artifact"))
			tok.getTypes().add("Artifact");

		if (types.toLowerCase().contains("creature"))
			tok.getTypes().add("Creature");

		if (value.getElementsByTagName("pt").item(0) != null) {
			tok.setPower(value.getElementsByTagName("pt").item(0).getTextContent().substring(0, value.getElementsByTagName("pt").item(0).getTextContent().indexOf('/')).trim());
			tok.setToughness(value.getElementsByTagName("pt").item(0).getTextContent().substring(value.getElementsByTagName("pt").item(0).getTextContent().indexOf('/') + 1).trim());
		}

		if (value.getElementsByTagName("text").item(0) != null)
			tok.setText(value.getElementsByTagName("text").item(0).getTextContent());

		NodeList sets = value.getElementsByTagName("set");


		tok.getEditions().add(MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(ed.getId()));
		for (var s = 0; s < sets.getLength(); s++) {
			String idSet = sets.item(s).getTextContent();
			if (!idSet.equals(ed.getId())) {
				MTGEdition ed2 = getEnabledPlugin(MTGCardsProvider.class).getSetById(idSet);
				tok.getEditions().add(ed2);
			}

		}
		tok.setId(DigestUtils.sha256Hex(tok.getEdition().getId() + tok.getName()));

		return tok;
	}

	@Override
	public BufferedImage getPictures(MTGCard tok) throws IOException {

		if(xPath==null)
			init();


		String expression = "//card[name=\"" + tok.getName() + "\"]";

		if (tok.getLayout()==EnumLayout.EMBLEM)
			expression = "//card[name=\"" + tok.getName() + " (Emblem)\"]";

		logger.trace("{} for {}",expression, tok);

		NodeList nodeList;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException e1) {
			throw new IOException(e1);
		}
		Map<String, URL> map = null;

		for (var i = 0; i < nodeList.getLength(); i++) {
			Element value = (Element) nodeList.item(i);
			NodeList sets = value.getElementsByTagName("set");
			map = new HashMap<>();
			for (var s = 0; s < sets.getLength(); s++) {
				var set = sets.item(s).getTextContent();
				var pic = "";
				if (sets.item(s).getAttributes().getNamedItem("picURL") != null)
					pic = sets.item(s).getAttributes().getNamedItem("picURL").getNodeValue();

				if (pic.startsWith("http://"))
					pic = pic.replace("http://", "https://");

				map.put(set, URI.create(pic).toURL());
			}
		}

		try {
			if (map == null)
				{
				logger.error("no pics found for {}",tok);
				return null;
				}

			URL u = null;
			if (map.get(tok.getEdition().getId()) != null) // error on
				u = map.get(tok.getEdition().getId());
			else
				u = map.get(map.keySet().iterator().next());

			logger.debug("Load token pics : {}",u);
			return URLTools.extractAsImage(u.toString());
		} catch (Exception e) {
			logger.error("error pics reading for {}",tok, e);
			return getEnabledPlugin(MTGPictureProvider.class).getBackPicture(tok);
		}
	}

	@Override
	public String getName() {
		return "Cockatrice";
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("URL", new MTGProperty("https://raw.githubusercontent.com/Cockatrice/Magic-Token/master/tokens.xml", "The url where the cockatrice tokens file is stored"));

	}


	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}



}
