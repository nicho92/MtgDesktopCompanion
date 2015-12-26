package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLEditorKit;

import org.magic.api.beans.MagicCollection;

public class WebSiteGeneratorDialog extends JDialog {
	private JTextField txtDest;
	
	private boolean value=false;
	JComboBox cboTemplates;
	JList<MagicCollection> list ;
	
	public File getDest() {
		return new File(txtDest.getText());
	}

	public String getTemplate() {
		return cboTemplates.getSelectedItem().toString();
	}
	

	public WebSiteGeneratorDialog(List<MagicCollection> cols) {
		setSize(new Dimension(571, 329));
		setModal(true);
		setTitle("WebSite Properties");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		File f = new File(WebSiteGeneratorDialog.class.getResource("/templates").getPath());
		
		List<String> arrayTemplates=new ArrayList<String>();
		
		for (File temp : f.listFiles())
			arrayTemplates.add(temp.getName());
		
		cboTemplates = new JComboBox(arrayTemplates.toArray());
	
		panel.add(cboTemplates);
		
		txtDest = new JTextField(new File("\\\\SYNOLOGY\\tomcat\\magic").getAbsolutePath());
		
		panel.add(txtDest);
		txtDest.setColumns(20);
		
		JButton btnDestChoose = new JButton("...");
		
		panel.add(btnDestChoose);
		
		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);
		
		JButton btnGenerate = new JButton("Generate");
		
		panneauBas.add(btnGenerate);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		list = new JList<MagicCollection>(cols.toArray(new MagicCollection[cols.size()]));
		scrollPane.setViewportView(list);
		
		btnDestChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser choose = new JFileChooser(txtDest.getText());
				choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				choose.showSaveDialog(null);
				File dest = choose.getSelectedFile();
				
				if(dest==null)
					dest=new File(".");

				txtDest.setText(dest.getAbsolutePath());
			}
		});
		
		btnGenerate.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent arg0) {
				value=true;
				setVisible(false);
				
			}
		});
	}


	public List<MagicCollection> getSelectedCollections() {
		return list.getSelectedValuesList();
	}

	public boolean value() {
		return value;
	}

}
