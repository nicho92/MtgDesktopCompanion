package org.magic.api.news.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.magic.api.beans.MTGNews;
import org.magic.api.beans.MTGNewsContent;
import org.magic.api.interfaces.abstracts.AbstractMagicNewsProvider;
import org.magic.services.network.URLTools;

public class MagicCorpForumProvider extends AbstractMagicNewsProvider {

	private static final String SITE = "SITE";
	private static final String PAGINATION = "PAGINATION";
	private String prefixForum = "gathering-forum-viewtopic-";


	@Override
	public List<MTGNewsContent> listNews(MTGNews n) throws IOException {
		List<MTGNewsContent> ret = new ArrayList<>();
		var maxpage = 0;
		Document d = URLTools.extractAsHtml(n.getUrl());
		try {

			String text = d.select("div.jump_page").text();
			maxpage = Integer.parseInt(text.substring(text.indexOf('/')+1,text.indexOf('-')).trim());
		} catch (Exception e) {
			maxpage = 1;
		}

		var suffix = n.getUrl().replaceAll(getString(SITE) + prefixForum, "");
		var idForum = suffix.split("-")[0];
		var idTopic = suffix.split("-")[1];
		var endUri = n.getUrl().substring(n.getUrl().indexOf(idTopic) + idTopic.length() + 1);

		var id = "";
		for (var i = 0; i < maxpage * getInt(PAGINATION); i += getInt(PAGINATION)) {
			var cont = new MTGNewsContent();
			cont.setAuthor("MagicCorp");

			if (i == 0)
				id = "";
			else
				id = i + "-";

			cont.setLink( URI.create(getString(SITE) + prefixForum + idForum + "-" + idTopic + "-" + id + endUri).toURL());
			cont.setTitle("Page " + id);
			ret.add(cont);
		}

		return ret;
	}



	@Override
	public String getName() {
		return "MagicCorporation";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(PAGINATION, "15",
								SITE, "http://www.magiccorporation.com/");
	}

	@Override
	public String getVersion() {
		return "0.5b";
	}



}
