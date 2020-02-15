package org.magic.api.recognition.impl;
import java.util.Collections;

import org.magic.api.interfaces.abstracts.AbstractRecognitionStrategy;
import org.magic.services.recognition.DescContainer;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;


public class HashNarrowedRecogStrat extends AbstractRecognitionStrategy{

	
	
	@Override
	public void initDefault() {
		super.initDefault();
		setProperty("LIMIT_TO_TOP_N_HASH_MATCH", "1000");
	}
	
	@Override
	public String getName()
	{
		return "Hash Narrowed";
	}

	@Override
	public MatchResult getMatch(ImageDesc in, double threshhold)
	{
		sortByHash(in);
		int ix = 0;
		double max = 0;
		int size = Math.min(desc.size(),getInt("LIMIT_TO_TOP_N_HASH_MATCH"));

		for(int i=0;i<size;i++)
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
		for(int i=0;i<desc.size();i++)
		{
			DescContainer d = desc.get(i);
			d.setMatch(id.compareHashWithFlip(d.getDescData()));
		}
		Collections.sort(desc);
	}


}
