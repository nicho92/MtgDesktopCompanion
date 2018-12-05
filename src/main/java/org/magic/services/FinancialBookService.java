package org.magic.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.OrderEntry;
import org.magic.api.exports.impl.JsonExport;

public class FinancialBookService {

	private File tamponFile;
	private List<OrderEntry> entries;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected JsonExport serializer;
	
	
	
	public FinancialBookService() {
		tamponFile = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "financialBook.json").toFile();
		serializer= new JsonExport();
	}
	
	public List<OrderEntry> loadFinancialBook()
	{
		if(tamponFile.exists())
		{
			
			try {
				entries= serializer.fromJsonList(FileUtils.readFileToString(tamponFile,MTGConstants.DEFAULT_ENCODING),OrderEntry.class);
			} catch (IOException e) {
				logger.error("error loading " + tamponFile,e);
			}
		}
		return entries;
	}

	public void saveBook(List<OrderEntry> items) {
		try {
			FileUtils.write(tamponFile, serializer.toJson(items),MTGConstants.DEFAULT_ENCODING.displayName());
		} catch (IOException e) {
			logger.error("error while saving in " + tamponFile,e);
		}
		
		
	}
	
}
