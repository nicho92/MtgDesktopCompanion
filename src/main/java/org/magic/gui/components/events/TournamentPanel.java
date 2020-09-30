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
		
		add(new JLabel("Event Title :"), UITools.createGridBagConstraints(GridBagConstraints.EAST, null, 0, 0));
		add(new JLabel("Event Location :"), UITools.createGridBagConstraints(GridBagConstraints.EAST, null, 0, 1));
		add(new JLabel("Event Date :"), UITools.createGridBagConstraints(GridBagConstraints.EAST, null, 0, 2));
		add(new JLabel("Event Format :"), UITools.createGridBagConstraints(GridBagConstraints.EAST, null, 0, 3));
		add(new JLabel("Event Structure :"), UITools.createGridBagConstraints(GridBagConstraints.EAST, null, 0, 4));
		add(new JLabel("Event Description :"), UITools.createGridBagConstraints(GridBagConstraints.EAST, null, 0, 5));
			
		
		txtEventName = new JTextField(10);
		add(txtEventName, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 0));
		
		
		txtLocation = new JTextField(10);
		add(txtLocation, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 1));
		
		JXDatePicker datePicker = new JXDatePicker();
		add(datePicker, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 2));
		
		
		JComboBox<EVENT_FORMAT> cboEventsFormat = UITools.createCombobox(MagicEvent.EVENT_FORMAT.values());
		add(cboEventsFormat, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 3));
		
		
		JComboBox<ROUNDS> cboEventRoundsFormat =UITools.createCombobox(MagicEvent.ROUNDS.values());
		add(cboEventRoundsFormat, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 4));
		
		JEditorPane editorPane = new JEditorPane();
		add(new JScrollPane(editorPane), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 5));
		
		
		JButton btnSave = new JButton(MTGConstants.ICON_SAVE);
		add(btnSave, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 7,2,null));
		
	}

}
