package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

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
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.tools.BeanTools;

public class ExportConfiguratorPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private DefaultTreeModel model = new DefaultTreeModel(root);
	
	public ExportConfiguratorPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JTextPane textPane = new JTextPane();
		JPanel panel = new JPanel();
		JTextField jtf = new JTextField(50);
		JXTree tree = new JXTree(model);
		JPanel panelBas = new JPanel();
		
		
		add(panel, BorderLayout.NORTH);
		add(new JScrollPane(tree), BorderLayout.WEST);
		panel.add(jtf);
		add(new JScrollPane(textPane), BorderLayout.CENTER);
		add(panelBas, BorderLayout.SOUTH);
				
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) 
				{
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					
					if(selPath!=null)
						jtf.setText(jtf.getText()+"{"+StringUtils.join(ArrayUtils.remove(selPath.getPath(), 0),".")+"}");
	             
	            }
			}
			});
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
				
				if(e.getValue() instanceof Map)
				{
					Map<?,?> t = (Map<?,?>)e.getValue();
					
					if(!t.isEmpty())
						BeanTools.describe(t.values().iterator().next()).entrySet().forEach(f->{
							DefaultMutableTreeNode entryEd = new DefaultMutableTreeNode(f.getKey());
							entry.add(entryEd);
						});
					
				}
				
			}
			root.add(entry);
		});
		model.reload();
	}

	@Override
	public String getTitle() {
		return "Personnal Export";
	}

	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
