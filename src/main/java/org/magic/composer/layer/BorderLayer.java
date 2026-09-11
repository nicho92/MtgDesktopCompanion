package org.magic.composer.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;

import org.magic.composer.models.BorderColor;

public class BorderLayer extends AbstractLayer {

    private double radius = 116.5;

    private Color color = Color.BLACK;
    private Image image;

    /*
     * Paramètres de positionnement de l'image
     */
    private double imageScale = 1.0;
    private double imageOffsetX = 0.0;
    private double imageOffsetY = 0.0;

    public BorderLayer(BorderColor c) {

        setWidth(2990);
        setHeight(4180);

        this.color = c.getColor();
        setName(c.toString());
    }

    @Override
    public String toString() {
        return "Border " + getName();
    }

    @Override
    public void paint(Graphics2D g2) {

        Graphics2D graphics = (Graphics2D) g2.create();

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

                graphics.setColor(color);
                graphics.fill(border);
            }

        } finally {
            graphics.dispose();
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        this.image = null;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
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