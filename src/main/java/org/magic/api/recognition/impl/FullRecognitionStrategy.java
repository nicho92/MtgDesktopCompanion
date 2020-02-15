package org.magic.api.recognition.impl;
import java.io.File;
import java.util.ArrayList;

import org.magic.api.interfaces.abstracts.AbstractRecognitionStrategy;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;


public class FullRecognitionStrategy extends AbstractRecognitionStrategy
{

	public FullRecognitionStrategy()
	{
		desc = new ArrayList<>();
	}

	public FullRecognitionStrategy(File f)
	{
		desc = new ArrayList<>();
		loadDatasFromFile(f);
	}

	public synchronized MatchResult getMatch(ImageDesc in, double threshhold)
	{
		int ix = 0;
		double max = 0;
		for(int i=0;i<desc.size();i++)
		{
			double score = in.compareSURF(desc.get(i).getDescData() );
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

	
	@Override
	public String getName() {
		return "Full Scan";
	}


	public synchronized void finalizeLoad()
	{
		//do nothing
	}

	public synchronized int size() {
		return desc.size();
	}
}
