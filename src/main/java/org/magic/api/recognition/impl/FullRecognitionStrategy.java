package org.magic.api.recognition.impl;
import org.magic.api.interfaces.abstracts.AbstractRecognitionStrategy;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;


public class FullRecognitionStrategy extends AbstractRecognitionStrategy
{

	
	public synchronized MatchResult getMatch(ImageDesc in, double threshhold)
	{
		var ix = 0;
		double max = 0;
		for(var i=0;i<dataList.size();i++)
		{
			double score = in.compareSURF(allDatas().get(i).getDescData());
			if(score>max)
			{
				max=score;
				ix=i;
			}
		}
		if(max>threshhold)
		{
			return new MatchResult(allDatas().get(ix).getStringData(),max);
		}
		
		return null;
	}

	
	@Override
	public String getName() {
		return "Full Scan";
	}

}
