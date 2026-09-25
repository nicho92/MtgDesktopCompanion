package org.magic.api.pictureseditor.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumExtraCardMetaData;
import org.magic.api.beans.layer.BorderLayer;
import org.magic.api.beans.layer.FrameLayer;
import org.magic.api.beans.layer.IllustrationLayer;
import org.magic.api.beans.layer.ManaLayer;
import org.magic.api.beans.layer.TextLayer;
import org.magic.api.beans.layer.enums.BorderColor;
import org.magic.api.beans.layer.enums.FrameType;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.AbstractPicturesEditorProvider;
import org.magic.api.interfaces.extra.Layer;
import org.magic.gui.components.composer.CardCanvas;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;

public class MTGCompanionComposerProvider extends AbstractPicturesEditorProvider {

    private CardCanvas canvas;
     
    
    @Override
    public STATUT getStatut() {
	return STATUT.DEV;
    }

    public MTGCompanionComposerProvider() {
	canvas = new CardCanvas();
	UITools.loadFonts();
    }

    @Override
    public BufferedImage getPicture(MTGCard mc, MTGEdition me) throws IOException {
	canvas.clear();

	var layout="Normal";

	if(mc.isExtendedArt())
	    layout="Extended";
	else if(mc.isBorderLess())
	    layout="Borderless"; 
	else if(mc.isSaga())
	    layout="Saga"; 
	
	
	if(mc.isLegendary())
	    layout+="-legendary";
	

	var json = URLTools.toJson(getClass().getResourceAsStream("/composer-layouts/"+layout+".json"));
	var layers = new JsonExport().fromJsonList(json.toString(),Layer.class);
	var print=true;

	for(var l : layers) 
	{
	    print=true;
	    if(l instanceof TextLayer tl)
	    {
		if(tl.getName().equals("NAME"))
		    tl.setText(mc.getName());

		if(tl.getName().equals("SET_LANG"))
		    tl.setText(mc.getEdition().getId().toUpperCase() + " • EN");

		if(tl.getName().equals("TYPES"))
		    tl.setText(mc.getFullType());

		if(tl.getName().equals("ARTIST"))
		    tl.setText(mc.getArtist());

		if(tl.getName().equals("TEXT"))
		    tl.setText(mc.getText());

		if(tl.getName().equals("PT_TEXT"))
		{
		    if(mc.isCreature())
			tl.setText(mc.getPower() +"/"+mc.getToughness());
		    else
			tl.setText("");
		}

		if(tl.getName().equals("RARITY_PRINTNUMBER"))
		    tl.setText(mc.getRarity().name().substring(0, 1) + " " + mc.getNumber() + "/ " + mc.getEdition().getCardCountOfficial());
	    }

	    if(l instanceof ManaLayer ml)
	    {
		ml.setCost(mc.getCost());
		ml.scale(1.3);
	    }

	    if(l instanceof BorderLayer bl)
	    {
		bl.setColor(BorderColor.valueOf(mc.getBorder().name()).getColor());
	    }


	    if(l instanceof IllustrationLayer tl)
	    {
		try {
		    tl.setSource(URI.create(mc.getUrl()).toURL());
		} catch (Exception e) {
		    logger.error(e);
		}
	    }

	    if(l instanceof FrameLayer fl)
	    {
			var code = EnumColors.determine(mc.getColors()).getCode();
			
			if(mc.isArtifact())
			    code = "Artifact";
			else if(mc.isLand())
			    code = "Land";
			
			fl.setPath(fl.getPath().replace("U.webp", code+".webp"));

			if(fl.getFrameType()==FrameType.SETICON)	{
				fl.setPath(fl.getPath().replace("a25_mythic.webp", mc.getEdition().getId().toLowerCase()+"_"+mc.getRarity().name().toLowerCase()+".webp"));
				print=mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.SHOW_SET_ICON,"false").equals("true");
			}
			
			if(fl.getFrameType()==FrameType.PT)	{
			    print=mc.isCreature();    
			}
			
			
			
	    }
	    
	    
	    if(print)
	    {
	    	l.reload();
	    	canvas.addLayer(l);
	    }
	}

	return ImageTools.resize(canvas.getCardImage(),1039,744);
    }

    @Override
    public MOD getMode() {
	return MOD.URI;
    }

    @Override
    public String getName() {
	return "MTGCompanion Composer";
    }


    @Override
    public Icon getIcon() {
	return new ImageIcon(MTGConstants.IMAGE_LOGO_32);
    }

}
