package org.magic.api.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicEdition;
import org.magic.services.recognition.DescContainer;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;

public interface MTGCardRecognition extends MTGPlugin {


	public MatchResult getMatch(ImageDesc id, double thresh);

	public void loadDatasForSet(MagicEdition ed);
	public void finalizeLoad();
	public void clear();
	public void clear(MagicEdition ed);
	public boolean isSetCached(MagicEdition ed);
	public boolean isSetLoaded(MagicEdition ed);
	public File downloadCardsData(MagicEdition set) throws IOException;
	public Map<String, List<DescContainer>> getDataList();
	void loadAllCachedData() throws IOException;
}
