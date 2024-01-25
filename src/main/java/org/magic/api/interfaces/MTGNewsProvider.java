package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGNews;
import org.magic.api.beans.MTGNewsContent;

public interface MTGNewsProvider extends MTGPlugin {

	public List<MTGNewsContent> listNews(MTGNews n) throws IOException;
}
