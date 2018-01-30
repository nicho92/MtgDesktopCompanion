package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.magic.api.beans.RSSBean;
import org.magic.gui.models.RssContentTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

import com.rometools.rome.feed.synd.SyndEntry;

public class RssGUI extends JPanel {
	private JTable table;
	private RssContentTableModel model;
	private JEditorPane editorPane;
	private DefaultMutableTreeNode curr;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	
	public RssGUI() {
		logger.info("init RSS GUI");
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollTable = new JScrollPane();
		model = new RssContentTableModel();
		table = new JTable(model);
		
		scrollTable.setViewportView(table);
		
		JScrollPane scrollTree = new JScrollPane();
		
		JTree tree = new JTree();
		
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Magic RSS");
		
		tree.setModel(new DefaultTreeModel(rootNode));
		
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
		
		JSplitPane splitPane1 = new JSplitPane();
		add(splitPane1, BorderLayout.CENTER);
		splitPane1.setLeftComponent(scrollTree);
		splitPane1.setRightComponent(splitPane);
		
		JPanel panelHaut = new JPanel();
		add(panelHaut, BorderLayout.NORTH);
		
		JButton btnNewButton = new JButton("");
		
		btnNewButton.setIcon(MTGConstants.ICON_NEW);
		panelHaut.add(btnNewButton);
		
		tree.addTreeSelectionListener(tse->{
				TreePath path = tse.getPath();
				curr = (DefaultMutableTreeNode) path.getLastPathComponent();
				
				if(curr.getUserObject() instanceof RSSBean)
					ThreadManager.getInstance().execute(()->{
							try {
								model.init((RSSBean)curr.getUserObject());
							} catch (Exception e) {
								MTGLogger.printStackTrace(e);
							} 
							model.fireTableDataChanged();
					}, "load RSS " + curr.getUserObject());
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				SyndEntry sel = model.getEntryAt(table.getSelectedRow());
				if(sel.getDescription()!=null)
					editorPane.setText(sel.getDescription().getValue());
				
			}
		});
		
	}

}
