package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.magic.api.beans.MagicCard;
import org.magic.gui.components.ManaPanel;

public class MagicCollectionTreeCellRenderer extends DefaultTreeCellRenderer {
	
	ManaPanel pane=new ManaPanel();
	
     public Component getTreeCellRendererComponent(JTree tree,Object value, boolean selected, boolean expanded,boolean isLeaf, int row, boolean focused) 
     {
    	Component c = super.getTreeCellRendererComponent(tree, value,selected, expanded, isLeaf, row, focused);
    	try 
    	{
    		Image gold= ImageIO.read(ManaCellRenderer.class.getResource("/res/gold.png"));
    		Image uncolor= ImageIO.read(ManaCellRenderer.class.getResource("/res/uncolor.jpg"));
		
    	 
		    	if(isLeaf)
		    	{ 
		    		MagicCard mc;
		    		
		    			mc=(MagicCard)((DefaultMutableTreeNode)value).getUserObject();
		    			//if(mc.getColors().contains(").size()<1)
		    			{
		    				setIcon(new ImageIcon(pane.getManaSymbol("{C}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
		    			}
		    			if(mc.getColorIdentity().size()==1)
						{
		    				setIcon(new ImageIcon(pane.getManaSymbol(mc.getColorIdentity().get(0)).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
						}
		    			if(mc.getColorIdentity().size()>1)
		    			{
		    				setIcon(new ImageIcon(gold.getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
		    			}
		    			if(mc.getFullType().toLowerCase().contains("artifact"))
		    			{
		    				setIcon(new ImageIcon(pane.getManaSymbol("{X}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
		    			}
		    			if(mc.getFullType().toLowerCase().contains("land"))
		    			{
		    				setIcon(new ImageIcon(uncolor.getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
		    			}
		    				
		    	}
	    	return c;
	   	}
	    catch(Exception e){
	 			return c;
	 	}
     }
}
