package org.magic.services;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.FileTools;
import org.utils.patterns.observer.Observable;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;


public class MagicWebSiteGenerator extends Observable {

	private Configuration cfg;
	private String dest;
	private List<MTGPricesProvider> pricesProvider;
	private List<MTGCollection> cols;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private int i = 0;

	public MagicWebSiteGenerator(String template, String dest) throws IOException {
		cfg = new Configuration(MTGConstants.FREEMARKER_VERSION);
		cfg.setDirectoryForTemplateLoading(new File(MTGConstants.MTG_TEMPLATES_DIR, template));
		cfg.setDefaultEncoding(MTGConstants.DEFAULT_ENCODING.displayName());
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(MTGConstants.FREEMARKER_VERSION).build());

		this.dest = dest;
		FileTools.copyDirectory(new File(MTGConstants.MTG_TEMPLATES_DIR, template), new File(dest), pathname -> {
			if (pathname.isDirectory())
				return true;

			return !pathname.getName().endsWith(".html");
		});
	}

	public void generate(List<MTGCollection> cols, List<MTGPricesProvider> providers) throws TemplateException, IOException, SQLException {
		this.pricesProvider = providers;
		this.cols = cols;

		var generatedTemplate = cfg.getTemplate("index.html");
		try (Writer out = new FileWriter(Paths.get(dest, "index.htm").toFile())) {
			Map<String, List<MTGCard>> root = new HashMap<>();
			for (MTGCollection col : cols)
				root.put(col.getName(), getEnabledPlugin(MTGDao.class).listCardsFromCollection(col));

			generatedTemplate.process(root, out);
			generateCollectionsTemplate();
		}
	}

	// lister les editions disponibles
	private void generateCollectionsTemplate() throws IOException, TemplateException, SQLException {
		var generatedColTemplate = cfg.getTemplate("page-col.html");

		for (MTGCollection col : cols) {
			Map<String,Object> rootEd = new HashMap<>();
			rootEd.put("cols", cols);
			rootEd.put("colName", col.getName());
			Set<MTGEdition> eds = new HashSet<>();
			for (MTGCard mc : getEnabledPlugin(MTGDao.class).listCardsFromCollection(col)) {
				eds.add(mc.getEdition());
				generateCardsTemplate(mc);
			}

			rootEd.put("editions", eds);

			var out = new FileWriter(Paths.get(dest, "page-col-" + col.getName() + ".htm").toFile());
			generatedColTemplate.process(rootEd, out);

			generateEditionsTemplate(eds, col);
			out.close();

		}
	}

	private void generateEditionsTemplate(Set<MTGEdition> eds, MTGCollection col)throws IOException, SQLException, TemplateException {
		var cardTemplate = cfg.getTemplate("page-ed.html");
		Map<String,Object> rootEd = new HashMap<>();
		rootEd.put("cols", cols);
		rootEd.put("editions", eds);
		rootEd.put("col", col);
		rootEd.put("colName", col.getName());
		for (MTGEdition ed : eds) {
			rootEd.put("cards", getEnabledPlugin(MTGDao.class).listCardsFromCollection(col, ed));
			rootEd.put("edition", ed);
			var out = new FileWriter(
					Paths.get(dest, "page-ed-" + col.getName() + "-" + ed.getId() + ".htm").toFile());
			cardTemplate.process(rootEd, out);
		}

	}


	private void generateCardsTemplate(MTGCard mc) throws IOException, TemplateException {
		var cardTemplate = cfg.getTemplate("page-card.html");

		Map<String,Object> rootEd = new HashMap<>();
		rootEd.put("card", mc);
		rootEd.put("cols", cols);

		List<MTGPrice> prices = new ArrayList<>();
		if (!pricesProvider.isEmpty()) {
			for (MTGPricesProvider prov : pricesProvider) {
				try {
					prices.addAll(prov.getPrice(mc));
				} catch (Exception e) {
					logger.error("Generating card template for {}", mc, e);
				}
			}
		}
		rootEd.put("prices", prices);
		var out = new FileWriter(Paths.get(dest, "page-card-" + mc.getScryfallId() + ".htm").toFile());
		cardTemplate.process(rootEd, out);

		setChanged();
		notifyObservers(i++);
		out.close();

	}
}
