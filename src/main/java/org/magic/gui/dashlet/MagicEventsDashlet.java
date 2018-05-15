package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.JBuzyLabel;
import org.magic.gui.models.MagicEventsTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.ThreadManager;
import org.magic.services.extra.MTGEventProvider;

public class MagicEventsDashlet extends AbstractJDashlet {
	private JXTable table;
	private MagicEventsTableModel eventsModel;
	private JComboBox<Integer> cboYear;
	private JBuzyLabel lblLoading;
	private JComboBox<Integer> cboMonth;
	private transient MTGEventProvider provider;
	private Calendar c;

	public MagicEventsDashlet() {
		super();
		setFrameIcon(MTGConstants.ICON_GAME);

	}

	public void initGUI() {

		provider = new MTGEventProvider();

		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		cboYear = new JComboBox<>();
		cboYear.addItemListener(ie -> init());
		panneauHaut.add(cboYear);

		lblLoading = new JBuzyLabel();

		cboMonth = new JComboBox<>();
		panneauHaut.add(cboMonth);
		panneauHaut.add(lblLoading);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		eventsModel = new MagicEventsTableModel();
		table = new JXTable();

		scrollPane.setViewportView(table);

		c = GregorianCalendar.getInstance();
		c.setTime(new Date());

		for (int i = c.get(Calendar.YEAR) - 1; i <= c.get(Calendar.YEAR) + 1; i++)
			cboYear.addItem(i);

		for (int i = 1; i < 13; i++)
			cboMonth.addItem(i);

		cboYear.setSelectedItem(c.get(Calendar.YEAR));
		cboMonth.setSelectedItem(c.get(Calendar.MONTH) + 1);

		cboYear.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
				init();
			}
		});

		cboMonth.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
				init();
			}
		});

		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			setBounds(r);
		}

		setVisible(true);

	}

	public void init() {
		ThreadManager.getInstance().execute(() -> {
			lblLoading.buzy(true);
			int y = c.get(Calendar.YEAR);
			int m = c.get(Calendar.MONTH) + 1;
			try {
				y = Integer.parseInt(cboYear.getSelectedItem().toString());
				m = Integer.parseInt(cboMonth.getSelectedItem().toString());
			} catch (Exception e) {
				logger.error(e);
			}
			try {
				eventsModel.init(provider.listEvents(y, m));
			} catch (IOException e1) {
				logger.error(e1);
			}

			try {
				table.setModel(eventsModel);
			} catch (Exception e) {
				logger.error(e);
			}
			lblLoading.buzy(false);
			eventsModel.fireTableDataChanged();
			table.packAll();

		}, "Init Events Dashlet");
	}

	@Override
	public String getName() {
		return "Magic Events";
	}

}
