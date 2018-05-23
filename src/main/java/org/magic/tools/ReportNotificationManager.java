package org.magic.tools;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class ReportNotificationManager {
	private Configuration cfg;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private boolean errorLoading=false;
	
	public ReportNotificationManager() {
		  	cfg = new Configuration(MTGConstants.FREEMARKER_VERSION);
	        try {
				cfg.setClassForTemplateLoading(ReportNotificationManager.class, MTGConstants.MTG_REPORTS_DIR);
				cfg.setDefaultEncoding(MTGConstants.DEFAULT_ENCODING);
				cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
				cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(MTGConstants.FREEMARKER_VERSION).build());
			} catch (Exception e) {
				errorLoading=true;
				logger.error("error init Freemarker ",e);
			}
			
	}
	
	public <T> String generate(FORMAT_NOTIFICATION f, T obj, Class<T> type)
	{
		List<T> t = new ArrayList<>();
		t.add(obj);
		return generate(f, t, type);
	}
	
	
	
	public <T> String generate(FORMAT_NOTIFICATION f, List<T> list, Class<T> type)
	{
		if(errorLoading)
			return list.toString();
		
		Map<String,Object> input = new HashMap<>();
		input.put("modele", list);
		try {
			
			String tmpl = type.getSimpleName()+"."+f.name().toLowerCase();
			Template template = cfg.getTemplate(tmpl);
			Writer writer = new StringWriter();
		     		template.process(input, writer);
		    
		    return writer.toString();
		      
		} catch (Exception e) {
			logger.error("error to generate notif : " + f,e);
		} 
		return list.toString();
		
	}
	
	
}
