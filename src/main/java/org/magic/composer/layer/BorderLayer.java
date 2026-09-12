package org.magic.composer.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URI;

import org.magic.composer.models.BorderColor;
import org.magic.services.network.URLTools;

public class BorderLayer extends AbstractLayer {

    private double radius = 116.5;

    private BorderColor color;
    private transient Image image;
    private URI uri;
    
    
    /*
     * Paramètres de positionnement de l'image
     */
    private double imageScale = 1.0;
    private double imageOffsetX = 0.0;
    private double imageOffsetY = 0.0;

    public BorderLayer(BorderColor c) {

        setWidth(2990);
        setHeight(4180);
        this.color = c;
        setName(c.toString());
    }

    @Override
    public String toString() {
        return "Border " + getName();
    }

    public void setUri(URI uri) {
	this.uri = uri;
    }
    
    @Override
    public void paint(Graphics2D g2) {

        var graphics = (Graphics2D) g2.create();

        try {

            var border = new RoundRectangle2D.Double(
                    getX(),
                    getY(),
                    getWidth(),
                    getHeight(),
                    radius,
                    radius
            );

            /*
             * L'image doit rester à l'intérieur du Border.
             */
            graphics.clip(border);

            if (image != null) {

                graphics.translate(
                        getX() + imageOffsetX,
                        getY() + imageOffsetY
                );

                graphics.scale(
                        imageScale,
                        imageScale
                );

                graphics.drawImage(
                        image,
                        0,
                        0,
                        null
                );

            } else {

                graphics.setColor(color.getColor());
                graphics.fill(border);
            }

        } finally {
            graphics.dispose();
        }
    }

    public Color getColor() {
        return color.getColor();
    }

    public void setColor(Color color) {
        this.color.setColor(color);
        this.image = null;
    }

    public Image getImage() {
        return image;
    }

    
    @Override
    public void reload() {
	try {
	    	
	    if(color==BorderColor.BORDERLESS)
		this.image = URLTools.extractAsImage(uri.toASCIIString());
	} catch (IOException e) {
		logger.error(e);
	}
    }
    

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getImageScale() {
        return imageScale;
    }

    public void setImageScale(double imageScale) {
        if (imageScale <= 0) {
            throw new IllegalArgumentException(
                    "Image scale must be greater than 0"
            );
        }

        this.imageScale = imageScale;
    }

    public double getImageOffsetX() {
        return imageOffsetX;
    }

    public void setImageOffsetX(double imageOffsetX) {
        this.imageOffsetX = imageOffsetX;
    }

    public double getImageOffsetY() {
        return imageOffsetY;
    }

    public void setImageOffsetY(double imageOffsetY) {
        this.imageOffsetY = imageOffsetY;
    }
    
    public void fitImage() {

	    if (image == null) {
	        return;
	    }

	    double scaleX = (double) getWidth() / image.getWidth(null);
	    double scaleY = (double) getHeight() / image.getHeight(null);

	    imageScale = Math.max(scaleX, scaleY);

	    int scaledWidth =
	            (int) (image.getWidth(null) * imageScale);

	    int scaledHeight =
	            (int) (image.getHeight(null) * imageScale);

	    imageOffsetX =
	            (getWidth() - scaledWidth) / 2.0;

	    imageOffsetY =
	            (getHeight() - scaledHeight) / 2.0;
	}
    
}