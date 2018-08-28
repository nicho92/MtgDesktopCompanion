package org.magic.api.interfaces;

import java.net.URI;

public interface MTGPictureEditor extends MTGPictureProvider {

	
	public void setFoil(Boolean b);
	public void setTextSize(int size);
	public void setCenter(boolean center);
	public void setImage(URI img);
	public void setColorIndicator(boolean selected);
	public void setColorAccentuation(String c);
}
