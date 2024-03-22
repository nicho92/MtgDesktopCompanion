package org.magic.services.technical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.magic.api.beans.abstracts.AbstractAuditableItem;
import org.magic.api.beans.messages.TalkMessage;
import org.magic.api.beans.technical.audit.DAOInfo;
import org.magic.api.beans.technical.audit.DiscordInfo;
import org.magic.api.beans.technical.audit.FileAccessInfo;
import org.magic.api.beans.technical.audit.JsonQueryInfo;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.beans.technical.audit.TaskInfo;
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
	public void persist()
	{

		if(isEnable())
		{
				try {
					storeItems(JsonQueryInfo.class,getJsonInfo().stream().filter(Objects::nonNull).toList());
					storeItems(DAOInfo.class,getDaoInfos().stream().filter(Objects::nonNull).toList());
					storeItems(NetworkInfo.class,getNetworkInfos().stream().filter(Objects::nonNull).toList());
					storeItems(TaskInfo.class,getTasksInfos().stream().filter(Objects::nonNull).toList());
					storeItems(DiscordInfo.class,getDiscordInfos().stream().filter(Objects::nonNull).toList());
					storeItems(FileAccessInfo.class,getFileInfos().stream().filter(Objects::nonNull).toList());
					storeItems(TalkMessage.class,getJsonMessages().stream().filter(Objects::nonNull).toList());
				}
				catch(Exception e)
				{
					logger.error(e);
				}
		}

	}

	@Override
	public void restoreData()
	{

				for(File f : FileTools.listFiles(logsDirectory))
					{
						if(f.getName().startsWith(JsonQueryInfo.class.getSimpleName()))
							try {
								getJsonInfo().addAll(restore(f,JsonQueryInfo.class));
							} catch (IOException e) {
								logger.error(e);
							}
						else if(f.getName().startsWith(DAOInfo.class.getSimpleName()))
							try {
								getDaoInfos().addAll(restore(f,DAOInfo.class));
							} catch (IOException e) {
								logger.error(e);							}
						else if(f.getName().startsWith(TaskInfo.class.getSimpleName()))
							try {
								getTasksInfos().addAll(restore(f,TaskInfo.class));
							} catch (IOException e) {
								logger.error(e);							}
						else if(f.getName().startsWith(NetworkInfo.class.getSimpleName()))
							try {
								getNetworkInfos().addAll(restore(f,NetworkInfo.class));
							} catch (IOException e) {
								logger.error(e);							}
						else if(f.getName().startsWith(DiscordInfo.class.getSimpleName()))
							try {
								getDiscordInfos().addAll(restore(f,DiscordInfo.class));
							} catch (IOException e) {
								logger.error(e);							}
						else if(f.getName().startsWith(FileAccessInfo.class.getSimpleName()))
							try {
								getFileInfos().addAll(restore(f,FileAccessInfo.class));
							} catch (IOException e) {
								logger.error(e);							}
						else if(f.getName().startsWith(TalkMessage.class.getSimpleName()))
							try {
								getJsonMessages().addAll(restore(f,TalkMessage.class));
							} catch (IOException e) {
								logger.error(e);}
					}
					logger.info("Technical data are loaded");
					
		

	}

	private <T  extends AbstractAuditableItem> List<T> restore(File f, Class<T> classe) throws IOException {
		
		if(serializer==null)
			serializer = new JsonExport();
		
		return serializer.fromJsonList(FileTools.readFile(f), classe).stream().distinct().toList();
	}

	private <T extends AbstractAuditableItem> void storeItems(Class<T> classe, List<T> items) throws IOException
	{
		if(isEnable())
		{

			if(serializer==null)
				serializer = new JsonExport();
			
			
			FileTools.saveLargeFile(Paths.get(logsDirectory.getAbsolutePath(),classe.getSimpleName()+".json").toFile(), serializer.toJson(items),MTGConstants.DEFAULT_ENCODING);
		}
	}

	
	

}
