package org.magic.composer.layer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.io.File;

import com.google.gson.JsonObject;

public class TextLayer extends AbstractLayer {

    private Font font;
    private Color color = Color.BLACK;
    private int maxWidth = 600;
    private String text;
    private int alignment;
        
    public TextLayer(String text, File source) {
        super(source);
        this.text = text;
        
        try {
	    var basefont = Font.createFont(Font.TRUETYPE_FONT, source);
	    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(basefont);
	    
	    
	    this.font = basefont.deriveFont(24f);
	    
	} catch (Exception e) {
	   logger.error(e);
	    font = new Font("Serif", Font.PLAIN, 24);
	} 
        
    }
    
    @Override
    public JsonObject toJson() {
       var obj= super.toJson();
       	    obj.addProperty("text", text);
       	    obj.addProperty("fontName", font.getFontName());
       	    obj.addProperty("fontFamily", font.getFamily());
       	    obj.addProperty("fontstyle", font.getStyle());
       	    obj.addProperty("fontstyle", font.getSize());
       	    obj.addProperty("alignment", alignment);
       	    obj.addProperty("color", color.getRGB());
       return obj;
    }
    
    
    
    public int getAlignment() {
	return alignment;
    }
    
    public void setAlignment(int alignement) {
	this.alignment = alignement;
    }
    
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }
    
    public void setFontSize(float size) {
        font = font.deriveFont(size);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getWidth() {
        return maxWidth;
    }
        
    public int getHeight(Graphics2D g2) {
	    return g2.getFontMetrics(font).getHeight();
	}

    public int getHeight() {
        return new Canvas().getFontMetrics(font).getHeight();
    }

    @Override
    public void paint(Graphics2D g2) {
        g2.setFont(font);
        g2.setColor(color);
        g2.drawString(text, x, y + font.getSize2D());
    }

}