package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ManualImportFrame extends JDialog {
	
	
	JEditorPane editorPane;
	
	public String getStringDeck()
	{
		return editorPane.getText();
	}
	
	public ManualImportFrame() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(new Dimension(400, 400));
		setModal(true);
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel.add(btnImport);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editorPane.setText("");
				dispose();
			}
		});
		panel.add(btnCancel);
		
		JLabel lblPastYourDeck = new JLabel("Past your deck here : NB Cards<space>Card Name");
		getContentPane().add(lblPastYourDeck, BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		editorPane = new JEditorPane();
		editorPane.setPreferredSize(new Dimension(106, 300));
		scrollPane.setViewportView(editorPane);
		setLocationRelativeTo(null);
	}

}
