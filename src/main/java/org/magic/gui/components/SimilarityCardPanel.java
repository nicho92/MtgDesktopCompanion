package org.magic.gui.components;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.SimilarityCardsTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;

public class SimilarityCardPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private MTGCard currentCard;
	private JXTable tableSimilarity;
	private SimilarityCardsTableModel model;


	public JXTable getTableSimilarity() {
		return tableSimilarity;
	}

	public SimilarityCardPanel() {
		setLayout(new BorderLayout(0, 0));

		model = new SimilarityCardsTableModel();
		tableSimilarity = UITools.createNewTable(model,false);

		add(new JScrollPane(tableSimilarity), BorderLayout.CENTER);


	}

	@Override
	public void onVisible() {
		init(currentCard);
	}


	public void init(MTGCard mc) {
		currentCard = mc;

		if(isVisible()) {
		try {
			model.init(getEnabledPlugin(MTGCardsIndexer.class).similarity(mc));
		} catch (IOException e) {
			logger.error(e);
		}

		}
	}


	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_SIMILARITY;
	}

	@Override
	public String getTitle() {
		return "MORE_LIKE_THIS";
	}


}
