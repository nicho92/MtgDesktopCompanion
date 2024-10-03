package org.beta;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGPlugin.PLUGINS;
import org.magic.api.interfaces.MTGPlugin.STATUT;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.tools.FileTools;

import com.mchange.v1.util.ArrayUtils;

public class WikiGenerator {

	private static final String USER_HOME_VAR = "$USER_HOME";
	private static final String WIKIDIR="D:\\programmation\\GIT\\MtgDesktopCompanion.wiki";

	public static void main(String[] args) throws IOException {
		
		MTGControler.getInstance();
		
		buildPluginsdirectory();
		
		buildePluginsIndex();
		
		System.exit(0);

	}

	@SuppressWarnings("unchecked")
	private static void buildePluginsIndex() throws IOException {
	var builder = new StringBuilder();
		
		var content = 
				"""
				Plugins extend and implement various functions in MTG Companion. There are a number of different categories of plugins, each with their own configuration and purpose. Check the page for the specific plugin for information on how to configure it.
				
				
				 Some plugins are still under development. If a plugin is in a DEV status be aware that there could be issues with it.
				 
				 DEPRECATED one, will be removed in the future
				""" ;
		
		builder.append(content);
		
		
		PluginRegistry.inst().entrySet().stream().filter(e->e.getValue().getType()!=PLUGINS.DASHLET).forEach(e->{
			
			builder.append("### ").append(e.getValue().getType()).append("\n\n");
			
			builder.append(e.getValue().getDesc()).append(".");
			
			if(!e.getValue().isMultiprovider())
				builder.append(" Only one can be active at a time.\n\n");
			else
				builder.append(" Multiple can be active.\n\n");
			
			
			List<MTGPlugin> ps = PluginRegistry.inst().listPlugins(e.getKey()).stream().sorted().toList(); 
			
			ps.forEach(p->
				builder.append("* [").append(p.getName()).append("]")
						  .append("(").append(p.getType()).append("-").append(p.getName().replace(" ", "_")).append(")")
						  .append(p.getStatut()!=STATUT.STABLE?" - _"+p.getStatut().name().toLowerCase()+"_":"")
						  .append("\n")
				);
			
		});
		
		FileTools.saveFile(new File(WIKIDIR+"\\Plugins.md"), builder.toString());
		
	}

	private static void buildPluginsdirectory() {

		PluginRegistry.inst().listPlugins().stream().filter(p->p.getType()!=PLUGINS.DASHLET).forEach(p->{
			
			var temp = new StringBuilder();
					temp.append("# Technical information").append("\n\n");
					temp.append("File Location : ").append(p.getConfFile().getAbsolutePath().replace(SystemUtils.getUserHome().getAbsolutePath(), USER_HOME_VAR).replace('\\', '/')).append("\n\n");
					temp.append("Category :").append(p.getType()).append("\n\n");
					temp.append("Status : ").append(p.getStatut()).append("\n\n");
					temp.append("Need Authenticator : ").append(p.listAuthenticationAttributes().isEmpty()?"No":"Yes").append("\n\n");
					
					temp.append("\n\n");
					temp.append("# Configure the plugin").append("\n\n");
					if(!p.getDefaultAttributes().isEmpty()) { 
						temp.append("| Key | Description | Type | Default Value | Allowed Values|\n");
						temp.append("|------|---------------|-------|-----------------|------------------|\n");
						
						p.getDefaultAttributes().entrySet().forEach(e->{
							temp.append(e.getKey()).append("|");
							temp.append(e.getValue().getComment()).append("|");
							temp.append(parseType(p.getProperties().getProperty(e.getKey()))).append("|");
							temp.append(e.getValue().getDefaultValue().replace(SystemUtils.getUserHome().getAbsolutePath(), USER_HOME_VAR)).append("|");
							
							if(e.getValue().getAllowedProperties()==null)
								temp.append("").append("|");
							else 
							{
								for(var v : e.getValue().getAllowedProperties())
								{
									temp.append(v).append(",");	
								}
								temp.append("|");
							}
							
							temp.append("\n");
						});

					}	
					else
					{
						temp.append("Nothing to do\n\n");
					}
					
					
					if(!p.listAuthenticationAttributes().isEmpty())
					{
						temp.append("\n\n");
						temp.append("# Configure the authenticator").append("\n\n");
						p.listAuthenticationAttributes().forEach(e->temp.append(e).append("\n\n"));
					}
					
					File exportFile = new File(WIKIDIR+"\\plugins\\"+p.getType()+"-"+p.getName()+".md");
					
				try {
					FileTools.saveFile(exportFile, temp.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
					
		});
		
		
	}

	private static String parseType(String s) {
		
		if(s==null)
			return "";
		
		try {
			Integer.parseInt(s);
			return "Number";
		}
		catch(Exception ex)
		{
			//do nothing
		}
		
		if(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false"))
				return "Boolean";
		
		if(s.startsWith(USER_HOME_VAR))
			return "File";
		
		if(s.startsWith("http"))
			return "Url";
		
		
		return "Text";
		
}


}
