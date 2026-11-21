package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGNews;
import org.magic.api.beans.MTGNewsContent;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIBrowserComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.NewsEditorPanel;
import org.magic.gui.models.MagicNewsTableModel;
import org.magic.gui.renderer.NewsTreeCellRenderer;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;

public class RssGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private final JXTable table;
	private final MagicNewsTableModel model;
	private final MTGUIBrowserComponent editorPane;
	private DefaultMutableTreeNode curr;
	private final NewsEditorPanel newsPanel;
	private final DefaultMutableTreeNode rootNode;
	private final JTree tree;
	private final AbstractBuzyIndicatorComponent lblLoading;
	private final JButton btnNewButton;
	private final JButton btnSave;
	private final JButton btnDelete;

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_NEWS;
	}

	@Override
	public String getTitle() {
		return capitalize("RSS_MODULE");
	}

	public RssGUI() {

		model = new MagicNewsTableModel();
		table = UITools.createNewTable(model, false);
		table.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));

		tree = new JTree();
		final var splitNews = new JSplitPane();
		editorPane = MTGUIBrowserComponent.createBrowser();
		final var splitTreeTable = new JSplitPane();
		final var leftPanel = new JPanel();
		rootNode = new DefaultMutableTreeNode(capitalize("RSS_MODULE"));

		btnNewButton = UITools.createBindableJButton(null, MTGConstants.ICON_NEW, KeyEvent.VK_N, "new news entry");
		btnSave = UITools.createBindableJButton(null, MTGConstants.ICON_SAVE, KeyEvent.VK_S, "save news entry");
		btnDelete = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_D, "delete news entry");
		lblLoading = AbstractBuzyIndicatorComponent.createLabelComponent();
		newsPanel = new NewsEditorPanel();

		setLayout(new BorderLayout(0, 0));
		tree.setPreferredSize(new Dimension(150, 64));
		splitNews.setOrientation(JSplitPane.VERTICAL_SPLIT);
		leftPanel.setLayout(new BorderLayout(0, 0));
		tree.setModel(new DefaultTreeModel(rootNode));
		tree.setCellRenderer(new NewsTreeCellRenderer());

		splitNews.setLeftComponent(new JScrollPane(table));
		splitNews.setRightComponent(new JScrollPane(editorPane));
		add(splitTreeTable, BorderLayout.CENTER);
		splitTreeTable.setRightComponent(splitNews);
		splitTreeTable.setLeftComponent(leftPanel);
		leftPanel.add(new JScrollPane(tree), BorderLayout.CENTER);

		final var panelControl = UITools.createFlowCenterPanel(btnNewButton, btnSave, btnDelete, lblLoading);

		final var bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

		bottomPanel.add(newsPanel);
		bottomPanel.add(panelControl);

		leftPanel.add(bottomPanel, BorderLayout.SOUTH);

		initActions();

	}

	@Override
	public void onFirstShowing() {
		initTree();
	}

	private void initActions() {
		btnNewButton.addActionListener(_ -> {
			newsPanel.setMagicNews(new MTGNews());
			newsPanel.setVisible(true);
		});

		btnSave.addActionListener(_ -> {
			try {
				getEnabledPlugin(MTGDao.class).saveOrUpdateNews(newsPanel.getMagicNews());
				initTree();
			} catch (final SQLException ex) {
				logger.error("Error saving news", ex);
				MTGControler.getInstance().notify(ex);
			}
		});

		btnDelete.addActionListener(_ -> {
			try {
				getEnabledPlugin(MTGDao.class).deleteNews(newsPanel.getMagicNews());
				initTree();
			} catch (final SQLException ex) {
				logger.error("Error delete news", ex);
				MTGControler.getInstance().notify(ex);
			}
		});

		tree.addTreeSelectionListener(tse -> {
			final var path = tse.getPath();
			curr = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (curr.getUserObject() instanceof final MTGNews n) {
				final var sw = new SwingWorker<List<MTGNewsContent>, MTGNews>() {

					@Override
					protected List<MTGNewsContent> doInBackground() throws Exception {
						return n.getProvider().listNews(n);
					}

					@Override
					protected void done() {
						try {
							model.init(get());
							model.fireTableDataChanged();

						} catch (final InterruptedException _) {
							Thread.currentThread().interrupt();
						} catch (final Exception e) {
							logger.error(e);
						}
						lblLoading.end();
					}
				};
				lblLoading.start();
				newsPanel.setMagicNews((MTGNews) curr.getUserObject());
				ThreadManager.getInstance().runInEdt(sw, "loading rss");
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				final MTGNewsContent sel = UITools.getTableSelection(table, 0);

				if (me.getClickCount() == 2) {
					try {
						UITools.browse(sel.getLink().toURI().toASCIIString());

					} catch (final Exception e1) {
						logger.error(e1);
					}
				} else {

					final var sw = new SwingWorker<Void, URL>() {
						@Override
						protected void done() {
							lblLoading.end();

						}

						@Override
						protected void process(List<URL> chunks) {
							try {
								editorPane.loadURL(chunks.get(0).toString());
							} catch (final Exception e) {
								logger.error("error loading {}", chunks.get(0), e);
							}

						}

						@Override
						protected Void doInBackground() throws Exception {
							publish(sel.getLink());
							return null;
						}

					};

					lblLoading.start();
					ThreadManager.getInstance().runInEdt(sw, "loading " + sel.getLink());
				}
			}
		});

	}

	private void initTree() {
		rootNode.removeAllChildren();

		final SwingWorker<Void, MTGNews> sw = new SwingWorker<>() {

			@Override
			protected void process(List<MTGNews> chunks) {
				chunks.forEach(cat -> add(cat.getCategorie(), cat));
			}

			@Override
			protected Void doInBackground() throws Exception {
				for (final var cat : getEnabledPlugin(MTGDao.class).listNews()) {
				    publish(cat);
				}

				return null;
			}

			@Override
			protected void done() {
				((DefaultTreeModel) tree.getModel()).reload();

				for (var i = 0; i < tree.getRowCount(); i++) {
				    tree.expandRow(i + 1);
				}

			}
		};
		ThreadManager.getInstance().runInEdt(sw, "Loading News Tree");
	}

	private void add(String cat, MTGNews n) {
		final var node = getNodeCateg(cat);
		node.add(new DefaultMutableTreeNode(n));
		rootNode.add(node);
	}

	private DefaultMutableTreeNode getNodeCateg(String cat) {
		final var e = rootNode.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			final var node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject().toString().equalsIgnoreCase(cat)) {
				return node;
			}
		}
		return new DefaultMutableTreeNode(cat);
	}

}
