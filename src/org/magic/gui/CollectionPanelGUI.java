package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import org.magic.gui.components.LazyLoadingTree;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.MassCollectionImporterDialog;
import org.magic.gui.components.MassMoverDialog;
import org.magic.gui.components.PriceCatalogExportDialog;
import org.magic.gui.components.WebSiteGeneratorDialog;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.MagicCollectionTableCellRenderer;
import org.magic.gui.renderer.MagicCollectionTreeCellRenderer;
import org.magic.services.MagicFactory;
import org.magic.services.ThreadManager;
import org.magic.services.exports.CSVExport;
import org.magic.services.exports.MagicWebSiteGenerator;
import org.magic.tools.TableColumnAdjuster;

public class CollectionPanelGUI extends JPanel {

	private JTable tableEditions;
	private MagicCardsProvider provider;
	private MagicDAO dao;
	private LazyLoadingTree tree;
	private MagicEditionsTableModel model;
	private JProgressBar progressBar;
	private TreePath path;
	static final Logger logger = LogManager.getLogger(CollectionPanelGUI.class.getName());
	private JXTable tablePrices;
	private CardsPriceTableModel modelPrices;
	private MagicCollection selectedcol;

	private TypeRepartitionPanel typeRepartitionPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private MagicCardDetailPanel magicCardDetailPanel;
	private HistoryPricesPanel historyPricesPanel;
	
	public CollectionPanelGUI() throws Exception {
		this.provider = MagicFactory.getInstance().getEnabledProviders();
		this.dao = MagicFactory.getInstance().getEnabledDAO();
		
		initGUI();
	}

	
	
	public void initGUI() throws Exception {
		
		logger.debug("init collection panel");
		setLayout(new BorderLayout(0, 0));
		model = new MagicEditionsTableModel(dao);
		model.init(provider.searchSetByCriteria(null, null));
		JPanel panneauHaut = new JPanel();
		add(panneauHaut, BorderLayout.NORTH);

		
		JButton btnAdd = new JButton(new ImageIcon(CollectionPanelGUI.class.getResource("/res/new.png")));
		btnAdd.setToolTipText("Add a new collection");

		btnAdd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Name ?");
				MagicCollection mc = new MagicCollection();
				mc.setName(name);
				try {
					dao.saveCollection(mc);
					((LazyLoadingTree.MyNode)getJTree().getModel().getRoot()).add(new DefaultMutableTreeNode(mc));//todo recalculate
					getJTree().refresh();
					initPopupCollection();
				} catch (Exception ex) {
					logger.error(ex);
					JOptionPane.showMessageDialog(null, ex,"Error",JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		
		panneauHaut.add(btnAdd);
		
		JButton btnRefresh = new JButton(new ImageIcon(CollectionPanelGUI.class.getResource("/res/refresh.png")));
		btnRefresh.setToolTipText("Refresh collections");

		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			//	tree.init();
				tree.refresh();
				try {
					model.calculate();
					
				} catch (Exception e) {
				}
				model.fireTableDataChanged();
			}
		});
		panneauHaut.add(btnRefresh);

		JButton btnRemove = new JButton(new ImageIcon(CollectionPanelGUI.class.getResource("/res/delete.png")));
		btnRemove.setToolTipText("remove selected item");
		btnRemove.setEnabled(true);

		panneauHaut.add(btnRemove);

		JButton btnAddAllSet = new JButton(new ImageIcon(CollectionPanelGUI.class.getResource("/res/check.png")));
		btnAddAllSet.setToolTipText("Mark set as full");

		panneauHaut.add(btnAddAllSet);

		final JButton btnExportCSV = new JButton(new ImageIcon(CollectionPanelGUI.class.getResource("/res/xls.png")));
						btnExportCSV.setToolTipText("Export as CSV");
						
		btnExportCSV.setEnabled(false);
		btnExportCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
				MagicCollection mc = (MagicCollection) curr.getUserObject();
				
				JFileChooser jf = new JFileChooser();
				jf.setSelectedFile(new File(mc.getName()+".csv"));
				jf.showSaveDialog(null);
				File f = jf.getSelectedFile();
				
				if(f!=null)
				try {
					CSVExport exp = new CSVExport();
					exp.export(dao.getCardsFromCollection(mc), f);
					JOptionPane.showMessageDialog(null, "Export Finished", "Finished", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					logger.error(e);
					JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JButton btnMassCollection = new JButton(new ImageIcon(CollectionPanelGUI.class.getResource("/res/import.png")));
		btnMassCollection.setToolTipText("Import collection");

		panneauHaut.add(btnMassCollection);
		panneauHaut.add(btnExportCSV);

		final JButton btnExportPriceCatalog = new JButton(
				new ImageIcon(CollectionPanelGUI.class.getResource("/res/euro.png")));
		btnExportPriceCatalog.setToolTipText("Export prices catalog for collection");
		btnExportPriceCatalog.setEnabled(false);

		panneauHaut.add(btnExportPriceCatalog);

		JButton btnGenerateWebSite = new JButton(
				new ImageIcon(CollectionPanelGUI.class.getResource("/res/website.png")));
		btnGenerateWebSite.setToolTipText("Generate website");

		panneauHaut.add(btnGenerateWebSite);

		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		panneauHaut.add(progressBar);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(270, 23));

		tableEditions = new JTable();
		tableEditions.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					int row = tableEditions.getSelectedRow();
					MagicEdition ed = new MagicEdition();
					ed.setId(tableEditions.getValueAt(row, 0).toString());
					ed.setSet(tableEditions.getValueAt(row, 1).toString());
					historyPricesPanel.init(MagicFactory.getInstance().getEnabledDashBoard().getPriceVariation(null,ed),ed.getSet());
				} catch (IOException e) {
					logger.error(e);
				}
			}
		});
		tableEditions.setModel(model);
		MagicCollectionTableCellRenderer render = new MagicCollectionTableCellRenderer();
		
		tableEditions.setDefaultRenderer(Object.class,render);
		tableEditions.setDefaultRenderer(Integer.class, render);
		tableEditions.setDefaultRenderer(double.class, render);
		
		tableEditions.setRowHeight(25);
		DefaultRowSorter sorterEditions = new TableRowSorter<DefaultTableModel>(model);

		List<SortKey> keys = new ArrayList<SortKey>();
		SortKey sortKey = new SortKey(3, SortOrder.DESCENDING);//column index 2
		keys.add(sortKey);
		sorterEditions.setSortKeys(keys);
		
		tableEditions.setRowSorter(sorterEditions);
		new TableColumnAdjuster(tableEditions).adjustColumns();
		
		
		scrollPane.setViewportView(tableEditions);
		
		
		JSplitPane splitListPanel = new JSplitPane();
		add(splitListPanel, BorderLayout.CENTER);
		splitListPanel.setLeftComponent(scrollPane);

		JPanel panneauDroite = new JPanel();
		panneauDroite.setLayout(new BorderLayout());
		splitListPanel.setRightComponent(panneauDroite);

	
		modelPrices = new CardsPriceTableModel();
				
		JSplitPane splitPane = new JSplitPane();
		panneauDroite.add(splitPane, BorderLayout.CENTER);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JScrollPane scrollPaneCollections = new JScrollPane();
		scrollPaneCollections.setMinimumSize(new Dimension(23, 250));
		splitPane.setLeftComponent(scrollPaneCollections);

		tree = new LazyLoadingTree();
		tree.setCellRenderer(new MagicCollectionTreeCellRenderer());
		scrollPaneCollections.setViewportView(tree);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(tabbedPane);


		JScrollPane scrollPrices = new JScrollPane();
		tablePrices = new JXTable(modelPrices);
		tablePrices.setColumnControlVisible(true);
		scrollPrices.setViewportView(tablePrices);

		magicCardDetailPanel = new MagicCardDetailPanel();
		magicCardDetailPanel.enableThumbnail(true);
		tabbedPane.addTab("Detail", null, magicCardDetailPanel, null);

		tabbedPane.addTab("Prices", null, scrollPrices, null);


		typeRepartitionPanel = new TypeRepartitionPanel();
		tabbedPane.addTab("Types", null, typeRepartitionPanel, null);

		manaRepartitionPanel = new ManaRepartitionPanel();
		tabbedPane.addTab("Mana", null, manaRepartitionPanel, null);

		rarityRepartitionPanel = new RarityRepartitionPanel();
		tabbedPane.addTab("Rarity", null, rarityRepartitionPanel, null);

		historyPricesPanel = new HistoryPricesPanel();
		tabbedPane.addTab("Variation", null, historyPricesPanel, null);


		tablePrices.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2 && !ev.isConsumed()) {
					ev.consume();
					try {
						String url = tablePrices.getValueAt(tablePrices.getSelectedRow(), 6).toString();
						Desktop.getDesktop().browse(new URI(url));
					} catch (Exception e) {
						logger.error(e);
					}
				}

			}
		});

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				path = tse.getPath();

				final DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();

				logger.debug("click on " + curr );

				if (curr.getUserObject() instanceof MagicCollection) 
				{
					btnExportCSV.setEnabled(true);
					btnExportPriceCatalog.setEnabled(true);
					ThreadManager.getInstance().execute(new Runnable() {
						public void run() {
							try{
								rarityRepartitionPanel.init(dao.getCardsFromCollection(((MagicCollection)curr.getUserObject())));
								typeRepartitionPanel.init(dao.getCardsFromCollection(((MagicCollection)curr.getUserObject())));
								manaRepartitionPanel.init(dao.getCardsFromCollection(((MagicCollection)curr.getUserObject())));

							}catch(Exception e)
							{
								logger.error(e);

							}
						}
					},"addTreeSelectionListener init graph Collection");
					selectedcol = (MagicCollection) curr.getUserObject();
					btnExportCSV.setEnabled(true);
					btnExportPriceCatalog.setEnabled(true);
				} 



				if(curr.getUserObject() instanceof MagicEdition)
				{

					btnExportCSV.setEnabled(false);
					btnExportPriceCatalog.setEnabled(false);

					ThreadManager.getInstance().execute(new Runnable() {
						public void run() {
							try{

								MagicCollection c = (MagicCollection)((DefaultMutableTreeNode)curr.getParent()).getUserObject();

								rarityRepartitionPanel.init(dao.getCardsFromCollection(c,(MagicEdition)curr.getUserObject()));
								typeRepartitionPanel.init(dao.getCardsFromCollection(c,((MagicEdition)curr.getUserObject())));
								manaRepartitionPanel.init(dao.getCardsFromCollection(c,((MagicEdition)curr.getUserObject())));
								historyPricesPanel.init(MagicFactory.getInstance().getEnabledDashBoard().getPriceVariation(null,(MagicEdition)curr.getUserObject()),curr.getUserObject().toString());


							}catch(Exception e)
							{
								logger.error(e);

							}
						}
					},"addTreeSelectionListener init graph edition");
				}


				if (curr.getUserObject() instanceof MagicCard) {
					final MagicCard card = (MagicCard) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					btnExportCSV.setEnabled(false);
					btnExportPriceCatalog.setEnabled(false);

					magicCardDetailPanel.setMagicCard((MagicCard)curr.getUserObject());
					magicCardDetailPanel.enableThumbnail(true);

					ThreadManager.getInstance().execute(new Runnable() {
						public void run() {
							ImageIcon icon;
							try {
								modelPrices.init(card, card.getEditions().get(0));
								modelPrices.fireTableDataChanged();
							} catch (Exception e) {
								logger.error(e);
							}

						}
					},"addTreeSelectionListener init graph cards");

					ThreadManager.getInstance().execute(new Runnable() {
						public void run() {
							try {
								historyPricesPanel.init(MagicFactory.getInstance().getEnabledDashBoard().getPriceVariation(card,null),card.getName());
							} catch (Exception e) {
								logger.error(e);
							}

						}
					},"addTreeSelectionListener init graph historyDashboard");
				}
			}
		});

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int row = tree.getClosestRowForLocation(e.getX(), e.getY());
					tree.setSelectionRow(row);

					final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

					if (node.getUserObject() instanceof MagicEdition)
					{
						popupMenuEdition.show(e.getComponent(), e.getX(), e.getY());

					}
					if (node.getUserObject() instanceof MagicCard)
					{
						popupMenuCards.show(e.getComponent(), e.getX(), e.getY());
					}
					if (node.getUserObject() instanceof MagicCollection)
					{
						JPopupMenu p = new JPopupMenu();
						JMenuItem it = new JMenuItem("Mass movement");
						p.add(it);

						it.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								MassMoverDialog d = new MassMoverDialog((MagicCollection)node.getUserObject());
								d.setVisible(true);
								logger.debug("closing mass import with change =" + d.hasChange());
								/*if(d.hasChange())
																		tree.reload();
								 */
							}
						});
						p.show(e.getComponent(), e.getX(), e.getY());
					}


				}
			}
		});

		initPopupCollection();

		btnMassCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				new MassCollectionImporterDialog(dao, provider, model.getEditions()).setVisible(true);
				try {
					model.calculate();
				} catch (Exception e) {
				}
				model.fireTableDataChanged();
				//tree.init();
			}
		});

		btnExportPriceCatalog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				ThreadManager.getInstance().execute(new Runnable() {
					public void run() {
						try {
							PriceCatalogExportDialog diag = new PriceCatalogExportDialog(selectedcol);
							diag.setVisible(true);
							if (diag.value() == true) {
								progressBar.setVisible(true);
								progressBar.setStringPainted(true);
								progressBar.setMinimum(0);
								progressBar.setMaximum(dao.getCardsCount(selectedcol,null));
								CSVExport exp = new CSVExport();
								exp.addObserver(new Observer() {
									public void update(Observable o, Object arg) {
										progressBar.setValue((int) arg);
									}
								});
								exp.exportPriceCatalog(dao.getCardsFromCollection(selectedcol), diag.getDest(),
										diag.getPriceProviders());

								JOptionPane.showMessageDialog(null, "Catalog generated");
								progressBar.setVisible(false);
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				},"btnExportPriceCatalog export Prices");
			}
		});

		btnGenerateWebSite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				ThreadManager.getInstance().execute(new Runnable() {
					public void run() {
						try {

							WebSiteGeneratorDialog diag = new WebSiteGeneratorDialog(dao.getCollections());
							diag.setVisible(true);
							if (diag.value() == true) {
								progressBar.setVisible(true);
								progressBar.setStringPainted(true);
								progressBar.setMinimum(0);

								int max = 0;
								for (MagicCollection col : diag.getSelectedCollections())
									max += dao.getCardsCount(col,null);

								progressBar.setMaximum(max);
								progressBar.setValue(0);

								MagicWebSiteGenerator gen = new MagicWebSiteGenerator(dao, diag.getTemplate(),
										diag.getDest().getAbsolutePath());

								gen.addObserver(new Observer() {
									public void update(Observable o, Object arg) {
										progressBar.setValue((int) arg);
									}
								});
								gen.generate(diag.getSelectedCollections(), diag.getPriceProviders());

								int res = JOptionPane.showConfirmDialog(null, "website generate. Want to see it ? ");

								if (res == JOptionPane.YES_OPTION) {
									// URI uri = new
									// URI("file:///"+diag.getDest().getAbsolutePath()+"/index.htm");
									Path p = Paths.get(diag.getDest().getAbsolutePath() + "/index.htm");
									Desktop.getDesktop().browse(p.toUri());
								}
								progressBar.setVisible(false);
							}

						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
						}

					}
				},"btnGenerateWebSite generate website");

			}
		});

		btnAddAllSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MagicEdition ed = (MagicEdition) tableEditions.getValueAt(tableEditions.getSelectedRow(), 1);

				int res = JOptionPane.showConfirmDialog(null, "Are you sure you adding " + ed + " to Library ?");

				if (res == JOptionPane.YES_OPTION)
					try {
						List<MagicCard> list = provider.searchCardByCriteria("set", ed.getId(),null);

						for (MagicCard mc : list) {
							MagicCollection col = new MagicCollection();
							col.setName("Library");
							dao.saveCard(mc, col);
						}
						model.calculate();
						model.fireTableDataChanged();
					} catch (Exception e) {
						logger.error(e);
					}
			}
		});

		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
				int res = 0;

				DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
				if (curr.getUserObject() instanceof MagicCard) {
					MagicCard card = (MagicCard) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

					try {
						res = JOptionPane.showConfirmDialog(null,
								"Are you sure you wan't delete " + card + " from " + col + "?");
						if (res == JOptionPane.YES_OPTION) {
							dao.removeCard(card, col);
							curr.removeFromParent();
						}
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					}
				}
				if (curr.getUserObject() instanceof MagicEdition) {
					MagicEdition me = (MagicEdition) ((DefaultMutableTreeNode) path.getPathComponent(2))
							.getUserObject();

					try {
						res = JOptionPane.showConfirmDialog(null,
								"Are you sure you wan't delete " + me + " from " + col + "?");
						if (res == JOptionPane.YES_OPTION) {
							dao.removeEdition(me, col);
							curr.removeFromParent();
						}
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					}
				}
				if (curr.getUserObject() instanceof MagicCollection) {
					try {
						res = JOptionPane.showConfirmDialog(null,
								"Are you sure you wan't delete " + col + " ? (" + dao.getCardsCount(col,null) + " cards)");
						if (res == JOptionPane.YES_OPTION) {
							dao.removeCollection(col);
							curr.removeFromParent();
						}
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					}
				}

				if (res == JOptionPane.YES_OPTION) {
					try {
						model.calculate();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					}
					//model.fireTableDataChanged();
					tree.refresh();

				}
			}
		});

	}

	private JPopupMenu popupMenuEdition = new JPopupMenu();
	private JPopupMenu popupMenuCards = new JPopupMenu();
	
	
	public void initPopupCollection() throws Exception {
		
		popupMenuEdition = new JPopupMenu();
		popupMenuCards = new JPopupMenu();
	
		
		
		JMenu menuItemAdd = new JMenu("Add missing cards in ");
		JMenu menuItemMove = new JMenu("Move this card to ");
		
		for (MagicCollection mc : dao.getCollections()) {
			JMenuItem adds = new JMenuItem(mc.getName());
			JMenuItem movs = new JMenuItem(mc.getName());
			
			movs.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultMutableTreeNode nodeCol = ((DefaultMutableTreeNode) path.getPathComponent(1));
					DefaultMutableTreeNode nodeCd = ((DefaultMutableTreeNode) path.getPathComponent(3));
					MagicCard card = (MagicCard) nodeCd.getUserObject();
					MagicCollection oldCol = (MagicCollection)nodeCol.getUserObject();
					
					final String collec = ((JMenuItem) e.getSource()).getText();
					MagicCollection mc = new MagicCollection();
					mc.setName(collec);
					
					try {
						dao.removeCard(card, oldCol);
						dao.saveCard(card, mc);
						nodeCd.removeFromParent();
						nodeCol.add(new DefaultMutableTreeNode(card));
						
						tree.refresh();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			
			adds.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					final String collec = ((JMenuItem) e.getSource()).getText();
					ThreadManager.getInstance().execute(new Runnable() {

						@Override
						public void run() {
							try {
								DefaultMutableTreeNode node = ((DefaultMutableTreeNode) path.getPathComponent(2));
								MagicEdition me = (MagicEdition) node.getUserObject();

								MagicCollection mc = new MagicCollection();
								mc.setName(collec);
								List<MagicCard> sets = provider.searchCardByCriteria("set", me.getId(),null);
								for (int i = 0; i < node.getChildCount(); i++) {
									MagicCard c = (MagicCard) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
									sets.remove(c);
								}

								for (MagicCard m : sets)
									dao.saveCard(m, mc);

								tree.refresh();
							} catch (Exception e1) {
								e1.printStackTrace();
							}

						}
					},"btnAdds addCardsCollection");

				}
			});
			
			menuItemAdd.add(adds);
			menuItemMove.add(movs);
		}

		popupMenuEdition.add(menuItemAdd);
		popupMenuCards.add(menuItemMove);
	}

	public LazyLoadingTree getJTree() {
		return tree;
	}

	
	public static void main(String[] args) throws Exception {
		MagicFactory.getInstance().getEnabledDAO().init();
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new CollectionPanelGUI());
		f.pack();
		f.setVisible(true);
	}
	
}
