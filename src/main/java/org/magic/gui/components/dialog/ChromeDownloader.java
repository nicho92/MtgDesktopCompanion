package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class ChromeDownloader extends JFrame {
	
	public static void main(String[] args) {
		ChromeDownloader dow = new ChromeDownloader();
		dow.setVisible(true);
	}
	
	public ChromeDownloader() {
		
		setTitle("Chrome Plugin");
		setIconImage(MTGConstants.ICON_CHROME.getImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		JPanel panelCenter = new JPanel();
		getContentPane().add(panelCenter, BorderLayout.CENTER);
		
		txtDirectory = new JTextField(System.getProperty("user.home"));
		panelCenter.add(txtDirectory);
		txtDirectory.setColumns(30);
		
		JButton btnOpenDirectory = new JButton("...");
		btnOpenDirectory.addActionListener(ev-> {
			JFileChooser choose = new JFileChooser();
						 choose.setCurrentDirectory(new File(txtDirectory.getText()));
						 choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int res= choose.showOpenDialog(null);
				if(res==JFileChooser.APPROVE_OPTION)
				{
					txtDirectory.setText(choose.getSelectedFile().getAbsolutePath());
				}
			
		});
		panelCenter.add(btnOpenDirectory);
		
		JPanel panelButtons = new JPanel();
		getContentPane().add(panelButtons, BorderLayout.SOUTH);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(ae->dispose());
		
		panelButtons.add(btnCancel);
		
		JButton btnExport = new JButton("Export");
		btnExport.addActionListener(e-> {
			try {
				FileUtils.copyDirectoryToDirectory(new File(MTGConstants.MTG_CHROME_PLUGIN.toURI()), new File(txtDirectory.getText()));
				MTGControler.getInstance().notify(new MTGNotification("Export", "Plugin copied in " + txtDirectory.getText(), MESSAGE_TYPE.INFO));
				dispose();
			} catch (Exception e1) {
				MTGControler.getInstance().notify(new MTGNotification("ERROR", e1));
			}
			
		});
		panelButtons.add(btnExport);
		
		pack();
	}

	private static final long serialVersionUID = 1L;
	private JTextField txtDirectory;

}
