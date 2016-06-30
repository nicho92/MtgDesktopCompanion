package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.magic.api.beans.RSSBean;
import org.magic.gui.models.RssContentTableModel;
import org.magic.tools.MagicFactory;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RssGUI extends JPanel {
	private JTable table;
	private RssContentTableModel model;
	private JEditorPane editorPane;
	
	
	public RssGUI() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollTable = new JScrollPane();
		model = new RssContentTableModel();
		table = new JTable(model);
		
		scrollTable.setViewportView(table);
		
		JScrollPane scrollTree = new JScrollPane();
		
		JTree tree = new JTree();
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Magic RSS") 
			{
				
				{
					Set<String> catg = new HashSet<String>();
					for(RSSBean r : MagicFactory.getInstance().getRss())
					{
							catg.add(r.getCategorie());
					}
					
					for(String cat : catg)
					{	
						DefaultMutableTreeNode node_1 = new DefaultMutableTreeNode(cat);
						for(RSSBean r : MagicFactory.getInstance().getRss())
						{
							if(r.getCategorie().equals(cat))
								node_1.add(new DefaultMutableTreeNode(r));
						}
						add(node_1);
					}
			
				}
			}
		));
		scrollTree.setViewportView(tree);
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		JScrollPane scrollEditor = new JScrollPane();
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		
		scrollEditor.setViewportView(editorPane);
		
		
		splitPane.setLeftComponent(scrollTable);
		splitPane.setRightComponent(scrollEditor);
		
		JSplitPane splitPane_1 = new JSplitPane();
		add(splitPane_1, BorderLayout.CENTER);
		splitPane_1.setLeftComponent(scrollTree);
		splitPane_1.setRightComponent(splitPane);
		
		JPanel panelHaut = new JPanel();
		add(panelHaut, BorderLayout.NORTH);
		
		JButton btnNewButton = new JButton("");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			
			}
		});
		btnNewButton.setIcon(new ImageIcon(RssGUI.class.getResource("/res/refresh.png")));
		panelHaut.add(btnNewButton);
		
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				TreePath path = tse.getPath();
				DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
				
				if(curr.getUserObject() instanceof RSSBean)
					try {
						model.init((RSSBean)curr.getUserObject());
					} catch (Exception e) {
						e.printStackTrace();
					} 
					model.fireTableDataChanged();
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				SyndEntry sel = model.getEntryAt(table.getSelectedRow());
				editorPane.setText(sel.getDescription().getValue());
				
			}
		});
		
	}

}
