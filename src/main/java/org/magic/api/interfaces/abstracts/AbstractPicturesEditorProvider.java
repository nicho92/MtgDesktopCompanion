package org.magic.api.interfaces.abstracts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExtraCardMetaData;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.beans.enums.EnumSecurityStamp;
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

public abstract class AbstractPicturesEditorProvider extends AbstractMTGPlugin implements MTGPictureEditor {

	@Override
	public PLUGINS getType() {
		return PLUGINS.EDITOR;
	}

	protected File toFile(String url, String fileName) throws IOException {
		if (url.startsWith("http")) {
			var f = new File(MTGConstants.MTG_WALLPAPER_DIRECTORY, fileName);
			URLTools.download(url, f);
			return f;
		} else {
			return new File(url);
		}

	}
	
	protected abstract BufferedImage generatePicture(MTGCard mc, MTGEdition ed) throws IOException;

	protected abstract String getFrameVersion();
	

	private void updateCardData(MTGCard mc) {
		mc.getCustomMetadata().put(EnumExtraCardMetaData.PLUGIN_NAME, getName());
	
		if (mc.getRarity() == EnumRarity.MYTHIC || mc.getRarity() == EnumRarity.RARE)
			mc.setSecurityStamp(EnumSecurityStamp.OVAL);
		else
			mc.setSecurityStamp(EnumSecurityStamp.NONE);
		
		if(mc.isRetro())
		    mc.setFrameVersion("1993");
		else
    		    mc.setFrameVersion(getFrameVersion());
		
		
	}

	public BufferedImage getPicture(MTGCard mc, MTGEdition me) throws IOException {
	    updateCardData(mc);
	    return generatePicture(mc, me);
	}
	

}
