package org.magic.gui.components.editor;

import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.magic.api.beans.MTGEdition;
import org.magic.services.tools.UITools;

public class BoosterQtyPanel extends JComponent {

	private static final long serialVersionUID = 1L;
	private JComboBox<MTGEdition> cboEditions;
	private JSpinner spinner;

	public BoosterQtyPanel() {
		initGUI();
	}

	public void setValue(MTGEdition ed, Integer qty) {
		spinner.setValue(qty);
		cboEditions.setSelectedItem(ed);
	}

	public BoosterQtyPanel(MTGEdition ed, Integer qty) {
		initGUI();
		spinner.setValue(qty);
		cboEditions.setSelectedItem(ed);
	}

	private void initGUI() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		cboEditions = UITools.createComboboxEditions();
		add(cboEditions);

		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(6, 0, null, 1));
		add(spinner);

	}

	public MTGEdition getEdition() {
		return (MTGEdition) cboEditions.getSelectedItem();
	}

	public int getQty() {
		return (Integer) spinner.getValue();
	}

}
