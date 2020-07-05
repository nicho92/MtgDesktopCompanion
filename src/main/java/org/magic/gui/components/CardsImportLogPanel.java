package org.magic.gui.components;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JTable;

import org.magic.api.beans.MTGImportExportException;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

import javax.swing.JButton;
import java.awt.FlowLayout;

public class CardsImportLogPanel extends MTGUIComponent {
	
	private GenericTableModel<MTGImportExportException> model;
	private static final long serialVersionUID = 1L;
	private JTable table;


	
	public CardsImportLogPanel() {
		setLayout(new BorderLayout(0, 0));
		
		model = new GenericTableModel<>("message") {
			private static final long serialVersionUID = 1L;

			public Object getValueAt(int row, int column) {
				return items.get(row).getMessage();
				
				
			};
		};
		
		table = new JTable(model);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(panel, BorderLayout.SOUTH);
		
		JButton btnClear = UITools.createBindableJButton("", MTGConstants.ICON_SMALL_CLEAR, KeyEvent.VK_C, "Clear");
		panel.add(btnClear);
		
		
		btnClear.addActionListener(l->model.clear());
	}
	
	@Override
	public String getTitle() {
		return "I/O Logs";
	}
	
	public void init(List<MTGImportExportException> list)
	{
		model.addItems(list);
	}
	
	
	

}
