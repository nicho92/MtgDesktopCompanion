package org.magic.api.news.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.MagicNews;
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
	
	public TwitterNewsProvider() {
		super();
		cb = new ConfigurationBuilder();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("CONSUMER_KEY", "");
			props.put("CONSUMER_SECRET", "");
			props.put("ACCESS_TOKEN", "");
			props.put("ACCESS_TOKEN_SECRET", "");
			save();
		}
	
		cb.setDebugEnabled(false)
		  .setOAuthConsumerKey(getProperty("CONSUMER_KEY").toString())
		  .setOAuthConsumerSecret(getProperty("CONSUMER_SECRET").toString())
		  .setOAuthAccessToken(getProperty("ACCESS_TOKEN").toString())
		  .setOAuthAccessTokenSecret(getProperty("ACCESS_TOKEN_SECRET").toString());
	}
	
	@Override
	public List<MagicNewsContent> listNews(MagicNews n) throws IOException {
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		Query query = new Query(n.getName());
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
}
