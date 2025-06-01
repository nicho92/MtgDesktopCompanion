package org.magic.gui.abstracts;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.UITools;

public abstract class MTGUIComponent extends JComponent {

	protected static final long serialVersionUID = 1L;
	protected transient Logger logger = MTGLogger.getLogger(getClass());
	protected boolean onlyOneRefresh=true;
	private boolean alreadyShow=false;
	private transient ComponentListener windowsListener;
	private JTabbedPane contextTabbedPane;


	public abstract String getTitle();

	public void setEnclosingScrollPane(JScrollPane scrollPane) {
		JScrollPane  enclosingScrollPane = scrollPane;
		  if (enclosingScrollPane != null) {
		    enclosingScrollPane.addComponentListener(windowsListener);
		  }
		}

	public JTabbedPane getContextTabbedPane() {
		return contextTabbedPane;
	}
	
	protected void addContextComponent(MTGUIComponent component)
	{
		UITools.addTab(contextTabbedPane,component);
	}
	
	protected void addContextComponent(JComponent component,String name, ImageIcon ic)
	{
		UITools.addTab(contextTabbedPane, MTGUIComponent.build(component, name, ic));
	}
	
	protected MTGUIComponent()
	{
		logger.trace("init GUI : {}",getTitle());
		
		contextTabbedPane = new JTabbedPane(SwingConstants.TOP);
		
		windowsListener = new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent evt)
			{
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
		};

		addComponentListener(windowsListener);

		setName(getTitle());

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
		onVisible();
	}

	public void onHide()
	{
		//do not
	}

	public void onResize()
	{
		//do nothing
	}


	public ImageIcon getIcon()
	{
		return MTGConstants.ICON_TAB_PACKAGE;
	}

	@Override
	public String toString() {
		return getName();
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


	public static JFrame createJFrame(MTGUIComponent p, boolean resizable,boolean exitOnClose)
	{
		var f = new JFrame(p.getTitle());
		if(p.getIcon()!=null)
			f.setIconImage(p.getIcon().getImage());
		
		
		f.getContentPane().add(p);
		f.pack();
		f.setResizable(resizable);
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				p.onDestroy();

				if(exitOnClose)
					System.exit(0);
			}
		});
		return f;
	}



	public static JDialog createJDialog(MTGUIComponent c, boolean resizable,boolean modal)
	{
		var j = new JDialog();


		j.getContentPane().setLayout(new BorderLayout());
		j.getContentPane().add(c, BorderLayout.CENTER);
		j.setTitle(c.getTitle());
		j.setLocationRelativeTo(SwingUtilities.getRoot(c));
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
