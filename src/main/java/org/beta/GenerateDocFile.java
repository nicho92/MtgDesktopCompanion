package org.beta;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

public class GenerateDocFile {

	
	public static void main(String[] args) {
		
		MTGControler.getInstance();
		
		File f = new File("D:\\programmation\\GIT\\MtgDesktopCompanion.wiki");
		
		PluginRegistry.inst().listPlugins().forEach(c->{
			
			File docFile = new File(f,c.getName()+".md");
				System.out.println(docFile);
				if(!docFile.exists())
				{
					try {
						
						String content = "# Technical information\r\n" + 
								"File Location : $USER_HOME/.magicDeskCompanion/"+c.getConfFile().getParentFile().getName()+"/"+c.getConfFile().getName()+"\r\n" + 
								"\r\n" + 
								"# Configure the plugin\r\n";
								for(Object e : c.getProperties().keySet()) {
									content+=e;
									content+="= XXX\r\n\r\n";
								}
						
						
						System.out.println(content);
						
						FileUtils.write(docFile,content,MTGConstants.DEFAULT_ENCODING);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
		});
	}
}
