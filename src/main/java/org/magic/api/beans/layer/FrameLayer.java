package org.magic.api.beans.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.magic.api.beans.abstracts.AbstractLayer;
import org.magic.api.beans.layer.enums.FrameType;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.ImageTools;

public class FrameLayer extends AbstractLayer {

    private transient BufferedImage image;
    private String path;
    private FrameType frameType;
    
    private boolean adaptedLegendaryFrame = false;
    
    
    public boolean isTypesLine() {
	return frameType==FrameType.TYPE_BORDER;
    }
    
    public boolean isTitleLine() {
   	return frameType==FrameType.TITLE_BORDER;
       }
    
    public void setAdaptedLegendaryFrame(boolean adaptedLegendaryFrame) {
	this.adaptedLegendaryFrame = adaptedLegendaryFrame;
    }
    
    public boolean isAdaptedLegendaryFrame() {
	return adaptedLegendaryFrame;
    }

    
    public void setFrameType(FrameType frameType) {
	this.frameType = frameType;
    }
    public FrameType getFrameType() {
	return frameType;
    }
    
    

    @Override
    public void paintLayer(Graphics2D g2) {
	
	var print = getImage();
	
	if(isTitleLine())
	{
	    print=image.getSubimage(0, 0, 2665, 261);
	    setHeight(261);
	}
	
	if(isTypesLine())
	{
	    print= image.getSubimage(0, 2149, 2665, 261);
	    setHeight(261);
	} 
	
	if(getFrameType()==FrameType.FRAME && isAdaptedLegendaryFrame())
	{
	    var nh = print.getHeight()-350;
	    print= image.getSubimage(0, 350, print.getWidth(), nh );
	    setHeight(nh);
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
	    MTGLogger.getLogger(this.getClass()).error("error with file {} : {}",getFile(),e.getMessage());
	}
       
     }

}
