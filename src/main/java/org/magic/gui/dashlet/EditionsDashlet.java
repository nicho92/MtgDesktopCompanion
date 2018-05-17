package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.JBuzyLabel;
import org.magic.gui.models.EditionsShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.gui.renderer.MagicEditionIconListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class EditionsDashlet extends AbstractJDashlet {

	private JXTable table;
	private JBuzyLabel lblLoading;
	private JComboBox<MagicEdition> cboEditions;
	private EditionsShakerTableModel modEdition;

	public EditionsDashlet() {
		super();
		setFrameIcon(MTGConstants.ICON_EURO);
	}

	public void initGUI() {
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		modEdition = new EditionsShakerTableModel();

		List<MagicEdition> eds = new ArrayList<>();

		try {
			eds.addAll(MTGControler.getInstance().getEnabledCardsProviders().loadEditions());
			Collections.sort(eds);
			eds.add(0, null);
		} catch (Exception e) {
			logger.error(e);
		}

		cboEditions = new JComboBox(new DefaultComboBoxModel<MagicEdition>(eds.toArray(new MagicEdition[eds.size()])));
		cboEditions.setRenderer(new MagicEditionIconListRenderer());

		panel.add(cboEditions);

		lblLoading = new JBuzyLabel();
		panel.add(lblLoading);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JXTable(modEdition);
		initToolTip(table, 0, 1);

		table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new CardShakeRenderer());

		scrollPane.setViewportView(table);
		setVisible(true);

		cboEditions.addActionListener(ae -> init());

		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			MagicEdition ed;
			try {
				ed = MTGControler.getInstance().getEnabledCardsProviders().getSetById(getString("EDITION"));
				cboEditions.setSelectedItem(ed);
			} catch (Exception e) {
				logger.error("Error retrieve editions", e);
			}

			setBounds(r);
		}

		try {
			table.packAll();
		} catch (Exception e) {
			// do nothing
		}

		new TableFilterHeader(table, AutoChoices.ENABLED);

	}

	@Override
	public String getName() {
		return "Editions Prices";
	}

	@Override
	public void init() {

		if (cboEditions.getSelectedItem() != null)
			ThreadManager.getInstance().execute(() -> {
				lblLoading.buzy(true);
				MagicEdition ed = (MagicEdition) cboEditions.getSelectedItem();
				modEdition.init(ed);
				try {
					modEdition.fireTableDataChanged();
					table.packAll();
					table.setRowSorter(new TableRowSorter(modEdition));
				} catch (Exception e) {
					// do nothing
				}
				setProperty("EDITION", ed.getId());
				lblLoading.buzy(false);
			}, "init EditionDashLet");

	}

}
