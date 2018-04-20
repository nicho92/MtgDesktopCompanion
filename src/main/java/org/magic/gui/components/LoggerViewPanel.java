package org.magic.gui.components;

import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowFilter;
import javax.swing.Timer;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Level;
import org.jdesktop.swingx.JXTable;
import org.magic.gui.models.LogTableModel;
import org.magic.services.MTGConstants;

public class LoggerViewPanel extends JPanel {
	
	private JXTable table;
	private LogTableModel model;
	private Timer t;
	private JCheckBox chckbxAutorefresh;
	private JButton btnRefresh;
	private JComboBox<Level> cboChooseLevel;

	public LoggerViewPanel() {
		model = new LogTableModel();
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		table = new JXTable(model);
		scrollPane.setViewportView(table);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		btnRefresh = new JButton("");
		btnRefresh.addActionListener(ae -> model.fireTableDataChanged());
		btnRefresh.setIcon(MTGConstants.ICON_REFRESH);
		panel.add(btnRefresh);

		t = new Timer(1000, e -> model.fireTableDataChanged());

		chckbxAutorefresh = new JCheckBox("Auto-refresh");
		chckbxAutorefresh.addItemListener(ie -> {

			if (chckbxAutorefresh.isSelected()) {
				t.start();
				btnRefresh.setEnabled(false);
			} else {
				t.stop();
				btnRefresh.setEnabled(true);
			}
		});
		panel.add(chckbxAutorefresh);
		
		cboChooseLevel = new JComboBox<>(new DefaultComboBoxModel<>(new Level[] {null, Level.INFO, Level.ERROR, Level.DEBUG, Level.TRACE }));
		cboChooseLevel.addActionListener(ae->{
			
			if(cboChooseLevel.getSelectedItem()!=null)
			{
				TableRowSorter<LogTableModel> sorter = new TableRowSorter<>(model);
				sorter.setRowFilter(RowFilter.regexFilter(cboChooseLevel.getSelectedItem().toString()));
				table.setRowSorter(sorter);
			}
			else
			{
				table.setRowSorter(null);
			}
			
			
			
		});
		panel.add(cboChooseLevel);
		table.packAll();
	}

}
