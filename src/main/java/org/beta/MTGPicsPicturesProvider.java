package org.beta;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.tools.URLTools;

public class MTGPicsPicturesProvider extends AbstractPicturesProvider {

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateUrl(MagicCard mc, MagicEdition me) {
		
		if(me==null)
			me=mc.getCurrentSet();
		
		return "https://www.mtgpics.com/pics/big/"+me.getId().toLowerCase()+"/"+me.getNumber()+".jpg";
	}

	@Override
	public String getName() {
		return "MTGPics";
	}

	@Override
	public BufferedImage getOnlinePicture(MagicCard mc, MagicEdition ed) throws IOException {
		return URLTools.extractImage(generateUrl(mc, ed));
	}


}
