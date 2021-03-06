package org.magic.api.recognition.impl;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractRecognitionStrategy;
import org.magic.services.recognition.DescContainer;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;


public class HashNarrowedRecogStrat extends AbstractRecognitionStrategy{


	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public MTGDocumentation getDocumentation() {
		try {
			return new MTGDocumentation(new URL("https://jenssegers.com/perceptual-image-hashes"),FORMAT_NOTIFICATION.HTML);
		} catch (MalformedURLException e) {
			return super.getDocumentation();
		}
	}
	
	@Override
	public void initDefault() {
		super.initDefault();
		setProperty("LIMIT_TO_TOP_N_HASH_MATCH", "1000");
	}
	
	@Override
	public String getName()
	{
		return "Perceptual hashing";
	}

	@Override
	public MatchResult getMatch(ImageDesc in, double threshhold)
	{
		sortByHash(in);
		var ix = 0;
		double max = 0;
		int size = Math.min(desc.size(),getInt("LIMIT_TO_TOP_N_HASH_MATCH"));

		for(var i=0;i<size;i++)
		{
			double score = in.compareSURF(desc.get(i).getDescData());
			if(score>max)
			{
				max=score;
				ix=i;
			}
		}
		if(max>threshhold)
		{
			return new MatchResult(desc.get(ix).getStringData(),max);
		}
		return null;
	}

	private void sortByHash(ImageDesc id)
	{
		for(var i=0;i<desc.size();i++)
		{
			DescContainer d = desc.get(i);
			d.setMatch(id.compareHashWithFlip(d.getDescData()));
		}
		Collections.sort(desc);
	}


}
