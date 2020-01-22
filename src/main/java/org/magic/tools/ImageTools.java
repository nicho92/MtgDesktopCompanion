package org.magic.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
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
import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

public class ImageTools {

	private static BufferedImage[] imgs;
	private static Logger logger = MTGLogger.getLogger(ImageTools.class);
	
	private ImageTools() {
	}
	
	public static boolean isImage(File f)
	{
		return isImage(f.toPath());
	}
	
	
	public static boolean isImage(Path f)
	{
		if(f==null)
			return false;
		
		
			try {
				return Files.probeContentType(f).startsWith("image");
			} catch (Exception e) {
				return false;
			}
	}
	
	
	public static BufferedImage[] splitManaImage()
	{
		if(imgs!=null)
			return imgs;
		
		int cols = 10;
		int rows = 7;
		int chunkWidth = 100;
		int chunkHeight = 100;
		
		int count=0;
		BufferedImage image;
		imgs= new BufferedImage[cols*rows];
			try {
				image = ImageIO.read(MTGConstants.URL_MANA_SYMBOLS);
				for (int x = 0; x < rows; x++) {
					for (int y = 0; y < cols; y++) {
						imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());
						Graphics2D gr = imgs[count++].createGraphics();
						gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x,
								chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
						gr.dispose();
					}
				}
			} catch (IOException e) {
				logger.error(e);
			}
			return imgs;
		
	}
		
	public static byte[] toByteArray(BufferedImage o) {
        if(o != null) {
            BufferedImage image = o;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            try {
                ImageIO.write(image, "png", baos);
            } catch (IOException e) {
                throw new IllegalStateException(e.toString());
            }
            return baos.toByteArray();
        }
        return new byte[0];
	}
	
	public static BufferedImage fromByteArray(byte[] imagebytes) {
        try {
            if (imagebytes != null && (imagebytes.length > 0)) {
                return ImageIO.read(new ByteArrayInputStream(imagebytes));
            }
            return null;
        } catch (IOException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }
	

	public static void saveImage(BufferedImage img, File f, String format) throws IOException {
		ImageIO.write(img, format, f);
	}

	
	public static BufferedImage trimAlpha(BufferedImage img) {
		
		if(img==null)
			return img;
		
	    int width = img.getWidth();
	    int height = img.getHeight();
	    int x0;
	    int x1;
	    int j;
	    int i;

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
		double ratio = (double)oldW/oldH;
		int h = (int) (width/ratio);
		return resize(img, h, width);
	}
	
	public static void resize(BufferedImage img, Dimension pictureProviderDimension) {
		resize(img,(int)pictureProviderDimension.getHeight(),(int)pictureProviderDimension.getWidth());
		
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
		
	     BufferedImage bi = new BufferedImage(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
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
	

	public static String toBase64(Image img) {
		if(img==null)
			return null;
	
		try (ByteArrayOutputStream os = new ByteArrayOutputStream())
		{
		    ImageIO.write((BufferedImage)img, "png", os);
		    return Base64.getEncoder().encodeToString(os.toByteArray());
		}
		catch (IOException ioe)
		{
		   logger.error(ioe);
		   return null;
		}
	}

	
	public static BufferedImage readBase64(String base) throws IOException
	{
		BufferedImage image = null;
		byte[] imageByte;
		
		if(base==null)
			return null;
		
		imageByte = Base64.getDecoder().decode(base);
		
		try(ByteArrayInputStream bis = new ByteArrayInputStream(imageByte))
		{
			image = ImageIO.read(bis);	
		}
		
		return image;
	}
	
	public static void saveImageInPng(BufferedImage img, File f) throws IOException {
		Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("png");
		
		if(!iw.hasNext())
			throw new IOException("PNG Writer not found");
		
		
		ImageWriter writer = iw.next();

		ImageWriteParam writeParam = writer.getDefaultWriteParam();
		ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
		IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
		setDPI(metadata);

		ImageOutputStream stream = ImageIO.createImageOutputStream(f);
		try {
			writer.setOutput(stream);
			writer.write(metadata, new IIOImage(img, null, metadata), writeParam);
		} finally {
			stream.close();
		}

	}
		
	
	public static Dimension toMM(Dimension d)
	{
		BigDecimal bd = BigDecimal.valueOf((d.getWidth() * 25.4) / MTGConstants.DPI);
				   bd=bd.setScale(2, RoundingMode.HALF_UP);
				   
	    BigDecimal bd2 = BigDecimal.valueOf((d.getHeight() * 25.4) / MTGConstants.DPI);
	    		   bd2=bd2.setScale(2, RoundingMode.HALF_UP);				   
				   
		return new Dimension((int)bd.doubleValue(), (int)bd2.doubleValue());
	}
	
	
	public static double toMM(double d)
	{
		BigDecimal bd = BigDecimal.valueOf((d * 25.4) / MTGConstants.DPI);
		bd=bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public static double toPX(double val)
	{
		BigDecimal bd = BigDecimal.valueOf((val / 25.4) * MTGConstants.DPI);
		bd=bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	

	 private static void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {
			double dotsPerMilli = 1.0 * MTGConstants.DPI / 10 / 2.54;
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

	public static BufferedImage readLocal(URL url) throws IOException {
			return ImageIO.read(url);
	}

	public static BufferedImage read(File file) throws IOException {
		return ImageIO.read(file);
	}
	
	public static BufferedImage read(byte[] imageInByte) throws IOException {
		return read(new ByteArrayInputStream(imageInByte));
	}

	public static BufferedImage read(InputStream inputStream) throws IOException {
		return ImageIO.read(inputStream);
	}

	public static void write(BufferedImage bi, String formatName, File file) throws IOException {
		ImageIO.write(bi, formatName, file);
		
	}

	public static void write(BufferedImage bi, String formatName, ByteArrayOutputStream baos) throws IOException {
		ImageIO.write(bi, formatName, baos);
		
	}


	
	
	
}
