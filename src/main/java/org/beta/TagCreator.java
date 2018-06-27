package org.beta;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.services.extra.BoosterPicturesProvider;
import org.magic.tools.ImageUtils;

public class TagCreator {

	
	public static void main(String[] args) throws IOException {
		BoosterPicturesProvider prov = new BoosterPicturesProvider();
		List<Image> ims = new ArrayList<>();
		
		
		for(String id:prov.listEditionsID())
		{
			if(id.startsWith("DD"))
			{
				Image im = prov.getBannerFor(id);
				if(im!=null)
					ims.add(im);

			}
		}
		
		ImageUtils.saveImage(add(ims), new File("d:/test.png"),"PNG");
		
	}
	
	public static BufferedImage add(List<Image> imgs) {

		int offset = 0;
		int hei = offset;
		int width=567;//4.801 cm
		for (Image im : imgs) {
			BufferedImage imgb = (BufferedImage) im;
			imgb=ImageUtils.scaleResize(imgb, width);
			hei += imgb.getHeight();
		}
		
		BufferedImage newImage = new BufferedImage(width, hei, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		int x = 0;
		for (Image im : imgs) {
			BufferedImage imgb = (BufferedImage) im;
			imgb=ImageUtils.scaleResize(imgb, width);
			g2.drawImage(imgb, null, 0, x);
			x += imgb.getHeight();
		}
		g2.dispose();
		return newImage;
	}
	

}
