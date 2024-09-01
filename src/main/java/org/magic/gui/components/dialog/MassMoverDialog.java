package org.magic.gui.components.dialog;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

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

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.sorters.NumberSorter;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.MagicEditionsComboBoxCellRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;


public class MassMoverDialog extends JDialog {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable tableCards;
	private MagicCardTableModel model;
	private transient MTGDao dao;
	private MTGCollection toSaveCol;
	private MTGEdition toSaveEd;
	private boolean change = false;
	private JComboBox<MTGCollection> cboCollections;
	private AbstractBuzyIndicatorComponent lblWaiting = AbstractBuzyIndicatorComponent.createProgressComponent();
	private JButton btnMove;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public MassMoverDialog(MTGCollection col, MTGEdition ed) {
		setSize(new Dimension(640, 370));
		setTitle(capitalize("MASS_MOVEMENTS") + " : " + col);
		setIconImage(MTGConstants.ICON_TAB_IMPORT_EXPORT.getImage());
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setLocationRelativeTo(null);

		cboCollections = UITools.createComboboxCollection();
		dao = getEnabledPlugin(MTGDao.class);


		this.toSaveCol = col;
		this.toSaveEd = ed;

		var panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		btnMove = new JButton(capitalize("MOVE_TO"));

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

		tableCards = UITools.createNewTable(model,true);
		tableCards.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		
		getContentPane().add(new JScrollPane(tableCards), BorderLayout.CENTER);
		
		tableCards.packAll();
		
		for(int i : model.defaultHiddenColumns())
			tableCards.getColumnExt(model.getColumnName(i)).setVisible(false);
		
			
		
		
		UITools.setSorter(tableCards, 6, new NumberSorter());
		
		btnMove.addActionListener(e -> {
			btnMove.setEnabled(false);


			var sw = new SwingWorker<Void, MTGCard>() {

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
				protected void process(List<MTGCard> chunks) {
					lblWaiting.setText("moving " + chunks);
					lblWaiting.progressSmooth(chunks.size());
				}

				@Override
				protected Void doInBackground(){
					List<MTGCard> list = UITools.getTableSelections(tableCards, 0);
					for(MTGCard mc : list) {
						try {
							dao.moveCard(mc, toSaveCol, (MTGCollection) cboCollections.getSelectedItem());
							publish(mc);
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
