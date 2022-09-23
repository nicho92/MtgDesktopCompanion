package org.magic.api.news.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNewsContent;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.Version;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterNewsProvider extends AbstractMagicNewsProvider {

	private TwitterFactory tf;


	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("CONSUMER_KEY","CONSUMER_SECRET","ACCESS_TOKEN","ACCESS_TOKEN_SECRET");
	}

	@Override
	public List<MagicNewsContent> listNews(MagicNews n) throws IOException {

		if(tf==null)
		{
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(getBoolean("LOG")).setOAuthConsumerKey(getAuthenticator().get("CONSUMER_KEY"))
					.setOAuthConsumerSecret(getAuthenticator().get("CONSUMER_SECRET")).setOAuthAccessToken(getAuthenticator().get("ACCESS_TOKEN"))
					.setOAuthAccessTokenSecret(getAuthenticator().get("ACCESS_TOKEN_SECRET"));
			tf = new TwitterFactory(cb.build());
		}



		var twitter = tf.getInstance();
		var query = new Query(n.getName());
		query.setCount(getInt("MAX_RESULT"));

		List<MagicNewsContent> ret = new ArrayList<>();

		QueryResult result;
		try {
			result = twitter.search(query);
			for (Status status : result.getTweets()) {

				if (!status.isRetweet()) {
					var content = new MagicNewsContent();
					content.setAuthor(status.getUser().getScreenName());
					content.setDate(status.getCreatedAt());
					content.setContent(status.getText());
					content.setLink(new URL(
							"https://mobile.twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId()));

					content.setTitle(status.getText());
					ret.add(content);
				}
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
	public String getVersion() {
		return Version.getVersion();
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("MAX_RESULT", "25",
								"LOG", "false");

	}

}
