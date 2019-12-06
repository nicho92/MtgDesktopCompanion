package org.magic.gui.abstracts;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;

import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

public abstract class MTGUIComponent extends JComponent {

	protected static final long serialVersionUID = 1L;
	protected static transient Logger logger = MTGLogger.getLogger(MTGUIComponent.class);
	protected boolean onlyOneRefresh=true;
	public abstract String getTitle();
	private boolean alreadyShow=false;
	
	@Override
	public String toString() {
		return getName();
	}
	
	public MTGUIComponent()
	{
		logger.debug("init GUI : " + getTitle());
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent evt) {
				if(!alreadyShow)
				{
					onFirstShowing();
					alreadyShow=true;
				}
				else
				{
					onVisible();
				}
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				onHide();
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				onResize();
			}
		});
		
	}
	
	public void onVisible()
	{
		//do nothing
	}
	
	public void onDestroy()
	{
		//do nothing
	}

	public void onFirstShowing()
	{
		//do nothing
	}
	
	public void onHide()
	{
		//do nothing
	}
	
	public void onResize()
	{
		//do nothing
	}
	
	
	public ImageIcon getIcon()
	{
		return MTGConstants.ICON_PACKAGE_SMALL;
	}
	
	
	public static MTGUIComponent build(JComponent c,String name,ImageIcon ic)
	{
		MTGUIComponent pane = new MTGUIComponent() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getTitle() {
				return name;
			}
			
			@Override
			public ImageIcon getIcon() {
				return ic;
			}
		};
		
		pane.setLayout(new BorderLayout());
		pane.add(c,BorderLayout.CENTER);
		
		return pane;
	}
	
	
	
	public static JDialog createJDialog(MTGUIComponent c, boolean resizable,boolean modal)
	{
		JDialog j = new JDialog();
		
		
		j.getContentPane().setLayout(new BorderLayout());
		j.getContentPane().add(c, BorderLayout.CENTER);
		j.setTitle(c.getTitle());
		j.setLocationRelativeTo(null);
		if(c.getIcon()!=null)
			j.setIconImage(c.getIcon().getImage());
		
		j.pack();
		j.setModal(modal);
		j.setResizable(resizable);
		j.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				c.onDestroy();
			}
		});
		
		return j;
	}
	
	
	
	
	
}
