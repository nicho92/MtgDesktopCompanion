package org.magic.gui.components;

import java.awt.SystemColor;
import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.BorderLayout;
import javax.swing.JEditorPane;

public class RssNewsPanel extends JDesktopPane {
	public RssNewsPanel() {
		setBackground(SystemColor.inactiveCaption);
		
		  JInternalFrame internalFrame = new JInternalFrame("MTG GoldFish News", true, true, true, true);
		  internalFrame.setLocation(81, 69);
		  	internalFrame.setSize(278, 225);
		  	internalFrame.setVisible(true);
		  add(internalFrame);
		  
		  JScrollPane scrollPane = new JScrollPane();
		  internalFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		  
		  JEditorPane editorPane = new JEditorPane();
		  HTMLEditorKit kit = new HTMLEditorKit();
			editorPane.setEditorKit(kit);
		  editorPane.setContentType("text/html");
		  scrollPane.setViewportView(editorPane);
		  
		  try {
			editorPane.setPage("http://www.mtggoldfish.com/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
