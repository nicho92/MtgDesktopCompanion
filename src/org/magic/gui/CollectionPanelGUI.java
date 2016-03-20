package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.db.MagicDAO;
import org.magic.gui.components.MagicCardsTree;
import org.magic.gui.components.MassCollectionImporterDialog;
import org.magic.gui.components.WebSiteGeneratorDialog;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.MagicCollectionTableCellRenderer;
import org.magic.gui.renderer.MagicCollectionTreeCellRenderer;
import org.magic.tools.MagicExporter;
import org.magic.tools.MagicFactory;
import org.magic.tools.MagicWebSiteGenerator;

public class CollectionPanelGUI extends JPanel {

	private JTable tableEditions;
	private MagicCardsProvider provider;
	private MagicDAO dao;
	private MagicCardsTree tree;
	private MagicEditionsTableModel model;
	private JProgressBar progressBar;
	private TreePath path;
	static final Logger logger = LogManager.getLogger(CollectionPanelGUI.class.getName());
	private JXTable tablePrices;
	private CardsPriceTableModel modelPrices;
	
	public CollectionPanelGUI(MagicCardsProvider provider,MagicDAO dao) throws Exception
	{
		this.provider=provider;
		this.dao=dao;
		initGUI();
	}
	
	
	public void add(MagicCollection coll, List<MagicCard> cards)
	{
		
	}
	
	
	public void initGUI() throws Exception {
		setLayout(new BorderLayout(0, 0));
		model = new MagicEditionsTableModel(dao,provider);
			model.init(provider.searchSetByCriteria(null, null));
		
		JPanel panneauHaut = new JPanel();
		add(panneauHaut, BorderLayout.NORTH);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tree.refresh();
				try {
					model.calculate();
				} catch (Exception e) {}
				model.fireTableDataChanged();
			}
		});
		panneauHaut.add(btnRefresh);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setEnabled(true);
		
		
		panneauHaut.add(btnRemove);
		
		JButton btnAddAllSet = new JButton("Mark set as full");
		
		
		
		panneauHaut.add(btnAddAllSet);
		
		final JButton btnExportCSV = new JButton("Export Collection");
		btnExportCSV.setEnabled(false);
		btnExportCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jf =new JFileChooser();
				jf.showSaveDialog(null);
				File f=jf.getSelectedFile();
				DefaultMutableTreeNode curr=(DefaultMutableTreeNode) path.getLastPathComponent();
				MagicCollection mc = (MagicCollection)curr.getUserObject();
				
				try {
					MagicExporter.exportCSV(dao.getCardsFromCollection(mc), f);
					JOptionPane.showMessageDialog(null, "Export Finished","Finished",JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					logger.error(e);
					JOptionPane.showMessageDialog(null, e,"Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JButton btnMassCollection = new JButton("Mass Import");
		
		panneauHaut.add(btnMassCollection);
		panneauHaut.add(btnExportCSV);
		
		JButton btnExportPriceCatalog = new JButton("Export Price Catalog");
		btnExportPriceCatalog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				MagicCollection c = new MagicCollection();
				c.setName("Needed");
				try {
					MagicExporter.exportPriceCatalog(dao.getCardsFromCollection(c), new File("test.csv"), MagicFactory.getInstance().getEnabledPricers().get(0));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		panneauHaut.add(btnExportPriceCatalog);
		
		JButton btnGenerateWebSite = new JButton("Generate website");
		
		panneauHaut.add(btnGenerateWebSite);
		
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		panneauHaut.add(progressBar);
		
		
		
		JScrollPane scrollPane = new JScrollPane();
		
		tableEditions = new JTable();
		tableEditions.setModel(model);
		tableEditions.setDefaultRenderer(Object.class,new MagicCollectionTableCellRenderer());
		DefaultRowSorter sorterEditions = new TableRowSorter<DefaultTableModel>(model);
		
		tableEditions.setRowSorter(sorterEditions);
		
		scrollPane.setViewportView(tableEditions);
		
		
		
		JSplitPane splitListPanel = new JSplitPane();
		add(splitListPanel, BorderLayout.CENTER);
		splitListPanel.setLeftComponent(scrollPane);
		
		JPanel panneauDroite = new JPanel();
		panneauDroite.setLayout(new BorderLayout(0, 0));
		splitListPanel.setRightComponent(panneauDroite);
		
		JPanel panneauTreeSearch = new JPanel();
		panneauDroite.add(panneauTreeSearch, BorderLayout.CENTER);
		panneauTreeSearch.setLayout(new BorderLayout(0, 0));
		
		
		JScrollPane scrollPaneCollections = new JScrollPane();
		panneauTreeSearch.add(scrollPaneCollections);
		
		tree = new MagicCardsTree(provider,dao);
		tree.setCellRenderer(new MagicCollectionTreeCellRenderer()); 
		scrollPaneCollections.setViewportView(tree);
		 
		 JPanel panneauBas = new JPanel();
		 panneauTreeSearch.add(panneauBas, BorderLayout.SOUTH);
		 panneauBas.setLayout(new BorderLayout(0, 0));
		 
		 final JLabel lblCard = new JLabel("");
		 lblCard.setPreferredSize(new Dimension(250, 0));
		 panneauBas.add(lblCard, BorderLayout.WEST);
		 
		 JScrollPane scrollPrices = new JScrollPane();
		 panneauBas.add(scrollPrices);
		 
		 modelPrices=new CardsPriceTableModel();
		 tablePrices = new JXTable(modelPrices);
		 tablePrices.setColumnControlVisible(true);
		 scrollPrices.setViewportView(tablePrices);

		 initPopupCollection();
		
		 
		MagicCollection selectedcol =null; 
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				path = tse.getPath();
				
				DefaultMutableTreeNode curr=(DefaultMutableTreeNode) path.getLastPathComponent();
				
				if(curr.getUserObject() instanceof MagicCollection)
				{
					btnExportCSV.setEnabled(true);
				}
				else
				{
					btnExportCSV.setEnabled(false);
				}
				
				if(curr.getUserObject() instanceof MagicCard)
				{	
					final MagicCard card = (MagicCard)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							ImageIcon icon;
							try {
								icon = new ImageIcon(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+card.getEditions().get(0).getMultiverse_id()+"&type=card"));
								lblCard.setIcon(icon);
								modelPrices.init(card, card.getEditions().get(0));
								modelPrices.fireTableDataChanged();
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}).start();
				}
			}
		});
		
				
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e))
				{
					int row = tree.getClosestRowForLocation(e.getX(), e.getY());
		            tree.setSelectionRow(row);
					try{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
						if(node.getUserObject() instanceof MagicEdition)
							popupMenu.show(e.getComponent(), e.getX(), e.getY());
					
					}
					catch(Exception ex){}
				}
			}
		});
		
		btnMassCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				new MassCollectionImporterDialog(dao,provider,model.getEditions()).setVisible(true);
				try {
					model.calculate();
				} catch (Exception e) {}
				model.fireTableDataChanged();
				tree.init();
			}
		});
		
		btnGenerateWebSite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				new Thread(new Runnable() {
					public void run() {
						try {
							
							WebSiteGeneratorDialog diag = new WebSiteGeneratorDialog(dao.getCollections());
												   diag.setVisible(true);
							if(diag.value()==true)
							{	
								progressBar.setVisible(true);
								progressBar.setStringPainted(true);
								progressBar.setMinimum(0);
								
								int max = 0;
								for(MagicCollection col : diag.getSelectedCollections())
									max+=dao.getCardsCount(col);
								
								progressBar.setMaximum(max);
								progressBar.setValue(0);
								   
								MagicWebSiteGenerator gen = new MagicWebSiteGenerator(dao, diag.getTemplate(), diag.getDest().getAbsolutePath());
								
								gen.addObserver(new Observer() {
									public void update(Observable o, Object arg) {
										progressBar.setValue((int)arg);
									}
								});				  
								gen.generate(diag.getSelectedCollections(),diag.getPriceProviders());
												  
												  
								int res= JOptionPane.showConfirmDialog(null, "website generate. Want to see it ? ");
								
								if(res==JOptionPane.YES_OPTION)
								{
									//URI uri = new URI("file:///"+diag.getDest().getAbsolutePath()+"/index.htm");
									Path p = Paths.get(diag.getDest().getAbsolutePath()+"/index.htm");
									Desktop.getDesktop().browse(p.toUri());
								}
								progressBar.setVisible(false);
							}
							
							
						} 
						catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, e,"Error",JOptionPane.ERROR_MESSAGE);
						}
						
					}
				}).start();
				
				
			}
		});
		
		btnAddAllSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
					MagicEdition ed = (MagicEdition) tableEditions.getValueAt(tableEditions.getSelectedRow(), 1);
					
					int res = JOptionPane.showConfirmDialog(null,"Are you sure you adding " + ed +" to Library ?");
					
					if(res==JOptionPane.YES_OPTION)
					try {
						List<MagicCard> list = provider.searchCardByCriteria("set", ed.getId());
						
						for(MagicCard mc : list)
						{
							MagicCollection col = new MagicCollection();
							col.setName("Library");
							dao.saveCard(mc, col);
						}
						model.calculate();
						model.fireTableDataChanged();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		
		tablePrices.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				if(ev.getClickCount()==2 && !ev.isConsumed())
				{
					ev.consume();
					try {
						String url = tablePrices.getValueAt(tablePrices.getSelectedRow(), 4).toString();
						Desktop.getDesktop().browse(new URI(url));
					} catch (Exception e) {
						logger.error(e);
					}
				}

			}
		});

		
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode)path.getPathComponent(1)).getUserObject();
				int res=0;
				
				DefaultMutableTreeNode curr=(DefaultMutableTreeNode) path.getLastPathComponent();
				if(curr.getUserObject() instanceof MagicCard)
				{	
					MagicCard card = (MagicCard)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
					
					try {
						res = JOptionPane.showConfirmDialog(null,"Are you sure you wan't delete " + card +" from " + col + "?");
						if(res==JOptionPane.YES_OPTION)
						{	dao.removeCard(card, col);
							curr.removeFromParent();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(curr.getUserObject() instanceof MagicEdition)
				{
					MagicEdition me = (MagicEdition)((DefaultMutableTreeNode)path.getPathComponent(2)).getUserObject();
					
					try {
						res = JOptionPane.showConfirmDialog(null,"Are you sure you wan't delete " + me +" from " + col + "?");
						if(res==JOptionPane.YES_OPTION)
						{
							dao.removeEdition(me, col);
							curr.removeFromParent();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(curr.getUserObject() instanceof MagicCollection)
				{
					try {
						res = JOptionPane.showConfirmDialog(null,"Are you sure you wan't delete " + col +" ? (" + dao.getCardsCount(col) +" cards)");
						if(res==JOptionPane.YES_OPTION)
						{
							dao.removeCollection(col);
							curr.removeFromParent();
						}
					} 
					catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
				if(res==JOptionPane.YES_OPTION)
				{
					try {
						model.calculate();
					} catch (Exception e) {
						e.printStackTrace();
					}
					model.fireTableDataChanged();
					tree.refresh();
					
				}
			}
		});
		
	}
	
	
	private JPopupMenu popupMenu = new JPopupMenu();
	
	public void initPopupCollection() throws Exception
	{
		JMenu menuItemAdd = new JMenu("Add missing cards in ");

		for(MagicCollection mc : dao.getCollections())
		{
			JMenuItem adds = new JMenuItem(mc.getName());
			adds.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					final String collec = ((JMenuItem)e.getSource()).getText();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							try {
								DefaultMutableTreeNode node = ((DefaultMutableTreeNode)path.getPathComponent(2));
								MagicEdition me = (MagicEdition)node.getUserObject();
								
								MagicCollection mc = new MagicCollection();
									mc.setName(collec);
								List<MagicCard> sets = provider.searchCardByCriteria("set", me.getId());
								 for(int i = 0 ; i < node.getChildCount() ; i++)
								 {
								        MagicCard c = (MagicCard)((DefaultMutableTreeNode)node.getChildAt(i)).getUserObject();
								        sets.remove(c);
								 }
								
								 for(MagicCard m : sets)
									 dao.saveCard(m, mc);
								 
								 tree.refresh();
							} catch (Exception e1) {
								e1.printStackTrace();
							}	
							
						}
					}).start();
					
					
				}
			});
			menuItemAdd.add(adds);
		}

		popupMenu.add(menuItemAdd);
	}
	
	
	public void setProvider(MagicCardsProvider provider)
	{
		this.provider=provider;
	}
	
	public MagicCardsTree getJTree()
	{
		return tree;
	}

}
