package org.magic.api.interfaces.abstracts;

import static org.magic.services.tools.MTG.getEnabledPlugin;

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
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.recognition.DescContainer;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;



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
	public boolean isSetCached(MTGEdition ed) {
		return getSetDirectory(ed.getId()).exists();
	}

	@Override
	public boolean isSetLoaded(MTGEdition ed) {
		return dataList.containsKey(ed.getId());
	}

	protected List<DescContainer> allDatas()
	{
		return dataList.values().stream().flatMap(List::stream).collect(Collectors.toList());
	}

	public final boolean isCached(MTGEdition ed) {
		return getSetDirectory(ed.getId()).exists();
	}

	protected void add(DescContainer dc) {
		dataList.computeIfAbsent(dc.getSetCode(), v->new ArrayList<>()).add(dc);

	}

	protected MatchResult result(List<DescContainer> datas, ImageDesc in, int size, double threshhold)
	{
		var ix = 0;
		double max = 0;
		for(var i=0;i<size;i++)
		{
			double score = in.compareSURF(datas.get(i).getDescData());
			if(score>max)
			{
				max=score;
				ix=i;
			}
		}

		if(!datas.isEmpty())
			logger.debug("Max ={} {}",max,datas.get(ix).getStringData());

		if(max>threshhold)
		{
			return new MatchResult(datas.get(ix).getStringData(),max);
		}
		return null;
	}


	@Override
	public void clear(MTGEdition ed) {
		dataList.remove(ed.getId());
	}

	@Override
	public void clear() {
		dataList.clear();
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
				logger.trace("{} is already loaded",handle);
			}
			else
			{
				logger.info("Loading {}",handle.getAbsolutePath());
				ByteBuffer buf = FileTools.getBuffer(handle);
				FileTools.readUTF8(buf);
				buf.getInt();
				var rec = buf.getInt();
				for(var i=0;i<rec;i++)
				{
					String s = FileTools.readUTF8(buf);
					var id = ImageDesc.readIn(buf);
					var dc = new DescContainer(id,s);
					if(!MTGCard.isBasicLand(dc.getName()))
					{
						add(dc);
					}
				}
			}


	}

	@Override
	public File downloadCardsData(MTGEdition set) throws IOException
	{
		logger.info("downloading {}",set);
		List<MTGCard> cards = getEnabledPlugin(MTGCardsProvider.class).searchCardByEdition(set);
		logger.info("Loading cards from {} {} found",set,cards.size());

		for(MTGCard card:cards)
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
	public final void loadDatasForSet(MTGEdition set)
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
			logger.info("{} doesn't exist. Building it",set);
			try {
				loadDatasFromFile(downloadCardsData(set));
			} catch (IOException e) {
				logger.error(e);
			}
		}

	}


	private void addFromCard(MTGCard card)
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
				String key = card.getName()+"|"+card.getCurrentSet().getId()+"|"+card.getId()+"|"+card.getNumber()+"|"+card.getScryfallId();
				try
				{
					add(new DescContainer(new ImageDesc(ImageTools.getScaledImage(topimg)), key));
				}
				catch(Exception e)
				{
					logger.error("Couldn't process card: {} : {} ",card,e);
				}
		}
	}

	private File getSetDirectory(String set)
	{
		return new File(getFile("DATA"),set.toLowerCase()+".dat");
	}


}
