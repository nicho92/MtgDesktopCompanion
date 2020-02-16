package org.magic.api.interfaces.abstracts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.recognition.DescContainer;
import org.magic.services.recognition.ImageDesc;
import org.magic.tools.FileTools;
import org.magic.tools.IDGenerator;
import org.magic.tools.ImageTools;



public abstract class AbstractRecognitionStrategy extends AbstractMTGPlugin implements MTGCardRecognition {
	
	protected List<DescContainer> desc;
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.RECOGNITION;
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	public void initDefault() {
		setProperty("DATA",Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"recog").toFile().getAbsolutePath());
	}
		
	
	@Override
	public boolean isCached(MagicEdition ed) {
		return getSetDirectory(ed).exists();
	}
	
	public AbstractRecognitionStrategy() {
		super();
		
		
		confdir = new File(MTGConstants.CONF_DIR, "strategies");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();

		}
		
		desc = new ArrayList<>();
		
	}
	
	
	@Override
	public void add(DescContainer dc) {
		desc.add(dc);
		
	}
	
	@Override
	public void clear() {
		desc.clear();
	}
	
	@Override
	public int size() {
		return desc.size();
	}
	

	@Override
	public void finalizeLoad() {
		//do nothing;
		
	}
	
	public void loadDatasFromFile(File handle)
	{
		try
		{
			logger.info("Loading " + handle.getAbsolutePath());
			ByteBuffer buf = FileTools.getBuffer(handle);
			FileTools.readUTF8(buf);
			buf.getInt();
			int rec = buf.getInt();
			for(int i=0;i<rec;i++)
			{
				String s = FileTools.readUTF8(buf);
				ImageDesc id = ImageDesc.readIn(buf);
				DescContainer dc = new DescContainer(id,s);
				if(!MagicCard.isBasicLand(dc.getName()))
				{
					add(dc);
				}
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		
		}
	}
	
	public File downloadCardsData(MagicEdition set) throws IOException
	{
		logger.info("downloading " + set);
		List<MagicCard> cards = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByEdition(set);
		logger.info("Loading cards from " + set +" " + cards.size() + " found");
		
		for(MagicCard card:cards)
		{
			if(!card.isBasicLand() )
			{
				addFromCard(card);
				notify(card);
			}
		}
		
		File f = getSetDirectory(set);
		FileTools.writeSetRecognition(f,set,cards.size(),desc);
		
		return f;
		
	}


	public void loadDatasForSet(String code)
	{
		try {
			loadDatasForSet(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(code));
		} catch (IOException e) {
			logger.error("Error loading " +code,e);
		}
	}
	
	
	private File getSetDirectory(MagicEdition set)
	{
		return new File(getFile("DATA"),set.getId().toLowerCase()+".dat");
	}

	public void loadDatasForSet(MagicEdition set)
	{
		File f = getSetDirectory(set);
		if(f.exists())
		{
			loadDatasFromFile(f);
		}
		else
		{	
			logger.info(set +" doesn't exist");
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
			topimg = MTGControler.getInstance().getEnabled(MTGPictureProvider.class).getPicture(card);
		} catch (IOException e1) {
			logger.error(e1);
			topimg=null;
		}
		
		if(topimg!=null)
		{
				String key = card.getName()+"|"+card.getCurrentSet().getId()+"|"+IDGenerator.generate(card);
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


	
}
