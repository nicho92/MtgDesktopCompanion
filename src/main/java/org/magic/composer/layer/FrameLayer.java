package org.magic.composer.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.gson.JsonObject;

public class FrameLayer extends AbstractLayer {

    private transient BufferedImage image;
    protected transient File source; 
    
    public FrameLayer(File source) {
	
    	try {
		    this.image =  ImageIO.read(source);
		    setName(source.getName());
		} catch (IOException e) {
		   logger.error(e);
		}
        
    }
    
    @Override
    public JsonObject toJson() {
       var obj =super.toJson();
      
   	    obj.addProperty("path", source.getAbsolutePath());
       return obj;
       
    }
    
    

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }
    
    @Override
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