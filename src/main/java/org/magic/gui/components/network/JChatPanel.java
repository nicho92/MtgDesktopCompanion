package org.magic.gui.components.network;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.gui.renderer.MessageRenderer;

public class JChatPanel extends JPanel{
    
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private DefaultListModel<AbstractMessage> listMsgModel;
	private JList<AbstractMessage> listMsg;
	
	
	public JChatPanel() {
	    
	    setLayout(new BorderLayout());
	    
	    listMsgModel = new DefaultListModel<>();
	    listMsg = new JList<>(listMsgModel) {
		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}
	    };
	    listMsg.setCellRenderer(new MessageRenderer());
	    listMsg.setBorder(new TitledBorder(null, "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
	    
	    var scroll = new JScrollPane(listMsg);
	    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    
	    add(scroll,BorderLayout.CENTER);
	}
	
	public void chatClear()
	{
	    listMsgModel.removeAllElements();
	}
	
	public void chatAddMessage(AbstractMessage msg)
	{
	    listMsgModel.addElement(msg);
	    listMsg.ensureIndexIsVisible(listMsg.getModel().getSize() - 1);
	}
	
	public synchronized void addMouseListener(MouseListener e)
	{
	    listMsg.addMouseListener(e);
	}
	
	public AbstractMessage getSelectedValue()
	{
	  return listMsg.getSelectedValue();
	}
	
	public void setSelectedIndex(Point e)
	{
	    listMsg.setSelectedIndex(listMsg.locationToIndex(e));
	}
	
	public JComponent getComponent()
	{
	    return listMsg;
	}
	
}
