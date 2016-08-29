package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.components.ManaPanel;
import org.magic.tools.ColorParser;

public class MagicCollectionTreeCellRenderer extends DefaultTreeCellRenderer {
	
	ManaPanel pane;
	ImageIcon gold, uncolor,back;
	Map<String,ImageIcon> map;
	
	
	public MagicCollectionTreeCellRenderer() {
		try{
			pane = new ManaPanel();
			map = new HashMap<String,ImageIcon>();
			gold= new ImageIcon(ImageIO.read(ManaCellRenderer.class.getResource("/res/gold.png")).getScaledInstance(15, 15, Image.SCALE_DEFAULT));
			uncolor= new ImageIcon(ImageIO.read(ManaCellRenderer.class.getResource("/res/uncolor.jpg")).getScaledInstance(15, 15, Image.SCALE_DEFAULT));
			back= new ImageIcon(ImageIO.read(ManaCellRenderer.class.getResource("/res/bottom.png")).getScaledInstance(15, 15, Image.SCALE_DEFAULT));
			
			map.put("{W}", new ImageIcon(pane.getManaSymbol("{W}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)) );
			map.put("{U}", new ImageIcon(pane.getManaSymbol("{U}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)) );
			map.put("{B}", new ImageIcon(pane.getManaSymbol("{B}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)) );
			map.put("{R}", new ImageIcon(pane.getManaSymbol("{R}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)) );
			map.put("{G}", new ImageIcon(pane.getManaSymbol("{G}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)) );
			map.put("{X}", new ImageIcon(pane.getManaSymbol("{X}").getScaledInstance(15, 15, Image.SCALE_DEFAULT)) );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	 
	}
	
     public Component getTreeCellRendererComponent(JTree tree,Object value, boolean selected, boolean expanded,boolean isLeaf, int row, boolean focused) 
     {
    	Component c = super.getTreeCellRendererComponent(tree, value,selected, expanded, isLeaf, row, focused);
    	try 
    	{
    			if(((DefaultMutableTreeNode)value).getUserObject() instanceof MagicEdition)
    			{
    				setIcon(back);
    			}
    			else
		    	if(((DefaultMutableTreeNode)value).getUserObject() instanceof MagicCard)
		    	{ 
		    			MagicCard mc=(MagicCard)((DefaultMutableTreeNode)value).getUserObject();
		    			
		    		
		    			//if(mc.getColors().contains(").size()<1)
		    			{
		    				setIcon(uncolor);
		    			}
		    			if(mc.getColors().size()==1)
						{
		    				setIcon(map.get(ColorParser.parse(mc.getColors().get(0))));
						}
		    			if(mc.getColors().size()>1)
		    			{
		    				setIcon(gold);
		    			}
		    			if(mc.getFullType().toLowerCase().contains("artifact"))
		    			{
		    				setIcon(map.get("{X}"));
		    			}
		    			if(mc.getFullType().toLowerCase().contains("land"))
		    			{
		    				setIcon(uncolor);
		    			}
		    			
		    		/*	
		    			if(mc.getEditions().get(0).getRarity().startsWith("Common"))
		    				setForeground(Color.BLACK);
		    			else
		    			if(mc.getEditions().get(0).getRarity().startsWith("Uncommon"))
		    				setForeground(new Color(32, 32, 32));
		    			else
		    			if(mc.getEditions().get(0).getRarity().startsWith("Rare"))
		    				setForeground(new Color(255, 213, 112));
		    			else
		    			if(mc.getEditions().get(0).getRarity().startsWith("Mythic"))
		    				setForeground(new Color(196, 108, 21));
		    				*/
		    	}
	    	return c;
	   	}
	    catch(Exception e){
	 			return c;
	 	}
     }

	
}
