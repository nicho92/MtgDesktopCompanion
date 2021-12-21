package org.magic.api.interfaces;

import org.magic.api.beans.MagicEdition;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.LoadedRecognitionEdition;
import org.magic.services.recognition.MatchResult;

public interface MTGCardRecognition extends MTGPlugin {


	public MatchResult getMatch(ImageDesc id, double thresh);
	
	public void loadDatasForSet(LoadedRecognitionEdition ed);
	public void finalizeLoad();

	public void clear();
	public void clear(MagicEdition ed);

	public int size();
	public boolean isCached(MagicEdition ed);
	public boolean isSetLoaded(MagicEdition ed);

}
