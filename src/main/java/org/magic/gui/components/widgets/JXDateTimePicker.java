package org.magic.gui.components.widgets;

import java.awt.Color;
import java.awt.FlowLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.magic.services.tools.MTG;

public class JXDateTimePicker extends JXDatePicker {

	private static final long serialVersionUID = 1L;
	private JSpinner timeSpinner;
	private JPanel timePanel;
	private DateFormat timeFormat;

	public JXDateTimePicker() {
		super();
		getMonthView().setSelectionModel(new SingleDaySelectionModel());
		setFormats(MTG.lang("DATE_FORMAT") + " HH:mm");
	}

	@Override
	public void commitEdit() throws ParseException {
		commitTime();
		super.commitEdit();
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setTimeSpinners();
	}

	@Override
	public JPanel getLinkPanel() {
		super.getLinkPanel();
		if (timePanel == null) {
			timePanel = createTimePanel();
		}
		setTimeSpinners();
		return timePanel;
	}

	private JPanel createTimePanel() {
		final JPanel newPanel = new JPanel();
		newPanel.setLayout(new FlowLayout());

		final SpinnerDateModel dateModel = new SpinnerDateModel();
		timeSpinner = new JSpinner(dateModel);
		if (timeFormat == null) {
		    timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		}
		updateTextFieldFormat();
		newPanel.add(new JLabel("Time:"));
		newPanel.add(timeSpinner);
		newPanel.setBackground(Color.WHITE);
		return newPanel;
	}

	private void updateTextFieldFormat() {
		if (timeSpinner == null) {
		    return;
		}
		final JFormattedTextField tf = ((JSpinner.DefaultEditor) timeSpinner.getEditor()).getTextField();
		final DefaultFormatterFactory factory = (DefaultFormatterFactory) tf.getFormatterFactory();
		final DateFormatter formatter = (DateFormatter) factory.getDefaultFormatter();
		// Change the date format to only show the hours
		formatter.setFormat(timeFormat);
	}

	private void commitTime() {
		final Date date = getDate();
		if (date != null) {
			final Date time = (Date) timeSpinner.getValue();
			final GregorianCalendar timeCalendar = new GregorianCalendar();
			timeCalendar.setTime(time);

			final GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			final Date newDate = calendar.getTime();
			setDate(newDate);
		}

	}

	private void setTimeSpinners() {
		final Date date = getDate();
		if (date != null) {
			timeSpinner.setValue(date);
		}
	}

	public DateFormat getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(DateFormat timeFormat) {
		this.timeFormat = timeFormat;
		updateTextFieldFormat();
	}

}