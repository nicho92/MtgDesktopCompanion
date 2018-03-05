package org.magic.api.news.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNews.NEWS_TYPE;
import org.magic.api.beans.MagicNewsContent;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;
import org.magic.services.MTGConstants;
import org.xml.sax.InputSource;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

public class RSSNewsProvider extends AbstractMagicNewsProvider {
	
	private SyndFeedInput input;
	
	public RSSNewsProvider() {
		super();
		input = new SyndFeedInput();
	}
	
	@Override
	public List<MagicNewsContent> listNews(MagicNews rssBean) throws IOException {
		InputStream is = null;
		SyndFeed feed;
		
		List<MagicNewsContent> ret = new ArrayList<>();
		try {
			URLConnection openConnection = new URL(rssBean.getUrl()).openConnection();
			openConnection.setRequestProperty("User-Agent",MTGConstants.USER_AGENT);
			is = openConnection.getInputStream();
			InputSource source = new InputSource(is);
			feed=input.build(source);
			String baseURI=feed.getLink();
			
			for(SyndEntry s: feed.getEntries())
			{
				MagicNewsContent content = new MagicNewsContent();
				content.setTitle(s.getTitle());
				content.setAuthor(s.getAuthor());
				content.setDate(s.getPublishedDate());
					URL link;
					if(!s.getLink().startsWith(baseURI))
						link = new URL(baseURI+s.getLink());
					else
						link = new URL(s.getLink());
					
					content.setLink(link);
				
				ret.add(content);
			}
			
			return ret;
			
		} catch (IllegalArgumentException|FeedException e) {
			throw new IOException(e);
		} 
		finally
		{
			if(is!=null)
				is.close();
		}

		
		
	}

	@Override
	public String getName() {
		return "RSS";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public NEWS_TYPE getProviderType() {
		return NEWS_TYPE.RSS;
	}

	@Override
	public void initDefault() {
		// TODO Auto-generated method stub
		
	}

	
}
