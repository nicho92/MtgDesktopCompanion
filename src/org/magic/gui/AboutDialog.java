package org.magic.gui;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class AboutDialog extends JDialog {
	public AboutDialog() {
		setTitle("About");
		setResizable(false);
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
		
		JLabel lblDeveloppedByNichow = new JLabel("Developped by Nichow - GPL " + new SimpleDateFormat("yyyy").format(new Date()));
		lblDeveloppedByNichow.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblDeveloppedByNichow, BorderLayout.NORTH);
		pack();
		setLocationRelativeTo(null);
	}

}
