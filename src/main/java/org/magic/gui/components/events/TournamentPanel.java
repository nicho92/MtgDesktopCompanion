package org.magic.gui.components.events;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXDatePicker;
import org.magic.api.beans.MagicEvent;
import org.magic.api.beans.MagicEvent.EVENT_FORMAT;
import org.magic.api.beans.MagicEvent.ROUNDS;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

public class TournamentPanel extends JPanel{
	private JTextField txtEventName;
	private JTextField txtLocation;
	
	
	public TournamentPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{112, 0, 0};
		gridBagLayout.rowHeights = new int[]{37, 37, 36, 37, 37, 36, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		GridBagConstraints gbclblNewLabel = new GridBagConstraints();
		gbclblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbclblNewLabel.anchor = GridBagConstraints.EAST;
		gbclblNewLabel.gridx = 0;
		gbclblNewLabel.gridy = 0;
		add(new JLabel("Event Title :"), gbclblNewLabel);
		
		txtEventName = new JTextField();
		GridBagConstraints gbctxtEventName = new GridBagConstraints();
		gbctxtEventName.insets = new Insets(0, 0, 5, 0);
		gbctxtEventName.fill = GridBagConstraints.BOTH;
		gbctxtEventName.gridx = 1;
		gbctxtEventName.gridy = 0;
		add(txtEventName, gbctxtEventName);
		txtEventName.setColumns(10);
		
		GridBagConstraints gbclblNewLabel1 = new GridBagConstraints();
		gbclblNewLabel1.insets = new Insets(0, 0, 5, 5);
		gbclblNewLabel1.anchor = GridBagConstraints.EAST;
		gbclblNewLabel1.gridx = 0;
		gbclblNewLabel1.gridy = 1;
		add(new JLabel("Event Location :"), gbclblNewLabel1);
		
		txtLocation = new JTextField(10);
		GridBagConstraints gbctxtLocation = new GridBagConstraints();
		gbctxtLocation.insets = new Insets(0, 0, 5, 0);
		gbctxtLocation.fill = GridBagConstraints.BOTH;
		gbctxtLocation.gridx = 1;
		gbctxtLocation.gridy = 1;
		add(txtLocation, gbctxtLocation);
		
		GridBagConstraints gbclblNewLabel2 = new GridBagConstraints();
		gbclblNewLabel2.anchor = GridBagConstraints.EAST;
		gbclblNewLabel2.insets = new Insets(0, 0, 5, 5);
		gbclblNewLabel2.gridx = 0;
		gbclblNewLabel2.gridy = 2;
		add(new JLabel("Event Date :"), gbclblNewLabel2);
		
		JXDatePicker datePicker = new JXDatePicker();
		GridBagConstraints gbcdatePicker = new GridBagConstraints();
		gbcdatePicker.insets = new Insets(0, 0, 5, 0);
		gbcdatePicker.fill = GridBagConstraints.BOTH;
		gbcdatePicker.gridx = 1;
		gbcdatePicker.gridy = 2;
		add(datePicker, gbcdatePicker);
		
		GridBagConstraints gbclblNewLabel3 = new GridBagConstraints();
		gbclblNewLabel3.anchor = GridBagConstraints.EAST;
		gbclblNewLabel3.insets = new Insets(0, 0, 5, 5);
		gbclblNewLabel3.gridx = 0;
		gbclblNewLabel3.gridy = 3;
		add(new JLabel("Event Format : "), gbclblNewLabel3);
		
		JComboBox<EVENT_FORMAT> cboEventsFormat = UITools.createCombobox(MagicEvent.EVENT_FORMAT.values());
		GridBagConstraints gbccomboBox = new GridBagConstraints();
		gbccomboBox.insets = new Insets(0, 0, 5, 0);
		gbccomboBox.fill = GridBagConstraints.BOTH;
		gbccomboBox.gridx = 1;
		gbccomboBox.gridy = 3;
		add(cboEventsFormat, gbccomboBox);
		
		GridBagConstraints gbclblNewLabel4 = new GridBagConstraints();
		gbclblNewLabel4.anchor = GridBagConstraints.EAST;
		gbclblNewLabel4.insets = new Insets(0, 0, 5, 5);
		gbclblNewLabel4.gridx = 0;
		gbclblNewLabel4.gridy = 4;
		add(new JLabel("Event Structure :"), gbclblNewLabel4);
		
		JComboBox<ROUNDS> cboEventRoundsFormat =UITools.createCombobox(MagicEvent.ROUNDS.values());
		GridBagConstraints gbccomboBox1 = new GridBagConstraints();
		gbccomboBox1.insets = new Insets(0, 0, 5, 0);
		gbccomboBox1.fill = GridBagConstraints.BOTH;
		gbccomboBox1.gridx = 1;
		gbccomboBox1.gridy = 4;
		add(cboEventRoundsFormat, gbccomboBox1);
		
		GridBagConstraints gbclblNewLabel5 = new GridBagConstraints();
		gbclblNewLabel5.anchor = GridBagConstraints.EAST;
		gbclblNewLabel5.insets = new Insets(0, 0, 5, 5);
		gbclblNewLabel5.gridx = 0;
		gbclblNewLabel5.gridy = 5;
		add(new JLabel("Event Description :"), gbclblNewLabel5);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbcscrollPane = new GridBagConstraints();
		gbcscrollPane.gridheight = 2;
		gbcscrollPane.insets = new Insets(0, 0, 5, 0);
		gbcscrollPane.fill = GridBagConstraints.BOTH;
		gbcscrollPane.gridx = 1;
		gbcscrollPane.gridy = 5;
		add(scrollPane, gbcscrollPane);
		
		JEditorPane editorPane = new JEditorPane();
		scrollPane.setViewportView(editorPane);
		
		JButton btnSave = new JButton(MTGConstants.ICON_SAVE);
		GridBagConstraints gbcbtnSave = new GridBagConstraints();
		gbcbtnSave.gridwidth = 2;
		gbcbtnSave.gridx = 0;
		gbcbtnSave.gridy = 7;
		add(btnSave, gbcbtnSave);
	}

}
