package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.DeckSelectionTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;

public class CardsDeckCheckerPanel extends JComponent {
	
	private static final long serialVersionUID = 1L;
	private AbstractBuzyIndicatorComponent buzyLabel;
	private JXTable table;
	private DeckSelectionTableModel model;
	private MagicCard selectedCard;
	private transient MTGDeckManager manager;
	
	public CardsDeckCheckerPanel() {
		setLayout(new BorderLayout(0, 0));
		buzyLabel = AbstractBuzyIndicatorComponent.createLabelComponent();
		var panel = new JPanel();
		manager = new MTGDeckManager();
		model = new DeckSelectionTableModel();
		table = UITools.createNewTable(model);
		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());

		
		add(panel, BorderLayout.NORTH);
		panel.add(buzyLabel);
		add(new JScrollPane(table), BorderLayout.CENTER);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init();
			}
		});
	}
	
	public void init(MagicCard mc)
	{
		this.selectedCard=mc;
		init();
	}

	public void init() {
		if(isVisible() && selectedCard!=null)
		{
			buzyLabel.start();
			buzyLabel.setText("looking for decks with " + selectedCard);
			var sw = new SwingWorker<List<MagicDeck>,Void>()
					{

						@Override
						protected List<MagicDeck> doInBackground() throws Exception {
							return manager.listDecksWith(selectedCard);
						}
						@Override
						protected void done() {
							try {
								model.init(get());
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							} catch (ExecutionException e) {
								MTGControler.getInstance().notify(e);
							}
							buzyLabel.end();
						}
						
					};
			
			
			ThreadManager.getInstance().runInEdt(sw, "search " + selectedCard +" in decks");
			
		}
		
	}

}
