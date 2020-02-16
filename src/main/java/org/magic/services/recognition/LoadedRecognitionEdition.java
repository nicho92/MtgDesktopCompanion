package org.magic.services.recognition;

import org.magic.api.beans.MagicEdition;

public class LoadedRecognitionEdition {

	
	private MagicEdition edition;
	private boolean loaded=false;
	private boolean cached;
	
	
	public LoadedRecognitionEdition(MagicEdition ed,boolean cached) {
		setEdition(ed);
		setCached(cached);
		setLoaded(false);
	}
	
	
	
	
	public MagicEdition getEdition() {
		return edition;
	}
	public void setEdition(MagicEdition edition) {
		this.edition = edition;
	}
	public boolean isLoaded() {
		return loaded;
	}
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	public boolean isCached() {
		return cached;
	}
	public void setCached(boolean cached) {
		this.cached = cached;
	}
	
	
	
}
