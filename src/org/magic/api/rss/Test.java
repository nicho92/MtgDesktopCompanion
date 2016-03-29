package org.magic.api.rss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class Test {

	public static void main(String[] args) throws IllegalArgumentException, MalformedURLException, FeedException, IOException {
		
		SyndFeedInput input = new SyndFeedInput();
		//SyndFeed feed = input.build(new XmlReader(new URL("http://www.magiccorporation.com/gathering-news-last-rss.html")));
		SyndFeed feed = input.build(new XmlReader(new URL("http://www.mtggoldfish.com/feed")));

		SyndEntry se = feed.getEntries().get(0);
		//for(SyndEntry se : feed.getEntries())
		{
			System.out.println(se.getTitle());
			Document doc = Jsoup.connect(se.getLink()).get();
		//	Elements images = doc.select("img");
			System.out.println(doc.getElementsByAttribute("body").text());
			
		}

	}

}
