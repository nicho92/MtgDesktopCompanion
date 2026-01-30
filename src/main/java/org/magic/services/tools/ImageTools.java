package org.magic.services.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
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
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.audit.FileAccessInfo;
import org.magic.api.beans.technical.audit.FileAccessInfo.ACCESSTYPE;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;

public class ImageTools {

	private static BufferedImage[] imgs;
	private static Logger logger = MTGLogger.getLogger(ImageTools.class);

	private ImageTools() {
	}

	public static boolean isImage(File f)
	{
		return isImage(f.toPath());
	}


	public static boolean isImage(byte[] array)
	{
		try{
			toImage(array);
			return true;
		}catch(Exception e)
		{
			logger.error(e);
			return false;
		}
	}


	public static boolean isImage(Path f)
	{
		if(f==null)
			return false;

			try {
				return Files.probeContentType(f).startsWith("image");
			} catch (Exception _) {
				return false;
			}
	}

	public static BufferedImage rotate(BufferedImage img, double angle) {
        double angleRadians = Math.toRadians(angle);
        int width = img.getWidth();
        int height = img.getHeight();
        double x = width/2.0;
        double y = height/2.0;

        double cos = Math.abs(Math.cos(angleRadians));
        double sin = Math.abs(Math.sin(angleRadians));

        int w = (int) (width * cos + height * sin + 0.5);
        int h = (int) (width * sin + height * cos + 0.5);
        var result = new BufferedImage(w, h, img.getType());

        var g = result.createGraphics();
        g.translate((w - img.getWidth()) / 2, (h - img.getHeight()) / 2);
        g.rotate(angleRadians, x, y);
        g.drawRenderedImage(img, null);
        g.dispose();

        return result;
    }
	
	public static BufferedImage fastBlur(BufferedImage src, int radius, double scale)
	{
		 int w = src.getWidth();
	        int h = src.getHeight();

	        int sw = Math.max(1, (int) (w * scale));
	        int sh = Math.max(1, (int) (h * scale));

	        // 1️⃣ Downscale
	        BufferedImage small = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g = small.createGraphics();
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                           RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        g.drawImage(src, 0, 0, sw, sh, null);
	        g.dispose();

	        // 2️⃣ Blur (StackBlur rapide)
	        int adjustedRadius = Math.max(1, (int) (radius * scale));
	        var blurredSmall = blurImage(small, adjustedRadius);

	        // 3️⃣ Upscale
	        var result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	        g = result.createGraphics();
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                           RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        g.drawImage(blurredSmall, 0, 0, w, h, null);
	        g.dispose();

	        return result;
	}
	

	public static BufferedImage blurImage(BufferedImage src, int radius) {
		
		if (radius < 1) 
			return src;

        int w = src.getWidth();
        int h = src.getHeight();

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(src, 0, 0, null);

        int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

        int wm = w - 1; 
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];

        int rsum;
        int gsum;
        int bsum;
        int x;
        int y;
        int i;
        int p;
        int yp;
        int yi;
        int yw;
        int[] vmin = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < dv.length; i++) {
            dv[i] = i / divsum;
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int routsum;
        int goutsum;
        int boutsum;
        int rinsum;
        int ginsum;
        int binsum;

        // Horizontal
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pixels[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p >> 16) & 0xff;
                sir[1] = (p >> 8) & 0xff;
                sir[2] = p & 0xff;
                rbs = radius + 1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) vmin[x] = Math.min(x + radius + 1, wm);
                p = pixels[yw + vmin[x]];

                sir[0] = (p >> 16) & 0xff;
                sir[1] = (p >> 8) & 0xff;
                sir[2] = p & 0xff;

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }

        // Vertical
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = radius + 1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) yp += w;
            }
            yi = x;
            stackpointer = radius;

            for (y = 0; y < h; y++) {
                pixels[yi] = (0xff000000)
                        | (dv[rsum] << 16)
                        | (dv[gsum] << 8)
                        | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) vmin[y] = Math.min(y + radius + 1, hm) * w;
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        return img;
		
	}
	
	
	
	public static BufferedImage getScaledImage(BufferedImage src){

		var squareSize = 300;
		var finalw = squareSize;
		var finalh = squareSize;
		double factor;
		if(src.getWidth() > src.getHeight()){
			factor = ((double)src.getHeight()/(double)src.getWidth());
			finalh = (int)(finalw * factor);
		}else{
			factor = ((double)src.getWidth()/(double)src.getHeight());
			finalw = (int)(finalh * factor);
		}

		var resizedImg = new BufferedImage(finalw, finalh, Transparency.TRANSLUCENT);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(src, 0, 0, finalw, finalh, null);
		g2.dispose();
		return resizedImg;
	}


	public static BufferedImage[] splitManaImage()
	{
		if(imgs!=null)
			return imgs;

		var cols = 10;
		var rows = 9;
		var chunkWidth = 100;
		var chunkHeight = 100;

		var count=0;
		BufferedImage image;
		imgs= new BufferedImage[cols*rows];
			try {
				image = ImageIO.read(MTGConstants.URL_MANA_SYMBOLS);
				for (var x = 0; x < rows; x++) {
					for (var y = 0; y < cols; y++) {
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
            var baos = new ByteArrayOutputStream(1024);
            try {
                ImageIO.write(image, "png", baos);
            } catch (IOException e) {
                throw new IllegalStateException(e.toString());
            }
            return baos.toByteArray();
        }
        return new byte[0];
	}

	public static BufferedImage fromByteArray(byte[] imagebytes){
		
		if(imagebytes==null)
			return null;
			
        try  (var stream = new ByteArrayInputStream(imagebytes)){
            if (imagebytes.length > 0) {
                return ImageIO.read(stream);
            }
            return null;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }
    }


	public static void saveImage(BufferedImage img, File f, String format) throws IOException {
		var info = new FileAccessInfo(f);
		ImageIO.write(img, format, f);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
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

	public static BufferedImage mirroring(BufferedImage image) {
		var tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-image.getWidth(null), 0);
		var op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		return image;
	}

	public static void initGraphics(Graphics2D g2d)
	{
		   g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		   g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		   g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		   g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

	}
	
	public static BufferedImage resize( Image img, int newH, int newW) {
		
		if(img==null)
			return null;
		
		var tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		var dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
				  initGraphics(g2d);


		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static BufferedImage imageToBufferedImage(Image im) {

		var bi = new BufferedImage(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
		var bg = bi.getGraphics();
	     bg.drawImage(im, 0, 0, null);
	     bg.dispose();
	     return bi;
	  }

	public static BufferedImage joinBufferedImage(List<Image> imgs) {

		var offset = 0;
		int wid = offset;
		var height = 0;
		for (Image im : imgs) {
			BufferedImage imgb = (BufferedImage) im;
			wid += imgb.getWidth();
			if (imgb.getHeight() >= height)
				height = imgb.getHeight();
		}

		// create a new buffer and draw two image into the new image
		var newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		var x = 0;
		for (Image im : imgs) {
			BufferedImage imgb = (BufferedImage) im;

			g2.drawImage(imgb, null, x, 0);
			x += imgb.getWidth();

		}
		g2.dispose();

		return newImage;
	}



	public static BufferedImage toImage(byte[] img) throws IOException {
	    return ImageIO.read(new ByteArrayInputStream(img));
	}


	public static ImageIcon resize(Icon icon, int newH, int newW) {
		var ic = ((ImageIcon)icon).getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		return new ImageIcon(ic);
	}

	public static ImageIcon resize(Icon icon, Dimension d) {
		return resize(icon,(int)d.getHeight(),(int)d.getWidth());
	}
	
	public static BufferedImage readBase64(String base) throws IOException
	{
		BufferedImage image = null;
		byte[] imageByte;

		if(base==null)
			return null;

		imageByte = CryptoUtils.fromBase64(base);

		try(var bis = new ByteArrayInputStream(imageByte))
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
		var typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
		IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
		setDPI(metadata);

		var stream = ImageIO.createImageOutputStream(f);
		try {
			writer.setOutput(stream);
			writer.write(metadata, new IIOImage(img, null, metadata), writeParam);
		} finally {
			stream.close();
		}

	}

	public static double toMM(double d)
	{
		var bd = BigDecimal.valueOf((d * 25.4) / MTGConstants.DPI);
		bd=bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double toPX(double val)
	{
		var bd = BigDecimal.valueOf((val / 25.4) * MTGConstants.DPI);
		bd=bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}


	 private static void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {
			double dotsPerMilli = 1.0 * MTGConstants.DPI / 10 / 2.54;
			var horiz = new IIOMetadataNode("HorizontalPixelSize");
			horiz.setAttribute("value", Double.toString(dotsPerMilli));
			var vert = new IIOMetadataNode("VerticalPixelSize");
			vert.setAttribute("value", Double.toString(dotsPerMilli));
			var dim = new IIOMetadataNode("Dimension");
			dim.appendChild(horiz);
			dim.appendChild(vert);
			var root = new IIOMetadataNode("javax_imageio_1.0");
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
		try(inputStream)
		{
			return ImageIO.read(inputStream);
		}
	}

	public static void write(BufferedImage bi, String formatName, ByteArrayOutputStream baos) throws IOException {
		
		if(bi==null)
			return;
		
		ImageIO.write(bi, formatName, baos);

	}

}
