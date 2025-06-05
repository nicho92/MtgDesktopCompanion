package org.magic.api.recognition.impl;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractRecognitionStrategy;
import org.magic.services.recognition.DescContainer;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;


public class HashNarrowedRecogStrat extends AbstractRecognitionStrategy{


	private List<DescContainer> datas;

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put("LIMIT_TO_TOP_N_HASH_MATCH", MTGProperty.newIntegerProperty("500", "maximum MatchResult", 10, -1));
		return m;
	}

	@Override
	public String getName()
	{
		return "Perceptual hashing";
	}

	@Override
	public synchronized MatchResult getMatch(ImageDesc in, double threshhold)
	{
		datas = allDatas();
		sortByHash(in);
		return result(datas,in,Math.min(datas.size(),getInt("LIMIT_TO_TOP_N_HASH_MATCH")),threshhold);


	}

	private void sortByHash(ImageDesc id)
	{
		datas.forEach(d->d.setMatch(id.compareHashWithFlip(d.getDescData())));
		Collections.sort(datas);
	}



}
