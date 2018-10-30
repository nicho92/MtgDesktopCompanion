package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class AboutDialog extends JComponent {

	private static final long serialVersionUID = 1L;

	public AboutDialog() {
		
		setLayout(new BorderLayout(0, 0));

		JTextArea txtrWizardsOfThe = new JTextArea();
		txtrWizardsOfThe.setBackground(Color.BLACK);
		txtrWizardsOfThe.setForeground(Color.LIGHT_GRAY);
		txtrWizardsOfThe.setWrapStyleWord(true);
		txtrWizardsOfThe.setEditable(false);
		txtrWizardsOfThe.setLineWrap(true);
		txtrWizardsOfThe.setText(
				"Wizards of the Coast, Magic: The Gathering, and their logos are trademarks of Wizards of the Coast LLC. \u00A9 1995-"
						+ Calendar.getInstance().get(Calendar.YEAR)
						+ " Wizards. All rights reserved. This app is not affiliated with Wizards of the Coast LLC.");
		add(txtrWizardsOfThe, BorderLayout.SOUTH);

		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setIcon(MTGConstants.ICON_ABOUT);
		add(lblNewLabel, BorderLayout.CENTER);

		JLabel lblDeveloppedByNichow = new JLabel(MTGControler.getInstance().getLangService().getCapitalize(
				"DEVELOPPERS_ABOUT", "Nichow", "GPL " + new SimpleDateFormat("yyyy").format(new Date())));
		lblDeveloppedByNichow.setOpaque(true);
		lblDeveloppedByNichow.setBackground(Color.BLACK);
		lblDeveloppedByNichow.setForeground(Color.WHITE);
		lblDeveloppedByNichow.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblDeveloppedByNichow, BorderLayout.NORTH);
		
	}

}
