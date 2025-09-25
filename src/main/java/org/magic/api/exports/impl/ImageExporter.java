package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.interfaces.extra.MTGComparator;
import org.magic.api.sorters.CardNameSorter;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.tools.ImageTools;

public class ImageExporter extends AbstractCardExport{
	private static final String SORTER = "SORTER";

	private static final String FORMAT = "FORMAT";

	int columnsCount = 5;
	int cardGroup = 3;
	int columnsSpace = 10;
	int cardWidthSize = 175;
	int headerSize=75;


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BufferedImage generateImageFor(MTGDeck d)
	{
		List<MTGCard> cards =  d.getMainAsList();

		
		
		MTGComparator<MTGCard> sorter=new CardNameSorter();
		
		if(!getString(SORTER).isEmpty())
		{
			try {
				sorter = (MTGComparator)PluginRegistry.inst().newInstance("org.magic.api.sorters."+getString(SORTER));
			} catch (ClassNotFoundException e) {
				logger.error("Can't load {} sorter : {}. Using default CardNameSorter.",getString(SORTER),e.getMessage());
			}	
		}
	
		
		Collections.sort(cards, sorter);
		int suggestedNbLines = cards.size()/((cardGroup)*columnsCount);


		BufferedImage tempPic = getEnabledPlugin(MTGPictureProvider.class).getBackPicture(cards.get(0));
		tempPic=ImageTools.scaleResize(tempPic,cardWidthSize);

		logger.debug("{} cards, by group of {} and columns={} lines={}.w={} h={}",cards.size(),cardGroup,columnsCount,suggestedNbLines,tempPic.getWidth(),tempPic.getHeight());

		int cardSpace = (int) (tempPic.getHeight()/9.72);
		int  picHeight = suggestedNbLines * (tempPic.getHeight()+((cardGroup+1)*cardSpace))+headerSize;


		var ret = new BufferedImage((cardWidthSize+columnsSpace)*columnsCount, picHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) ret.getGraphics();


		var start = headerSize + 10;
		var ycard=start;
		var xcard=0;
		var cardCount = 0;
		var columnNumber=1;

		drawHeader(g,d,ret);

		for(MTGCard mc : cards)
		{

			try {
				BufferedImage cardPic = getEnabledPlugin(MTGPictureProvider.class).getPicture(mc);
				cardPic=ImageTools.scaleResize(cardPic,cardWidthSize);
				if(cardCount<cardGroup)
				{
					ycard+=cardSpace;
					cardCount++;
				}
				else
				{
					cardCount=0;
					columnNumber++;
					xcard=xcard+cardPic.getWidth()+columnsSpace;
					ycard=start;
					if(columnNumber==(columnsCount+1))
					{
						columnNumber=1;
						start = start + (cardPic.getHeight()+(cardGroup*cardSpace))+20;
						xcard=0;
						cardCount=0;
						ycard=start;
						logger.debug("new Line");

					}
				}
				logger.debug("{}  {}  {}/{}",mc,columnNumber,xcard,ycard);

				g.drawImage(cardPic, null, xcard, ycard);



				notify(mc);


			} catch (IOException e) {
				logger.error(e);
			}

		}

		g.dispose();
		return ret;
	}


	private void drawHeader(Graphics2D g, MTGDeck d, BufferedImage ret) {


		g.setColor(Color.ORANGE);
		g.fillRect(0, 0, ret.getWidth(),headerSize);
		try {
			g.drawImage(ImageTools.readLocal(ImageExporter.class.getResource( "/icons/logo_src.png" )) , 10, 10,50,50, null);
		} catch (IOException e) {
			logger.error("error loading logo_src.png :{} ",e.getMessage());
		}

		g.setFont(MTGControler.getInstance().getFont().deriveFont((float)headerSize-30));
		g.setColor(Color.WHITE);
		g.drawString(d.getName(),70,headerSize-25);
	}

	@Override
	public String getStockFileExtension() {
		return "."+getString(FORMAT);
	}


	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		ImageTools.saveImage(generateImageFor(deck), dest,getString(FORMAT));

	}


	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
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
	public Map<String, MTGProperty> getDefaultAttributes() {
		
		var m = super.getDefaultAttributes();
			 m.put(FORMAT,new MTGProperty("PNG","File format for the image","PNG","JPG"));
			 m.put(SORTER,new MTGProperty("ColorSorter","how is sorted your export","ColorSorter","CardNameSorter","NumberSorter","RaritySorter","TypesSorter","CmcSorter"));
			 
			 return m;
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

}
