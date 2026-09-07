package org.magic.composer.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.gson.JsonObject;

public class FrameLayer extends AbstractLayer {

    private transient BufferedImage image;
     
    public FrameLayer(File source) {
	
	super(source);
	
        try {
	    this.image =  ImageIO.read(source);
	} catch (IOException e) {
	   logger.error(e);
	}
        
    }
    
    @Override
    public JsonObject toJson() {
       var obj =super.toJson();
       	obj.addProperty("width", getWidth());
       	obj.addProperty("height", getHeight());
       return obj;
       
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