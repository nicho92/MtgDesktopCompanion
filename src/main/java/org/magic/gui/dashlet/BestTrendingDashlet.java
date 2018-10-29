package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.models.CardShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.sorters.CardsShakeSorter;
import org.magic.sorters.CardsShakeSorter.SORT;
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
	private JComboBox<CardsShakeSorter.SORT> cboSorter;
	

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
		ThreadManager.getInstance().execute(() -> {

			try {
				List<CardShake> shakes = new ArrayList<>();

				if (boxM.isSelected())
					shakes.addAll(
							MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(MTGFormat.MODERN));
				if (boxS.isSelected())
					shakes.addAll(
							MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(MTGFormat.STANDARD));
				if (boxL.isSelected())
					shakes.addAll(
							MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(MTGFormat.LEGACY));
				if (boxV.isSelected())
					shakes.addAll(
							MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(MTGFormat.VINTAGE));

				if(shakes.isEmpty())
					shakes.addAll(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(null));
				
				
			
				int val = (Integer) spinner.getValue();
				setProperty("LIMIT", String.valueOf(val));
				setProperty("STD", String.valueOf(boxS.isSelected()));
				setProperty("MDN", String.valueOf(boxM.isSelected()));
				setProperty("LEG", String.valueOf(boxL.isSelected()));
				setProperty("VIN", String.valueOf(boxV.isSelected()));
				setProperty("SORT",String.valueOf(cboSorter.getSelectedItem()));
				
				List<CardShake> ret = new ArrayList<>();
				ret.addAll(shakes.subList(0, val));
				ret.addAll(shakes.subList(shakes.size() - (val + 1), shakes.size())); // x last
				
				
				Collections.sort(ret, new CardsShakeSorter((SORT)cboSorter.getSelectedItem()));
				modStandard.init(ret);
			} catch (IOException e) {
				logger.error(e);
			}

			table.setModel(modStandard);
			modStandard.fireTableDataChanged();
			table.setRowSorter(new TableRowSorter(modStandard));
			table.packAll();
			table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());
			
		}, "Init best Dashlet");

	}

	@Override
	public void initGUI() {
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		Action a = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				init();
			}
		};
		
		cboSorter = UITools.createCombobox(CardsShakeSorter.SORT.values());
		
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

		spinner = new JSpinner();
		spinner.addChangeListener(ce -> init());
		cboSorter.addItemListener(ie -> init());
		
		
		spinner.setModel(new SpinnerNumberModel(5, 1, null, 1));
		panneauHaut.add(spinner);
		panneauHaut.add(boxS);
		panneauHaut.add(boxM);
		panneauHaut.add(boxL);
		panneauHaut.add(boxV);
		panneauHaut.add(cboSorter);
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		modStandard = new CardShakerTableModel();
		table = new JXTable(modStandard);
		scrollPane.setViewportView(table);
		UITools.initCardToolTipTable(table, 0, 1);

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

		setVisible(true);
	}

}
