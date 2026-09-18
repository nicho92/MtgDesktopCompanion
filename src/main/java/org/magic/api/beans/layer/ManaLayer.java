package org.magic.api.beans.layer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.magic.api.beans.abstracts.AbstractLayer;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.IconsProvider;
import org.magic.services.tools.ImageTools;

public class ManaLayer extends AbstractLayer{
    
    private String cost;
    private transient BufferedImage manaImage;
    
    public ManaLayer(String cost)
    {
	setCost(cost);
	setName("Symbol " + cost);
	
    }
    
    public String getCost() {
	return cost;
    }
    
    public void setCost(String cost) {
	this.cost = cost;
	reload();
    }
    
    
    @Override
    protected void paintLayer(Graphics2D g2) {
	g2.drawImage(manaImage, getX(), getY(), getWidth(), getHeight(), null);
	
    }
    
    @Override
    public void reload() {
        manaImage = ImageTools.joinBufferedImage(parseManaCost(cost));
        
        if (manaImage != null) { 
            
            int oldWidth = getWidth();

            setWidth(manaImage.getWidth());
            setHeight(manaImage.getHeight());

            setX(getX() + oldWidth - getWidth());
          } 
        else 
        { 
            setWidth(0); 
            setHeight(0); 
        }
        
    }
    
    private List<Image> parseManaCost(String manaCost) {

        var symbols = new ArrayList<Image>();

        var matcher = Pattern.compile("\\{([^}]+)}").matcher(manaCost);
        while (matcher.find()) {

            var symbol = matcher.group(1);
            var manaSymbol =IconsProvider.getInstance().getManaSymbol(symbol);
            
            if (manaSymbol != null) {
                symbols.add(manaSymbol);
            }
            else {
        	   MTGLogger.getLogger(this.getClass()).error("no symbol for {}", symbol);
            }
        }

        return symbols;
    }
    

}
