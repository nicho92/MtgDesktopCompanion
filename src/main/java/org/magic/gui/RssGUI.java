package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.fit.cssbox.swingbox.BrowserPane;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNewsContent;
import org.magic.gui.components.JBuzyLabel;
import org.magic.gui.components.NewsEditorPanel;
import org.magic.gui.models.MagicNewsTableModel;
import org.magic.gui.renderer.NewsTreeCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class RssGUI extends JPanel {
	private JTable table;
	private MagicNewsTableModel model;
	private BrowserPane editorPane;
	private DefaultMutableTreeNode curr;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private NewsEditorPanel newsPanel;
	private DefaultMutableTreeNode rootNode;
	private JTree tree;
	private JBuzyLabel lblLoading;
	private JButton btnNewButton;
	private JButton btnSave;
	private JButton btnDelete;
	
	
	public RssGUI() {
		logger.info("init RSS GUI");
		
		JScrollPane scrollTable = new JScrollPane();
		model = new MagicNewsTableModel();
		table = new JTable(model);
		tree = new JTree();
		JSplitPane splitNews = new JSplitPane();
		JScrollPane scrollEditor = new JScrollPane();
		editorPane = new BrowserPane();
		JSplitPane splitTreeTable = new JSplitPane();
		JPanel leftPanel = new JPanel();
		JScrollPane scrollTree = new JScrollPane();
		rootNode = new DefaultMutableTreeNode(MTGControler.getInstance().getLangService().getCapitalize("RSS_MODULE"));
		JPanel panelControl = new JPanel();
		btnNewButton = new JButton(MTGConstants.ICON_NEW);
		btnSave = new JButton(MTGConstants.ICON_SAVE);
		btnDelete = new JButton(MTGConstants.ICON_DELETE);
		lblLoading = new JBuzyLabel();
		newsPanel = new NewsEditorPanel();
		
		setLayout(new BorderLayout(0, 0));
		tree.setPreferredSize(new Dimension(150, 64));
		splitNews.setOrientation(JSplitPane.VERTICAL_SPLIT);
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		leftPanel.setLayout(new BorderLayout(0, 0));
		tree.setModel(new DefaultTreeModel(rootNode));
		tree.setCellRenderer(new NewsTreeCellRenderer());

		
		scrollEditor.setViewportView(editorPane);
		splitNews.setLeftComponent(scrollTable);
		splitNews.setRightComponent(scrollEditor);
		scrollTable.setViewportView(table);
		add(splitTreeTable, BorderLayout.CENTER);
		splitTreeTable.setRightComponent(splitNews);
		splitTreeTable.setLeftComponent(leftPanel);
		leftPanel.add(scrollTree, BorderLayout.CENTER);
		scrollTree.setViewportView(tree);
		leftPanel.add(panelControl, BorderLayout.NORTH);
		panelControl.add(btnNewButton);
		panelControl.add(btnSave);
		panelControl.add(btnDelete);
		panelControl.add(lblLoading);
		leftPanel.add(newsPanel, BorderLayout.SOUTH);
				
	
		initTree();

		initActions();
	

	}

	private void initActions() {
		btnNewButton.addActionListener(ae -> {
			newsPanel.setMagicNews(new MagicNews());
			newsPanel.setVisible(true);
		});

		

		btnSave.addActionListener(ae -> {
			try {
				MTGControler.getInstance().getEnabledDAO().saveOrUpdateNews(newsPanel.getMagicNews());
				initTree();
			} catch (SQLException ex) {
				logger.error("Error saving news", ex);
				JOptionPane.showMessageDialog(null, ex, MTGControler.getInstance().getLangService().getError(),
						JOptionPane.ERROR_MESSAGE);
			}
		});

		
		btnDelete.addActionListener(ae -> {
			try {
				MTGControler.getInstance().getEnabledDAO().deleteNews(newsPanel.getMagicNews());
				initTree();
			} catch (SQLException ex) {
				logger.error("Error delete news", ex);
				JOptionPane.showMessageDialog(null, ex, MTGControler.getInstance().getLangService().getError(),
						JOptionPane.ERROR_MESSAGE);
			}
		});
	
		
		

		

		tree.addTreeSelectionListener(tse -> {
			TreePath path = tse.getPath();
			curr = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (curr.getUserObject() instanceof MagicNews)
				ThreadManager.getInstance().execute(() -> {
					try {
						lblLoading.buzy(true);
						newsPanel.setMagicNews((MagicNews) curr.getUserObject());
						model.init((MagicNews) curr.getUserObject());
					} catch (Exception e) {
						logger.error("error reading rss", e);
					}
					model.fireTableDataChanged();
					lblLoading.buzy(false);
				}, "load RSS " + curr.getUserObject());
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				MagicNewsContent sel = model.getEntryAt(table.getSelectedRow());

				if (me.getClickCount() == 2) {
					try {
						Desktop.getDesktop().browse(sel.getLink().toURI());
					} catch (Exception e1) {
						logger.error(e1);
					}
				} else {
					ThreadManager.getInstance().execute(() -> {
						lblLoading.buzy(true);
						try {
							logger.debug("loading " + sel.getLink());
							editorPane.setPage(sel.getLink());
							editorPane.setCaretPosition(0);
							lblLoading.buzy(false);
						} catch (IOException e) {
							logger.error("Error reading " + sel.getLink(), e);
							lblLoading.buzy(false);
						}

					});
				}
			}
		});
		
	}

	private void initTree() {
		rootNode.removeAllChildren();
		for (MagicNews cat : MTGControler.getInstance().getEnabledDAO().listNews())
			add(cat.getCategorie(), cat);

		((DefaultTreeModel) tree.getModel()).reload();

		for (int i = 0; i < tree.getRowCount(); i++)
			tree.expandRow(i + 1);

	}

	private void add(String cat, MagicNews n) {
		DefaultMutableTreeNode node = getNodeCateg(cat);
		node.add(new DefaultMutableTreeNode(n));
		rootNode.add(node);
	}

	private DefaultMutableTreeNode getNodeCateg(String cat) {
		Enumeration e = rootNode.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject().toString().equalsIgnoreCase(cat)) {
				return node;
			}
		}
		return new DefaultMutableTreeNode(cat);
	}

}
