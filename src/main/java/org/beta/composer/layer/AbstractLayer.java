package org.beta.composer.layer;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;

public abstract class AbstractLayer implements Layer {

    protected String name;
    protected int x=0;
    protected int y=0;
    protected boolean visible = true;
    protected File source;
    protected Logger logger = MTGLogger.getLogger(this.getClass());

    public AbstractLayer(File source) {
	this.source=source;
	
	if(source!=null)
	    this.name = source.getName();
	
    }
    
    public File getSource() {
	return source;
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
