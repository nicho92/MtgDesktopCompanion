package org.magic.composer.layer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;

import org.magic.composer.models.TextRole;

import com.google.gson.JsonObject;

public class TextLayer extends AbstractLayer {

    private String text;
    private float size;
    private Color color;
    
    
    private TextRole role;
    
    public TextLayer(String text, TextRole role) {
	this.role=role;
        this.text = text;
        this.color=role.getColor();
        setName(role.name().toLowerCase());
        	
	if(role==TextRole.SEPARATOR)
	    setText("a");
	
        
    }
    
    @Override
    public JsonObject toJson() {
       var obj= super.toJson();
  	    obj.addProperty("role", role.name());
       	    obj.addProperty("text", text);
       	    obj.addProperty("size", size);
       	    obj.addProperty("color", color.getRGB());
       return obj;
    }
    
    public TextRole getRole() {
	return role;
    }
        
    public Color getColor() {
	return color;
    }
    
    public void setColor(Color color) {
	this.color = color;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Font getFont() {
        return role.getFont().deriveFont(size);
    }
    
    public void setFontSize(float size) {
        this.size=size;
    }

    @Override
    public void paint(Graphics2D g2) {
	
	var f = getFont();
	
        g2.setFont(f);
        g2.setColor(color);
        g2.drawString(text, x, y + f.getSize2D());
    }

    @Override
    public int getWidth() {
	 return (int) Math.ceil(
		        getFont().getStringBounds(text, FRC).getWidth()
		    );
    }

    @Override
    public int getHeight() {
	 return (int) Math.ceil(
		        getFont().getLineMetrics(text, FRC).getHeight()
		    );
    }

    private static final FontRenderContext FRC = new FontRenderContext(null, true, true);
    
}