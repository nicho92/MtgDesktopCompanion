package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.components.JBuzyLabel;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class MassMoverDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable tableCards;
	private MagicCardTableModel model;
	private transient MTGDao dao;
	private MagicCollection toSaveCol;
	private MagicEdition toSaveEd;
	private boolean change = false;
	private JComboBox<MagicCollection> cboCollections;
	private JBuzyLabel lblWaiting;
	private JButton btnMove;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public MassMoverDialog(MagicCollection col, MagicEdition ed) {
		setSize(new Dimension(640, 370));
		setTitle(MTGControler.getInstance().getLangService().getCapitalize("MASS_MOVEMENTS") + " : " + col);
		setIconImage(MTGConstants.ICON_TAB_IMPORT_EXPORT.getImage());
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setLocationRelativeTo(null);

		dao = MTGControler.getInstance().getEnabledDAO();
		this.toSaveCol = col;
		this.toSaveEd = ed;

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		btnMove = new JButton(MTGControler.getInstance().getLangService().getCapitalize("MOVE_TO"));

		panel.add(btnMove);

		cboCollections = null;
		try {
			cboCollections = new JComboBox<>(
					dao.getCollections().toArray(new MagicCollection[dao.getCollections().size()]));
		} catch (SQLException e) {
			logger.error(e);
		}
		panel.add(cboCollections);

		lblWaiting = new JBuzyLabel();
		panel.add(lblWaiting);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		model = new MagicCardTableModel();
		try {
			if (ed == null)
				model.init(dao.listCardsFromCollection(col));
			else
				model.init(dao.listCardsFromCollection(col, ed));
		} catch (SQLException e) {
			logger.error(e);
		}

		tableCards = new JTable(model);
		tableCards.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());

		scrollPane.setViewportView(tableCards);

		TableFilterHeader filterHeader = new TableFilterHeader(tableCards, AutoChoices.ENABLED);
		filterHeader.setSelectionBackground(Color.LIGHT_GRAY);

		btnMove.addActionListener(e -> {
			lblWaiting.buzy(true);
			btnMove.setEnabled(false);

			if (tableCards.getSelectedRowCount() > 0) {
				ThreadManager.getInstance().execute(() -> {

					for (int i = 0; i < tableCards.getSelectedRowCount(); i++) {
						int viewRow = tableCards.getSelectedRows()[i];
						int modelRow = tableCards.convertRowIndexToModel(viewRow);
						MagicCard mc = (MagicCard) tableCards.getModel().getValueAt(modelRow, 0);
						try {
							
							dao.moveCard(mc, toSaveCol, (MagicCollection) cboCollections.getSelectedItem());
							
							logger.info("moving " + mc + " to " + cboCollections.getSelectedItem());
							change = true;
							lblWaiting.buzy(true,"moving " + mc);
						} catch (SQLException e1) {
							logger.error(e1);
							MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e1));
							lblWaiting.buzy(false);
							btnMove.setEnabled(true);

						}
					}

					try {
						lblWaiting.buzy(true,MTGControler.getInstance().getLangService().getCapitalize("UPDATE"));
						if (toSaveEd == null)
							model.init(dao.listCardsFromCollection(toSaveCol));
						else
							model.init(dao.listCardsFromCollection(toSaveCol, toSaveEd));
					} catch (SQLException ex) {
						logger.error(ex);
						MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),ex));
					}

					model.fireTableDataChanged();
					lblWaiting.buzy(false);
					btnMove.setEnabled(true);

				}, "mass movement");

			}
		});

	}

	public boolean hasChange() {
		return change;
	}

}
