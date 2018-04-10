package org.magic.api.news.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNews.NEWS_TYPE;
import org.magic.api.beans.MagicNewsContent;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;
import org.magic.services.MTGConstants;

public class MagicCorpForumProvider extends AbstractMagicNewsProvider {

	
	public static void main(String[] args) throws IOException {
		MagicNews n = new MagicNews();
		n.setUrl("http://www.magiccorporation.com/gathering-forum-viewtopic-91-122496-dominaria-set-du-printemps-.html");
		
		new MagicCorpForumProvider().listNews(n);
	}
	
	
	
	@Override
	public List<MagicNewsContent> listNews(MagicNews n) throws IOException {
		List<MagicNewsContent> ret = new ArrayList<>();
		
		int maxpage=0;
		
		Document d = Jsoup.connect(n.getUrl()).userAgent(MTGConstants.USER_AGENT).get();
		
		maxpage = Integer.parseInt(d.select("a[title=Dernière Page]").first().text());
	
		String id="";
		for(int i=0;i<maxpage*getInt("PAGINATION");i+=getInt("PAGINATION"))
		{
			
			
			
			MagicNewsContent cont = new MagicNewsContent();
			cont.setAuthor("MagicCorp");
			cont.setLink(new URL("http://www.magiccorporation.com/gathering-forum-viewtopic-91-122496-"+id+"-dominaria-set-du-printemps-.html"));
			cont.setTitle("Page "+ id);
			id=String.valueOf(i);
			
			ret.add(cont);
		}
		
		
		return ret;
	}

	@Override
	public NEWS_TYPE getProviderType() {
		return NEWS_TYPE.FORUM;
	}

	@Override
	public String getName() {
		return "MagicCorporation Topic";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty("PAGINATION", 15);
		
	}

	@Override
	public String getVersion() {
		return "0.5b";
	}
	
	
	

}
