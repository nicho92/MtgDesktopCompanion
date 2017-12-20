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
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public abstract class AbstractJDashlet extends JInternalFrame {

	public static final File confdir = new File(MTGControler.CONF_DIR, "dashboards/dashlets");
	protected Properties props;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private MagicCardDetailPanel pane;
	
	public AbstractJDashlet() {
		props=new Properties();
		
		
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosed(InternalFrameEvent e) {
				AbstractJDashlet dash = (AbstractJDashlet)e.getInternalFrame();
				if(dash.getProperties().get("id")!=null)
					FileUtils.deleteQuietly(new File(confdir, dash.getProperties().get("id")+".conf"));
			}
		});
		
		
		setTitle(getName());
		setResizable(true);
		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);
		setSize(new Dimension(536, 346));
		
	}
	
	public void setProperties(Properties p)
	{
		this.props=p;
	}
	
	public Properties getProperties()
	{
		return props;
	}
	

	
	protected void initToolTip(final JTable table,final Integer cardPos,final Integer edPos)
	{
		pane = new MagicCardDetailPanel();
				pane.enableThumbnail(true);
				
				//pane.setPreferredSize(new Dimension(880, 350));
				
		final JPopupMenu popUp = new JPopupMenu();

		table.addMouseListener(new MouseAdapter() {
		    
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				
				if(row>-1) {
					table.setRowSelectionInterval(row, row);
					String cardName = table.getValueAt(row, cardPos.intValue()).toString();
					
					MagicEdition ed =null;
					if(edPos!=null) {
						String edID = table.getValueAt(row, edPos).toString();
						ed = new MagicEdition();
						ed.setId(edID);
					}
					
					try 
					{
						MagicCard mc =  MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName,ed).get(0);
						pane.setMagicCard(mc);
						//pane.setMagicLogo(edID, mc.getEditions().get(0).getRarity());
							SwingUtilities.updateComponentTreeUI(popUp);
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
						logger.error("Error on " + cardName,ex);
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
