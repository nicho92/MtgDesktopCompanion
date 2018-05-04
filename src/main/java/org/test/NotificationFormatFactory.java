package org.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.magic.api.beans.CardShake;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class NotificationFormatFactory {

	
	public static void main(String[] args) throws TemplateException, IOException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

        // Where do we load the templates from:
        cfg.setDirectoryForTemplateLoading(new File(NotificationFormatFactory.class.getResource("/report").getFile()));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // 2.1. Prepare the template input:

        Map<String, Object> input = new HashMap<>();

        input.put("title", "example");
        List<CardShake> shakes = new ArrayList<>();
        
        CardShake cs = new CardShake();
        cs.setName("test");
        shakes.add(cs);
        
        input.put("shakes", shakes);

        // 2.2. Get the template

        Template template = cfg.getTemplate("cardshakes.html");

        // 2.3. Generate the output

        // Write output to the console
        Writer consoleWriter = new OutputStreamWriter(System.out);
        template.process(input, consoleWriter);

	}
}
