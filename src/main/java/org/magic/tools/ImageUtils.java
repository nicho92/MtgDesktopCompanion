package org.magic.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtils {

	private ImageUtils() {
	}

	public static void saveImage(BufferedImage img, File f, String format) throws IOException {
		ImageIO.write(img, format, f);
	}

	
	public static BufferedImage trimAlpha(BufferedImage img) {
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

	public static void saveImageInPng(BufferedImage img, File f,int dpi) throws IOException {
		for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext();) 
		{
		   ImageWriter writer = iw.next();
		   ImageWriteParam writeParam = writer.getDefaultWriteParam();
		   ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
		   IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
		   if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
		      continue;
		   }

		   setDPI(metadata,dpi);

		   ImageOutputStream stream = ImageIO.createImageOutputStream(f);
		   try {
		      writer.setOutput(stream);
		      writer.write(metadata, new IIOImage(img, null, metadata), writeParam);
		   } finally {
		      stream.close();
		   }
		   break;
		}
	}
		
	
	public static Dimension toMM(Dimension d,int dpi)
	{
		BigDecimal bd = BigDecimal.valueOf((d.getWidth() * 25.4) / dpi);
				   bd=bd.setScale(2, RoundingMode.HALF_UP);
				   
	    BigDecimal bd2 = BigDecimal.valueOf((d.getHeight() * 25.4) / dpi);
	    		   bd2=bd2.setScale(2, RoundingMode.HALF_UP);				   
				   
		return new Dimension((int)bd.doubleValue(), (int)bd2.doubleValue());
	}
	
	
	public static double toMM(double d,int dpi)
	{
		BigDecimal bd = BigDecimal.valueOf((d * 25.4) / dpi);
		bd=bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public static double toPX(double val,int dpi)
	{
		BigDecimal bd = BigDecimal.valueOf((val / 25.4) * dpi);
		bd=bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	

	 private static void setDPI(IIOMetadata metadata,int dpi) throws IIOInvalidTreeException {
			double dotsPerMilli = 1.0 * dpi / 10 / 2.54;
			IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
			horiz.setAttribute("value", Double.toString(dotsPerMilli));
			IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
			vert.setAttribute("value", Double.toString(dotsPerMilli));
			IIOMetadataNode dim = new IIOMetadataNode("Dimension");
			dim.appendChild(horiz);
			dim.appendChild(vert);
			IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
			root.appendChild(dim);
			metadata.mergeTree("javax_imageio_1.0", root);
	 }
	
	
	
	
}
