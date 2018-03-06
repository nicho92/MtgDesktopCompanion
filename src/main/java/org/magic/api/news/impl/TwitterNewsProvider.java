package org.magic.api.news.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNews.NEWS_TYPE;
import org.magic.api.beans.MagicNewsContent;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterNewsProvider extends AbstractMagicNewsProvider {

	private ConfigurationBuilder cb;
	private TwitterFactory tf ;
	public TwitterNewsProvider() {
		super();
		cb = new ConfigurationBuilder();
		cb.setDebugEnabled(getProperty("LOG").equals("true"))
		  .setOAuthConsumerKey(getProperty("CONSUMER_KEY"))
		  .setOAuthConsumerSecret(getProperty("CONSUMER_SECRET"))
		  .setOAuthAccessToken(getProperty("ACCESS_TOKEN"))
		  .setOAuthAccessTokenSecret(getProperty("ACCESS_TOKEN_SECRET"));
		tf = new TwitterFactory(cb.build());
	}
	
	@Override
	public List<MagicNewsContent> listNews(MagicNews n) throws IOException {
		
		Twitter twitter = tf.getInstance();
		Query query = new Query(n.getName());
			  query.setCount(Integer.parseInt(getProperty("MAX_RESULT")));
		List<MagicNewsContent> ret=new ArrayList<>();
		
	        QueryResult result;
			try {
				result = twitter.search(query);
				for (Status status : result.getTweets()) {
					MagicNewsContent content = new MagicNewsContent();
					content.setAuthor(status.getUser().getScreenName());
					content.setDate(status.getCreatedAt());
					content.setContent(status.getText());
					content.setLink(new URL("https://twitter.com/" + status.getUser().getScreenName()+ "/status/" + status.getId()));
					content.setTitle(status.getText());
					ret.add(content);
				}
			} catch (TwitterException e) {
				throw new IOException(e);
			}
			
			return ret;
		
	}

	@Override
	public String getName() {
		return "Twitter";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public NEWS_TYPE getProviderType() {
		return NEWS_TYPE.TWITTER;
	}

	@Override
	public void initDefault() {
		setProperty("CONSUMER_KEY", "");
		setProperty("CONSUMER_SECRET", "");
		setProperty("ACCESS_TOKEN", "");
		setProperty("ACCESS_TOKEN_SECRET", "");
		setProperty("MAX_RESULT", "25");
		setProperty("LOG", "false");
		
	}
}
