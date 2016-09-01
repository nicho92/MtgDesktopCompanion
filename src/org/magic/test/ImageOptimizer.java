package org.magic.test;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageOptimizer {

	
	public static void main(String[] args) throws IOException {
		
		for(File f : new File("D:\\programmation\\GIT\\MtgDesktopCompanion\\src\\res\\set\\icons").listFiles())
		{
			Image temp = ImageIO.read(f).getScaledInstance(26, 24, Image.SCALE_SMOOTH);
			BufferedImage newImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null),BufferedImage.TYPE_INT_ARGB);
				Graphics g = newImage.getGraphics();
				g.drawImage(temp, 0, 0, null);
				g.dispose();
			
			
			ImageIO.write(newImage, "png", f);
		}
		
		
	}
}
