package org.magic.gui.components;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.gui.models.SimilarityCardsTableModel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

import java.awt.BorderLayout;
import javax.swing.JTable;

public class SimilarityCardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private MagicCard currentCard;
	private JTable tableSimilarity;
	private SimilarityCardsTableModel model;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	
	public SimilarityCardPanel() {
		setLayout(new BorderLayout(0, 0));
		
		model = new SimilarityCardsTableModel();
		tableSimilarity = new JTable(model);
		
		add(new JScrollPane(tableSimilarity), BorderLayout.CENTER);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(currentCard);
			}
		});
	}

	public void init(MagicCard mc) {
		currentCard = mc;
		
		if(isVisible()) {
		try {
			MTGControler.getInstance().getEnabledCardIndexer().open();
			model.init(MTGControler.getInstance().getEnabledCardIndexer().similarity(mc));
			MTGControler.getInstance().getEnabledCardIndexer().close();
		} catch (IOException e) {
			logger.error(e);
		}
		
		}
	}

	
}
