	package org.magic.gui.components.dialog;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.editor.JTagsPanel;
import org.magic.gui.models.DeckSelectionTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.MTG;
import org.magic.tools.UITools;
public class JDeckChooserDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private CmcChartPanel cmcChartPanel;
	private MagicDeck selectedDeck;
	private JTagsPanel tagsPanel;
	private AbstractBuzyIndicatorComponent buzy;
	private JTree tree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private DefaultMutableTreeNode creatureNode = new DefaultMutableTreeNode("Creatures");
	private DefaultMutableTreeNode artifactsNode = new DefaultMutableTreeNode("Artifacts");
	private DefaultMutableTreeNode planeswalkerNode = new DefaultMutableTreeNode("Planeswalkers");
	private DefaultMutableTreeNode landsNode = new DefaultMutableTreeNode("Lands");
	private DefaultMutableTreeNode spellsNode = new DefaultMutableTreeNode("Spells");
	private DefaultMutableTreeNode sideNode = new DefaultMutableTreeNode("Sideboard");
	private transient MTGDeckManager manager;


	public MagicDeck getSelectedDeck() {
		return selectedDeck;
	}

	private void initTree() {

		creatureNode.removeAllChildren();
		artifactsNode.removeAllChildren();
		planeswalkerNode.removeAllChildren();
		landsNode.removeAllChildren();
		spellsNode.removeAllChildren();
		sideNode.removeAllChildren();

		if (selectedDeck != null) {
			for (MagicCard mc : selectedDeck.getMain().keySet()) {
				if (mc.isCreature() && !mc.isArtifact())
					creatureNode.add(new DefaultMutableTreeNode(selectedDeck.getMain().get(mc) + " " + mc));
				else if (mc.isArtifact())
					artifactsNode.add(new DefaultMutableTreeNode(selectedDeck.getMain().get(mc) + " " + mc));
				else if (mc.isLand())
					landsNode.add(new DefaultMutableTreeNode(selectedDeck.getMain().get(mc) + " " + mc));
				else if (mc.isPlaneswalker())
					planeswalkerNode.add(new DefaultMutableTreeNode(selectedDeck.getMain().get(mc) + " " + mc));
				else
					spellsNode.add(new DefaultMutableTreeNode(selectedDeck.getMain().get(mc) + " " + mc));
			}

			for (MagicCard mc : selectedDeck.getSideBoard().keySet())
				sideNode.add(new DefaultMutableTreeNode(selectedDeck.getSideBoard().get(mc) + " " + mc));

			model.reload();

			expandAll(new TreePath(root));
		}
	}

	private void expandAll(TreePath parent) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<? extends TreeNode>e = node.children(); e.hasMoreElements();) {
				TreeNode n =  e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(path);
			}
		}
		tree.expandPath(parent);
	}

	public JDeckChooserDialog() {
		setTitle(capitalize("OPEN_DECK"));
		setIconImage(MTGConstants.ICON_DECK.getImage());
		setSize(950, 600);

		model = new DefaultTreeModel(root);
		manager = new MTGDeckManager();
		var decksModel = new DeckSelectionTableModel();
		manager.addObserver((o,d)->decksModel.addItem((MagicDeck)d));
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				selectedDeck = null;
			}

			@Override
			public void windowClosing(WindowEvent e) {
				selectedDeck = null;
			}
		});


		AbstractObservableWorker<List<MagicDeck>, MagicDeck, MTGDao> sw2 = new AbstractObservableWorker<>(buzy,MTG.getEnabledPlugin(MTGDao.class))
				{

					@Override
					protected List<MagicDeck> doInBackground() throws Exception {
						return plug.listDecks();
					}
					@Override
					protected void process(List<MagicDeck> chunks) {
						super.process(chunks);
						decksModel.addItems(chunks);
					}
					@Override
					protected void done() {
						super.done();

						table.packAll();
					}
				};

		ThreadManager.getInstance().runInEdt(sw2,"loading decks");


		table = UITools.createNewTable(decksModel);
				UITools.initTableFilter(table);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {

				if(UITools.getTableSelections(table, 0).isEmpty())
					return;

				selectedDeck = UITools.getTableSelection(table, 0);
				initTree();
				tagsPanel.clean();
				tagsPanel.addTags(selectedDeck.getTags());
				cmcChartPanel.init(selectedDeck.getMainAsList());
				cmcChartPanel.revalidate();
				cmcChartPanel.repaint();

				if (event.getClickCount() == 2) {
					dispose();
				}

			}

		});

		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		var panelBas = new JPanel();
		getContentPane().add(panelBas, BorderLayout.SOUTH);

		var btnSelect = new JButton(MTGConstants.ICON_OPEN);
		btnSelect.setToolTipText(capitalize("OPEN"));
		btnSelect.addActionListener(e -> {
			if (selectedDeck == null)
				MTGControler.getInstance().notify(new NullPointerException(capitalize("CHOOSE_DECK")));
			else
				dispose();
		});
		panelBas.add(btnSelect);

		var btnCancel = new JButton(MTGConstants.ICON_CANCEL);
		btnCancel.setToolTipText(capitalize("CANCEL"));
		btnCancel.addActionListener(e -> {
			selectedDeck = null;
			dispose();
		});

		panelBas.add(btnCancel);

		var btnDelete = new JButton(MTGConstants.ICON_DELETE);
		btnDelete.setToolTipText(capitalize("DELETE"));
		btnDelete.addActionListener(e -> {

			if(selectedDeck==null)
				return;

			int res = JOptionPane.showConfirmDialog(null,
					capitalize("CONFIRM_DELETE", selectedDeck.getName()),
					capitalize("CONFIRMATION") + " ?",
					JOptionPane.YES_NO_OPTION);

			if (res == JOptionPane.YES_OPTION) {
				try {
					manager.remove(selectedDeck);
					((DeckSelectionTableModel) table.getModel()).removeItem(selectedDeck);
				} catch (IOException e1) {
					MTGControler.getInstance().notify(e1);
				}
			}
		});

		panelBas.add(btnDelete);

		var panelRight = new JSplitPane();
		panelRight.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(panelRight, BorderLayout.EAST);

		cmcChartPanel = new CmcChartPanel();
		cmcChartPanel.setPreferredSize(new Dimension(250, 150));
		panelRight.setBottomComponent(cmcChartPanel);
		root = new DefaultMutableTreeNode("Cards");
		// add the child nodes to the root node
		root.add(creatureNode);
		root.add(artifactsNode);
		root.add(planeswalkerNode);
		root.add(landsNode);
		root.add(spellsNode);
		root.add(sideNode);
		model = new DefaultTreeModel(root);

		panelRight.setDividerLocation(300);

		var panelTree = new JPanel();
		panelTree.setLayout(new BorderLayout(0, 0));
		panelRight.setTopComponent(panelTree);
		tree = new JTree();
		tree.setModel(model);

		panelTree.add(new JScrollPane(tree));
		tagsPanel = new JTagsPanel();
		tagsPanel.setFontSize(11);
		tagsPanel.setColors(Color.DARK_GRAY, Color.WHITE);
		tagsPanel.setEditable(false);
		panelTree.add(tagsPanel, BorderLayout.SOUTH);

		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());

		panelBas.add(buzy);

		setLocationRelativeTo(null);
		setModal(true);

	}
}
