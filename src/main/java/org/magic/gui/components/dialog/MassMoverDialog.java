package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.tools.UITools;

public class MassMoverDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JXTable tableCards;
	private MagicCardTableModel model;
	private transient MTGDao dao;
	private MagicCollection toSaveCol;
	private MagicEdition toSaveEd;
	private boolean change = false;
	private JComboBox<MagicCollection> cboCollections;
	private AbstractBuzyIndicatorComponent lblWaiting = AbstractBuzyIndicatorComponent.createProgressComponent();
	private JButton btnMove;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public MassMoverDialog(MagicCollection col, MagicEdition ed) {
		setSize(new Dimension(640, 370));
		setTitle(MTGControler.getInstance().getLangService().getCapitalize("MASS_MOVEMENTS") + " : " + col);
		setIconImage(MTGConstants.ICON_TAB_IMPORT_EXPORT.getImage());
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setLocationRelativeTo(null);

		cboCollections = UITools.createComboboxCollection();
		dao = MTGControler.getInstance().getEnabled(MTGDao.class);
		
		
		this.toSaveCol = col;
		this.toSaveEd = ed;

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		btnMove = new JButton(MTGControler.getInstance().getLangService().getCapitalize("MOVE_TO"));

		panel.add(btnMove);
		panel.add(cboCollections);
		panel.add(lblWaiting);

	
		model = new MagicCardTableModel();
		try {
			if (ed == null)
				model.init(dao.listCardsFromCollection(col));
			else
				model.init(dao.listCardsFromCollection(col, ed));
		} catch (SQLException e) {
			logger.error(e);
		}

		tableCards = new JXTable(model);
		tableCards.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		getContentPane().add(new JScrollPane(tableCards), BorderLayout.CENTER);

		UITools.initTableFilter(tableCards);

		btnMove.addActionListener(e -> {
			btnMove.setEnabled(false);

			
			SwingWorker<Void, MagicCard> sw = new SwingWorker<Void, MagicCard>() {
				
				@Override
				protected void done() {
					model.fireTableDataChanged();
					try {
						get();
						if (toSaveEd == null)
							model.init(dao.listCardsFromCollection(toSaveCol));
						else
							model.init(dao.listCardsFromCollection(toSaveCol, toSaveEd));
					} catch (SQLException | ExecutionException ex) {
						logger.error(ex);
						MTGControler.getInstance().notify(ex);
						lblWaiting.end();
						btnMove.setEnabled(true);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						logger.error(e);
						lblWaiting.end();
						btnMove.setEnabled(true);
					}
					lblWaiting.end();
					btnMove.setEnabled(true);
				}

				@Override
				protected void process(List<MagicCard> chunks) {
					lblWaiting.setText("moving " + chunks);
					lblWaiting.progressSmooth(chunks.size());
				}

				@Override
				protected Void doInBackground(){
					for (int i = 0; i < tableCards.getSelectedRowCount(); i++) {
						int viewRow = tableCards.getSelectedRows()[i];
						int modelRow = tableCards.convertRowIndexToModel(viewRow);
						MagicCard mc = (MagicCard) tableCards.getModel().getValueAt(modelRow, 0);
						try {
							dao.moveCard(mc, toSaveCol, (MagicCollection) cboCollections.getSelectedItem());
							publish(mc);
							logger.info("moving " + mc + " to " + cboCollections.getSelectedItem());
							change = true;
						} catch (SQLException e1) {
							logger.error(e1);
						}
					}
					
					

					
					return null;

				}
			};
			
			
			
			if (tableCards.getSelectedRowCount() > 0) {
				lblWaiting.start(tableCards.getSelectedRowCount());
				ThreadManager.getInstance().runInEdt(sw, "mass movement");

			}
		});

	}

	public boolean hasChange() {
		return change;
	}

}
