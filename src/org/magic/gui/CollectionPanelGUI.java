package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import org.magic.api.exports.impl.CSVExport;
import org.magic.api.interfaces.CardExporter;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import org.magic.gui.components.CardStockPanel;
import org.magic.gui.components.JSONPanel;
import org.magic.gui.components.LazyLoadingTree;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.components.dialog.MassCollectionImporterDialog;
import org.magic.gui.components.dialog.MassMoverDialog;
import org.magic.gui.components.dialog.PriceCatalogExportDialog;
import org.magic.gui.components.dialog.WebSiteGeneratorDialog;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.MagicCollectionTableCellRenderer;
import org.magic.gui.renderer.MagicCollectionTreeCellRenderer;
import org.magic.services.MTGControler;
import org.magic.services.MagicWebSiteGenerator;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class CollectionPanelGUI extends JPanel {

	private JXTable tableEditions;
	private TableFilterHeader filter;
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
	private JTabbedPane tabbedPane; 
	private TypeRepartitionPanel typeRepartitionPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private MagicCardDetailPanel magicCardDetailPanel;
	private HistoryPricesPanel historyPricesPanel;
	private CardStockPanel statsPanel;
	private JLabel lblTotal ;
	private JSONPanel jsonPanel;
	
	public CollectionPanelGUI() throws Exception {
		this.provider = MTGControler.getInstance().getEnabledProviders();
		this.dao = MTGControler.getInstance().getEnabledDAO();
		initGUI();
	}

	
	
	public void initGUI() throws Exception {
		
		logger.info("init collection GUI");
		setLayout(new BorderLayout(0, 0));
		model = new MagicEditionsTableModel();
		model.init(provider.loadEditions());
		lblTotal = new JLabel();
		
		lblTotal.setText("Total : " + model.getCountDefaultLibrary() +"/" + model.getCountTotal());
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

				ThreadManager.getInstance().execute(new Runnable() {
					
					@Override
					public void run() {
						progressBar.setVisible(true);
						tree.refresh();
						try {
							model.calculate();
							
						} catch (Exception e) {
						}
						model.fireTableDataChanged();
						progressBar.setVisible(false);
						
					}
				}, "update Tree");
				
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

		final JButton btnExportCSV = new JButton(new ImageIcon(CollectionPanelGUI.class.getResource("/res/export.png")));
						btnExportCSV.setToolTipText("Export as ");
						
		btnExportCSV.setEnabled(false);
		btnExportCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JPopupMenu menu = new JPopupMenu();
				
				for(final CardExporter exp : MTGControler.getInstance().getEnabledDeckExports())
				{
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							ThreadManager.getInstance().execute(new Runnable() {
								
								@Override
								public void run() {
									try {
										DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
										JFileChooser jf = new JFileChooser();
									
										MagicCollection mc=null;
										MagicEdition ed=null;
										
										if(curr.getUserObject() instanceof MagicEdition)
										{
											ed = (MagicEdition) curr.getUserObject();
											mc = (MagicCollection)((DefaultMutableTreeNode)curr.getParent()).getUserObject();
										}
										else
										{
											mc = (MagicCollection) curr.getUserObject();
										}
										
										jf.setSelectedFile(new File(mc.getName()+exp.getFileExtension()));
										int result = jf.showSaveDialog(null);
										File f = jf.getSelectedFile();
										
										if(result==JFileChooser.APPROVE_OPTION)
										
										if(ed==null)
											exp.export(dao.getCardsFromCollection(mc), f);
										else
											exp.export(dao.getCardsFromCollection(mc,ed), f);
										
										JOptionPane.showMessageDialog(null, "Export Finished", "Finished", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										e.printStackTrace();
										logger.error(e);
										JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
									}
									
								}
							}, "export collection with " + exp);
								
							
							
							
							
							
						}
					});
					
					menu.add(it);
				}
				
				Component b=(Component)ae.getSource();
		        Point p=b.getLocationOnScreen();
		        menu.show(b,0,0);
		        menu.setLocation(p.x,p.y+b.getHeight());
			}
		});

		
		JButton btnMassCollection = new JButton(new ImageIcon(CollectionPanelGUI.class.getResource("/res/import.png")));
		btnMassCollection.setToolTipText("Import collection");

		panneauHaut.add(btnMassCollection);
		panneauHaut.add(btnExportCSV);

		final JButton btnExportPriceCatalog = new JButton(new ImageIcon(CollectionPanelGUI.class.getResource("/res/euro.png")));
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
		MagicCollectionTableCellRenderer render = new MagicCollectionTableCellRenderer();
		DefaultRowSorter sorterEditions = new TableRowSorter<DefaultTableModel>(model);

		List<SortKey> keys = new ArrayList<SortKey>();
		SortKey sortKey = new SortKey(3, SortOrder.DESCENDING);//column index 2
		keys.add(sortKey);
		sorterEditions.setSortKeys(keys);
		
		
		JSplitPane splitListPanel = new JSplitPane();
		splitListPanel.setDividerLocation(0.5);
		splitListPanel.setResizeWeight(0.5);
		
		
		add(splitListPanel, BorderLayout.CENTER);

		JPanel panneauDroite = new JPanel();
		panneauDroite.setLayout(new BorderLayout());
		splitListPanel.setRightComponent(panneauDroite);

	
		modelPrices = new CardsPriceTableModel();
				
		final JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		panneauDroite.add(splitPane, BorderLayout.CENTER);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JScrollPane scrollPaneCollections = new JScrollPane();
		scrollPaneCollections.setMinimumSize(new Dimension(0, 0));
		splitPane.setLeftComponent(scrollPaneCollections);

		tree = new LazyLoadingTree();
		tree.setCellRenderer(new MagicCollectionTreeCellRenderer());
		scrollPaneCollections.setViewportView(tree);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(tabbedPane);


		JScrollPane scrollPrices = new JScrollPane();
		tablePrices = new JXTable(modelPrices);
		tablePrices.setColumnControlVisible(true);
		scrollPrices.setViewportView(tablePrices);

		
		
		magicCardDetailPanel = new MagicCardDetailPanel();
		magicCardDetailPanel.setPreferredSize(new Dimension(0, 0));
		magicCardDetailPanel.enableThumbnail(true);
		tabbedPane.addTab("Detail", null, magicCardDetailPanel, null);

		tabbedPane.addTab("Prices", null, scrollPrices, null);


		typeRepartitionPanel = new TypeRepartitionPanel();
		tabbedPane.addTab("Types", null, typeRepartitionPanel, null);

		manaRepartitionPanel = new ManaRepartitionPanel();
		tabbedPane.addTab("Mana", null, manaRepartitionPanel, null);

		rarityRepartitionPanel = new RarityRepartitionPanel();
		tabbedPane.addTab("Rarity", null, rarityRepartitionPanel, null);

		statsPanel = new CardStockPanel();
		tabbedPane.addTab("Stock", null, statsPanel, null);
		
		
		historyPricesPanel = new HistoryPricesPanel();
		tabbedPane.addTab("Variation", null, historyPricesPanel, null);
		
		
		jsonPanel = new JSONPanel();
		
		if(MTGControler.getInstance().get("debug-json-panel").equalsIgnoreCase("true"))
			tabbedPane.addTab("Json", null, jsonPanel, null);
				
				JPanel panneauGauche = new JPanel();
				splitListPanel.setLeftComponent(panneauGauche);
				panneauGauche.setLayout(new BorderLayout(0, 0));
		
				JScrollPane scrollPane = new JScrollPane();
				panneauGauche.add(scrollPane);
				
						tableEditions = new JXTable();
						filter = new TableFilterHeader(tableEditions, AutoChoices.ENABLED);
						
						tableEditions.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent arg0) {
								try {
									int row = tableEditions.getSelectedRow();
									MagicEdition ed = (MagicEdition)tableEditions.getValueAt(row, 1);
									historyPricesPanel.init(MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(null,ed),ed.getSet());
								} catch (IOException e) {
									logger.error(e);
								}
							}
						});
						tableEditions.setModel(model);
						
						tableEditions.setDefaultRenderer(Object.class,render);
						//tableEditions.setDefaultRenderer(ImageIcon.class,render);
						tableEditions.setDefaultRenderer(String.class,render);
						tableEditions.setDefaultRenderer(Integer.class, render);
						tableEditions.setDefaultRenderer(double.class, render);
						
						tableEditions.setRowHeight(25);
						
						tableEditions.setRowSorter(sorterEditions);
						tableEditions.packAll();
						
						
						
						scrollPane.setViewportView(tableEditions);
						
						JPanel panelTotal = new JPanel();
						panneauGauche.add(panelTotal, BorderLayout.SOUTH);
						
						
						panelTotal.add(lblTotal);
						
						splitPane.addComponentListener(new ComponentAdapter() {

						      @Override
						      public void componentShown(ComponentEvent componentEvent) {
						        splitPane.setDividerLocation(.5);
						        removeComponentListener(this);
						      }
						    });


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
				
				if(curr.getUserObject() instanceof String)
				{
					btnExportCSV.setEnabled(false);
					btnExportPriceCatalog.setEnabled(false);
					statsPanel.enabledAdd(false);
				}
				
				
				if (curr.getUserObject() instanceof MagicCollection) 
				{
					btnExportCSV.setEnabled(true);
					btnExportPriceCatalog.setEnabled(true);
					
					//too memory for big collection
					/*ThreadManager.getInstance().execute(new Runnable() {
						public void run() {
							try{
								List<MagicCard> list = dao.getCardsFromCollection(((MagicCollection)curr.getUserObject()));
								rarityRepartitionPanel.init(list);
								typeRepartitionPanel.init(list);
								manaRepartitionPanel.init(list);

							}catch(Exception e)
							{
								logger.error(e);

							}
						}
					},"addTreeSelectionListener init graph Collection");
					*/
					selectedcol = (MagicCollection) curr.getUserObject();
					statsPanel.enabledAdd(false);
					btnExportCSV.setEnabled(true);
					btnExportPriceCatalog.setEnabled(true);
				} 



				if(curr.getUserObject() instanceof MagicEdition)
				{

					btnExportCSV.setEnabled(true);
					btnExportPriceCatalog.setEnabled(false);
					statsPanel.enabledAdd(false);
					ThreadManager.getInstance().execute(new Runnable() {
						public void run() {
							try{

								MagicCollection c = (MagicCollection)((DefaultMutableTreeNode)curr.getParent()).getUserObject();
								List<MagicCard> list = dao.getCardsFromCollection(c,(MagicEdition)curr.getUserObject());
								rarityRepartitionPanel.init(list);
								typeRepartitionPanel.init(list);
								manaRepartitionPanel.init(list);
								historyPricesPanel.init(MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(null,(MagicEdition)curr.getUserObject()),curr.getUserObject().toString());


							}catch(Exception e)
							{
								//e.printStackTrace();
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
					jsonPanel.showCard((MagicCard)curr.getUserObject());
					
					ThreadManager.getInstance().execute(new Runnable() {
						
						@Override
						public void run() {
							statsPanel.initMagicCardStock(card,(MagicCollection)((DefaultMutableTreeNode)curr.getParent().getParent()).getUserObject() );
							statsPanel.enabledAdd(true);
							
							
						}
					}, "loading stock for " + curr.getUserObject() );
					
					
					
					
					if(tabbedPane.getSelectedIndex()==1)
					{ 
						loadPrices(card);
					}
					
					
					ThreadManager.getInstance().execute(new Runnable() {
						public void run() {
							try {
								historyPricesPanel.init(MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(card,null),card.getName());
							} catch (Exception e) {
								logger.error(e);
							}

						}
					},"addTreeSelectionListener init graph historyDashboard");
				}
			}
		});
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(tabbedPane.getSelectedIndex()==1)
					if(((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject() instanceof MagicCard)
						loadPrices((MagicCard)((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject() );
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
								MassMoverDialog d = new MassMoverDialog((MagicCollection)node.getUserObject(),null);
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

								MagicWebSiteGenerator gen = new MagicWebSiteGenerator( diag.getTemplate(),
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

				int res = JOptionPane.showConfirmDialog(null, "Are you sure you adding " + ed + " to "+MTGControler.getInstance().get("default-library")+" ?");

				if (res == JOptionPane.YES_OPTION)
					try {
						List<MagicCard> list = provider.searchCardByCriteria("set", ed.getId(),null);

						for (MagicCard mc : list) {
							MagicCollection col = new MagicCollection();
							col.setName(MTGControler.getInstance().get("default-library"));
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

	protected void loadPrices(final MagicCard card) {
		ThreadManager.getInstance().execute(new Runnable() {
			public void run() {
				try {
					modelPrices.init(card, card.getEditions().get(0));
					modelPrices.fireTableDataChanged();
				} catch (Exception e) {
					logger.error(e);
				}

			}
		},"addTreeSelectionListener init graph cards");
		
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

					final String destinationCollection = ((JMenuItem) e.getSource()).getText();
					ThreadManager.getInstance().execute(new Runnable() {

						@Override
						public void run() {
							try {
								DefaultMutableTreeNode node = ((DefaultMutableTreeNode) path.getPathComponent(2));
								MagicEdition me = (MagicEdition) node.getUserObject();

								MagicCollection mc = new MagicCollection();
								mc.setName(destinationCollection);
								List<MagicCard> sets = provider.searchCardByCriteria("set", me.getId(),null);
								
								MagicCollection sourceCol = new MagicCollection();
								sourceCol.setName(node.getPath()[1].toString());
								
								
								List<MagicCard> list = dao.getCardsFromCollection(sourceCol, me);
								sets.removeAll(list);
						
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
			
			/*
			JMenuItem it = new JMenuItem("Mass movement");
			it.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject();
					MassMoverDialog d = new MassMoverDialog(col,(MagicEdition)node.getUserObject());
					d.setVisible(true);
					logger.debug("closing mass import with change =" + d.hasChange());
				}
			});
			popupMenuEdition.add(it);
			*/
			
			
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
		MTGControler.getInstance().getEnabledDAO().init();
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new CollectionPanelGUI());
		f.pack();
		f.setVisible(true);
	}
	
}
