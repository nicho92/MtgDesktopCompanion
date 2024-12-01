package org.magic.gui.components.deck;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.components.dialog.importer.CardImporterDialog;
import org.magic.gui.models.DeckSnifferTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;
public class DeckSnifferDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private JComboBox<MTGDeckSniffer> cboSniffers;
	private JComboBox<String> cboFormats;
	private DeckSnifferTableModel model;
	private MTGDeck importedDeck;
	private AbstractBuzyIndicatorComponent lblLoad = AbstractBuzyIndicatorComponent.createLabelComponent();
	private JButton btnImport;
	private transient MTGDeckSniffer selectedSniffer;
	private JButton btnConnect;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JPanel cardFilterPanel;
	
	private MTGCard filteredCard=null;
	

	public DeckSnifferDialog() {

		importedDeck = new MTGDeck();
		setSize(new Dimension(500, 300));
		setTitle(capitalize("DECKS_IMPORTER"));
		setIconImage(MTGConstants.ICON_DECK.getImage());
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		model = new DeckSnifferTableModel();
		table = UITools.createNewTable(model,true);
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);


		var panelNorth = new JPanel();
		getContentPane().add(panelNorth, BorderLayout.NORTH);

		selectedSniffer = listEnabledPlugins(MTGDeckSniffer.class).get(0);
		panelNorth.setLayout(new BorderLayout(0, 0));


		panelNorth.add(lblLoad, BorderLayout.CENTER);

				cboSniffers =UITools.createComboboxPlugins(MTGDeckSniffer.class,false);
				btnConnect = new JButton(capitalize("CONNECT"));
				cboFormats = new JComboBox<>();
				
				
				var labCardFilter = new JLabel("With this card : ");
				var btnCardImport = UITools.createBindableJButton("", MTGConstants.ICON_TAB_IMPORT, KeyEvent.VK_I, "WithCard");
				var lblCard = new JLabel();
				var btnRemoveCard = new JButton(MTGConstants.ICON_SMALL_DELETE);
				
				btnCardImport.addActionListener(al->{
					var importer = new CardImporterDialog();
					importer.setVisible(true);
					
					if(importer.hasSelected())
					{
						filteredCard = importer.getSelectedItem();
						lblCard.setText(filteredCard.getName());
					}
				});
				
				btnRemoveCard.addActionListener(al->{
					filteredCard = null;
					lblCard.setText("");
					
				});
				
				
				
				cardFilterPanel = UITools.createFlowPanel(labCardFilter,btnCardImport,lblCard,btnRemoveCard);
				
				cardFilterPanel.setVisible(selectedSniffer.hasCardFilter());
				
				
				panelNorth.add(UITools.createFlowPanel(cboSniffers,btnConnect,cboFormats,cardFilterPanel), BorderLayout.WEST);
						
						
						cboFormats.addActionListener(e -> {
							try {
								lblLoad.start();
								
								if(selectedSniffer.hasCardFilter())
									model.init(selectedSniffer.getDeckList(cboFormats.getSelectedItem().toString(),filteredCard));
								else
									model.init(selectedSniffer.getDeckList(cboFormats.getSelectedItem().toString(),null));
								
								model.fireTableDataChanged();
								lblLoad.end();
							}catch (NullPointerException e1) {
								//	do nothing
							} 
							catch (Exception e1) {
								lblLoad.end();
								logger.error("error change cboFormat", e1);
								MTGControler.getInstance().notify(e1);
							}
						});

				btnConnect.addActionListener(e -> ThreadManager.getInstance().executeThread(new MTGRunnable() {

					@Override
					protected void auditedRun() {
						try {
							lblLoad.start();
							selectedSniffer.connect();
							cboFormats.removeAllItems();

							for (String s : selectedSniffer.listFilter())
								cboFormats.addItem(s);


							lblLoad.end();

						} catch (Exception e1) {
							lblLoad.end();
							MTGControler.getInstance().notify(e1);
						}

					}
				}, "Connection to " + selectedSniffer));

		cboSniffers.addActionListener(e -> {
			selectedSniffer = (MTGDeckSniffer) cboSniffers.getSelectedItem();
			cardFilterPanel.setVisible(selectedSniffer.hasCardFilter());
		});

		var panelButton = new JPanel();
		getContentPane().add(panelButton, BorderLayout.SOUTH);

		var btnClose = new JButton(MTGConstants.ICON_CANCEL);
		btnClose.setToolTipText(capitalize("CANCEL"));

		btnClose.addActionListener(e -> dispose());
		panelButton.add(btnClose);

		setLocationRelativeTo(null);

		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		btnImport = new JButton(MTGConstants.ICON_CHECK);
		btnImport.setToolTipText(capitalize("IMPORT"));

		panelButton.add(btnImport);



		btnImport.addActionListener(e ->{

			AbstractObservableWorker<MTGDeck, MTGCard, MTGDeckSniffer> sw = new AbstractObservableWorker<>(lblLoad,selectedSniffer) {

				@Override
				protected void process(List<MTGCard> chunks) {
					buzy.progressSmooth(chunks.size());
					buzy.setText(chunks.toString());
				}

				@Override
				protected MTGDeck doInBackground() throws Exception {
					return plug.getDeck((RetrievableDeck)UITools.getTableSelection(table, 0));
				}

				@Override
				protected void done() {
					super.done();

					importedDeck = getResult();
					btnImport.setEnabled(true);
					dispose();
				}

				@Override
				protected void error(Exception e) {
					MTGControler.getInstance().notify(e);
				}


			};

		ThreadManager.getInstance().runInEdt(sw, "Import deck");

		});


	}

	public MTGDeck getSelectedItem() {
		return importedDeck;
	}

}
