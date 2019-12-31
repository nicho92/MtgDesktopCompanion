package org.magic.services.extra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.magic.api.beans.Grader;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.URLTools;

import com.google.gson.JsonElement;

public class GraderServices {

	private static GraderServices inst;
	private Logger logger = MTGLogger.getLogger(this.getClass());

	JsonElement el;
	
	public static GraderServices inst()
	{
		if(inst ==null)
			inst=new GraderServices();
		
		return inst;
	}
	
	
	private GraderServices()
	{
		try {
			el = URLTools.extractJson(MTGConstants.MTG_GRADERS_URI);
		} catch (IOException e) {
			logger.error("Error loading " + MTGConstants.MTG_GRADERS_URI,e );
		}
	}
	
	public List<Grader> listGraders()
	{
		List<Grader> grades = new ArrayList<>();
		
		if(el==null)
			return grades;
		
		grades.add(new Grader());
		el.getAsJsonArray().forEach(e->grades.add(new Grader(e.getAsJsonObject().get("name").getAsString(), e.getAsJsonObject().get("url").getAsString())));
		
		
		return grades;
	}
	
	public List<String> listGradersNames()
	{
		return listGraders().stream().map(Grader::getName).collect(Collectors.toList());
	}
	
	
}
