package org.magic.services.technical;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.magic.api.beans.abstracts.AbstractAuditableItem;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

import com.google.common.io.Files;

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
	protected <T  extends AbstractAuditableItem> List<T> restore(Class<T> classe,Instant start ,Instant end) throws IOException {
		
		if(serializer==null)
			serializer = new JsonExport();
		
		 Predicate<T> p = t ->{
			 var b1 = t.getStart().isAfter(start);
			 var b2 = true;
			 
			 if(t.getEnd()!=null)
				 b2 = t.getEnd().isBefore(end==null?Instant.now():end);
			
			return b1&&b2;
		 };
		 
		return serializer.fromJsonList(FileTools.readFile(new File(logsDirectory,classe.getSimpleName()+".json")), classe).stream().filter(p).distinct().toList();
	}
	
	@Override
	protected <T extends AbstractAuditableItem> void store(Class<T> classe, List<T> items) throws IOException
	{
			if(serializer==null)
				serializer = new JsonExport();
			
			if(items.isEmpty())
				return;
			
			var f = Paths.get(logsDirectory.getAbsolutePath(),classe.getSimpleName()+".json").toFile();
			
			if(!f.exists())
				Files.touch(f);

			var itemsStored = serializer.fromJsonList(new FileReader(f), classe);
			itemsStored.addAll(items.stream().filter(Objects::nonNull).toList());
			
			FileTools.saveLargeFile(f, serializer.toJson(itemsStored),MTGConstants.DEFAULT_ENCODING);
	}

	
	

}
