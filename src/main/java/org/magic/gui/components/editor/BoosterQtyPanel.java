package org.magic.gui.components.editor;

import java.awt.FlowLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.renderer.MagicEditionIconListRenderer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class BoosterQtyPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JComboBox<MagicEdition> cboEditions;
	private JSpinner spinner;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public BoosterQtyPanel() {
		initGUI();
	}

	public void setValue(MagicEdition ed, Integer qty) {
		spinner.setValue(qty);
		cboEditions.setSelectedItem(ed);
	}

	public BoosterQtyPanel(MagicEdition ed, Integer qty) {
		initGUI();
		spinner.setValue(qty);
		cboEditions.setSelectedItem(ed);
	}

	private void initGUI() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		List<MagicEdition> li = new ArrayList<>();
		try {
			li = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).loadEditions();
		} catch (IOException e1) {
			logger.error(e1);
		}
		cboEditions = new JComboBox<>(new DefaultComboBoxModel<MagicEdition>(li.toArray(new MagicEdition[li.size()])));
		cboEditions.setRenderer(new MagicEditionIconListRenderer());
		add(cboEditions);

		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(6, 0, null, 1));
		add(spinner);

	}

	public MagicEdition getEdition() {
		return (MagicEdition) cboEditions.getSelectedItem();
	}

	public int getQty() {
		return (Integer) spinner.getValue();
	}

}
