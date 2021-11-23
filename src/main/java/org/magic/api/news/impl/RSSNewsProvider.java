package org.magic.api.news.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNewsContent;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;
import org.magic.services.network.URLTools;
import org.xml.sax.InputSource;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

public class RSSNewsProvider extends AbstractMagicNewsProvider {

	private SyndFeedInput input;

	@Override
	public List<MagicNewsContent> listNews(MagicNews rssBean) throws IOException {
		
		
		if(input==null)
			input = new SyndFeedInput();

		SyndFeed feed;

		List<MagicNewsContent> ret = new ArrayList<>();
		try {
			logger.debug("reading " + rssBean.getUrl());
			var is = URLTools.extractAsInputStream(rssBean.getUrl());

			var source = new InputSource(is);

			feed = input.build(source);
			var baseURI = feed.getLink();

			for (SyndEntry s : feed.getEntries()) {
				var content = new MagicNewsContent();
				content.setTitle(s.getTitle());
				content.setAuthor(s.getAuthor());
				if(s.getPublishedDate()==null)
					content.setDate(s.getUpdatedDate());
				else
					content.setDate(s.getPublishedDate());
				URL link;
				if (!s.getLink().startsWith(baseURI))
					link = new URL(baseURI + s.getLink());
				else
					link = new URL(s.getLink());

				content.setLink(link);

				ret.add(content);
			}

			return ret;

		} catch (IllegalArgumentException | FeedException e) {
			throw new IOException(e);
		} 

	}

	@Override
	public String getName() {
		return "RSS";
	}

	
	@Override
	public String getVersion() {
		return "1.12.2";
	}
}
