package org.magic.api.recognition.impl;
import org.magic.api.interfaces.abstracts.AbstractRecognitionStrategy;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;


public class FullRecognitionStrategy extends AbstractRecognitionStrategy
{
	public synchronized MatchResult getMatch(ImageDesc in, double threshhold)
	{
		var datas = allDatas();
		return result(datas,in, datas.size(), threshhold);
	}
	
	@Override
	public String getName() {
		return "Full Scan";
	}


}
