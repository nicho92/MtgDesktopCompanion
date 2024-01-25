package org.magic.api.news.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGNews;
import org.magic.api.beans.MTGNewsContent;
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
	public List<MTGNewsContent> listNews(MTGNews rssBean) throws IOException {


		if(input==null)
			input = new SyndFeedInput();

		SyndFeed feed;

		List<MTGNewsContent> ret = new ArrayList<>();
		try {
			logger.debug("reading {}",rssBean.getUrl());
			var is = URLTools.extractAsInputStream(rssBean.getUrl());

			var source = new InputSource(is);

			feed = input.build(source);
	
			for (SyndEntry s : feed.getEntries()) {
				var content = new MTGNewsContent();
				content.setTitle(s.getTitle());
				content.setAuthor(s.getAuthor());
				if(s.getPublishedDate()==null)
					content.setDate(s.getUpdatedDate());
				else
					content.setDate(s.getPublishedDate());

				content.setLink(URI.create(s.getLink()).toURL());

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
