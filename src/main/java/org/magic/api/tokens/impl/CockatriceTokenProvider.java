package org.magic.api.tokens.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractTokensProvider;
import org.magic.tools.URLTools;
import org.magic.tools.XMLTools;
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
			DocumentBuilder builder =  XMLTools.createSecureXMLDocumentBuilder();
			document = builder.parse(URLTools.openConnection(getURL("URL")).getInputStream());
			xPath = XPathFactory.newInstance().newXPath();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	
	@Override
	public boolean isTokenizer(MagicCard mc) {
		
		if(xPath==null)
			init();
		
		String expression = CARD_REVERSE_RELATED + mc.getName() + "\"][not(contains(name,'Emblem'))]";
		logger.trace("looking for token : " + expression);
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			return (nodeList.getLength() > 0);
		} catch (XPathExpressionException e) {
			return false;
		}
	}

	@Override
	public boolean isEmblemizer(MagicCard mc) {
	
		if(mc.getLayout()==MTGLayout.EMBLEM)
			return false;
		
		
		if(xPath==null)
			init();
	
		String expression = CARD_REVERSE_RELATED + mc.getName() + "\"][contains(name,'Emblem')]";
		
		logger.trace("looking for emblem : " + expression);
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			return (nodeList.getLength() > 0);
		} catch (XPathExpressionException e) {
			return false;
		}
	}

	@Override
	public MagicCard generateTokenFor(MagicCard mc) {
		
		if(xPath==null)
			init();
		
		
		String expression = CARD_REVERSE_RELATED + mc.getName() + "\"][not(contains(name,'emblem'))]";
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			Element value = (Element) nodeList.item(0);
			MagicCard tok = new MagicCard();
			tok.setLayout(MTGLayout.TOKEN);
			tok.setCmc(0);
			tok.setName(value.getElementsByTagName("name").item(0).getTextContent());

			if (value.getElementsByTagName(COLOR).item(0) != null) {
				MTGColor c = MTGColor.colorByCode(value.getElementsByTagName(COLOR).item(0).getTextContent());
				tok.getColors().add(c);
				tok.getColorIdentity().add(c);
			}

			String types = value.getElementsByTagName("type").item(0).getTextContent();

			if (types.toLowerCase().contains("legendary"))
				tok.getSupertypes().add("Legendary");

			if (types.toLowerCase().contains("artifact"))
				tok.getTypes().add("Artifact");

			if (types.toLowerCase().contains("creature"))
				tok.getTypes().add("Creature");

			tok.getSupertypes().add(MTGLayout.TOKEN.toPrettyString());

			tok.getSubtypes().add(types.substring(types.indexOf("\u2014") + 1));

			if (value.getElementsByTagName("pt").item(0) != null) {
				tok.setPower(value.getElementsByTagName("pt").item(0).getTextContent()
						.substring(0, value.getElementsByTagName("pt").item(0).getTextContent().indexOf('/')).trim());
				tok.setToughness(value.getElementsByTagName("pt").item(0).getTextContent()
						.substring(value.getElementsByTagName("pt").item(0).getTextContent().indexOf('/') + 1).trim());
			}
			if (value.getElementsByTagName("text").item(0) != null)
				tok.setText(value.getElementsByTagName("text").item(0).getTextContent());

			tok.getEditions().add(getEnabledPlugin(MTGCardsProvider.class).getSetById(mc.getCurrentSet().getId()));
			tok.getCurrentSet().setNumber("T");

			NodeList sets = value.getElementsByTagName("set");
			for (int s = 0; s < sets.getLength(); s++) {
				String idSet = sets.item(s).getTextContent();

				if (idSet.equals(mc.getCurrentSet().getId())) {
					MagicEdition ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(idSet);
					tok.getEditions().add(ed);
				}

			}

			tok.setId(DigestUtils.sha1Hex(tok.getCurrentSet().getId() + tok.getName()));

			return tok;

		} catch (XPathExpressionException e) {
			logger.error("erreur generate token for" + mc, e);
			return null;
		} catch (IOException e) {
			logger.error("getSetById error " + mc, e);
			return null;
		}
	}

	@Override
	public MagicCard generateEmblemFor(MagicCard mc) throws IOException {
		
		if(xPath==null)
			init();
		
		String expression = CARD_REVERSE_RELATED + mc.getName() + "\"][contains(name,'Emblem')]";
		logger.debug(expression);
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			Element value = (Element) nodeList.item(0);
			MagicCard tok = new MagicCard();
			tok.setLayout(MTGLayout.EMBLEM);
			tok.setCmc(0);
			tok.setName(
					value.getElementsByTagName("name").item(0).getTextContent().replaceAll("\\(Emblem\\)", "").trim());
			String types = value.getElementsByTagName("type").item(0).getTextContent();
			tok.getSupertypes().add(MTGLayout.EMBLEM.toPrettyString());
			tok.getSubtypes().add(types.substring(types.indexOf("\u2014") + 1));
			tok.setText(value.getElementsByTagName("text").item(0).getTextContent());
			tok.getEditions().add(getEnabledPlugin(MTGCardsProvider.class).getSetById(mc.getCurrentSet().getId()));
			tok.getCurrentSet().setNumber("E");

			
			logger.debug("Create token " + tok);
			return tok;

		} catch (XPathExpressionException e) {
			logger.error("Erreur XPath", e);
			return null;
		}
	}

	@Override
	public BufferedImage getPictures(MagicCard tok) throws IOException {
		
		if(xPath==null)
			init();
		
		
		String expression = "//card[name=\"" + tok.getName() + "\"]";

		if (tok.getLayout()==MTGLayout.EMBLEM)
			expression = "//card[name=\"" + tok.getName() + " (Emblem)\"]";

		logger.trace(expression + " for " + tok);

		NodeList nodeList;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException e1) {
			throw new IOException(e1);
		}
		Map<String, URL> map = null;

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element value = (Element) nodeList.item(i);
			NodeList sets = value.getElementsByTagName("set");
			map = new HashMap<>();
			for (int s = 0; s < sets.getLength(); s++) {
				String set = sets.item(s).getTextContent();
				String pic = "";
				if (sets.item(s).getAttributes().getNamedItem("picURL") != null)
					pic = sets.item(s).getAttributes().getNamedItem("picURL").getNodeValue();

				if (pic.startsWith("http://"))
					pic = pic.replace("http://", "https://");

				map.put(set, new URL(pic));
			}
		}

		try {
			if (map == null)
				{
				logger.error("no pics found for" + tok);
				return null;
				}
			
			URL u = null;
			if (map.get(tok.getCurrentSet().getId()) != null) // error on
				u = map.get(tok.getCurrentSet().getId());
			else
				u = map.get(map.keySet().iterator().next());

			logger.debug("Load token pics : " + u);
			return URLTools.extractImage(u);
		} catch (Exception e) {
			logger.error("error pics reading for " + tok, e);
			return getEnabledPlugin(MTGPictureProvider.class).getBackPicture();
		}
	}

	@Override
	public String getName() {
		return "Cockatrice";
	}

	@Override
	public void initDefault() {
		setProperty("URL", "https://raw.githubusercontent.com/Cockatrice/Magic-Token/master/tokens.xml");

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
