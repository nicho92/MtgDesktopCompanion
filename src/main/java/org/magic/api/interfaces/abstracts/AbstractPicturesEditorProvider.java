package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGPictureEditor;

public abstract class AbstractPicturesEditorProvider extends AbstractMTGPlugin implements MTGPictureEditor {
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.EDITOR;
	}

}
