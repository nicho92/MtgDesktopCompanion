package org.magic.api.beans.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.magic.api.beans.abstracts.AbstractLayer;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.ImageTools;

public class FrameLayer extends AbstractLayer {

    private transient BufferedImage image;
    private boolean titleLine=false;
    private boolean typesLine=false;
    private String path;
    
    public void setTitleLineFrame(boolean b)
    {
	titleLine=b;
	typesLine=!b;
    }
 
    public void setTypesLineFrame(boolean b)
    {
	typesLine=b;
	titleLine=!b;
    }
    
    public boolean isTitleLine() {
	return titleLine;
    }
    
    public boolean isTypesLine() {
	return typesLine;
    }
    

    @Override
    public void paintLayer(Graphics2D g2) {
	
	var print = getImage();
	if(titleLine)
	{
	    print=image.getSubimage(0, 0, 2665, 261);
	    setHeight(261);
	}
	
	if(typesLine)
	{
	    print= image.getSubimage(0, 2151, 2665, 261);
	    setHeight(261);
	} 
	
	g2.drawImage(
		print,
	        getX(),
	        getY(),
	        getWidth(),
	        getHeight(),
	        null
	    );

    }
    
    public File getFile()
    {
	return new File(MTGConstants.MTG_COMPOSER_DIR,path);
    }
    
    public void setFile(File f)
    {
	this.path= MTGConstants.MTG_COMPOSER_DIR.toURI().relativize(f.toURI()).getPath();
    }
    
    public String getPath() {
	return path;
    }
    
    public void setPath(String path) {
	this.path = path;
    }
    
    
    public FrameLayer(File source) {
	setFile(source);
	setName(source.getName());
	reload();
	setWidth(image.getWidth());
	setHeight(image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }

    
    @Override
    public void reload() {
	
	try {
	    this.image =  ImageTools.read(getFile());
	} catch (IOException e) {
	    MTGLogger.getLogger(this.getClass()).error(e);
	}
       
     }

}