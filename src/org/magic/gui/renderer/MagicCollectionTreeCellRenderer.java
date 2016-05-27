package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
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
    		Image back= ImageIO.read(ManaCellRenderer.class.getResource("/res/bottom.png"));
    	 
    		
    		
    			if(((DefaultMutableTreeNode)value).getUserObject() instanceof MagicEdition)
    			{
    				setIcon(new ImageIcon(back.getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
    			}
    			else
		    	if(((DefaultMutableTreeNode)value).getUserObject() instanceof MagicCard)
		    	{ 
		    			MagicCard mc=(MagicCard)((DefaultMutableTreeNode)value).getUserObject();
		    			
		    			System.out.println(mc.getColors());
				    	
		    			//if(mc.getColors().contains(").size()<1)
		    			{
		    				setIcon(new ImageIcon(pane.getManaSymbol("{C}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
		    			}
		    			if(mc.getColors().size()==1)
						{
		    				setIcon(new ImageIcon(pane.getManaSymbol(parse(mc.getColors().get(0))).getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
						}
		    			if(mc.getColors().size()>1)
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

	private String parse(String string) {
		if(string.equals("White"))
			return "{W}";
		else
			if(string.equals("Blue"))
				return "{U}";
			else
				if(string.equals("Black"))
					return "{B}";
				else
					if(string.equals("Red"))
						return "{R}";
					else
						if(string.equals("Green"))
							return "{G}";
							
							return string;
	}
}
