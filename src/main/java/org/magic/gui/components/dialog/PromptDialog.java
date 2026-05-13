package org.magic.gui.components.dialog;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;

import org.magic.api.beans.MTGEdition;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;
public class PromptDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextPane editorPane;
	private SpinnerNumberModel qtyModel;
	
	private JPanel qtyPanel;
	
	public String getPrompt() {
		return editorPane.getText();
	}

	public PromptDialog() {
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(new Dimension(400, 235));
		setTitle("Prompt");
		setIconImage(MTGConstants.ICON_TAB_IA.getImage());
		setModal(true);
		setLocationRelativeTo(null);

		var panel = new JPanel();
		qtyPanel= new JPanel();
		editorPane = new JTextPane();
		var btnImport = new JButton(MTGConstants.ICON_SAVE);
		var btnCancel = new JButton(MTGConstants.ICON_CANCEL);

		btnImport.setToolTipText(capitalize("IMPORT"));
		btnCancel.setToolTipText(capitalize("CANCEL"));

		getContentPane().add(UITools.createFlowPanel(qtyPanel,panel), BorderLayout.SOUTH);
		panel.add(btnImport);
		panel.add(btnCancel);
		getContentPane().add(new JScrollPane(editorPane), BorderLayout.CENTER);

		btnCancel.addActionListener(_ -> {
			editorPane.setText("");
			dispose();
		});

		btnImport.addActionListener(_ -> {
			dispose();
		});
	}

	public void showCardBuilderDialog(MTGEdition set) {
		qtyModel = new SpinnerNumberModel(10, 1, 50, 1);
		var spinner = new JSpinner(qtyModel);
		qtyPanel.add(spinner);
		setTitle("Generates cards for set " + set);
		setVisible(true);
	}

	public Integer getCardsCreationSize() {
		return (Integer)qtyModel.getValue();
	}
	
	
}
