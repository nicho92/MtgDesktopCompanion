package org.magic.gui.components.network;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.gui.renderer.MessageRenderer;

public class JChatPanel extends JPanel{
    
	private DefaultListModel<AbstractMessage> listMsgModel;
	private JList<AbstractMessage> listMsg;
	
	
	public JChatPanel() {
	    
	    setLayout(new BorderLayout());
	    
	    listMsgModel = new DefaultListModel<>();
	    listMsg = new JList<>(listMsgModel);
	    listMsg.setCellRenderer(new MessageRenderer());
	    listMsg.setBorder(new TitledBorder(null, "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
	    
	    
	    add(new JScrollPane(listMsg),BorderLayout.CENTER);
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
	
	public void addMouseListener(MouseListener e)
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
