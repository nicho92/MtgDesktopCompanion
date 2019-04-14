package org.beta;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNewsContent;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

public class RedditNewsProvider extends AbstractMagicNewsProvider {

	@Override
	public List<MagicNewsContent> listNews(MagicNews n) throws IOException {
		
		
		UserAgent userAgent = new UserAgent("mtgdesktopCompanion");
		Credentials credentials = Credentials.script(getString("USER"), getString("PASSWORD"),getString("APPID"), getString("SECRET"));
		NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
		RedditClient reddit = OAuthHelper.automatic(adapter, credentials);
		
		
		System.out.println(reddit.subreddit(n.getName()).posts());
		
		return null;
		
	}
	
	
	@Override
	public void initDefault() {
		setProperty("USER", "");
		setProperty("PASSWORD", "");
		setProperty("APPID", "");
		setProperty("SECRET", "");
		
	}

	@Override
	public NEWS_TYPE getProviderType() {
		return NEWS_TYPE.REDDIT;
	}

	@Override
	public String getName() {
		return "Reddit";
	}

	public static void main(String[] args) throws IOException {
		
		MagicNews n = new MagicNews();
		n.setName("mtgfinance");
		
		new RedditNewsProvider().listNews(n);
	}

}
