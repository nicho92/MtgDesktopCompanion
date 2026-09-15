package org.magic.api.beans.layer;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

public abstract class AbstractLayer implements Layer {

    protected String name;
    private int x=0;
    private int y=0;
    private int width;
    private int height;
    private float alpha =1f;
    protected boolean visible = true;
    private String layerType = this.getClass().getSimpleName();
    
    @Override
    public String getLayerType() {
		return layerType;
	}
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void scale(double scale) {
	    if (scale <= 0) {
	        throw new IllegalArgumentException("Scale must be greater than 0");
	    }

	    setWidth((int) Math.round(getWidth() * scale));
	    setHeight((int) Math.round(getHeight() * scale));
	}
    

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int getHeight() {
    	return height;
    }
    @Override
    public int getWidth() {
    	return width;
    }
    
    @Override
    public void setHeight(int height) {
	this.height = height;
    }
    
    @Override
    public void setWidth(int width) {
	this.width = width;
    }

    public boolean contains(int px, int py) {

        return px >= x
                && px < x + getWidth()
                && py >= y
                && py < y + getHeight();
    }

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public void reload() {
     // do nothing by default;
        
    }
    
    protected abstract void paintLayer(Graphics2D g2);
    
    
    @Override
    public void paint(Graphics2D g2)
    {
	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
	
	paintLayer(g2);
	
	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
    }
    
    @Override
    public void setAlpha(float alpha) {
	this.alpha = alpha;
    }
    @Override
    public float getAlpha() {
	return alpha;
    }
    
    

}
