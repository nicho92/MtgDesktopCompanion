package org.magic.api.shopping.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.tools.InstallCert;
import org.magic.tools.URLTools;

public class LeboncoinShopper extends AbstractMagicShopper {

	private static final String MAX_RESULT = "MAX_RESULT";
	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";
	private Document doc;
	private SimpleDateFormat formatter;



	public LeboncoinShopper() {
		super();
		init();
	}

	public static void main(String[] args) {
		new LeboncoinShopper().search("lot cartes magic");
	}
	
	private void init() {
		formatter = new SimpleDateFormat(getString("DATE_FORMAT"));
		if(getBoolean(LOAD_CERTIFICATE))
		{
			try {
				InstallCert.installCert("leboncoin.fr");
				setProperty(LOAD_CERTIFICATE, "false");
			} catch (Exception e1) {
				logger.error(e1);
			}
		}
	}

	@Override
	public List<ShopItem> search(String search) {

		List<ShopItem> list = new ArrayList<>();
		String html = "";
		int maxPage = Integer.parseInt(getString("MAX_PAGE"));

		for (int p = 1; p <= maxPage; p++) {
			html = getString("URL").replaceAll("%SEARCH%", search).replaceAll("%PAGE%", String.valueOf(p));

			logger.trace("parsing item from " + html);

			try {
				doc =URLTools.extractHtml(html);
				
			} catch (IOException e1) {
				logger.error("error",e1);
				return list;
			}

			Elements listElements = doc.select("div.react-tabs__tab-panel").get(0).getElementsByTag("li");

			for (int i = 0; i < listElements.size(); i++) {
				String url = listElements.get(i).getElementsByTag("a").get(0).attr("href");
				ShopItem a = new ShopItem();
				a.setName(listElements.get(i).getElementsByTag("a").get(0).attr("title"));
				try {
					a.setUrl(new URL("https://" + url));
				} catch (MalformedURLException e1) {
					a.setUrl(null);
				}
				a.setLieu(listElements.get(i).getElementsByClass("item_infos").get(0).getElementsByClass("item_supp")
						.get(1).text());
				a.setType(listElements.get(i).getElementsByClass("item_infos").get(0).getElementsByClass("item_supp")
						.get(0).text());
				a.setId(url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.')).trim());
				a.setShopName(getName());
				try {
					a.setImage(new URL(listElements.get(i).getElementsByClass("lazyload").get(0).attr("data-imgsrc")));
				} catch (IndexOutOfBoundsException e) {
					try {
						a.setImage(new URL("https://static.leboncoin.fr/img/no-picture.png"));
					} catch (MalformedURLException e1) {
						logger.error(e1);
					}
				} catch (MalformedURLException e1) {
					logger.error(e1);
				}

				if (!listElements.get(i).getElementsByClass("item_price").isEmpty())
					a.setPrice(parsePrice(listElements.get(i).getElementsByClass("item_price").get(0).text()));

				try {
					a.setDate(parseDate(listElements.get(i).getElementsByClass("item_infos").get(0)
							.getElementsByClass("item_supp").get(2).text()));
				} catch (Exception e) {
					logger.error(e);
				}
				a.setUrgent(listElements.get(i).getElementsByClass("item_infos").get(0).getElementsByClass("item_supp")
						.get(2).text().startsWith("Urgent"));
				list.add(a);

			}
		}

		if (list.size() > Integer.parseInt(getString(MAX_RESULT)) && (Integer.parseInt(getString(MAX_RESULT)) > -1))
			return list.subList(0, Integer.parseInt(getString(MAX_RESULT)));

		return list;
	}

	private Date parseDate(String e) {
		String aujourdhui = "Aujourd'hui, ";
		String hier = "Hier, ";
		e = e.replaceAll("Urgent ", "");
		Calendar cal = GregorianCalendar.getInstance(Locale.FRANCE);

		if (e.contains(aujourdhui)) {
			String hour = e.substring(aujourdhui.length(), aujourdhui.length() + 2).trim();
			String minute = e.substring(aujourdhui.length() + 3, aujourdhui.length() + 5).trim();
			cal.setTime(new Date());
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
			cal.set(Calendar.MINUTE, Integer.parseInt(minute));
			return cal.getTime();
		}

		if (e.contains(hier)) {
			if (e.startsWith(" "))
				e = e.substring(1, e.length());
			String hour = e.substring(hier.length(), hier.length() + 2).trim();
			String minute = e.substring(hier.length() + 3, hier.length() + 5).trim();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, -1);
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
			cal.set(Calendar.MINUTE, Integer.parseInt(minute));
			return cal.getTime();
		}

		try {
			cal.setTime(formatter.parse(e.replaceAll(",", ".")));
			cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

			return cal.getTime();
		} catch (ParseException e1) {
			logger.error(e1.getMessage());
		}
		return null;
	}

	private Double parsePrice(String price) {
		try {
			price = price.substring(0, price.length() - 2).trim().replaceAll(" ", "");
			return Double.parseDouble(price);
		} catch (Exception e) {
			return 0.0;
		}

	}

	@Override
	public String getName() {
		return "LeBonCoin";
	}

	@Override
	public void initDefault() {
		setProperty("TITLE_ONLY", "0");
		setProperty("MAX_PAGE", "2");
		setProperty(MAX_RESULT, "30");
		setProperty("URL", "https://www.leboncoin.fr/recherche/?text=%SEARCH%&page=%PAGE%");
		setProperty("DATE_FORMAT", "dd MMMM. H:m");
		setProperty("ROOT_TAG", "section[class=tabsContent block-white dontSwitch]");
		setProperty(LOAD_CERTIFICATE, "false");
	}


}
