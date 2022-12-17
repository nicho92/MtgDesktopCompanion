package org.magic.gui.components.editor;

import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.magic.api.beans.MagicEdition;
import org.magic.services.tools.UITools;

public class BoosterQtyPanel extends JComponent {

	private static final long serialVersionUID = 1L;
	private JComboBox<MagicEdition> cboEditions;
	private JSpinner spinner;

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
		cboEditions = UITools.createComboboxEditions();
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
