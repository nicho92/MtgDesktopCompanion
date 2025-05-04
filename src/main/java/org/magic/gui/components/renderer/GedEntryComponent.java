package org.magic.gui.components.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXLabel;
import org.magic.api.beans.technical.GedEntry;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.ImageTools;

@SuppressWarnings("rawtypes")
public class GedEntryComponent extends JPanel {

	private static final long serialVersionUID = 1L;
	private GedEntry entry;
	private boolean selected = false;
	private Color defaultColor;
	private JLabel lblDelete;
	private int w=150;
	private int h=100;
	protected transient Logger logger = MTGLogger.getLogger(GedEntryComponent.class);


	public boolean isSelected() {
		return selected;
	}

	public GedEntry getEntry() {
		return entry;
	}

	public GedEntryComponent(GedEntry e, int w,int h)
	{
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		setLayout(new BorderLayout(0, 0));
		this.w=w;
		this.h=h;


		lblDelete = new JLabel(MTGConstants.ICON_SMALL_DELETE);
		lblDelete.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblDelete, BorderLayout.NORTH);


		var l = new JXLabel();
		l.setHorizontalAlignment(SwingConstants.CENTER);
		this.entry = e;
		l.setText(entry.getName());

		try {
			l.setIcon(new ImageIcon(entry.getContent()));
		}catch(Exception ex)
		{
			logger.error(ex);
		}


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



	public ImageIcon getThumbnail()
	{
			   var buff = getPicture();

			   if(buff!=null)
				   return new ImageIcon(buff.getScaledInstance(w, h, Image.SCALE_SMOOTH));

			   return null;
	}

	public BufferedImage getPicture()
	{
		   if(entry.isImage())
		   {
			   try {
				   return ImageTools.read(entry.getContent());
			   } catch (IOException _) {
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

				if(me.getClickCount()==2)
				{
					try {
						callable.call();
					} catch (Exception e) {
						logger.error(e);
					}

				}
				else
				{
					selected=!selected;

					if(selected)
						setBackground(SystemColor.activeCaption);
					else
						setBackground(defaultColor);
				}





			}
		});
	}


	public JLabel getRemoveComponent() {
		return lblDelete;
	}
}
