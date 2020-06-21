package org.beta;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.tools.ImageTools;

public class ImageExporter extends AbstractCardExport{
	
	public BufferedImage generateImageFor(MagicDeck d)
	{
		BufferedImage ret = new BufferedImage(getInt("WIDTH"), getInt("HEIGHT"), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = (Graphics2D) ret.getGraphics();
		
		int bandeau = drawHeader(g,d);
		int start = bandeau + 20;
		int ycard=start;
		int xcard=0;
		int cardCount = 0;
		int columnNumber=0;
		int lineNumber=0;
		
		
		final int cardSpace = 25;
		final int cardGroup = 4;
		
		for(MagicCard mc : d.getMainAsList()) 
		{
			try {
				BufferedImage cardPic = MTGControler.getInstance().getEnabled(MTGPictureProvider.class).getPicture(mc);
				cardPic=ImageTools.scaleResize(cardPic,150);
				
				
				if(cardCount<cardGroup)
				{
					g.drawImage(cardPic, null, xcard, ycard);
					ycard+=cardSpace;
					cardCount++;
				}
				else
				{
					ycard=start;
					cardCount=0;
					xcard=xcard+cardPic.getWidth()+10;
					columnNumber++;
				}
				
				if(columnNumber==5)
				{
					
					columnNumber=0;
					xcard=0;
					ycard=start+lineNumber*(cardPic.getHeight()+(cardGroup*cardSpace));
					lineNumber++;
				}
				
				notify(mc);
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return ret;
	}
	
	
	private int drawHeader(Graphics2D g, MagicDeck d) {
		
		int headerSize=75;
		
		
		g.setColor(Color.ORANGE);
		g.fillRect(0, 0, getInt("WIDTH"),headerSize);
		//g.drawImage(MTGConstants.IMAGE_LOGO, 10, 10, MTGConstants.IMAGE_LOGO.getWidth(null),MTGConstants.IMAGE_LOGO.getHeight(null),null );
		
		g.setFont(MTGControler.getInstance().getFont().deriveFont((float)headerSize-30)); 
		g.setColor(Color.WHITE);
		g.drawString(d.getName(),0,headerSize-10);
		
		return headerSize;
		
	}


	public static void main(String[] args) throws IOException {
		new ImageExporter().exportDeck(new MTGDeckManager().getDeck("[IKO] Temur Mutate"), new File("d:/test.png"));
	}


	@Override
	public String getFileExtension() {
		return "."+getString("FORMAT");
	}


	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		ImageTools.saveImageInPng(generateImageFor(deck), dest);

	}


	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		throw new IOException("Not Implemented");
	}

	
	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}

	@Override
	public String getName() {
		return "Image";
	}

	@Override
	public void initDefault() {
		setProperty("FORMAT", "png");
		setProperty("WIDTH", "1024");
		setProperty("HEIGHT", "768");
	}
	
}
