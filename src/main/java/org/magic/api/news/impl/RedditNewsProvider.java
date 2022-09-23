package org.magic.api.news.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNewsContent;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;
import org.magic.services.MTGConstants;

import net.dean.jraw.Version;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Submission;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.Paginator;

public class RedditNewsProvider extends AbstractMagicNewsProvider {

	private NetworkAdapter adapter;

	@Override
	public List<MagicNewsContent> listNews(MagicNews n) throws IOException {


		if(adapter==null)
			adapter = new OkHttpNetworkAdapter(new UserAgent(MTGConstants.MTG_APP_NAME));

		List<MagicNewsContent> ret = new ArrayList<>();
		var credentials = Credentials.script(getAuthenticator().get("USER"), getAuthenticator().get("PASSWORD"),getAuthenticator().get("APPID"), getAuthenticator().get("SECRET"));
		var reddit = OAuthHelper.automatic(adapter, credentials);
		Paginator<Submission> pagin = reddit.subreddit(n.getName()).posts().limit(getInt("LIMIT")).build();

		List<Submission> l = pagin.next();

		l.forEach(s->{
			var content = new MagicNewsContent();
			content.setAuthor(s.getAuthor());
			content.setTitle(s.getTitle());
			content.setDate(s.getCreated());
			try {
				content.setLink(new URL(s.getUrl()));
			} catch (MalformedURLException e) {
				logger.error(e);
			}
			ret.add(content);
		});
		return ret;

	}

	@Override
	public String getVersion() {
		return Version.get();
	}

@Override
public List<String> listAuthenticationAttributes() {
	return List.of("USER","PASSWORD","APPID","SECRET");
}


	@Override
	public Map<String, String> getDefaultAttributes() {
			return Map.of("LIMIT", "10");
	}



	@Override
	public String getName() {
		return "Reddit";
	}

}
