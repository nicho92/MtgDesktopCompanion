package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;

import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

public abstract class AbstractPicturesEditorProvider extends AbstractMTGPlugin implements MTGPictureEditor {
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.EDITOR;
	}

	protected File toFile(String url, String fileName) throws IOException
	{
		if(url.startsWith("http"))
		{
			var f = new File(MTGConstants.MTG_WALLPAPER_DIRECTORY,fileName);
			URLTools.download(url, f);
			return f;
		}
		else
		{
			return new File(url);
		}
		
	}
	
	
}
