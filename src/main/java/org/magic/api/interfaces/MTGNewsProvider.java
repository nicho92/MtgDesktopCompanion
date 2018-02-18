package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNews.NEWS_TYPE;
import org.magic.api.beans.MagicNewsContent;

public interface MTGNewsProvider extends MTGPlugin {

	
	public List<MagicNewsContent> listNews(MagicNews n) throws IOException;
	public NEWS_TYPE getProviderType();
	
}
