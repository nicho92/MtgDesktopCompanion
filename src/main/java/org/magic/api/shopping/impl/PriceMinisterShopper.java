package org.magic.api.shopping.impl;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.MTGConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PriceMinisterShopper extends AbstractMagicShopper {

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public List<ShopItem> search(String search) {
		List<ShopItem> list = new ArrayList<>();
		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			StringBuilder url = new StringBuilder();

			url.append(getString("URL")).append("&login=").append(getString("LOGIN")).append("&pwd=")
					.append(getString("PASS")).append("&version=").append(getString("VERSION")).append("&scope=")
					.append(getString("SCOPE")).append("&nbproductsperpage=").append(getString("NB_PRODUCT_PAGE"))
					.append("&kw=").append(URLEncoder.encode(search, MTGConstants.DEFAULT_ENCODING)).append("&nav=")
					.append(getString("CATEGORIE"));

			logger.debug(getName() + " parsing item from " + url);

			Document doc = dBuilder.parse(url.toString());
			doc.getDocumentElement().normalize();

			NodeList lst = doc.getElementsByTagName("product");
			for (int temp = 0; temp < lst.getLength(); temp++) {
				Node nNode = lst.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) nNode;
					ShopItem it = new ShopItem();
					it.setId(e.getElementsByTagName("productid").item(0).getTextContent());
					it.setType(e.getElementsByTagName("topic").item(0).getTextContent());
					it.setUrl(new URL(e.getElementsByTagName("url").item(0).getTextContent()));

					it.setImage(new URL(e.getElementsByTagName("image").item(0).getTextContent()));
					it.setName(e.getElementsByTagName("headline").item(0).getTextContent());
					it.setShopName(getName());
					it.setPrice(Double.parseDouble(parsePrice((Element) e.getElementsByTagName("global").item(0))));
					list.add(it);

				}

			}
			logger.debug(getName() + " found " + list.size() + " items");

			return list;

		} catch (Exception e) {
			logger.error("error in search " + search, e);
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
		setProperty("VERSION", "2015-07-05");
		setProperty("CATEGORIE", "");
		setProperty("URL", "https://ws.priceminister.com/listing_ssl_ws?action=listing");
		setProperty("SCOPE", "PRICING");
		setProperty("NB_PRODUCT_PAGE", "20");
		
		setProperty("WEBSITE", "http://www.priceminister.com/");
		

	}

	@Override
	public String getVersion() {
		return "1.0";
	}

}
