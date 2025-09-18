package org.magic.api.exports.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.tools.XMLTools;

import com.github.javaparser.ast.Node;

public class MTGStudioExport extends AbstractCardExport{

	@Override
	public String getName() {
		return "MTGStudio";
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}

	
	

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	public String getFileExtension() {
		return ".xml";
	}


	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {

	}
	
	public static void main(String[] args) throws IOException {
		new MTGStudioExport().importDeckFromFile(new File("D:\\Desktop\\New Deck.deck"));
	}
	
	
	@Override
	public MTGDeck importDeck(String content, String name) throws IOException {
		var d = new MTGDeck();
		
		try {
			var builder = XMLTools.createSecureXMLDocumentBuilder();
			var xmlDocument = builder.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
			var nodes = XMLTools.parseNodes(xmlDocument, "/mtgstudiodeck/deck/deckinfo/*");

			for(var i=0; i<nodes.getLength();i++)
			{
				var info = nodes.item(i);
				
				if(info.getNodeName().equalsIgnoreCase("title"))
					d.setName(info.getTextContent());
				
				if(info.getNodeName().equalsIgnoreCase("description"))
					d.setDescription(info.getTextContent());
				
				if(info.getNodeName().equalsIgnoreCase("created"))
					d.setCreationDate(UITools.parseDate(info.getTextContent(),"yyyy-MM-dd"));

				if(info.getNodeName().equalsIgnoreCase("modified"))
					d.setDateUpdate(UITools.parseDate(info.getTextContent(),"yyyy-MM-dd"));
			}
			
			nodes = XMLTools.parseNodes(xmlDocument, "/mtgstudiodeck/deck/cards/*");
			for(var i=0; i<nodes.getLength();i++)
			{
				
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		
		
		
		return d;
	}
	
	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		// TODO Auto-generated method stub
		super.exportDeck(deck, dest);
	}
	
	
	
	
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		var list = new ArrayList<MTGCardStock>();
	
		try {
			var builder = XMLTools.createSecureXMLDocumentBuilder();
			var xmlDocument = builder.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
			String expression = "/CACHE/LINES/LINE";
			var nodes = XMLTools.parseNodes(xmlDocument, expression);
			

			for(int i = 1; i < nodes.getLength()-1;i++)
			{
				var cells = nodes.item(i).getChildNodes();
				var qty = Integer.parseInt(cells.item(7).getTextContent());
				var condition =  cells.item(21).getTextContent();
				var foil =  cells.item(23).getTextContent().equalsIgnoreCase("true");
				var comment = cells.item(25).getTextContent();
				var cardName = cells.item(3).getTextContent().trim();
				var cardSet = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(aliases.getReversedSetIdFor(this, cells.item(5).getTextContent()));
				var m = Pattern.compile("(.*?)( \\[(.*?)\\])?( \\((\\d+)\\))?$").matcher(cardName);
				
				if(m.find())
				{
					var cards = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cleanName(m.group(1)), cardSet, true,null);
					
					if(!cards.isEmpty())
					{ 
						var mcs = MTGControler.getInstance().getDefaultStock();
							mcs.setQte(qty);
							mcs.setFoil(foil);
							mcs.setComment(comment);
							
							if(cells.item(29).getTextContent().equals("Proxy"))
								mcs.setCondition(EnumCondition.PROXY);
							else
								mcs.setCondition(aliases.getReversedConditionFor(this, condition, EnumCondition.MINT));
							
							var index = 0;
							if(cards.size()>1 && m.groupCount()>1 && m.group(5)!=null)
									index = Integer.parseInt(m.group(5))-1;

							mcs.setProduct(cards.get(index));
							notify(mcs.getProduct());
							list.add(mcs);
					 }
					else
					{
						logger.error("No card found for {} {}",cardName,cardSet);
					}

				}
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
		
		
		return list;
	}

	
	
}
