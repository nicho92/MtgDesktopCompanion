package org.magic.composer.layer;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;

public abstract class AbstractLayer implements Layer {

    protected String name;
    protected int x=0;
    protected int y=0;
    protected int width;
    protected int height;
    
    protected boolean visible = true;
    private String layerType = this.getClass().getSimpleName();
    
    protected transient Logger logger = MTGLogger.getLogger(this.getClass());
    
    public String getLayerType() {
		return layerType;
	}
    
    public String getName() {
        return name;
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

}
