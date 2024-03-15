package org.magic.gui.dashlet;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.api.sorters.PricesCardsShakeSorter;
import org.magic.api.sorters.PricesCardsShakeSorter.SORT;
import org.magic.gui.models.CardShakerTableModel;
import org.magic.gui.renderer.standard.DoubleCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;

public class BestTrendingDashlet extends AbstractJDashlet {

	private static final String TRUE = "true";
	private static final String FALSE = "false";
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private CardShakerTableModel modStandard;
	private JSpinner spinner;

	private JCheckBox boxS;
	private JCheckBox boxM;
	private JCheckBox boxV;
	private JCheckBox boxL;
	private JCheckBox boxP;
	private JCheckBox boxPioneer;
	private JComboBox<PricesCardsShakeSorter.SORT> cboSorter;


	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_UP;
	}

	@Override
	public String getName() {
		return "Winners/Loosers";
	}

	@Override
	public String getCategory() {
		return "Market";
	}

	@Override
	public void init() {


		SwingWorker<List<CardShake>, Void> sw = new SwingWorker<>()
		{

			@Override
			protected List<CardShake> doInBackground() {
				List<CardShake> ret = new ArrayList<>();
				try {
					List<CardShake> shakes = new ArrayList<>();

					if (boxM.isSelected())
						shakes.addAll(getEnabledPlugin(MTGDashBoard.class).getShakerFor(MTGFormat.FORMATS.MODERN));
					if (boxS.isSelected())
						shakes.addAll(getEnabledPlugin(MTGDashBoard.class).getShakerFor(MTGFormat.FORMATS.STANDARD));
					if (boxL.isSelected())
						shakes.addAll(getEnabledPlugin(MTGDashBoard.class).getShakerFor(MTGFormat.FORMATS.LEGACY));
					if (boxV.isSelected())
						shakes.addAll(getEnabledPlugin(MTGDashBoard.class).getShakerFor(MTGFormat.FORMATS.VINTAGE));
					if(boxPioneer.isSelected())
						shakes.addAll(getEnabledPlugin(MTGDashBoard.class).getShakerFor(MTGFormat.FORMATS.PIONEER));
					if (boxP.isSelected())
						shakes.addAll(getEnabledPlugin(MTGDashBoard.class).getShakerFor(MTGFormat.FORMATS.PAUPER));

					if(!boxM.isSelected() && !boxS.isSelected() && !boxL.isSelected() && !boxV.isSelected() && !boxP.isSelected())
						shakes.addAll(getEnabledPlugin(MTGDashBoard.class).getShakerFor(null));



					int val = (Integer) spinner.getValue();
					setProperty("LIMIT", String.valueOf(val));
					setProperty("STD", String.valueOf(boxS.isSelected()));
					setProperty("MDN", String.valueOf(boxM.isSelected()));
					setProperty("LEG", String.valueOf(boxL.isSelected()));
					setProperty("VIN", String.valueOf(boxV.isSelected()));
					setProperty("PAU", String.valueOf(boxP.isSelected()));
					setProperty("PIO",String.valueOf(boxPioneer.isSelected()));
					setProperty("SORT",String.valueOf(cboSorter.getSelectedItem()));


					ret.addAll(shakes.subList(0, val));
					ret.addAll(shakes.subList(shakes.size() - (val + 1), shakes.size())); // x last

				} catch (Exception e) {
					logger.error(e);
				}
				return ret;

			}

			@Override
			protected void done() {

				List<CardShake> ret;
				try {
					ret = get();

					Collections.sort(ret, new PricesCardsShakeSorter((SORT)cboSorter.getSelectedItem(),false));
					modStandard.init(ret);
					table.setRowSorter(new TableRowSorter<>(modStandard));
					table.packAll();
				} catch(InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}catch (Exception e) {
					logger.error(e);
				}

			}

		};
		ThreadManager.getInstance().runInEdt(sw,"update best trending");
	}

	@Override
	public void initGUI() {
		var panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		Action a = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				init();
			}
		};

		cboSorter = UITools.createCombobox(PricesCardsShakeSorter.SORT.values());

		boxS = new JCheckBox();
		boxS.setAction(a);
		boxS.setText("STD");
		boxM = new JCheckBox();
		boxM.setAction(a);
		boxM.setText("MDN");
		boxL = new JCheckBox();
		boxL.setAction(a);
		boxL.setText("LEG");
		boxV = new JCheckBox("V");
		boxV.setAction(a);
		boxV.setText("VIN");
		boxP = new JCheckBox();
		boxP.setAction(a);
		boxP.setText("PAU");
		boxPioneer = new JCheckBox();
		boxPioneer.setAction(a);
		boxPioneer.setText("PIO");


		spinner = new JSpinner();
		spinner.addChangeListener(ce -> init());
		cboSorter.addItemListener(ie -> {
			if(ie.getStateChange()==ItemEvent.SELECTED)
				init();
		});


		spinner.setModel(new SpinnerNumberModel(5, 1, null, 1));
		panneauHaut.add(spinner);
		panneauHaut.add(boxS);
		panneauHaut.add(boxM);
		panneauHaut.add(boxL);
		panneauHaut.add(boxV);
		panneauHaut.add(boxP);
		panneauHaut.add(boxPioneer);
		panneauHaut.add(cboSorter);

		modStandard = new CardShakerTableModel();
		table = UITools.createNewTable(modStandard,true);
		table.getColumnModel().getColumn(4).setCellRenderer(new DoubleCellEditorRenderer(true,true));
		table.getColumnModel().getColumn(3).setCellRenderer(new DoubleCellEditorRenderer(true));
		table.getColumnModel().getColumn(5).setCellRenderer(new DoubleCellEditorRenderer(true));
		table.getColumnModel().getColumn(6).setCellRenderer(new DoubleCellEditorRenderer(true));

		table.getColumnExt(modStandard.getColumnName(5)).setVisible(false);
		table.getColumnExt(modStandard.getColumnName(6)).setVisible(false);


		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		UITools.initCardToolTipTable(table, 0, 1, 8,new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				CardShake cs=null;
					cs = UITools.getTableSelection(table, 0);
					UITools.browse(cs.getLink());

				return null;
			}
		});

		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			try {
				spinner.setValue(Integer.parseInt(getProperty("LIMIT", "5")));
			} catch (Exception e) {
				logger.error("can't get LIMIT value", e);
			}

			try {
				boxS.setSelected(Boolean.parseBoolean(getProperty("STD", FALSE)));
				boxM.setSelected(Boolean.parseBoolean(getProperty("MDN", TRUE)));
				boxL.setSelected(Boolean.parseBoolean(getProperty("LEG", FALSE)));
				boxV.setSelected(Boolean.parseBoolean(getProperty("VIN", FALSE)));
				cboSorter.setSelectedItem(SORT.valueOf(getProperty("SORT","DAY_PRICE_CHANGE")));
			} catch (Exception e) {
				logger.error("can't get boxs values", e);
			}

			setBounds(r);
		}
	}

}
