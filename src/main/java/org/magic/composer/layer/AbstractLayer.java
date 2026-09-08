package org.magic.composer.layer;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;

import com.google.gson.JsonObject;

public abstract class AbstractLayer implements Layer {

    protected String name;
    protected int x=0;
    protected int y=0;
    protected boolean visible = true;

    protected transient Logger logger = MTGLogger.getLogger(this.getClass());
    
    @Override
    public JsonObject toJson() {
        var obj = new JsonObject();
        	obj.addProperty("type", getClass().getName());
        	obj.addProperty("name", name);
        	obj.addProperty("x", x);
        	obj.addProperty("y", y);
         	obj.addProperty("width", getWidth());
           	obj.addProperty("height", getHeight());
        	obj.addProperty("visible", visible);
        	
        	
        	
        return obj;
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
