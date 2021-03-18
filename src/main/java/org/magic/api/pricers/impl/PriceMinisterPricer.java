package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.tools.URLTools;
import org.magic.tools.XMLTools;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PriceMinisterPricer extends AbstractPricesProvider {

	@Override
	public STATUT getStatut() {
		return STATUT.BUGGED;
	}

	
	@Override
	public List<MagicPrice> getLocalePrice(MagicCard card) throws IOException
	{
		List<MagicPrice> list = new ArrayList<>();
		try {

			DocumentBuilder dBuilder = XMLTools.createSecureXMLDocumentBuilder();

			StringBuilder url = new StringBuilder();

			url.append(getString("URL"))
				.append("&login=").append(getString("LOGIN"))
				.append("&pwd=").append(getString("PASS"))
				.append("&version=").append("2018-06-28")
				.append("&scope=").append(getString("SCOPE"))
				.append("&nbproductsperpage=").append(getString("NB_PRODUCT_PAGE"))
				.append("&kw=").append(URLTools.encode(card.getName()))
				.append("&nav=").append(getString("CATEGORIE"));

			logger.debug(getName() + " parsing items from " + url);

			Document doc = dBuilder.parse(url.toString());
			doc.getDocumentElement().normalize();

			NodeList lst = doc.getElementsByTagName("product");
			for (int temp = 0; temp < lst.getLength(); temp++) {
				Node nNode = lst.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) nNode;
					MagicPrice it = new MagicPrice();
					it.setMagicCard(card);
					it.setUrl(e.getElementsByTagName("url").item(0).getTextContent());
					it.setSite(getName());
					it.setValue(Double.parseDouble(parsePrice((Element) e.getElementsByTagName("global").item(0))));
					it.setCurrency("EUR");
					list.add(it);

				}

			}
			logger.debug(getName() + " found " + list.size() + " items");

			return list;

		} catch (Exception e) {
			logger.error("error in search " + card.getName(), e);
		}

		return list;
	}

	private String parsePrice(Element item) {
		try {
			return ((Element) item.getElementsByTagName("advertprice").item(0)).getElementsByTagName("amount").item(0)
					.getTextContent();
		} catch (Exception e) {
			logger.error(item);
			return "0.0";
		}
	}
	
	

	@Override
	public String getName() {
		return "PriceMinister";
	}

	@Override
	public void initDefault() {
		setProperty("LOGIN", "login");
		setProperty("PASS", "PASS");
		setProperty("CATEGORIE", "");
		setProperty("URL", "https://ws.fr.shopping.rakuten.com/listing_ssl_ws?action=listing");
		setProperty("SCOPE", "PRICING");
		setProperty("NB_PRODUCT_PAGE", "20");
		setProperty("WEBSITE", "http://www.priceminister.com/");
	}

}
