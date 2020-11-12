package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.DeckSelectionTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
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
		JPanel panel = new JPanel();
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
			ThreadManager.getInstance().executeThread(()->{
					buzyLabel.start();
					buzyLabel.setText("looking for " + selectedCard);
					model.init(manager.listDecksWith(selectedCard));
					buzyLabel.end();
			}, "search " + selectedCard +" in decks");
			
		}
		
	}

}
