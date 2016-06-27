package org.magic.gui;

import java.awt.BorderLayout;
import java.util.Calendar;

import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class AboutDialog extends JDialog {
	public AboutDialog() {
		setTitle("About");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTextArea txtrWizardsOfThe = new JTextArea();
		txtrWizardsOfThe.setWrapStyleWord(true);
		txtrWizardsOfThe.setEditable(false);
		txtrWizardsOfThe.setLineWrap(true);
		txtrWizardsOfThe.setText("Wizards of the Coast, Magic: The Gathering, and their logos are trademarks of Wizards of the Coast LLC. \u00A9 1995-"+Calendar.getInstance().get(Calendar.YEAR)+" Wizards. All rights reserved. This app is not affiliated with Wizards of the Coast LLC.");
		getContentPane().add(txtrWizardsOfThe, BorderLayout.SOUTH);
		
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setIcon(new ImageIcon(AboutDialog.class.getResource("/res/MTGlogo.jpg")));
		getContentPane().add(lblNewLabel, BorderLayout.CENTER);
		pack();
	}

}
