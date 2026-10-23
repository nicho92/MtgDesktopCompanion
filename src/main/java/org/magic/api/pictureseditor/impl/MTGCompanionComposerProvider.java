package org.magic.api.pictureseditor.impl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.magic.api.ast.engine.OracleParser;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumExtraCardMetaData;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.enums.EnumLayout;
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
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;

public class MTGCompanionComposerProvider extends AbstractPicturesEditorProvider {

    private static final String TEMPLATE_REPLACE = "U.webp";
    private CardCanvas canvas;
    private String forceTemplate;
     
    
    @Override
    public STATUT getStatut() {
	return STATUT.DEV;
    }

    public MTGCompanionComposerProvider() {
	canvas = new CardCanvas();
	UITools.loadFonts();
    }
    
    
    @Override
    protected String getFrameVersion() {
      return "2015";
    }
      
    @Override
    public BufferedImage generatePicture(MTGCard mc, MTGEdition me) throws IOException {
	
	
	MTGControler.getInstance().downloadComposerDatas();
	
	
	canvas.clear();

	var layout=findLayout(mc);

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
		{
		    if(mc.isRetro())
			tl.setText("Illus. "+ mc.getArtist());
		    else
			tl.setText(mc.getArtist());
		}

		if(tl.getName().equals("TEXT")) {
		    tl.setText(mc.getText());
		    tl.setFlavor(mc.getFlavor());
		}
		
		
		
		if(tl.getName().equals("ROTATED_NAME") && mc.getRotatedCard()!=null)
		{
		    tl.setText(mc.getRotatedCard().getName());
		}
		
		if(tl.getName().equals("ROTATED_COST") && mc.getRotatedCard()!=null)
		{
		    tl.setText(mc.getRotatedCard().getCost());
		}
		

		if(tl.getName().equals("ROTATED_TYPES") && mc.getRotatedCard()!=null)
		{
		    tl.setText(mc.getRotatedCard().getFullType());
		}
		
		if(tl.getName().equals("ROTATED_TEXT") && mc.getRotatedCard()!=null)
		{
		    tl.setText(mc.getRotatedCard().getFullType());
		}
		
		
		if(tl.getName().equals("ROTATED_PT"))
		{
		    if(mc.getRotatedCard()!=null && mc.getRotatedCard().isCreature())
		    {
			tl.setText(mc.getRotatedCard().getPower()+"/"+mc.getRotatedCard().getToughness());
		    }
		    else
		    {
			tl.setText("");
		    }
		}
		
		if(tl.getName().equals("PT_TEXT"))
		{
		    if(mc.isVehicule())
			tl.setColor(Color.WHITE);
		    
		    if(mc.isCreature())
			tl.setText(mc.getPower() +"/"+mc.getToughness());
		    else
			tl.setText("");
		}

		if(tl.getName().equals("RARITY_PRINTNUMBER"))
		    tl.setText(mc.getRarity().name().substring(0, 1) + " " + mc.getNumber() + "/ " + (me!=null?me.getCardCountOfficial():mc.getEdition().getCardCountOfficial()));
		
		if(tl.getName().equals("COPYRIGHT"))
		{
		    if(mc.isRetro())
		    {
			 tl.setText("© 1993-"+LocalDate.now().getYear()+" Wizards of the Coast, Inc.");	
		    }
		    else
		    {
			   tl.setText("™ & © "+LocalDate.now().getYear()+" Wizards of the Coast");	
		    }
		 
		}
		
	    }

	    if(l instanceof ManaLayer ml)
	    {
		ml.setCost(mc.getCost());
		
		if(ml.getName().equals("ROTATED_COST")&& mc.getRotatedCard()!=null)
		    ml.setCost(mc.getRotatedCard().getCost());
		
	    }

	    if(l instanceof BorderLayer bl)
	    {
		bl.setColor(BorderColor.valueOf(mc.getBorder().name()).getColor());
		
		if(bl.getColor()==BorderColor.BORDERLESS)
		{
		    bl.setUri(URI.create(mc.getUrl()));
		    bl.reload();
		    bl.fitImage();
		}
		
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
		
			if(mc.isDoubleFaced() && mc.getSide().equals("b"))
			    code = mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT);
		
			
			if(mc.isArtifact())
			    code = "Artifact";
			else if(mc.isLand())
			    code = "Land";
			
			if((fl.getFrameType()==FrameType.BOX || fl.getFrameType()==FrameType.LEGENDARY || fl.isTitleLine() || fl.isTypesLine()) && !StringUtils.isEmpty( mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT)))
			{
			    code = mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT);
			    
			   if( fl.getFrameType().name().endsWith("_BORDER") && mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT).length()>1)
				code="Gold";
			   else
			       code=mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT);
			}
			
			if((fl.getFrameType()==FrameType.FRAME  || fl.getFrameType()==FrameType.PT)&& mc.isVehicule())	{
			    code="Vehicle";
			}
		
			
			if(fl.getFrameType()==FrameType.BORDERED_FRAME && mc.isLand())
			{
			    code="Land_Gold";
			    
			    if(!mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT).isEmpty())
				code="Land_"+mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT);
			}
				
			fl.setPath(fl.getPath().replace(TEMPLATE_REPLACE, code+".webp"));

			if(fl.getFrameType()==FrameType.SETICON)	{
				fl.setPath(fl.getPath().replace("a25_mythic.webp", mc.getEdition().getId().toLowerCase()+"_"+mc.getRarity().name().toLowerCase()+".webp"));
				print=mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.SHOW_SET_ICON,"false").equals("true");
			}
			
			if(fl.getFrameType()==FrameType.TRANSFORM_ICON)
			{
			    	var directory="Frames/transform_icons";
				if(mc.getSide().equals("b"))
				    directory ="Frames/transform_back_icons";
				    
				   fl.setPath(directory+"/"+mc.getFrameEffects().getFirst().name().toLowerCase()+".webp");
				   print=mc.isDoubleFaced();
			    
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
				    else if(pas.get(value).loyalty().startsWith("−") || pas.get(value).loyalty().startsWith("-"))
				    	code="LoyaltyMinus";
				    else 
					code = "LoyaltyZero"; 
				    
				    fl.setPath(fl.getPath().replace("LoyaltyZero.webp", code+".webp"));
				    fl.reload();
				}
	    }
	}

	return ImageTools.resize(canvas.getCardImage(),1039,744);
    }


    public void setTemplate(File currentFile) {
	forceTemplate = FilenameUtils.getBaseName(currentFile.getAbsolutePath());
	
    }

  
    private String findLayout(MTGCard mc) {
	
	var layout="Normal";

	if(mc.isExtendedArt())
	    layout="Extended";
	else if(mc.isSaga())
	    layout="Saga"; 
	else if(mc.isPlaneswalker())
	{
	    layout="Planeswalker";
	    if(OracleParser.toFacade(mc.getName(), mc.getText()).getPlaneswalkerAbilities().size()>3)
		layout="Planeswalker-4";
	    
	}
	else if(mc.isBorderLess())
	    layout="Borderless"; 
	
	if(mc.isLegendary() && !mc.isPlaneswalker())
	    layout+="-legendary";
	
	
	if(mc.isRetro())
	    layout="old";
	
	if(mc.isDoubleFaced())
	{
	    layout="Transform";
	    
	    if(mc.getFrameEffects().contains(EnumFrameEffects.COMPASSLANDDFC) && mc.getSide().equals("b"))
		layout="Ixalan";
	}
	
	if(mc.getLayout()==EnumLayout.ADVENTURE)
	    layout="Adventures";
	
	if(forceTemplate!=null)
	    layout= forceTemplate;
	
	

	
	logger.debug("loading layout {}",layout);
	
	return layout;
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
