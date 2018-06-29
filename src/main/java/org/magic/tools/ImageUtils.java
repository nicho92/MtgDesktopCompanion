package org.magic.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageUtils {

	private ImageUtils() {
	}

	public static void saveImage(BufferedImage img, File f, String format) throws IOException {
		ImageIO.write(img, format, f);
	}

	
	public static BufferedImage trimHorizontally(BufferedImage img) {
	    int width = img.getWidth();
	    int height = img.getHeight();
	    int x0;
	    int x1;
	    int j;
	    int i;
	    int alpha;

	    leftLoop:
	        for(i = 0; i < width; i++) {

	            for(j = 0; j < height; j++) {
	                if(new Color(img.getRGB(i, j), true).getAlpha() != 0)  {
	                    break leftLoop;
	                }
	            }
	        }
	    x0 = i;
	    rightLoop:
	        for(i = width-1; i >= 0; i--) {

	            for(j = 0; j < height; j++) {
	                if(new Color(img.getRGB(i, j), true).getAlpha() != 0) {
	                    break rightLoop;
	                }
	            }
	        }
	    x1 = i+1;

	    return img.getSubimage(x0, 0, x1-x0, height);
	}
	
	public static BufferedImage scaleResize(BufferedImage img, int width)
	{
		int oldW=img.getWidth();
		int oldH=img.getHeight();
		double ratio = oldW/oldH;
		int h = (int) (width/ratio);
		return resize(img, h, width);
	}
	
	
	public static BufferedImage resize(BufferedImage img, int newH, int newW) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}
	
	public static BufferedImage imageToBufferedImage(Image im) {
		
	     BufferedImage bi = new BufferedImage
	        (im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
	     Graphics bg = bi.getGraphics();
	     bg.drawImage(im, 0, 0, null);
	     bg.dispose();
	     return bi;
	  }

	public static BufferedImage joinBufferedImage(List<Image> imgs) {

		int offset = 0;
		int wid = offset;
		int height = 0;
		for (Image im : imgs) {
			BufferedImage imgb = (BufferedImage) im;
			wid += imgb.getWidth();
			if (imgb.getHeight() >= height)
				height = imgb.getHeight();
		}

		// create a new buffer and draw two image into the new image
		BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		int x = 0;
		for (Image im : imgs) {
			BufferedImage imgb = (BufferedImage) im;

			g2.drawImage(imgb, null, x, 0);
			x += imgb.getWidth();

		}
		g2.dispose();

		return newImage;
	}
	
	
	
}
