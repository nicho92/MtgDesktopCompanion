package org.magic.gui.dashlet;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.models.MapTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.UITools;

public class IndexationDashlet extends AbstractJDashlet {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private MapTableModel<String, Long> indexModel;

	private JComboBox<String> cboField;

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_TAB_ANALYSE;
	}

	@Override
	public String getCategory() {
		return "Tools";
	}


	@Override
	public void initGUI() {

		var panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		try {
		cboField = UITools.createCombobox(getEnabledPlugin(MTGCardsIndexer.class).listFields());
		}
		catch(Exception e)
		{
			cboField.addItem("NO INDEXER FILE FOUND");
		}

		panneauHaut.add(cboField);
		indexModel = new MapTableModel<>();
		indexModel.setColumnNameAt(0, "Term");
		indexModel.setColumnNameAt(1, "Occurences");
		var table = UITools.createNewTable(indexModel,false);

		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		cboField.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
				init();
			}
		});

		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			setBounds(r);
		}

	}

	@Override
	public void init() {
		try {
			indexModel.init(getEnabledPlugin(MTGCardsIndexer.class).terms(cboField.getSelectedItem().toString()));
			indexModel.fireTableDataChanged();
		}catch(Exception e)
		{
			MTGControler.getInstance().notify(new NullPointerException("Indexation is not initied"));
		}
	}

	@Override
	public String getName() {
		return "Magic Indexation Stats";
	}

}
