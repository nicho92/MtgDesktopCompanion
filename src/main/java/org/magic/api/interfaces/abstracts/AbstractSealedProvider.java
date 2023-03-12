package org.magic.api.interfaces.abstracts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;

public abstract class AbstractSealedProvider extends AbstractMTGPlugin  implements MTGSealedProvider {

	private static final String PACKAGING_DIR_NAME = "packaging";

	

	public List<MTGSealedProduct> getItemsFor(String me)
	{
		return getItemsFor(new MagicEdition(me));
	}

	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t, String lang, EnumExtra extra)
	{
		return get(me,t).stream().filter(e->e.getLang().equalsIgnoreCase(lang) && e.getExtra()==extra).toList();
	}

	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t, String lang)
	{
		return get(me,t).stream().filter(e->e.getLang().equalsIgnoreCase(lang)).toList();
	}

	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t, EnumExtra extra)
	{
		if(extra==null)
			return get(me,t);

		return get(me,t).stream().filter(e->e.getExtra()==extra).toList();
	}

	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t)
	{
		return getItemsFor(me).stream().filter(e->e.getTypeProduct()==t).toList();
	}

	
	
	public BufferedImage getPictureFor(MTGSealedProduct p)
	{
		try {
			var b=Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), PACKAGING_DIR_NAME,p.getEdition().getId().replace("CON", "CON_"),p.getTypeProduct().name(),p.getStoreId()+".png").toFile();

			if(b.exists())
				return ImageTools.read(b);
			else
				return caching(false, p);
		} catch (Exception e) {
			logger.error("[{}] ERROR for {}-{} : {}",p.getEdition().getId(),p.getTypeProduct(),p.getUrl(),e.getMessage());
			return null;
		}
	}


	public void clear() {
		var f = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), PACKAGING_DIR_NAME).toFile();
		try {
			FileTools.cleanDirectory(f);
		} catch (IOException e) {
			logger.error("error removing data in {}",f,e);
		}
	}
	
	
	protected BufferedImage caching(boolean force, MTGSealedProduct p) {

		if(p.getUrl()==null)
			return null;

		var f = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), PACKAGING_DIR_NAME,p.getEdition().getId().replace("CON", "CON_"),p.getTypeProduct().name()).toFile();
		var pkgFile = new File(f,p.getStoreId()+".png");

		try {
			FileTools.forceMkdir(f);
			if(force||!pkgFile.exists())
			{
				BufferedImage im = URLTools.extractAsImage(p.getUrl());
				ImageTools.saveImage(im, pkgFile, "PNG");
				logger.debug("[{}] SAVED for {}-{}",p.getEdition().getId(),p.getTypeProduct(),p);
				return im;
			}
		} catch (Exception e) {
			logger.error("[{}] ERROR for {}-{} : {}",p.getEdition().getId(),p.getTypeProduct(),p.getUrl(),e.getMessage());
		}
		return null;

	}


	@Override
	public PLUGINS getType() {
		return PLUGINS.SEALED;
	}

	
}
