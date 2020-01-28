package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXLabel;
import org.magic.api.beans.GedEntry;
import org.magic.services.GedService;
import org.magic.tools.ImageTools;
import java.awt.BorderLayout;
import javax.swing.border.LineBorder;

@SuppressWarnings("rawtypes") 
public class GedEntryComponent extends JPanel {

	private static final long serialVersionUID = 1L;
	private GedEntry entry;
	private boolean selected = false;
	private Color defaultColor;
	private JLabel lblDelete;
	private int w=150;
	private int h=100;
	
	public boolean isSelected() {
		return selected;
	}
	
	public GedEntryComponent(GedEntry e, int w,int h) 
	{
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		setLayout(new BorderLayout(0, 0));
		this.w=w;
		this.h=h;
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
		setPreferredSize(new Dimension(w,h));
		
		add(l);
		
	}
	
	protected void delete() {
		GedService.inst().delete(entry);
	}

	public ImageIcon getThumbnail()
	{
		   if(entry.isImage()) 
		   {
			   BufferedImage buff = getPicture();
			   
			   if(buff!=null)
				   return new ImageIcon(getPicture().getScaledInstance(w, h, Image.SCALE_SMOOTH));
		   }
		   
		   return null;
	}
	
	public BufferedImage getPicture()
	{
		   if(entry.isImage()) 
		   {
			   try {
				   return ImageTools.read(entry.getContent());
			   } catch (IOException e) {
				   return null;
			   }
		   }
		   
		   return null;
	}

	
	
	public void setCallable(Callable<Void> callable)
	{
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				selected=!selected;
				
				if(selected)
					setBackground(SystemColor.activeCaption);
				else
					setBackground(defaultColor);
				
				try {
					callable.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	
	public JLabel getRemoveComponent() {
		return lblDelete;
	}
}
