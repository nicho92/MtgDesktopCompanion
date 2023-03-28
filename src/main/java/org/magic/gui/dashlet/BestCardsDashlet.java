package org.magic.gui.dashlet;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.MTGFormat;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.models.CardDominanceTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;

public class BestCardsDashlet extends AbstractJDashlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private CardDominanceTableModel models;
	private JComboBox<MTGFormat.FORMATS> cboFormat;
	private JComboBox<String> cboFilter;


	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_UP;
	}

	@Override
	public String getCategory() {
		return "Market";
	}




	@Override
	public String getName() {
		return "Most Played cards";
	}

	@Override
	public void initGUI() {
		var panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		cboFormat = UITools.createCombobox(MTGFormat.FORMATS.values());

		panneauHaut.add(cboFormat);

		cboFilter = UITools.createCombobox(getEnabledPlugin(MTGDashBoard.class).getDominanceFilters());
		panneauHaut.add(cboFilter);

		panneauHaut.add(buzy);

		models = new CardDominanceTableModel();
		table = UITools.createNewTable(models);
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		UITools.initCardToolTipTable(table, 0, null,null,null);

		cboFormat.addItemListener(ie -> {
			if(ie.getStateChange()==ItemEvent.SELECTED)
				init();
		});

		cboFilter.addItemListener(ie -> {

			if(ie.getStateChange()==ItemEvent.SELECTED)
				init();
		});

		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			try {
				cboFormat.setSelectedItem(getProperty("FORMAT", "standard"));
				cboFilter.setSelectedItem(getProperty("FILTER", "all"));
			} catch (Exception e) {
				logger.error("can't get value", e);
			}
			setBounds(r);
		}
	}

	@Override
	public void init() {
		buzy.start();
		var sw = new SwingWorker<List<CardDominance>, Void>() {

			@Override
			protected List<CardDominance> doInBackground() throws Exception {
				return getEnabledPlugin(MTGDashBoard.class).getBestCards((MTGFormat.FORMATS) cboFormat.getSelectedItem(), cboFilter.getSelectedItem().toString());
			}

			@Override
			protected void done() {

				try {
					models.init(get());
					models.fireTableDataChanged();
					table.packAll();
					table.setRowSorter(new TableRowSorter<>(models));
					setProperty("FORMAT", cboFormat.getSelectedItem().toString());
					setProperty("FILTER", cboFilter.getSelectedItem().toString());

				}
				catch(InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}
				catch(Exception e)
				{
					logger.error(e);
				}
				buzy.end();
			}

		};


		ThreadManager.getInstance().runInEdt(sw, "Loading best cards");
	}

}
