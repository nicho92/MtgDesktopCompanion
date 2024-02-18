package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.abstracts.AbstractCardExport;

import com.esotericsoftware.yamlbeans.Version;
import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

public class YamlExport extends AbstractCardExport {

	
	private YamlConfig config;
	
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
		config = new YamlConfig();
		config.setClassTag("Card", MTGCard.class);
		config.setClassTag("Set", MTGEdition.class);
		config.setClassTag("StockItem", MTGCardStock.class);
		config.setClassTag("Deck", MTGDeck.class);
	}

	@Override
	public String getVersion() {
		return Version.DEFAULT_VERSION.toString();
	}
	
	
	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		init();
		try(var writer = new YamlWriter(new FileWriter(dest),config))
		{
			writer.write(deck);
		}
	}

	@Override
	public MTGDeck importDeck(String content, String name) throws IOException {
		init();
		try(var reader = new YamlReader(content))
		{
			return reader.read(MTGDeck.class);
		}
	}
	
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		init();
		
		try(var reader = new YamlReader(content))
		{
			var list = new ArrayList<MTGCardStock>();		
			reader.readAll(MTGCardStock.class).forEachRemaining(c->{
				list.add(c);
				notify(c.getProduct());
			});
			return list;
		}
		
	}
	
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File dest) throws IOException {
		init();
		
		try(var writer = new YamlWriter(new FileWriter(dest),config))
		{
			for(var mcs : stock)
			{
				writer.write(mcs);
				notify(mcs.getProduct());
			}
		}		
		
	}
	
}
