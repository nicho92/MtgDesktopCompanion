package org.beta.composer.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLayer extends AbstractLayer {

    private BufferedImage image;
     
    public ImageLayer(File source) {
	
	super(source);
	
        try {
	    this.image =  ImageIO.read(source);
	} catch (IOException e) {
	   logger.error(e);
	}
        
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
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