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

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.services.logging.MTGLogger;
import org.utils.patterns.observer.Observable;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;


public class MagicWebSiteGenerator extends Observable {

	private Configuration cfg;
	private String dest;
	private List<MTGPricesProvider> pricesProvider;
	private List<MagicCollection> cols;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private int i = 0;

	public MagicWebSiteGenerator(String template, String dest) throws IOException {
		cfg = new Configuration(MTGConstants.FREEMARKER_VERSION);
		cfg.setDirectoryForTemplateLoading(new File(MTGConstants.MTG_TEMPLATES_DIR, template));
		cfg.setDefaultEncoding(MTGConstants.DEFAULT_ENCODING.displayName());
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(MTGConstants.FREEMARKER_VERSION).build());

		this.dest = dest;
		FileUtils.copyDirectory(new File(MTGConstants.MTG_TEMPLATES_DIR, template), new File(dest), pathname -> {
			if (pathname.isDirectory())
				return true;

			return !pathname.getName().endsWith(".html");
		});
	}

	public void generate(List<MagicCollection> cols, List<MTGPricesProvider> providers) throws TemplateException, IOException, SQLException {
		this.pricesProvider = providers;
		this.cols = cols;

		var generatedTemplate = cfg.getTemplate("index.html");
		try (Writer out = new FileWriter(Paths.get(dest, "index.htm").toFile())) {
			Map<String, List<MagicCard>> root = new HashMap<>();
			for (MagicCollection col : cols)
				root.put(col.getName(), getEnabledPlugin(MTGDao.class).listCardsFromCollection(col));

			generatedTemplate.process(root, out);
			generateCollectionsTemplate();
		}
	}

	// lister les editions disponibles
	private void generateCollectionsTemplate() throws IOException, TemplateException, SQLException {
		var generatedColTemplate = cfg.getTemplate("page-col.html");

		for (MagicCollection col : cols) {
			Map<String,Object> rootEd = new HashMap<>();
			rootEd.put("cols", cols);
			rootEd.put("colName", col.getName());
			Set<MagicEdition> eds = new HashSet<>();
			for (MagicCard mc : getEnabledPlugin(MTGDao.class).listCardsFromCollection(col)) {
				eds.add(mc.getCurrentSet());
				generateCardsTemplate(mc);
			}

			rootEd.put("editions", eds);

			var out = new FileWriter(Paths.get(dest, "page-col-" + col.getName() + ".htm").toFile());
			generatedColTemplate.process(rootEd, out);

			generateEditionsTemplate(eds, col);
			out.close();

		}
	}

	private void generateEditionsTemplate(Set<MagicEdition> eds, MagicCollection col)throws IOException, SQLException, TemplateException {
		var cardTemplate = cfg.getTemplate("page-ed.html");
		Map<String,Object> rootEd = new HashMap<>();
		rootEd.put("cols", cols);
		rootEd.put("editions", eds);
		rootEd.put("col", col);
		rootEd.put("colName", col.getName());
		for (MagicEdition ed : eds) {
			rootEd.put("cards", getEnabledPlugin(MTGDao.class).listCardsFromCollection(col, ed));
			rootEd.put("edition", ed);
			var out = new FileWriter(
					Paths.get(dest, "page-ed-" + col.getName() + "-" + ed.getId() + ".htm").toFile());
			cardTemplate.process(rootEd, out);
		}

	}


	private void generateCardsTemplate(MagicCard mc) throws IOException, TemplateException {
		var cardTemplate = cfg.getTemplate("page-card.html");

		Map<String,Object> rootEd = new HashMap<>();
		rootEd.put("card", mc);
		rootEd.put("cols", cols);

		List<MagicPrice> prices = new ArrayList<>();
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
		var out = new FileWriter(Paths.get(dest, "page-card-" + mc.getId() + ".htm").toFile());
		cardTemplate.process(rootEd, out);

		setChanged();
		notifyObservers(i++);
		out.close();

	}
}
