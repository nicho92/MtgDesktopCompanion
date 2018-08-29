package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.services.MTGConstants;

public abstract class AbstractPicturesEditorProvider extends AbstractMTGPlugin implements MTGPictureEditor {

	@Override
	public PLUGINS getType() {
		return PLUGINS.EDITOR;
	}


	public AbstractPicturesEditorProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "editors");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}

	@Override
	public void initDefault() {
	}

}
