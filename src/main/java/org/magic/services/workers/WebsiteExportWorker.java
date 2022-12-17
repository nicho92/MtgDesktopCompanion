package org.magic.services.workers;

import static org.magic.services.tools.MTG.capitalize;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.services.MTGControler;
import org.magic.services.MagicWebSiteGenerator;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.UITools;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;
public class WebsiteExportWorker extends SwingWorker<Void, Integer> {


	private String templateName;
	private File dest;
	protected AbstractBuzyIndicatorComponent buzy;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected Observer o;
	private List<MagicCollection> cols;
	private List<MTGPricesProvider> pricers;

	public WebsiteExportWorker(String templateName,File dest,List<MagicCollection> cols,List<MTGPricesProvider> pricers,AbstractBuzyIndicatorComponent buzy) {
		this.dest = dest;
		this.templateName=templateName;
		this.cols=cols;
		this.pricers=pricers;
		this.buzy=buzy;
		o=(Observable obs, Object c)->publish((Integer)c);
	}


	@Override
	protected Void doInBackground() throws Exception {
		var gen = new MagicWebSiteGenerator(templateName, dest.getAbsolutePath());
		gen.addObserver(o);
		gen.generate(cols,pricers );

		return null;
	}

	@Override
	protected void process(List<Integer> chunks) {
		buzy.progressSmooth(chunks.size());
	}


	@Override
	protected void done() {


		try {
			get();
		}
		catch(InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
		catch(Exception e)
		{
			logger.error("error generating website",e);
			MTGControler.getInstance().notify(e);
			buzy.end();
			return;
		}


		int res = JOptionPane.showConfirmDialog(null,capitalize("WEBSITE_CONFIRMATION_VIEW"));

		if (res == JOptionPane.YES_OPTION) {
			var p = Paths.get(dest.getAbsolutePath());
			UITools.browse(p.toUri().toASCIIString());

		}
		buzy.end();
	}

}
