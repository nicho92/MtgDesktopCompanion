package org.magic.services.technical;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.magic.api.beans.abstracts.AbstractAuditableItem;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

public class FileStorageTechnicalServiceManager extends AbstractTechnicalServiceManager {

	private File logsDirectory = new File(MTGConstants.DATA_DIR,"audits");

	public FileStorageTechnicalServiceManager() {
		super();
		
		if(!logsDirectory.exists())
		{
			try {
				FileTools.forceMkdir(logsDirectory);
			} catch (IOException e) {
				logger.error("error creating {} : {}",logsDirectory.getAbsolutePath(),e);
			}
		}

	}



	@Override
	protected <T  extends AbstractAuditableItem> List<T> restore(Class<T> classe) throws IOException {
		
		if(serializer==null)
			serializer = new JsonExport();
		
		return serializer.fromJsonList(FileTools.readFile(new File(logsDirectory,classe.getSimpleName()+".json")), classe).stream().distinct().toList();
	}

	
	@Override
	protected <T extends AbstractAuditableItem> void store(Class<T> classe, List<T> items) throws IOException
	{
			if(serializer==null)
				serializer = new JsonExport();
			
			
			if(items.isEmpty())
				return;
			
			var f = Paths.get(logsDirectory.getAbsolutePath(),classe.getSimpleName()+".json").toFile();
			var itemsStored = serializer.fromJsonList(new FileReader(f), classe).addAll(items.stream().filter(Objects::nonNull).toList());
			FileTools.saveLargeFile(f, serializer.toJson(itemsStored),MTGConstants.DEFAULT_ENCODING);
	}

	
	

}
