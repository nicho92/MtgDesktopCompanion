package org.magic.gui.components;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.JXTree;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.MagicCardsTreeCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;

public class PackagesBrowserPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private DefaultTreeModel model;
	private ImagePanel2 panelDraw;
	private JXTree tree;
	private final boolean view;
	private DefaultMutableTreeNode firstItem;

	public PackagesBrowserPanel(boolean viewThumbnail) {
		this.view = viewThumbnail;
		initGUI();

	}

	public JXTree getTree() {
		return tree;
	}

	public ImagePanel2 getThumbnailPanel() {
		return panelDraw;
	}

	public void init(MTGEdition ed) {
		final var root = (DefaultMutableTreeNode) model.getRoot();
		root.setUserObject(ed);
		root.removeAllChildren();

		Arrays.asList(EnumItems.values()).forEach(t -> {
			final List<MTGSealedProduct> pks = MTG.getEnabledPlugin(MTGSealedProvider.class).get(ed, t);
			if (!pks.isEmpty()) {
				final var dir = new DefaultMutableTreeNode(t);
				pks.forEach(p -> dir.add(new DefaultMutableTreeNode(p)));
				root.add(dir);
			}
		});
		model.reload();
		for (var i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}

		if (view) {
		    panelDraw.setImg(null);
		}

	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		firstItem = new DefaultMutableTreeNode("Loading...");
		model = new DefaultTreeModel(firstItem);
		panelDraw = new ImagePanel2(true, false, true, false);

		if (view) {
			add(panelDraw, BorderLayout.CENTER);
		}

		final var panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		if (!view) {
			add(panel, BorderLayout.CENTER);
		} else {
			add(panel, BorderLayout.WEST);
		}

		tree = new JXTree(model);
		tree.setCellRenderer(new MagicCardsTreeCellRenderer());

		panel.add(new JScrollPane(tree), BorderLayout.CENTER);

		tree.addTreeSelectionListener(_ -> {
			final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

			if (selectedNode != null && (selectedNode.getUserObject() instanceof final MTGSealedProduct msp)) {
			    load(msp);
			}
		});

		final SwingWorker<Void, Void> sw = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				initTree();
				return null;
			}
			@Override
			protected void done() {

				reload();
			}
		};
		ThreadManager.getInstance().runInEdt(sw, "Loading sealed tree");
	}

	public void load(MTGSealedProduct p) {
		panelDraw.setImg(MTG.getEnabledPlugin(MTGSealedProvider.class).getPictureFor(p));
		panelDraw.revalidate();
		panelDraw.repaint();
	}

	public List<MTGSealedProduct> getSelecteds() {

		final var ret = new ArrayList<MTGSealedProduct>();

		for (final var p : tree.getSelectionPaths()) {
			final var selectedNode = (DefaultMutableTreeNode) p.getLastPathComponent();

			if (selectedNode != null && (selectedNode.getUserObject() instanceof final MTGSealedProduct msp)) {
				ret.add(msp);
			}

		}

		return ret;
	}

	public void initTree() {
		final var root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();

		MTG.getEnabledPlugin(MTGSealedProvider.class).listSets().stream().sorted().forEach(ed -> {

			final var edNode = new DefaultMutableTreeNode(ed);

			Arrays.asList(EnumItems.values()).forEach(t -> {
				final var pks = MTG.getEnabledPlugin(MTGSealedProvider.class).get(ed, t);
				if (!pks.isEmpty()) {
					final var dir = new DefaultMutableTreeNode(t);
					pks.forEach(p -> dir.add(new DefaultMutableTreeNode(p)));
					edNode.add(dir);
				}
			});

			if (!edNode.isLeaf()) {
			    root.add(edNode);
			}
		});

		if (view) {
		    panelDraw.setImg(null);
		}

		firstItem.setUserObject("Package");

	}

	public void reload() {
		model.reload();
		tree.expandRow(0);
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_PACKAGE;
	}

	@Override
	public String getTitle() {
		return "Package Browser";
	}

}
