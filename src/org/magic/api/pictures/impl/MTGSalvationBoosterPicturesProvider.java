package org.magic.api.pictures.impl;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MTGSalvationBoosterPicturesProvider {

	String url="http://mtgsalvation.gamepedia.com/";
	
	public static void main(String[] args) throws IOException {
		new MTGSalvationBoosterPicturesProvider();
	}
	
	
	public MTGSalvationBoosterPicturesProvider() throws IOException {
		Document d = Jsoup.connect(url+"index.php?title=Special:Search&limit=500&search=File%3ABooster&fulltext=Search&profile=images").userAgent("Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13").get();
		
		Elements li = d.select("table.searchResultImage");
		
		StringBuffer temp = new StringBuffer();
		
		for(Element e : li)
		{
			Elements a = e.select("a");
			System.out.println(a);
		}
		
		
	}
}
