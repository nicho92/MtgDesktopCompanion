package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
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
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.models.CardShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.sorters.PricesCardsShakeSorter;
import org.magic.sorters.PricesCardsShakeSorter.SORT;
import org.magic.tools.UITools;

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
	private JComboBox<PricesCardsShakeSorter.SORT> cboSorter;
	

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_UP;
	}
	
	@Override
	public String getName() {
		return "Winners/Loosers";
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
						shakes.addAll(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(MagicFormat.FORMATS.MODERN));
					if (boxS.isSelected())
						shakes.addAll(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(MagicFormat.FORMATS.STANDARD));
					if (boxL.isSelected())
						shakes.addAll(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(MagicFormat.FORMATS.LEGACY));
					if (boxV.isSelected())
						shakes.addAll(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(MagicFormat.FORMATS.VINTAGE));
					if (boxP.isSelected())
						shakes.addAll(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(MagicFormat.FORMATS.PAUPER));
				
					if(!boxM.isSelected() && !boxS.isSelected() && !boxL.isSelected() && !boxV.isSelected() && !boxP.isSelected())
						shakes.addAll(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(null));
					
					
				
					int val = (Integer) spinner.getValue();
					setProperty("LIMIT", String.valueOf(val));
					setProperty("STD", String.valueOf(boxS.isSelected()));
					setProperty("MDN", String.valueOf(boxM.isSelected()));
					setProperty("LEG", String.valueOf(boxL.isSelected()));
					setProperty("VIN", String.valueOf(boxV.isSelected()));
					setProperty("PAU", String.valueOf(boxP.isSelected()));
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
					//table.setModel(modStandard);
					modStandard.fireTableDataChanged();
					table.setRowSorter(new TableRowSorter(modStandard));
					table.packAll();
					table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());
				} catch (Exception e) {
					logger.error(e);
				} 
				
			}
	
		};
		ThreadManager.getInstance().runInEdt(sw,"update best trending");
	}

	@Override
	public void initGUI() {
		JPanel panneauHaut = new JPanel();
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
		boxP = new JCheckBox("V");
		boxP.setAction(a);
		boxP.setText("PAU");

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
		panneauHaut.add(cboSorter);
		
		modStandard = new CardShakerTableModel();
		table = new JXTable(modStandard);
				
		table.getColumnExt(modStandard.getColumnName(5)).setVisible(false);
		table.getColumnExt(modStandard.getColumnName(6)).setVisible(false);
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		UITools.initTableFilter(table);
		UITools.initCardToolTipTable(table, 0, 1, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				CardShake cs=null;
				try {
					cs = UITools.getTableSelection(table, 0);
					Desktop.getDesktop().browse(new URI(cs.getLink()));
				
				}catch(Exception ex)
				{
					logger.error("error opening browser for " + cs + " :"+ ex);
				}
				return null;
			}
		});

		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
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
