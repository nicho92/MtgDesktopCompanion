package org.magic.api.beans.layer;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.ImageTools;

public class FrameLayer extends AbstractLayer {

    private transient BufferedImage image;
    protected File source; 
    private boolean titleLine=false;
    private boolean typesLine=false;
    
    
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
    public void paint(Graphics2D g2) {
	
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
	
	
	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
	
	
	g2.drawImage(
		print,
	        getX(),
	        getY(),
	        getWidth(),
	        getHeight(),
	        null
	    );
	
	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	
    }
    
    public FrameLayer(File source) {
	this.source=source;
	setName(source.getName());
	reload();
	setWidth(image.getWidth());
	setHeight(image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
        reload();
    }
    
    @Override
    public void reload() {
	
	try {
	    this.image =  ImageTools.read(source);
	} catch (IOException e) {
	    MTGLogger.getLogger(this.getClass()).error(e);
	}
       
     }

}