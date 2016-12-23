package org.magic.gui.components.editor;

import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import java.awt.FlowLayout;

public class MagicCardLanguagesEditorPanel extends JPanel {
	private JTextField textField;
	public MagicCardLanguagesEditorPanel() {
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		JComboBox comboBox = new JComboBox();
		
		for(Locale l : Locale.getAvailableLocales())
		{
			if(!l.getDisplayCountry(Locale.US).equals(""))
				comboBox.addItem(l.getDisplayCountry(Locale.US));
		}
		
		add(comboBox);
		
		textField = new JTextField();
		add(textField);
		textField.setColumns(20);
	}


	public void setMagicCard(org.magic.api.beans.MagicCard newMagicCard) {
		
	}

	
	public static void main(String[] args) {
		JDialog log = new JDialog();
		log.getContentPane().add(new MagicCardLanguagesEditorPanel());
		log.setVisible(true);
	}
}
