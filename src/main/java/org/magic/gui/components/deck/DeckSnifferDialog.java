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
	private JPanel cardFilterPanel;
	
	private MTGCard filteredCard=null;
	

	public DeckSnifferDialog() {

		importedDeck = new MTGDeck();
		setSize(new Dimension(500, 300));
		setTitle(capitalize("DECKS_IMPORTER"));
		setLocationRelativeTo(null);
		setIconImage(MTGConstants.ICON_DECK.getImage());
		setModal(true);
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		
		model = new DeckSnifferTableModel();
		table=UITools.createNewTable(model,true);
		selectedSniffer = listEnabledPlugins(MTGDeckSniffer.class).get(0);
		var panelButton = new JPanel();
		cboSniffers =UITools.createComboboxPlugins(MTGDeckSniffer.class,false);
		btnConnect = new JButton(capitalize("CONNECT"));
		cboFormats = new JComboBox<>();
		var labCardFilter = new JLabel("With this card : ");
		var btnCardImport = UITools.createBindableJButton("", MTGConstants.ICON_TAB_IMPORT, KeyEvent.VK_I, "WithCard");
		var lblCard = new JLabel();
		var btnRemoveCard = new JButton(MTGConstants.ICON_SMALL_DELETE);
		cardFilterPanel = UITools.createFlowPanel(labCardFilter,btnCardImport,lblCard,btnRemoveCard);
		var panelNorth = UITools.createFlowPanel(cboSniffers,cardFilterPanel,cboFormats,btnConnect,lblLoad);
		var btnClose = new JButton(MTGConstants.ICON_CANCEL);
		btnImport = new JButton(MTGConstants.ICON_CHECK);
		
		
		cardFilterPanel.setVisible(selectedSniffer.hasCardFilter());
		btnClose.setToolTipText(capitalize("CANCEL"));
		btnImport.setToolTipText(capitalize("IMPORT"));
		
		
		
		getContentPane().add(panelNorth, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		getContentPane().add(panelButton, BorderLayout.SOUTH);
		panelButton.add(btnClose);

		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		

		panelButton.add(btnImport);

		
		
		btnClose.addActionListener(e -> dispose());
		
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
		
		
		

		cboSniffers.addActionListener(e -> {
			cboFormats.removeAllItems();
			selectedSniffer = (MTGDeckSniffer) cboSniffers.getSelectedItem();
			
			for (String s : selectedSniffer.listFilter())
				cboFormats.addItem(s);
			
			cardFilterPanel.setVisible(selectedSniffer.hasCardFilter());
			pack();
		});
		
				
		btnConnect.addActionListener(e -> {
				lblLoad.start();
				ThreadManager.getInstance().runInEdt(new AbstractObservableWorker<List <RetrievableDeck>, MTGCard, MTGDeckSniffer>(lblLoad,selectedSniffer){

					@Override
					protected List <RetrievableDeck> doInBackground() throws Exception {
						if(plug.hasCardFilter())
							return plug.getDeckList(cboFormats.getSelectedItem().toString(),filteredCard);
						else
							return plug.getDeckList(cboFormats.getSelectedItem().toString(),null);
					}

					@Override
					protected void notifyEnd() {
						model.init(getResult());
						model.fireTableDataChanged();
					}
				}, "snif deck");
		});


		btnImport.addActionListener(e ->{

				var sw = new AbstractObservableWorker<MTGDeck, MTGCard, MTGDeckSniffer>(lblLoad,selectedSniffer) {

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

		
		pack();
		
		
		});


	}

	public MTGDeck getSelectedItem() {
		return importedDeck;
	}

}
