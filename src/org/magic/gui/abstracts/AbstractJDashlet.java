package org.magic.gui.abstracts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Properties;

import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.services.MTGControler;

public abstract class AbstractJDashlet extends JInternalFrame {

	public static final File confdir = new File(MTGControler.CONF_DIR, "dashboards/dashlets");
	protected Properties props;
	protected static final Logger logger = LogManager.getLogger(AbstractJDashlet.class.getName());

	public AbstractJDashlet() {
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		
		setSize(new Dimension(536, 346));
		
		
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosed(InternalFrameEvent e) {
				AbstractJDashlet dash = (AbstractJDashlet)e.getInternalFrame();
				if(dash.getProperties().get("id")!=null)
					FileUtils.deleteQuietly(new File(confdir, dash.getProperties().get("id")+".conf"));
			}
		});
		
	}
	
	public void setProperties(Properties p)
	{
		this.props=p;
	}
	
	public Properties getProperties()
	{
		return props;
	}
	

	
	protected void initToolTip(final JTable table)
	{
		final MagicCardDetailPanel pane = new MagicCardDetailPanel();
				pane.enableThumbnail(true);
				//pane.setPreferredSize(new Dimension(880, 350));
				
		final JPopupMenu popUp = new JPopupMenu("Customized Tool Tip");

		table.addMouseListener(new MouseAdapter() {
		    
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				
				if(row>-1) {
					table.setRowSelectionInterval(row, row);
					String cardName = table.getValueAt(row, 0).toString();
					
					String edID = table.getValueAt(row, 1).toString();
					
					MagicEdition ed = new MagicEdition();
					ed.setId(edID);
					try 
					{
						MagicCard mc =  MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName,ed).get(0);
						pane.setMagicCard(mc);
						pane.setMagicLogo(edID, mc.getEditions().get(0).getRarity());
							popUp.setBorder(new LineBorder(Color.black));
						    popUp.setVisible(false);
						    popUp.removeAll();
						    popUp.setLayout(new BorderLayout());
						    popUp.add(pane,BorderLayout.CENTER);
						    popUp.show(table, e.getX(), e.getY());// + bounds.height);
						    popUp.setVisible(true);
					}
					catch (Exception ex) 
					{
						logger.error(cardName +" " + edID,ex);
					}
					
				}
		   }
		});
	}
	
	public abstract String getName();
	
	public void save(String k, Object value) {
		props.put(k, value);
		
	}
	
	public abstract void initGUI();
	
	public abstract void init();
	
	@Override
	public String toString() {
		return getName();
	}
}
