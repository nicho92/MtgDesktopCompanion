package org.magic.tests;

import java.io.IOException;

import org.magic.api.beans.RSSBean;
import org.magic.services.MagicFactory;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class RSSTest {

	public static void main(String[] args) throws IllegalArgumentException, FeedException, IOException {
		SyndFeedInput input = new SyndFeedInput();
		
		//for(RSSBean r : MagicFactory.getInstance().getRss())
		RSSBean r = MagicFactory.getInstance().getRss().get(0);
		{
			SyndFeed feed = input.build(new XmlReader(r.getUrl()));
			System.out.println(feed.getTitle());
			
			for(SyndEntry se : feed.getEntries())
			{
				System.out.println(se.getTitle());
				System.out.println(se.getLink());
				System.out.println(se.getDescription().getValue());
			}
			
		}
	}

}
