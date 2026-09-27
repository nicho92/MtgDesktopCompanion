package org.magic.api.beans.layer;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magic.api.beans.abstracts.AbstractLayer;
import org.magic.api.beans.layer.enums.ShapeType;

public class ShapeLayer extends AbstractLayer {

    
    private Color color;
    
    private ShapeType shape;
    
    public ShapeLayer() {
	setX(0);
	setY(0);
	setWidth(200);
	setHeight(200);
	setColor(Color.WHITE);
	setShape(ShapeType.SQUARE);
    } 
    
    
    public void setColor(Color color) {
	this.color = color;
    }
    
    public Color getColor() {
	return color;
    }
    
    public void setShape(ShapeType shape) {
	this.shape = shape;
    }
    
    public ShapeType getShape() {
	return shape;
    }
    
    
    @Override
    protected void paintLayer(Graphics2D g2) {
	 g2.setColor(color);
	 
	 
	 if(shape==ShapeType.CIRCLE)
	     g2.fillOval(getX(), getY(), getWidth(), getHeight());
	 else
	     g2.fillRect(getX(), getY(), getWidth(), getHeight());
 
    }

}
