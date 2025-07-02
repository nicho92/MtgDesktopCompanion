package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGPictureEditor;

public abstract class AbstractPicturesEditorProvider extends AbstractMTGPlugin implements MTGPictureEditor {

	public static final String ACCENT = "ACCENT";
	public static final String CENTER = "CENTER";
	public static final String INDICATOR = "INDICATOR";
	public static final String SIZE = "SIZE";
	public static final String FOIL="FOIL";
	public static final String ZOOM="ZOOM";
	public static final String X = "X";
	public static final String Y = "Y";
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.EDITOR;
	}

}
