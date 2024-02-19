package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.POMReader;
import org.yaml.snakeyaml.Yaml;

public class YamlExport extends AbstractCardExport {

	private Yaml yaml;
	
	
	@Override
	public String getFileExtension() {
		return ".yml";
	}

	@Override
	public String getName() {
		return "Yaml";
	}
	
	private void init()
	{
		yaml = new Yaml();
	}

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(Yaml.class, "/META-INF/maven/org.yaml/snakeyaml/pom.properties");
	}
	
	
	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		init();
		FileTools.saveFile(dest, yaml.dump(deck));
	}

	@Override
	public MTGDeck importDeck(String content, String name) throws IOException {
		init();
		return yaml.load(content);
	}
	
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		init();
	
			var list = new ArrayList<MTGCardStock>();
			
			
			
			yaml.loadAll(content).forEach(o->{
				list.add((MTGCardStock)o);
			});
			return list;
		
	}
	
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File dest) throws IOException {
		init();
		
		
		FileTools.saveFile(dest, yaml.dumpAll(stock.iterator()));
		
	}
	
}
