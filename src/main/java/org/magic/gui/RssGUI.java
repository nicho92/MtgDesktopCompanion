package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicNews;
import org.magic.gui.components.NewsPanel;
import org.magic.gui.models.MagicNewsTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

import com.rometools.rome.feed.synd.SyndEntry;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RssGUI extends JPanel {
	private JTable table;
	private MagicNewsTableModel model;
	private JEditorPane editorPane;
	private DefaultMutableTreeNode curr;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private NewsPanel newsPanel ;
	private DefaultMutableTreeNode rootNode ;
	
	
	public RssGUI() {
		logger.info("init RSS GUI");
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollTable = new JScrollPane();
		model = new MagicNewsTableModel();
		table = new JTable(model);
		
		scrollTable.setViewportView(table);
		
		
		rootNode = new DefaultMutableTreeNode(MTGControler.getInstance().getLangService().getCapitalize("RSS_MODULE"));
		
		
		for(MagicNews rss : MTGControler.getInstance().getEnabledDAO().listNews())
		{
			rootNode.add(new DefaultMutableTreeNode(rss));
		}
		
		
		JSplitPane splitNews = new JSplitPane();
		splitNews.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		JScrollPane scrollEditor = new JScrollPane();
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		
			HTMLEditorKit kit = new HTMLEditorKit();
			editorPane.setEditorKit(kit);
			Document doc = kit.createDefaultDocument();
			editorPane.setDocument(doc);
		scrollEditor.setViewportView(editorPane);
		
		
		splitNews.setLeftComponent(scrollTable);
		splitNews.setRightComponent(scrollEditor);
		
		JSplitPane splitTreeTable = new JSplitPane();
		add(splitTreeTable, BorderLayout.CENTER);
		splitTreeTable.setRightComponent(splitNews);
		
		JPanel leftPanel = new JPanel();
		splitTreeTable.setLeftComponent(leftPanel);
		leftPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollTree = new JScrollPane();
		leftPanel.add(scrollTree, BorderLayout.CENTER);
		
		JTree tree = new JTree();
		
		
		
		tree.setModel(new DefaultTreeModel(rootNode));
		
		scrollTree.setViewportView(tree);
		
		JPanel panelControl = new JPanel();
		leftPanel.add(panelControl, BorderLayout.NORTH);
		
		JButton btnNewButton = new JButton("");
		btnNewButton.addActionListener(ae->{
				newsPanel.setMagicNews(new MagicNews());
				newsPanel.setVisible(true);
		});
		
		btnNewButton.setIcon(MTGConstants.ICON_NEW);
		panelControl.add(btnNewButton);
		
		JButton btnSave = new JButton(MTGConstants.ICON_SAVE);
		btnSave.addActionListener(ae->{
				try {
					MTGControler.getInstance().getEnabledDAO().saveOrUpdateNews(newsPanel.getMagicNews());
				} catch (SQLException ex) {
					logger.error("Error saving news", ex);
					JOptionPane.showMessageDialog(null, ex, MTGControler.getInstance().getLangService().getCapitalize("ERROR"), JOptionPane.ERROR_MESSAGE);
				}
		});
		panelControl.add(btnSave);
		
		JButton btnDelete = new JButton(MTGConstants.ICON_DELETE);
		btnDelete.addActionListener(ae-> {
			try {
				MTGControler.getInstance().getEnabledDAO().deleteNews(newsPanel.getMagicNews());
			} catch (SQLException ex) {
				logger.error("Error delete news", ex);
				JOptionPane.showMessageDialog(null, ex, MTGControler.getInstance().getLangService().getCapitalize("ERROR"), JOptionPane.ERROR_MESSAGE);
			}
		});
		panelControl.add(btnDelete);
		
		newsPanel = new NewsPanel();
		leftPanel.add(newsPanel, BorderLayout.SOUTH);
		
		tree.addTreeSelectionListener(tse->{
				TreePath path = tse.getPath();
				curr = (DefaultMutableTreeNode) path.getLastPathComponent();
				
				if(curr.getUserObject() instanceof MagicNews)
					ThreadManager.getInstance().execute(()->{
							try {
								newsPanel.setMagicNews((MagicNews)curr.getUserObject());
								model.init((MagicNews)curr.getUserObject());
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
