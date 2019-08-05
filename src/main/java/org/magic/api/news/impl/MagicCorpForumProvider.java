package org.magic.api.news.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jsoup.nodes.Document;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNewsContent;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;
import org.magic.tools.URLTools;

public class MagicCorpForumProvider extends AbstractMagicNewsProvider {

	private static final String SITE = "SITE";
	private static final String PAGINATION = "PAGINATION";
	private String prefixForum = "gathering-forum-viewtopic-";

	
	@Override
	public List<MagicNewsContent> listNews(MagicNews n) throws IOException {
		List<MagicNewsContent> ret = new ArrayList<>();
		int maxpage = 0;
		Document d = URLTools.extractHtml(n.getUrl());
		try {
			
			String text = d.select("div.jump_page").text();
			maxpage = Integer.parseInt(text.substring(text.indexOf('/')+1,text.indexOf('-')).trim());
		} catch (Exception e) {
			maxpage = 1;
		}

		String suffix = n.getUrl().replaceAll(getString(SITE) + prefixForum, "");
		String idForum = suffix.split("-")[0];
		String idTopic = suffix.split("-")[1];
		String endUri = n.getUrl().substring(n.getUrl().indexOf(idTopic) + idTopic.length() + 1);

		String id = "";
		for (int i = 0; i < maxpage * getInt(PAGINATION); i += getInt(PAGINATION)) {
			MagicNewsContent cont = new MagicNewsContent();
			cont.setAuthor("MagicCorp");

			if (i == 0)
				id = "";
			else
				id = i + "-";

			cont.setLink(new URL(getString(SITE) + prefixForum + idForum + "-" + idTopic + "-" + id + endUri));
			cont.setTitle("Page " + id);
			ret.add(cont);
		}

		return ret;
	}

	

	@Override
	public String getName() {
		return "MagicCorporation Topic";
	}
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/magiccorporation.png"));
	}
	

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty(PAGINATION, "15");
		setProperty(SITE, "http://www.magiccorporation.com/");
	}

	@Override
	public String getVersion() {
		return "0.5b";
	}
	

	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

}
