package org.magic.services.providers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.ConverterItem;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.services.MTGLogger;
import org.magic.tools.FileTools;

public class StockItemConversionManager {

	private List<ConverterItem> conversionsItems;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private String separator;
	private File file;
	
	public void loadConversions(File f) throws IOException
	{
		loadConversions(f, ";");
	}
	
	public StockItemConversionManager() {
		conversionsItems = new ArrayList<>();
	}

	public int getOutputId(String lang, int idSource)
	{
		return conversionsItems.stream().filter(p->(p.getLang().equalsIgnoreCase(lang) && p.getIdMkmProduct()==idSource)).findFirst().orElse(new ConverterItem()).getOutputId();
	}
	
	public void loadConversions(File f,String separator) throws IOException
	{
		this.file = f ;
		this.separator=separator;
		
			conversionsItems.clear();
			var list = Files.readAllLines(f.toPath());
			list.remove(0); // remove title
			list.forEach(s->{
				
				var arr = s.split(separator);
				
				try {
					conversionsItems.add( new ConverterItem(MkmConstants.MKM_NAME,WooCommerceExport.WOO_COMMERCE,arr[0],Integer.parseInt(arr[3]) ,Integer.parseInt(arr[2]), arr[1]));
				} catch (Exception e) {
					logger.error(s+"|"+e.getMessage());
				}
			});
			logger.debug("Conversions loaded with " + conversionsItems.size() + " items");
	}
	
	public void appendConversion(ConverterItem c)
	{
		conversionsItems.add(c);
		String s = c.getName()+separator+c.getLang()+separator+c.getOutputId()+separator+c.getInputId();
		try {
			FileTools.appendLine(file, s);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
