package org.magic.composer.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FrameLayer extends AbstractLayer {

    private transient BufferedImage image;
    protected File source; 
    
    public FrameLayer(File source) {
	
    	try {
    	    this.source=source;
		    this.image =  ImageIO.read(source);
		    setName(source.getName());
		    
		    width = image.getWidth();
		    height = image.getHeight();
		} catch (IOException e) {
		   logger.error(e);
		}
        
    }
    
  
    
    

    public BufferedImage getImage() {
        return image;
    }


    @Override
    public void paint(Graphics2D g2) {
	g2.drawImage(
	        getImage(),
	        getX(),
	        getY(),
	        null
	    );
	
    }
}