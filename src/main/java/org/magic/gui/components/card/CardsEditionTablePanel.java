package org.magic.gui.components.card;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.sorters.CardsEditionSorter;
import org.magic.api.sorters.NumberSorter;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.CardsManagerService;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

public class CardsEditionTablePanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private MagicCardTableModel model;
	private MTGEdition currentEdition;
	private AbstractBuzyIndicatorComponent buzy;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JButton btnImport;
	private JComboBox<MTGCollection> cboCollection;
	private transient AbstractObservableWorker<List<MTGCard>, MTGCard,MTGCardsProvider> sw;
	private JCheckBox chkNeededCards;


	public CardsEditionTablePanel() {
		setLayout(new BorderLayout(0, 0));

		var panneauHaut = new JPanel();
		model = new MagicCardTableModel();
		
		model.addHiddenColumns(7);
		
		table = UITools.createNewTable(model,true);
		buzy=AbstractBuzyIndicatorComponent.createProgressComponent();

		table.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		table.setColumnControlVisible(true);

		UITools.initTableVisibility(table, model);
		
		UITools.setSorter(table, 6, new NumberSorter());
		
		panneauHaut.add(buzy);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(panneauHaut,BorderLayout.NORTH);

		var panneauBas = new JPanel();
		add(panneauBas, BorderLayout.SOUTH);

		cboCollection =  UITools.createComboboxCollection();
		panneauBas.add(cboCollection);

		btnImport = new JButton(MTGConstants.ICON_MASS_IMPORT_SMALL);
		btnImport.setEnabled(false);
		panneauBas.add(btnImport);

		chkNeededCards = new JCheckBox(capitalize("FILTER_NEEDED"));
		panneauBas.add(chkNeededCards);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(currentEdition);
			}

		});

		chkNeededCards.addActionListener(il->{


			if(chkNeededCards.isSelected()) {
				var work = new AbstractObservableWorker<List<MTGCard>,MTGCard, MTGDao>(buzy,getEnabledPlugin(MTGDao.class),model.getRowCount()) {

					@Override
					protected List<MTGCard> doInBackground() throws Exception {
						return plug.listCardsFromCollection(new MTGCollection(MTGControler.getInstance().get("default-library")),currentEdition);
					}

					@Override
					protected void error(Exception e) {
						logger.error(e);
					}

					@Override
					protected void notifyEnd() {
						try {
							model.removeItem(get());
						} catch(InterruptedException ex)
						{
							Thread.currentThread().interrupt();
						}catch (Exception e) {
							logger.error(e);
						}
					}
				};

				ThreadManager.getInstance().runInEdt(work, "filtering missing cards");
			}
			else
			{
				init(currentEdition);
			}
		});


		btnImport.addActionListener(ae->{
			var list = getSelectedCards();

			int res = JOptionPane.showConfirmDialog(null,capitalize("COLLECTION_IMPORT") + " :" + list.size() + " cards in " + cboCollection.getSelectedItem());
			if(res==JOptionPane.YES_OPTION)
			{
				buzy.start(list.size());

				SwingWorker<Void, MTGCard> swImp = new SwingWorker<>()
				{
				@Override
					protected void done() {
						buzy.end();
					}

					@Override
					protected void process(List<MTGCard> chunks) {
						buzy.progressSmooth(chunks.size());
					}

					@Override
					protected Void doInBackground() throws Exception {
						for(MTGCard mc : list)
							try {
								CardsManagerService.saveCard(mc, (MTGCollection)cboCollection.getSelectedItem(),null);
								publish(mc);
							} catch (SQLException e) {
								logger.error("couln't save {}", mc,e);
							}
						return null;
						}

						};



				ThreadManager.getInstance().runInEdt(swImp, "import cards in "+cboCollection.getSelectedItem());
			}
		});
	}

	public MTGCard getSelectedCard()
	{
		if(table.getSelectedRow()>-1)
		{
			return UITools.getTableSelection(table, 0);
		}

		return null;
	}

	public List<MTGCard> getSelectedCards()
	{
		return UITools.getTableSelections(table,0);
	}


	public JXTable getTable() {
		return table;
	}

	public void init(MTGEdition ed)
	{
		this.currentEdition=ed;
		chkNeededCards.setSelected(false);
		if(isVisible())
			refresh();
	}

	public void enabledImport(boolean t)
	{
		btnImport.setEnabled(t);
	}

	private void refresh()
	{
		if(currentEdition==null)
			return;


		btnImport.setEnabled(false);


		if(sw!=null && !sw.isDone())
		{
			sw.cancel(true);
		}


		sw = new AbstractObservableWorker<>(buzy,getEnabledPlugin(MTGCardsProvider.class),currentEdition.getCardCount()) {
			
			@Override
			protected List<MTGCard> doInBackground() {
				List<MTGCard> cards = new ArrayList<>();
				try {
					cards = getEnabledPlugin(MTGCardsProvider.class).searchCardByEdition(currentEdition);
					Collections.sort(cards, new CardsEditionSorter() );
					return cards;
				} catch (Exception e) {
					logger.error(e);
					return cards;
				}

			}

			@Override
			protected void process(List<MTGCard> chunks) {
				super.process(chunks);
				model.addItems(chunks);
			}


			@Override
			protected void done() {
				try {
					super.done();
					model.init(get());
					table.packAll();
				} catch(InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}
				catch (ExecutionException e) {
					logger.error(e);
				}
			}



		};

		ThreadManager.getInstance().runInEdt(sw, "loading edition "+currentEdition);
	}



}
