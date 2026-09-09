package org.magic.composer.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import org.magic.composer.models.BorderColor;

public class BorderLayer extends AbstractLayer {

    
    private double radius=116.5;
    private Color color = Color.BLACK; 
    
    
    public BorderLayer(BorderColor c) {
    	width = 2990;
    	height = 4180;
  
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
		g2.fill(new RoundRectangle2D.Double(x, y, width, height, radius, radius));

    }

 
    public Color getColor() {
	return color;
    }
    
    public double getRadius() {
	return radius;
    }
    
   
}
