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
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.tools.XMLTools;

public class MTGStudioExport extends AbstractCardExport{

	private String cardRegex="(.*?)( \\[(.*?)\\])?( \\((\\d+)\\))?$";

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
		return STATUT.BETA;
	}
	
	
	@Override
	public String getStockFileExtension() {
		return ".xml";
	}

	@Override
	public String getDeckFileExtension() {
		return ".xml";
	}
	

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		
		var builder = new StringBuilder("Name,Set,Qty,Foil,Price,Condition,Notes").append(System.lineSeparator());
		
		
		for(var mcs : stock)
		{
			builder.append(commated(mcs.getProduct().getName())).append(",");
			builder.append(aliases.getReversedSetIdFor(this, mcs.getProduct().getEdition())).append(",");
			builder.append(mcs.getQte()).append(",");
			builder.append(mcs.isFoil()?"y":"n").append(",");
			builder.append(UITools.formatDouble(mcs.getValue().doubleValue(),'.')).append(",");
			builder.append(aliases.getConditionFor(this, mcs.getCondition())).append(",");
			builder.append(mcs.getComment()).append(System.lineSeparator());
			notify(mcs.getProduct());
		}
		FileTools.saveFile(f, builder.toString());
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
				var c = nodes.item(i);
				
				var qtyMain = Integer.parseInt(c.getAttributes().getNamedItem("deck").getTextContent());
				var qtySide = Integer.parseInt(c.getAttributes().getNamedItem("sb").getTextContent());
				var set = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(aliases.getReversedSetIdFor(this, c.getAttributes().getNamedItem("edition").getTextContent()));
				var m = Pattern.compile(cardRegex).matcher(c.getTextContent());
				if(m.find())
				{
					var cards = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cleanName(m.group(1)), set, true,null);
					if(!cards.isEmpty())
					{
						
						if(qtyMain>0)
							d.getMain().put(cards.get(0), qtyMain);
						
						if(qtySide>0)
							d.getSideBoard().put(cards.get(0), qtySide);
					}
				}
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
		var builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>").append(System.lineSeparator());
		builder.append("<mtgstudiodeck version=\"1.0\"><deck>").append(System.lineSeparator());
		
		builder.append("<deckinfo>").append(System.lineSeparator());
			builder.append("<title>").append(deck.getName()).append("</title>");
			builder.append("<archetype>").append("Unspecified").append("</archetype>");
			builder.append("<creator>").append(System.getProperty("user.name")).append("</creator>");
			builder.append("<created>").append(UITools.formatDate(deck.getDateCreation())).append("</created>");
			builder.append("<modified>").append(UITools.formatDate(deck.getDateUpdate())).append("</modified>");
			builder.append("<version>1.0</version>");
			builder.append("<description>").append(deck.getDescription()).append("</description>");
			builder.append("<email/>");
			
			
		builder.append("</deckinfo>").append(System.lineSeparator());
		builder.append("<cards>").append(System.lineSeparator());
		
			deck.getAllUniqueCards().forEach(c->{
				builder.append("<card deck=\"").append(deck.getMain().getOrDefault(c,0)).append("\" ").append("sb=\"").append(deck.getSideBoard().getOrDefault(c,0)).append("\"").append("edition=\"").append(aliases.getReversedSetIdFor(this, c.getEdition())).append("\">").append(c.getName()).append("</card>").append(System.lineSeparator());
			});
			
		
		builder.append("</cards>");
		builder.append("</deck></mtgstudiodeck>");
		FileTools.saveFile(dest, builder.toString());
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
				var price = UITools.parseDouble(cells.item(13).getTextContent());
				var m = Pattern.compile(cardRegex).matcher(cardName);
				
				if(m.find())
				{
					var cards = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cleanName(m.group(1)), cardSet, true,null);
					
					if(!cards.isEmpty())
					{ 
						var mcs = MTGControler.getInstance().getDefaultStock();
							mcs.setQte(qty);
							mcs.setFoil(foil);
							mcs.setComment(comment);
							mcs.setPrice(price);
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
