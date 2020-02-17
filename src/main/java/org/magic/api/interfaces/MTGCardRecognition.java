package org.magic.api.interfaces;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicEdition;
import org.magic.services.recognition.DescContainer;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;

public interface MTGCardRecognition extends MTGPlugin {


	public MatchResult getMatch(ImageDesc id, double thresh);
	public void loadDatasFromFile(File handle) throws IOException;
	public File downloadCardsData(MagicEdition set) throws IOException;
	public void loadDatasForSet(MagicEdition set);
	public void loadDatasForSet(String set);
	public void finalizeLoad();

	public void clear();
	public void clear(MagicEdition ed);
	public void add(DescContainer dc);

	public int size();
	public boolean isCached(MagicEdition ed);
	public boolean isSetLoaded(MagicEdition ed);

}
