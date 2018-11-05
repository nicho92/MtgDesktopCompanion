package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.fit.cssbox.swingbox.BrowserPane;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNewsContent;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.NewsEditorPanel;
import org.magic.gui.models.MagicNewsTableModel;
import org.magic.gui.renderer.NewsTreeCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.tools.UITools;

public class RssGUI extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private JTable table;
	private MagicNewsTableModel model;
	private BrowserPane editorPane;
	private DefaultMutableTreeNode curr;
	private NewsEditorPanel newsPanel;
	private DefaultMutableTreeNode rootNode;
	private JTree tree;
	private AbstractBuzyIndicatorComponent lblLoading;
	private JButton btnNewButton;
	private JButton btnSave;
	private JButton btnDelete;
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_NEWS;
	}
	
	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("RSS_MODULE");
	}
	
	public RssGUI() {
		
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
		lblLoading = AbstractBuzyIndicatorComponent.createLabelComponent();
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
				MTGControler.getInstance().getEnabled(MTGDao.class).saveOrUpdateNews(newsPanel.getMagicNews());
				initTree();
			} catch (SQLException ex) {
				logger.error("Error saving news", ex);
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),ex));
			}
		});

		
		btnDelete.addActionListener(ae -> {
			try {
				MTGControler.getInstance().getEnabled(MTGDao.class).deleteNews(newsPanel.getMagicNews());
				initTree();
			} catch (SQLException ex) {
				logger.error("Error delete news", ex);
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),ex));
			}
		});
	
		
		

		

		tree.addTreeSelectionListener(tse -> {
			TreePath path = tse.getPath();
			curr = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (curr.getUserObject() instanceof MagicNews)
				ThreadManager.getInstance().execute(() -> {
					try {
						lblLoading.start();
						newsPanel.setMagicNews((MagicNews) curr.getUserObject());
						
						MagicNews n = (MagicNews) curr.getUserObject();
						
						model.init(n.getProvider().listNews(n));
					} catch (Exception e) {
						logger.error("error reading rss", e);
					}
					model.fireTableDataChanged();
					lblLoading.end();
				}, "load RSS " + curr.getUserObject());
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				List<MagicNewsContent> sels = UITools.getTableSelection(table, 0);
				MagicNewsContent sel = sels.get(0);
				if (me.getClickCount() == 2) {
					try {
						Desktop.getDesktop().browse(sel.getLink().toURI());
						
					} catch (Exception e1) {
						logger.error(e1);
					}
				} else {
					ThreadManager.getInstance().execute(() -> {
						lblLoading.start();
						try {
							logger.debug("loading " + sel.getLink());
							editorPane.setPage(sel.getLink());
							editorPane.setCaretPosition(0);
							lblLoading.end();
						} catch (IOException e) {
							logger.error("Error reading " + sel.getLink(), e);
							lblLoading.end();
						}
					}, "reading news "+sel.getLink());
				}
			}
		});
		
	}

	private void initTree() {
		rootNode.removeAllChildren();
		for (MagicNews cat : MTGControler.getInstance().getEnabled(MTGDao.class).listNews())
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
