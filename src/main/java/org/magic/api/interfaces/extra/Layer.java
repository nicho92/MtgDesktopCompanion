package org.magic.api.interfaces.extra;

import java.awt.Graphics2D;


public interface Layer {
    
    String getName();

    void setName(String name);

    int getX();

    int getY();

    void setX(int x);

    void setY(int y);

    boolean contains(int x, int y);

    void paint(Graphics2D g2);

    int getWidth();

    int getHeight();

    void setHeight(int height);

    void setWidth(int width);
    
    void reload();

    String getLayerType();

    void scale(double scale);

    void setAlpha(float alpha);

    float getAlpha();
    
}