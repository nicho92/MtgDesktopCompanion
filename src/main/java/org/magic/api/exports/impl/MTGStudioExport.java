package org.magic.api.exports.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.XMLTools;

public class MTGStudioExport extends AbstractCardExport{

	@Override
	public String getName() {
		return "MTGStudio";
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
		var f = new File("D:\\Desktop\\MTGStudioExport.xml");
		
		new MTGStudioExport().importStockFromFile(f);
		
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
				
				var cardName = cells.item(3).getTextContent().trim();
				var cardSet = aliases.getReversedSetIdFor(this, cells.item(5).getTextContent());
				var m = Pattern.compile("(.*?)( \\[(.*?)\\])?$").matcher(cardName); 

				System.out.println(cardSet);
				
				
				var qty = Integer.parseInt(cells.item(7).getTextContent());
				var condition =  cells.item(21).getTextContent();
				var foil =  cells.item(23).getTextContent().equalsIgnoreCase("true");
				var comment = cells.item(25).getTextContent();
				
				
				
				var mcs = MTGControler.getInstance().getDefaultStock();
					 mcs.setQte(qty);
					 mcs.setFoil(foil);
					 mcs.setComment(comment);
					 mcs.setCondition(aliases.getReversedConditionFor(this, condition, EnumCondition.MINT));

					 
				list.add(mcs);

			}
			
			
		
		
		} catch (Exception e) {
			throw new IOException(e);
		}
		
		
		return list;
	}

	
	
}
