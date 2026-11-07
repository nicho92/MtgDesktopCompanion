package org.magic.api.beans.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.abstracts.AbstractLayer;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.beans.layer.enums.FrameType;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.IconsProvider;
import org.magic.services.tools.ImageTools;

public class SetIconLayer extends AbstractLayer {

    private String idSet;
    private  transient BufferedImage image;
    private EnumRarity  rarity;
    
    
    public SetIconLayer(String id) {
	setName("SETICON_"+id);
	setRarity(EnumRarity.COMMON);
	setIdSet(id);
	
    }
    
    
    public EnumRarity getRarity() {
	return rarity;
    }
    
    public void setRarity(EnumRarity rarity) {
	this.rarity = rarity;
    }

    public FrameType getFrameType() {
	return FrameType.SETICON;
    }
    
    
    public void setIdSet(String idSet) {
	this.idSet = idSet;
    }
    
    @Override
    public void paintLayer(Graphics2D g2) {
		
	if(image==null)
	    return;
	
	
	g2.drawImage(
		image,
	        getX(),
	        getY(),
	        getWidth(),
	        getHeight(),
	        null
	    );
    }
   
    @Override
    public void reload() {
	 try {
	    image =   IconsProvider.getInstance().getSetColoredSetImage(rarity, idSet);
	    
	    if(rarity!=EnumRarity.COMMON)
		image = ImageTools.addOutline(image, Color.BLACK, 1);
	    else
		image = ImageTools.addOutline(image, Color.WHITE, 1);
	
	    
	    setWidth(image.getWidth());
	    setHeight(image.getHeight());
	    scale(6);
	    
	 } catch (IOException e) {
	     MTGLogger.getLogger(this.getClass()).error("with set {} Rarity= {}",getName(),idSet,rarity,e);
	 }
     }


}
