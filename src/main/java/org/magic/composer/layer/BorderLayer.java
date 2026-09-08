package org.magic.composer.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import org.magic.composer.models.BorderColor;

import com.google.gson.JsonObject;

public class BorderLayer extends AbstractLayer {

    private final int CARD_WIDTH = 2990;
    private final int CARD_HEIGHT = 4180;
    private double radius=116.5;
    private Color color = Color.BLACK; 
    
    
    public BorderLayer(BorderColor c) {
		super(null);
		this.color=c.getColor();
		setName(c.toString());
    }
    
    @Override
    public String toString() {
    	return "Border " + getName();
    }

    @Override
    public void paint(Graphics2D g2) {
	g2.setColor(color);
	g2.fill(new RoundRectangle2D.Double(x, y, CARD_WIDTH, CARD_HEIGHT, radius, radius));

    }

    @Override
    public int getWidth() {
	return CARD_WIDTH;
    }

    @Override
    public int getHeight() {
	return CARD_HEIGHT;
    }

    
    public Color getColor() {
	return color;
    }
    
    public double getRadius() {
	return radius;
    }
    
    @Override
    public JsonObject toJson() {
       var obj = super.toJson();
       
       obj.addProperty("color", color.getRGB());
       obj.addProperty("radius", radius);
       
       return obj;
    }
    
    
}
