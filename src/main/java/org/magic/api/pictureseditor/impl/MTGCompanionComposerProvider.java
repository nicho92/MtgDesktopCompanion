package org.magic.api.pictureseditor.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.ast.engine.OracleParser;
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
	mc.getCustomMetadata().put(EnumExtraCardMetaData.PLUGIN_NAME, getName());
	var layout="Normal";

	if(mc.isExtendedArt())
	    layout="Extended";
	else if(mc.isSaga())
	    layout="Saga"; 
	else if(mc.isPlaneswalker())
	    layout="Planeswalker"; 
	
	if(mc.isLegendary() && !mc.isPlaneswalker())
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

		if(tl.getName().equals("TEXT")) {
		    tl.setText(mc.getText());
		    tl.setFlavorText(mc.getFlavor());
		}

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
			
			
			if((fl.getFrameType()==FrameType.BOX || fl.getFrameType()==FrameType.LEGENDARY) && !StringUtils.isEmpty( mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT)))
			{
			    code = mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT);
			}
			
			
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
	
	//post treatment for PW.
	
	if(mc.isPlaneswalker())
	{
	    
	    var pas = OracleParser.toFacade(mc.getName(), mc.getText()).getPlaneswalkerAbilities();
	    
	    for (int i=0;i<pas.size();i++)
	    {
			final int value = i;
			layers.stream().filter(l->l.getName().equals("loyalty_value_"+(value+1))).map(TextLayer.class::cast).findFirst().get().setText(pas.get(value).loyalty());
			layers.stream().filter(l->l.getName().equals("loyalty_text_"+(value+1))).map(TextLayer.class::cast).findFirst().get().setText(pas.get(value).effects().getFirst().text());
			
			var optfl = layers.stream().filter(l->l.getName().equals("loyalty_frame_"+(value+1))).map(FrameLayer.class::cast).findFirst();
				if(optfl.isPresent())
				{
				    var fl = optfl.get();
				    var code = "LoyaltyZero"; 
				    if(pas.get(value).loyalty().startsWith("+"))
				    	code="LoyaltyPlus";
				    else if(pas.get(value).loyalty().startsWith("−"))
				    	code="LoyaltyMinus";
				    else code = "LoyaltyZero"; 
				    
				    fl.setPath(fl.getPath().replace("LoyaltyZero.webp", code+".webp"));
				    fl.reload();
				}
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
