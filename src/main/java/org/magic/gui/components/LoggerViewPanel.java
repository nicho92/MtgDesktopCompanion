package org.magic.gui.components;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.Timer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Level;
import org.jdesktop.swingx.JXTable;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.conf.LogTableModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;
public class LoggerViewPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private LogTableModel model;
	private Timer t;
	private JCheckBox chckbxAutorefresh;
	private JButton btnRefresh;
	private JComboBox<Level> cboChooseLevel;
	private transient TableRowSorter<TableModel> datesorter;
	
	
	public LoggerViewPanel() {
		model = new LogTableModel();
		cboChooseLevel = UITools.createCombobox(new Level[] {null, Level.INFO, Level.ERROR, Level.DEBUG, Level.TRACE });
		var panel = new JPanel();
		table = UITools.createNewTable(model);
		
		
		table.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));
		
		btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		t = new Timer(1000, e -> model.fireTableDataChanged());
		chckbxAutorefresh = new JCheckBox("Auto-refresh");
		
		setLayout(new BorderLayout(0, 0));


		add(new JScrollPane(table), BorderLayout.CENTER);
		add(panel, BorderLayout.NORTH);
		panel.add(chckbxAutorefresh);
		panel.add(btnRefresh);
		panel.add(cboChooseLevel);
		
		datesorter = new TableRowSorter<>(table.getModel());
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		var columnIndexToSort = 1;
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		datesorter.setSortKeys(sortKeys);
		table.setRowSorter(datesorter);
		
		model.setDefaultHiddenComlumns(2,3,4);
		
		
		btnRefresh.addActionListener(ae -> model.fireTableDataChanged());
		
		chckbxAutorefresh.addItemListener(ie -> {

			if (chckbxAutorefresh.isSelected()) {
				t.start();
				btnRefresh.setEnabled(false);
			} else {
				t.stop();
				btnRefresh.setEnabled(true);
			}
		});
		
		cboChooseLevel.addActionListener(ae->{
			
			if(cboChooseLevel.getSelectedItem()!=null)
			{
				TableRowSorter<LogTableModel> sorter = new TableRowSorter<>(model);
				sorter.setRowFilter(RowFilter.regexFilter(cboChooseLevel.getSelectedItem().toString()));
				table.setRowSorter(sorter);
			}
			else
			{
				table.setRowSorter(datesorter);
				
			}
		});
		
		table.packAll();
	}
	
	public void enabledAutoLoad()
	{
		chckbxAutorefresh.doClick();
	}
	
	public void setLevel(Level l)
	{
		cboChooseLevel.setSelectedItem(l);
	}
	
	
	public void setClassFilter(Class c)
	{
		logger.debug("Filtering logs for " + c.getName() );
		TableRowSorter<LogTableModel> sorter = new TableRowSorter<>(model);
		sorter.setRowFilter(RowFilter.regexFilter(c.getName()));
		table.setRowSorter(sorter);
	}
	
	@Override
	public void onDestroy() {
		t.stop();
	}


	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_CONFIG;
	}


	@Override
	public String getTitle() {
		return capitalize("LOGS");
	}

}
