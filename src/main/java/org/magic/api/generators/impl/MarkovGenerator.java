package org.magic.api.generators.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.abstracts.AbstractMTGTextGenerator;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

import rita.RiMarkov;
import rita.RiTa;

public class MarkovGenerator extends AbstractMTGTextGenerator {

	private RiMarkov rs;
	private File cache;


	@Override
	public String generateText()
	{
		if(rs==null)
			init();

		return StringUtils.join(rs.generate(),System.lineSeparator());
	}

	@Override
	public String[] suggestWords(String[] start)
	{
		if(rs==null)
			init();

		return rs.completions(start);
	}

	@Override
	public void init()
	{
		  rs = new RiMarkov(getInt("NGEN"));
		  cache = getFile("CACHE_FILE");

		  if(!cache.exists() || cache.length()==0)
		  {
			  logger.debug("Init MarkovGenerator");
			  var build = new StringBuilder();
			  var count =0;
			  for(var mc : getEnabledPlugin(MTGCardsIndexer.class).listCards())
			  {

				  if((mc.getText()!=null || !mc.getText().isEmpty() || !mc.getText().equalsIgnoreCase("null"))) 
				  {
						  var r = mc.getText().replace(EnumCardsPatterns.REMINDER.getPattern(), "")
								  				 .replace("\n", " ")
								  				 .replace(mc.getName(), getString("TAG_NAME"))
								  				 .trim();

						  count++;
						  rs.addText(r);

						  build.append(r).append(System.lineSeparator());
				  }
			  }

			try {
				if(count>0)
					saveCache(build.toString());
				else
					logger.warn("No cards to index {}",count);

			} catch (IOException e) {
				logger.error("error saving file {}",cache.getAbsolutePath(),e);
			}

		  }
		  else
		  {
			  try {
				logger.debug("loading cache from {}",cache);
				FileTools.readAllLines(cache).forEach(rs::addText);
			} catch (Exception e) {
				logger.error("error loading file {} ",cache.getAbsolutePath(),e);
			}
		  }


	}

	private void saveCache(String s) throws IOException
	{
		logger.debug("saving cache to {}",cache);
		FileTools.saveFile(cache, s);
	}

	@Override
	public String getName() {
		return "Markov";
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("CACHE_FILE", MTGProperty.newFileProperty(new File(MTGConstants.DATA_DIR,"markov.gen")),
							"NGEN", MTGProperty.newIntegerProperty("5", "n-factor - the length of each n-gram stored in the model", 2, 10),
							"TAG_NAME",new MTGProperty("CARD_NAME", "tagname to replace card name in the oracle text"));
	}

	@Override
	public String getVersion() {
		return RiTa.VERSION;
	}

}
