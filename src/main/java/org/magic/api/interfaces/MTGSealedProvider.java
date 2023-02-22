package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.util.List;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.sealedprovider.impl.MTGCompanionSealedProvider.LOGO;

public interface MTGSealedProvider extends MTGPlugin {

	
	public List<MTGSealedProduct> getItemsFor(String me);
	public List<MTGSealedProduct> getItemsFor(MagicEdition me);
	public List<MTGSealedProduct> search(String name);
		
	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t, String lang, EnumExtra extra);
	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t, String lang);
	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t, EnumExtra extra);
	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t);
	
	public BufferedImage getLogo(LOGO logo);
	
	public BufferedImage getPictureFor(MTGSealedProduct p);
	

	
}
