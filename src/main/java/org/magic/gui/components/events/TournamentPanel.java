package org.magic.gui.components.events;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;

import org.jdesktop.swingx.JXDatePicker;
import org.magic.api.beans.MagicEvent;
import org.magic.api.beans.MagicEvent.EVENT_FORMAT;
import org.magic.api.beans.MagicEvent.ROUNDS;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

public class TournamentPanel extends MTGUIComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtEventName;
	private JTextField txtLocation;
	private JXDatePicker datePicker;
	private JComboBox<EVENT_FORMAT> cboEventsFormat;
	private JComboBox<ROUNDS> cboEventRoundsFormat;
	private JTextPane editorPane ;
	private MagicEvent currentEvent;
	private JSpinner spinNbRounds;
	private JSpinner spinBestOf;
	private JSpinner spinRoundTime;
	
	
	public TournamentPanel() {
		
		currentEvent = new MagicEvent();
		
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
		add(new JLabel("Number of rounds:"), UITools.createGridBagConstraints(GridBagConstraints.EAST, null, 0, 6));
		add(new JLabel("Best of: X games :"), UITools.createGridBagConstraints(GridBagConstraints.EAST, null, 0, 7));
		add(new JLabel("Round time limit (in min) :"), UITools.createGridBagConstraints(GridBagConstraints.EAST, null, 0, 8));
		
		txtEventName = new JTextField(10);
		add(txtEventName, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 0));
		
		txtLocation = new JTextField(10);
		add(txtLocation, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 1));
		
		datePicker = new JXDatePicker();
		add(datePicker, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 2));
			
		cboEventsFormat = UITools.createCombobox(MagicEvent.EVENT_FORMAT.values());
		add(cboEventsFormat, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 3));
		
		cboEventRoundsFormat =UITools.createCombobox(MagicEvent.ROUNDS.values());
		add(cboEventRoundsFormat, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 4));
		
		editorPane = new JTextPane();
		add(new JScrollPane(editorPane), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 5));
		
		spinNbRounds = new JSpinner(new SpinnerNumberModel(currentEvent.getRounds().intValue(), 3, 5, 1));
		add(spinNbRounds, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 6));
		
		
		spinBestOf = new JSpinner(new SpinnerNumberModel(currentEvent.getMaxWinRound().intValue(), 3, 5, 1));
		add(spinBestOf, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 7));
		
		
		spinRoundTime = new JSpinner(new SpinnerNumberModel(currentEvent.getRoundTime().intValue(), 10, 90, 15));
		add(spinRoundTime, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 8));
		
	}
	
	public void save()
	{
		if(currentEvent!=null) 
		{
			currentEvent.setTitle(txtEventName.getText());
			currentEvent.setDescription(editorPane.getText());
			currentEvent.setLocalisation(txtLocation.getText());
			currentEvent.setFormat((EVENT_FORMAT) cboEventsFormat.getSelectedItem());
			currentEvent.setRoundFormat((ROUNDS) cboEventRoundsFormat.getSelectedItem());
			currentEvent.setStartDate(datePicker.getDate());
			currentEvent.setRounds((Integer)spinNbRounds.getValue());
			currentEvent.setRoundTime((Integer)spinRoundTime.getValue());
			currentEvent.setMaxWinRound((Integer)spinBestOf.getValue());
		}
		else
		{
			logger.error("no event selected");
		}
	}
	
	
	public MagicEvent getCurrentEvent() {
		return currentEvent;
	}
	
	public void setTournament(MagicEvent event) {
		
		if(event==null)
			return;
		
		
		this.currentEvent = event;
		txtEventName.setText(event.getTitle());
		txtLocation.setText(event.getLocalisation());
		datePicker.setDate(event.getStartDate());
		cboEventRoundsFormat.setSelectedItem(event.getRoundFormat());
		cboEventsFormat.setSelectedItem(event.getFormat());
		editorPane.setText(event.getDescription());
		spinNbRounds.setValue(event.getRounds());
		spinRoundTime.setValue(event.getRoundTime());
		spinBestOf.setValue(event.getMaxWinRound());
	}
	
	
	
	@Override
	public String getTitle() {
		return "Tournament";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_BACK;
	}

	public MagicEvent newEvent() {
		MagicEvent eventNew = new MagicEvent();
		eventNew.setTitle(txtEventName.getText());
		eventNew.setDescription(editorPane.getText());
		eventNew.setLocalisation(txtLocation.getText());
		eventNew.setFormat((EVENT_FORMAT) cboEventsFormat.getSelectedItem());
		eventNew.setRoundFormat((ROUNDS) cboEventRoundsFormat.getSelectedItem());
		eventNew.setStartDate(datePicker.getDate());
		eventNew.setRounds((Integer)spinNbRounds.getValue());
		eventNew.setRoundTime((Integer)spinRoundTime.getValue());
		eventNew.setMaxWinRound((Integer)spinBestOf.getValue());
		return eventNew;
	}

}
