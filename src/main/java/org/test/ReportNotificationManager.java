package org.test;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.CardShake;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class ReportNotificationManager {
	
	
	public static void main(String[] args) throws Exception {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
			        cfg.setDirectoryForTemplateLoading(new File(ReportNotificationManager.class.getResource("/report").getFile()));
					cfg.setDefaultEncoding("UTF-8");
					cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
					cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28).build());
     
		
		Map<String,Object> input = new HashMap<>();
        List<CardShake> cardshakes = new ArrayList<>();
		        		cardshakes.add(new CardShake());
		        		cardshakes.add(new CardShake());
		        		cardshakes.add(new CardShake());
        		
        input.put("values", cardshakes);
        
        // 2.2. Get the template

       Template template = cfg.getTemplate("cardshake.html");

        // 2.3. Generate the output
       
        // Write output to the console
        Writer writer = new StringWriter();
        template.process(input, writer);
        
        System.out.println(writer.toString());

    }
}
