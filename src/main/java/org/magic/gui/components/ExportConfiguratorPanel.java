package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.jdesktop.swingx.JXTree;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.beans.enums.MTGColor;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.tools.BeanTools;
import org.magic.tools.UITools;

public class ExportConfiguratorPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private DefaultTreeModel model = new DefaultTreeModel(root);
	private JTextField jtf;
	private JButton btnExport ;
	
	public ExportConfiguratorPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JTextPane textPane = new JTextPane();
		JPanel panel = new JPanel();
		jtf = new JTextField(50);
		JXTree tree = new JXTree(model);
		JPanel panelBas = new JPanel();
		btnExport = UITools.createBindableJButton(null, MTGConstants.ICON_EXPORT, KeyEvent.VK_E, "export");
		
		add(panel, BorderLayout.NORTH);
		add(new JScrollPane(tree), BorderLayout.WEST);
		panel.add(jtf);
		add(new JScrollPane(textPane), BorderLayout.CENTER);
		add(panelBas, BorderLayout.SOUTH);
		
		
		panelBas.add(btnExport);
				
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) 
				{
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					
					if(selPath!=null)
						jtf.setText(jtf.getText()+BeanTools.TOKEN_START+StringUtils.join(ArrayUtils.remove(selPath.getPath(), 0),".")+BeanTools.TOKEN_END);
	             
	            }
			}
			});
		
		MagicCard mc = new MagicCard();
		mc.getEditions().add(new MagicEdition("test"));
		mc.setId("test");
		mc.getLegalities().add(new MagicFormat());
		mc.getRulings().add(new MagicRuling());
		mc.getForeignNames().add(new MagicCardNames());
		mc.setRotatedCard(mc);
		mc.getColors().add(MTGColor.BLACK);
	
		initTree(mc);
		
	}
	
	public void initTree(Object o)
	{
		BeanTools.describe(o).entrySet().forEach(e->{
			
			DefaultMutableTreeNode entry = new DefaultMutableTreeNode(e.getKey());
			
			if(e.getValue()!=null && !(ClassUtils.isPrimitiveOrWrapper(e.getValue().getClass())) && !(e.getValue() instanceof String))
			{
				BeanTools.describe(e.getValue()).entrySet().forEach(f->{
					DefaultMutableTreeNode entryEd = new DefaultMutableTreeNode(f.getKey());
					entry.add(entryEd);
				});
				
				if(e.getValue() instanceof List)
				{
					List<?> t = (List<?>)e.getValue();
					
					if(!t.isEmpty())
						BeanTools.describe(t.get(0)).entrySet().forEach(f->{
							DefaultMutableTreeNode entryEd = new DefaultMutableTreeNode(f.getKey());
							entry.add(entryEd);
						});
					
				}
//				
//				if(e.getValue() instanceof Map)
//				{
//					Map<?,?> t = (Map<?,?>)e.getValue();
//					
//					if(!t.isEmpty())
//						BeanTools.describe(t.values().iterator().next()).entrySet().forEach(f->{
//							DefaultMutableTreeNode entryEd = new DefaultMutableTreeNode(f.getKey());
//							entry.add(entryEd);
//						});
//				}
				
			}
			root.add(entry);
		});
		model.reload();
	}

	public JButton getBtnExport() {
		return btnExport;
	}

	@Override
	public String getTitle() {
		return "Personnal Export";
	}

	public String getResult() {
		return jtf.getText();
	}
	
		
}
