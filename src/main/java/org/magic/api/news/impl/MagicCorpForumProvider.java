package org.magic.api.news.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNewsContent;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;
import org.magic.services.MTGConstants;

public class MagicCorpForumProvider extends AbstractMagicNewsProvider {

	private String prefixForum="gathering-forum-viewtopic-";
	
	
	public static void main(String[] args) throws IOException {
		MagicNews n = new MagicNews();
		n.setUrl("http://www.magiccorporation.com/gathering-forum-viewtopic-91-122496-dominaria-set-du-printemps-.html");
			   // http://www.magiccorporation.com/gathering-forum-viewtopic-3-124166-questions-sur-manaless-dredge.html
		List<MagicNewsContent> news = new MagicCorpForumProvider().listNews(n);
		
		for(MagicNewsContent mnc : news)
			System.out.println(mnc.getLink());
		
	}
	
	
	
	@Override
	public List<MagicNewsContent> listNews(MagicNews n) throws IOException {
		List<MagicNewsContent> ret = new ArrayList<>();
		int maxpage=0;
		Document d = Jsoup.connect(n.getUrl()).userAgent(MTGConstants.USER_AGENT).get();
		try{
			maxpage = Integer.parseInt(d.select("a[title=Dernière Page]").first().text());
		}
		catch(Exception e)
		{
			maxpage=1;
		}
		
		
		String suffix=n.getUrl().replaceAll(getString("SITE")+prefixForum, ""); 
		String idForum=suffix.split("-")[0];
		String idTopic=suffix.split("-")[1];
		String endUri=n.getUrl().substring(n.getUrl().indexOf(idTopic)+idTopic.length()+1);
				
		String id="";
		for(int i=0;i<maxpage*getInt("PAGINATION");i+=getInt("PAGINATION"))
		{
			MagicNewsContent cont = new MagicNewsContent();
			cont.setAuthor("MagicCorp");
			
			if(i==0)
				id="";
			else
				id=i+"-";
			
			cont.setLink(new URL(getString("SITE")+prefixForum+idForum+"-"+idTopic+"-"+id+endUri));
			cont.setTitle("Page "+ id);
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
		setProperty("PAGINATION", "15");
		setProperty("SITE", "http://www.magiccorporation.com/");
	}

	@Override
	public String getVersion() {
		return "0.5b";
	}
	
	
	

}
