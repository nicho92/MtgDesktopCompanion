package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Properties;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface PictureProvider {

	
	public BufferedImage getPicture(MagicCard mc,MagicEdition me) throws Exception;
	public BufferedImage getSetLogo(String setID,String rarity) throws Exception;
	public BufferedImage getBackPicture() throws Exception;
	public String getName();
	public BufferedImage extractPicture(MagicCard mc) throws Exception;
	
	
	public Properties getProperties();
	public void setProperties(String k,Object value);
	public Object getProperty(String k);
	public boolean isEnable();
	public void save();
	public void load();
	public void enable(boolean t);
}
