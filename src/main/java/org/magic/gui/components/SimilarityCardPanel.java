package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.gui.models.SimilarityCardsTableModel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class SimilarityCardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private MagicCard currentCard;
	private JXTable tableSimilarity;
	private SimilarityCardsTableModel model;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	
	public JXTable getTableSimilarity() {
		return tableSimilarity;
	}
	
	public SimilarityCardPanel() {
		setLayout(new BorderLayout(0, 0));
		
		model = new SimilarityCardsTableModel();
		tableSimilarity = new JXTable(model);
		
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
			model.init(MTGControler.getInstance().getEnabled(MTGCardsIndexer.class).similarity(mc));
		} catch (IOException e) {
			logger.error(e);
		}
		
		}
	}

	
}
