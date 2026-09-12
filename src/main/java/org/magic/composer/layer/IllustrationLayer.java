package org.magic.composer.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import org.magic.services.network.URLTools;

public class IllustrationLayer extends AbstractLayer {

	private transient BufferedImage image;
	private URL url;
	 private float fadeSize = 0.0f;
	 private float fadeStrength = 1.0f;

	
	public IllustrationLayer(URL source) {

		this.url = source;
		setName("Illustration");
		reload();
		setWidth(image.getWidth());
		setHeight(image.getHeight());
	}

	public BufferedImage getImage() {
		return image;
	}

    public URL getSource() {
        return url;
    }

    public void setSource(URL source) {
        this.url = source;
        reload();
    }
	
	@Override
	public void reload() {

		try {
			this.image = URLTools.extractAsImage(url.toString());
		} catch (IOException e) {
			logger.error(e);
		}
		
	}
	
	@Override
	public void paint(Graphics2D g2) {
		
	    if (getImage() == null) return;
	    
	    if(getFadeSize()>0)
		paintFade(g2);
	    else
		g2.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), null);
		
	}
	
	 
	
	 public float getFadeSize() {
	        return fadeSize;
	    }

	    public void setFadeSize(float fadeSize) {
	        this.fadeSize = Math.max(0, fadeSize);
	    }

	    public float getFadeStrength() {
	        return fadeStrength;
	    }

	    public void setFadeStrength(float fadeStrength) {
	        this.fadeStrength = Math.max(0, Math.min(1, fadeStrength));
	    }
	
	 private void paintFade(Graphics2D g2) {
    
	         // 1. Création d'une image tampon aux dimensions de la couche
	         var bufferedFade = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	         var gLayer = bufferedFade.createGraphics();

	         // 2. Dessiner l'image d'origine dans le buffer
	         gLayer.drawImage(getImage(), 0, 0, getWidth(), getHeight(), null);

	         // 3. Appliquer le masque d'effacement Alpha (DST_OUT) sur les bords du buffer
	         gLayer.setComposite(AlphaComposite.DstOut);

	         float sizeX = Math.min(fadeSize, getWidth() / 2.0f);
	         float sizeY = Math.min(fadeSize, getHeight() / 2.0f);
	         int alpha = Math.round(Math.min(1.0f, getFadeStrength()) * 255);

	         // GAUCHE
	         gLayer.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, alpha), sizeX, 0, new Color(0, 0, 0, 0)));
	         gLayer.fillRect(0, 0, Math.round(sizeX), getHeight());

	         // DROITE
	         gLayer.setPaint(new GradientPaint(getWidth(), 0, new Color(0, 0, 0, alpha), getWidth() - sizeX, 0, new Color(0, 0, 0, 0)));
	         gLayer.fillRect(getWidth() - Math.round(sizeX), 0, Math.round(sizeX), getHeight());

	         // HAUT
	         gLayer.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, alpha), 0, sizeY, new Color(0, 0, 0, 0)));
	         gLayer.fillRect(0, 0, getWidth(), Math.round(sizeY));

	         // BAS
	         gLayer.setPaint(new GradientPaint(0, getHeight(), new Color(0, 0, 0, alpha), 0, getHeight() - sizeY, new Color(0, 0, 0, 0)));
	         gLayer.fillRect(0, getHeight() - Math.round(sizeY), getWidth(), Math.round(sizeY));

	         gLayer.dispose();

	         // 4. Rendu de l'image fondu sur la scène globale
	         g2.drawImage(bufferedFade, getX(), getY(), null);
	     }
	    
	    
	
	
}
