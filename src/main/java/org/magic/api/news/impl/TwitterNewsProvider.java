package org.magic.api.news.impl;

import java.io.IOException;
import java.net.URI;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGNews;
import org.magic.api.beans.MTGNewsContent;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.Version;
import twitter4j.v1.Query;
import twitter4j.v1.QueryResult;
import twitter4j.v1.Status;


public class TwitterNewsProvider extends AbstractMagicNewsProvider {

	private Twitter twitter;


	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("CONSUMER_KEY","CONSUMER_SECRET","ACCESS_TOKEN","ACCESS_TOKEN_SECRET");
	}

	@Override
	public List<MTGNewsContent> listNews(MTGNews n) throws IOException {

		if(twitter==null)
		{
			twitter = Twitter.newBuilder().oAuthConsumer(getAuthenticator().get("CONSUMER_KEY"),getAuthenticator().get("CONSUMER_SECRET"))
					.oAuthAccessToken(getAuthenticator().get("ACCESS_TOKEN"),getAuthenticator().get("ACCESS_TOKEN_SECRET")).prettyDebugEnabled(true).build();
		}
		var query = Query.of(n.getName());
		 	 query.count(getInt("MAX_RESULT"));

		List<MTGNewsContent> ret = new ArrayList<>();

		QueryResult result;
		try {
			result = twitter.v1().search().search(query);
			for (Status status : result.getTweets()) {

				if (!status.isRetweet()) {
					var content = new MTGNewsContent();
					content.setAuthor(status.getUser().getScreenName());
					content.setDate(Date.from(status.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
					content.setContent(status.getText());
					content.setLink(URI.create("https://mobile.twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId()).toURL());

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
