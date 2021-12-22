package org.magic.api.interfaces.abstracts;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.recognition.DescContainer;
import org.magic.services.recognition.ImageDesc;
import org.magic.tools.FileTools;
import org.magic.tools.IDGenerator;
import org.magic.tools.ImageTools;



public abstract class AbstractRecognitionStrategy extends AbstractMTGPlugin implements MTGCardRecognition {
	
	protected Map<String,List<DescContainer>> dataList;
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.STRATEGY;
	}

	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = new HashMap<String,String>();
		m.put("DATA",Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"recog").toFile().getAbsolutePath());
		
		return m;
	}
	
	protected AbstractRecognitionStrategy() {
		dataList =new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	@Override
	public final Map<String, List<DescContainer>> getDataList() {
		return dataList;
	}
	
	@Override
	public boolean isSetCached(MagicEdition ed) {
		return getSetDirectory(ed.getId()).exists();
	}
	
	@Override
	public boolean isSetLoaded(MagicEdition ed) {
		return dataList.containsKey(ed.getId());
	}
	
	protected List<DescContainer> allDatas()
	{
		return dataList.values().stream().flatMap(List::stream).collect(Collectors.toList());
	}

	public final boolean isCached(MagicEdition ed) {
		return getSetDirectory(ed.getId()).exists();
	}
	
	protected void add(DescContainer dc) {
		dataList.computeIfAbsent(dc.getSetCode(), v->new ArrayList<DescContainer>()).add(dc);
		
	}
	
	@Override
	public void clear(MagicEdition ed) {
		dataList.remove(ed.getId());
	}
	
	@Override
	public void clear() {
		dataList.clear();
	}
	
	@Override
	public int size() {
		return allDatas().size();
	}
	

	@Override
	public void finalizeLoad() {
		//do nothing
	}
	
	@Override
	public final void loadAllCachedData() throws IOException
	{
		for(File f : getFile("DATA").listFiles())
		{
			loadDatasFromFile(f);
		}
	}
	
	
	private final void loadDatasFromFile(File handle) throws IOException
	{
			if(dataList.get(FilenameUtils.getBaseName(handle.getName()))!=null)
			{
				logger.debug(handle + " is already loaded");
			}
			else
			{
				logger.info("Loading " + handle.getAbsolutePath());
				ByteBuffer buf = FileTools.getBuffer(handle);
				FileTools.readUTF8(buf);
				buf.getInt();
				var rec = buf.getInt();
				for(var i=0;i<rec;i++)
				{
					String s = FileTools.readUTF8(buf);
					var id = ImageDesc.readIn(buf);
					var dc = new DescContainer(id,s);
					if(!MagicCard.isBasicLand(dc.getName()))
					{
						add(dc);
					}
				}
			}
		
		
	}
	
	@Override
	public File downloadCardsData(MagicEdition set) throws IOException
	{
		logger.info("downloading " + set);
		List<MagicCard> cards = getEnabledPlugin(MTGCardsProvider.class).searchCardByEdition(set);
		logger.info("Loading cards from " + set +" " + cards.size() + " found");
		
		for(MagicCard card:cards)
		{
			if(!card.isBasicLand() )
			{
				addFromCard(card);
				notify(card);
			}
		}
		
		File f = getSetDirectory(set.getId());
		FileTools.writeSetRecognition(f,set,cards.size(),dataList.get(set.getId()));
		
		return f;
		
	}
	

	@Override
	public final void loadDatasForSet(MagicEdition set)
	{
		File f = getSetDirectory(set.getId());
		if(f.exists())
		{
			try {
				loadDatasFromFile(f);
			} catch (IOException e) {
				logger.error(e);
			}
		}
		else
		{	
			logger.info(set +" doesn't exist. Building it");
			try {
				loadDatasFromFile(downloadCardsData(set));
			} catch (IOException e) {
				logger.error(e);
			}
		}
		
	}
	
	
	private void addFromCard(MagicCard card)
	{
		BufferedImage topimg;
		try {
			topimg = getEnabledPlugin(MTGPictureProvider.class).getPicture(card);
		} catch (IOException e1) {
			logger.error(e1);
			topimg=null;
		}
		
		if(topimg!=null)
		{
				String key = card.getName()+"|"+card.getCurrentSet().getId()+"|"+IDGenerator.generate(card)+"|"+card.getCurrentSet().getNumber();
				try
				{
					add(new DescContainer(new ImageDesc(ImageTools.getScaledImage(topimg)), key));
				}
				catch(Exception e)
				{
					logger.error("Couldn't process card: "+card.toString()+"; "+e.getLocalizedMessage()+"\n");
				}
		}
	}
	
	private File getSetDirectory(String set)
	{
		return new File(getFile("DATA"),set.toLowerCase()+".dat");
	}

	
}
