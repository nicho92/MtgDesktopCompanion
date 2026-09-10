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
	this.source=source;
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
	    this.image =  ImageIO.read(source);
          } catch (IOException e) {
        	   logger.error(e);
       	}
     }

    @Override
    public void paint(Graphics2D g2) {
	g2.drawImage(
	        getImage(),
	        getX(),
	        getY(),
	        getWidth(),
	        getHeight(),
	        null
	    );
	
    }
}