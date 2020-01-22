package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXLabel;
import org.magic.api.beans.GedEntry;
import org.magic.services.GedService;
import org.magic.tools.ImageTools;
import java.awt.BorderLayout;
import javax.swing.border.LineBorder;


public class GedEntryComponent extends JPanel {

	private static final long serialVersionUID = 1L;
	private GedEntry entry;
	private boolean selected = false;
	private Color defaultColor;
	private JLabel lblDelete;
	public boolean isSelected() {
		return selected;
	}
	
	public GedEntryComponent(GedEntry e) 
	{
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		setLayout(new BorderLayout(0, 0));
		
		lblDelete = new JLabel("X");
		lblDelete.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblDelete, BorderLayout.NORTH);
		
		
		JXLabel l = new JXLabel();
		l.setHorizontalAlignment(SwingConstants.CENTER);
		this.entry = e;
		l.setText(entry.getName());
		l.setIcon(entry.getIcon());
		l.setLineWrap(true);
		defaultColor = getBackground();
		
		if(entry.isImage())
			l.setIcon(getThumbnail());
		
		l.setVerticalTextPosition(SwingConstants.BOTTOM);
		l.setHorizontalTextPosition(SwingConstants.CENTER);
		
		setToolTipText(e.getName());
		setOpaque(true);
		setPreferredSize(new Dimension(150,75));
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				selected=!selected;
				
				if(selected)
					setBackground(SystemColor.activeCaption);
				else
					setBackground(defaultColor);
			}
		});
		add(l);
		
	}
	
	protected void delete() {
		GedService.inst().delete(entry);
	}

	public ImageIcon getThumbnail()
	{
		   if(entry.isImage()) 
		   {
			   try {
				   return new ImageIcon(ImageTools.read(entry.getContent()).getScaledInstance(50, 50, Image.SCALE_SMOOTH));
			   } catch (IOException e) {
				   return null;
			   }
		   }
		   
		   return null;
	}
	
	public JLabel getRemoveComponent() {
		return lblDelete;
	}
}
