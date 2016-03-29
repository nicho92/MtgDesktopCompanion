package org.magic.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageUtils {

	
	public static BufferedImage joinBufferedImage(List<Image> imgs) {
		
		int offset  = 0;
        int wid = offset;
        int height = 0;
        for(Image im : imgs)
        {
        	BufferedImage imgb = (BufferedImage)im;
        	wid+=imgb.getWidth();
        	if(imgb.getHeight()>=height)
        		height = imgb.getHeight();
        }
        
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        int x=0;
        for(Image im : imgs)
        {
        	BufferedImage imgb = (BufferedImage)im;
        	
        	
        	g2.drawImage(imgb, null, x, 0);
        	x+=imgb.getWidth();
        	
        	
        }
        g2.dispose();
        
        
        return newImage;
    }
}
