package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MTGPicturesCache {

	static final Logger logger = LogManager.getLogger(MTGPicturesCache.class.getName());

	
	public String getName();
	
	public BufferedImage getPic(MagicCard mc,MagicEdition ed);
	
	public void put(BufferedImage im,MagicCard mc,MagicEdition ed) throws Exception;
	
	
	public Properties getProperties();
	public void setProperties(String k,Object value);
	public Object getProperty(String k);
	public boolean isEnable();
	public void save();
	public void load();
	public void enable(boolean t);
}
