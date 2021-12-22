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
		
		var datas = allDatas();
		
		for(var i=0;i<size();i++)
		{
			double score = in.compareSURF(datas.get(i).getDescData());
			if(score>max)
			{
				max=score;
				ix=i;
			}
		}
		
		if(max>threshhold)
		{
			return new MatchResult(datas.get(ix).getStringData(),max);
		}
		
		return null;
	}

	
	@Override
	public String getName() {
		return "Full Scan";
	}


}
