package org.beta.composer.layer;

import java.awt.Graphics2D;

public interface Layer {
    
    String getName();

    void setName(String name);

    int getX();

    int getY();

    void setX(int x);

    void setY(int y);

    boolean isVisible();

    void setVisible(boolean visible);

    boolean contains(int x, int y);

    void paint(Graphics2D g2);

    int getWidth();

    int getHeight();
}