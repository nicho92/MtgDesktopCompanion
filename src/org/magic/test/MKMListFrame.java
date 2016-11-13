package org.magic.test;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MKMListFrame extends JFrame {
	private JTable table;
	private JComboBox<WantList> comboBox;
	private WantsTableModel model;
	
	MkmProductProvider list ;
	
	public MKMListFrame() throws Exception {
		
		list = new MkmProductProvider();
		
		
		model=new WantsTableModel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		comboBox = new JComboBox<WantList>();
		for(WantList l : list.getWantList())
			comboBox.addItem(l);
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WantList l = (WantList)comboBox.getSelectedItem();
				try {
					model.init(list.getWants(l));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		panel.add(comboBox);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
		
				
			}
		});
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable(model);
		
		scrollPane.setViewportView(table);
	}

	
	
	
	public JComboBox getComboBox() {
		return comboBox;
	}
}
