package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.CardExporter;
import org.magic.gui.components.CardsPicPanel;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.components.ManaPanel;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.ThumbnailPanel;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.BoosterPicturesProvider;
import org.magic.services.MTGDesktopCompanionControler;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;


public class CardSearchGUI extends JPanel {

		static final Logger logger = LogManager.getLogger(MagicGUI.class.getName());

		private static final int INDEX_PRICES = 2;

		private MagicCard selected;
		private MagicEdition selectedEdition;
		
		private CardsPriceTableModel priceModel;
		private MagicCardTableModel cardsModeltable;

		private JTabbedPane tabbedCardsView;
		private JTabbedPane tabbedCardsInfo ;
		
		
		private ThumbnailPanel thumbnailPanel;
		private ManaRepartitionPanel manaRepartitionPanel;
		private TypeRepartitionPanel typeRepartitionPanel;
		private RarityRepartitionPanel rarityRepartitionPanel;
		private CmcChartPanel cmcChart;
		private CardsPicPanel cardsPicPanel;
		private HistoryPricesPanel historyChartPanel;
		private MagicEditionDetailPanel magicEditionDetailPanel;
		private MagicCardDetailPanel detailCardPanel;
		private JPanel boosterPanel;
		private JPanel panelResultsCards;
		private JPanel panelFilters;
	    private JPanel panelmana;
		private JPanel editionDetailPanel;
		private JPanel panneauHaut;
		private JPanel panneauCard = new JPanel();
		private JPanel panneauStat;
		private JTextArea txtRulesArea;
		private JTextField txtFilter;
		private JTextField txtMagicSearch;
		
		private JPopupMenu popupMenu = new JPopupMenu();
	    
		private JComboBox<MagicEdition> cboEdition;
	    private JComboBox<MagicCardNames> cboLanguages;
		private JComboBox<String> cboQuereableItems;
		private JComboBox<MagicCollection> cboCollections;
		
		private JXTable tableCards;
		private JXTable tablePrice;
	    private DefaultRowSorter<DefaultTableModel, Integer> sorterCards ;
	    private TableFilterHeader filterHeader;

	    private JButton btnClear;
		private JButton btnGenerateBooster;
		private JButton btnSearch;
		private JButton btnExport;
		private JButton btnFilter;
		
		private List<MagicCard> cards;
		private JList<MagicEdition> listEdition;
		
		private JLabel lblBoosterPic;
		private JLabel lblLoading;
		
		private BoosterPicturesProvider boosterProvider;
		
		public void loading(boolean show,String text)
		{
			lblLoading.setText(text);
			lblLoading.setVisible(show);
		}

		public void setLookAndFeel(String lookAndFeel)
		{
			try {
				UIManager.put("Table.alternateRowColor", Color.decode("#E1E4F2"));
				UIManager.setLookAndFeel(lookAndFeel);
				MTGDesktopCompanionControler.getInstance().setProperty("lookAndFeel", lookAndFeel);
				SwingUtilities.updateComponentTreeUI(this);
				
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			
		}

		public void initPopupCollection() throws Exception
		{
			JMenu menuItemAdd = new JMenu("Add");

			for(MagicCollection mc : MTGDesktopCompanionControler.getInstance().getEnabledDAO().getCollections())
			{
				
				JMenuItem adds = new JMenuItem(mc.getName());
				adds.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						String collec = ((JMenuItem)e.getSource()).getText();
						loading(true, "add cards to " + collec); 

						for (int i = 0; i < tableCards.getSelectedRowCount(); i++) { 
							
							
							int viewRow = tableCards.getSelectedRows()[i];
							int modelRow = tableCards.convertRowIndexToModel(viewRow);
							
							
							MagicCard mc = (MagicCard)tableCards.getModel().getValueAt(modelRow, 0);
							try {
								MTGDesktopCompanionControler.getInstance().getEnabledDAO().saveCard(mc, MTGDesktopCompanionControler.getInstance().getEnabledDAO().getCollection(collec));
							} catch (SQLException e1) {
								logger.error(e1);
								e1.printStackTrace();
								JOptionPane.showMessageDialog(null, e1,"ERROR",JOptionPane.ERROR_MESSAGE);
							}

						}
						loading(false, "");
					}
				});
				menuItemAdd.add(adds);
			}

			popupMenu.add(menuItemAdd);
		}

		public void initGUI() throws Exception
		{
			logger.debug("init search GUI");

			DefaultRowSorter<DefaultTableModel, Integer> sorterPrice = new TableRowSorter<DefaultTableModel>(priceModel);
			sorterCards = new TableRowSorter<DefaultTableModel>(cardsModeltable);
			sorterCards.setComparator(7, new Comparator<String>() {
			   public int compare(String num1, String num2) {
			        	try{
			        		num1=num1.replaceAll("a","").replaceAll("b", "").trim();
			        		num2=num2.replaceAll("a","").replaceAll("b", "").trim();
				   			if(Integer.parseInt(num1)>Integer.parseInt(num2))
				   				return 1;
				   			else
				   				return -1;
						}
						catch(NumberFormatException e)
						{
							return -1;
						}
			    }
			});

			List<MagicEdition> li = MTGDesktopCompanionControler.getInstance().getEnabledProviders().loadEditions();
			Collections.sort(li);
		
			
			
////////INIT COMPONENTS
			JScrollPane scrollEditions = new JScrollPane();
			JScrollPane scrollThumbnails = new JScrollPane();
			JScrollPane scrollPaneRules = new JScrollPane();
			JScrollPane scrollPanePrices = new JScrollPane();
			JScrollPane scrollCards = new JScrollPane();
			JSplitPane panneauCentral = new JSplitPane();
			panneauStat = new JPanel();
			panneauHaut = new JPanel();
			panneauCard = new JPanel();
			boosterPanel = new JPanel();
			editionDetailPanel = new JPanel();
			panelResultsCards = new JPanel();
			cmcChart = new CmcChartPanel();
			manaRepartitionPanel = new ManaRepartitionPanel();
			typeRepartitionPanel = new TypeRepartitionPanel();
			historyChartPanel = new HistoryPricesPanel();
			cardsPicPanel = new CardsPicPanel();
			rarityRepartitionPanel = new RarityRepartitionPanel();
			detailCardPanel = new MagicCardDetailPanel(new MagicCard());
			magicEditionDetailPanel = new MagicEditionDetailPanel();
			panelmana = new JPanel();
			panelFilters = new JPanel();
			ManaPanel pan = new ManaPanel();
			
			tabbedCardsView = new JTabbedPane(JTabbedPane.TOP);
			tabbedCardsInfo = new JTabbedPane(JTabbedPane.TOP);
			thumbnailPanel = new ThumbnailPanel();
			
			
			
			btnSearch = new JButton(new ImageIcon(MagicGUI.class.getResource("/res/search.png")));
			btnExport = new JButton(new ImageIcon(MagicGUI.class.getResource("/res/export.png")));
			btnFilter = new JButton(new ImageIcon(MagicGUI.class.getResource("/res/filter.png")));
			btnClear = new JButton(new ImageIcon(MagicGUI.class.getResource("/res/09_clear_location.png")));
			btnGenerateBooster = new JButton("Open a Booster");
			
			cboQuereableItems = new JComboBox<String>(MTGDesktopCompanionControler.getInstance().getEnabledProviders().getQueryableAttributs());
			cboCollections= new JComboBox<MagicCollection>(MTGDesktopCompanionControler.getInstance().getEnabledDAO().getCollections().toArray(new MagicCollection[MTGDesktopCompanionControler.getInstance().getEnabledDAO().getCollections().size()]));
			cboLanguages = new JComboBox<MagicCardNames>();
			
			tablePrice = new JXTable();
			tableCards = new JXTable();
			
			lblBoosterPic = new JLabel();
			lblLoading = new JLabel(new ImageIcon(MagicGUI.class.getResource("/res/load.gif")));
			JLabel lblFilter = new JLabel();
			
			listEdition = new JList<MagicEdition>();
			txtMagicSearch = new JTextField();
			txtRulesArea = new JTextArea();
			txtFilter = new JTextField();

			filterHeader = new TableFilterHeader(tableCards, AutoChoices.ENABLED);
			
			cboEdition = new JComboBox<MagicEdition>(li.toArray(new MagicEdition[li.size()]));
			
			
			
/////////CONFIGURE COMPONENTS			
			txtRulesArea.setLineWrap(true);
			txtRulesArea.setWrapStyleWord(true);
			txtRulesArea.setEditable(false);
			btnFilter.setToolTipText("Filter result");
			btnExport.setToolTipText("Export Result");
			btnExport.setEnabled(false);
			filterHeader.setSelectionBackground(Color.LIGHT_GRAY);
			cboQuereableItems.addItem("collections");
			tablePrice.setRowSorter(sorterPrice);
			listEdition.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			thumbnailPanel.enableDragging(false);
			panneauCentral.setOrientation(JSplitPane.VERTICAL_SPLIT);
			panneauCentral.setRightComponent(tabbedCardsInfo);
			panneauCentral.setLeftComponent(tabbedCardsView);
			tableCards.setRowHeight(ManaPanel.row_height);
			tableCards.setRowSorter(sorterCards);

///////LAYOUT		
			setLayout(new BorderLayout());
			panneauStat.setLayout(new GridLayout(2, 2, 0, 0));
			panneauCard.setLayout(new BorderLayout());
			editionDetailPanel.setLayout(new BorderLayout());
			boosterPanel.setLayout(new BorderLayout());
			panelResultsCards.setLayout(new BorderLayout(0, 0));
			panelmana.setLayout(new GridLayout(1, 0, 2, 2));
			
			FlowLayout fl_panelFilters = (FlowLayout) panelFilters.getLayout();
			fl_panelFilters.setAlignment(FlowLayout.LEFT);
			
			FlowLayout flowLayout = (FlowLayout) panneauHaut.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);

	
			
		
////////MODELS
			
			listEdition.setModel(new DefaultListModel<MagicEdition>());
			tablePrice.setModel(priceModel);
			tableCards.setModel(cardsModeltable);

///////DIMENSION	
			thumbnailPanel.setThumbnailSize(179, 240);
			tabbedCardsInfo.setPreferredSize(new Dimension(0, 350));
			historyChartPanel.setPreferredSize(new Dimension(400, 10));
			cardsPicPanel.setPreferredSize(new Dimension(400, 10));
			tabbedCardsInfo.setMinimumSize(new Dimension(23,200));
			scrollCards.setMinimumSize(new Dimension(0, 0));
			scrollThumbnails.getVerticalScrollBar().setUnitIncrement(10);
			txtFilter.setColumns(25);
			txtMagicSearch.setColumns(35);

		
///////VISIBILITY
			tableCards.setColumnControlVisible(true);
			filterHeader.setVisible(false);
			panelFilters.setVisible(false);
			lblLoading.setVisible(false);
			cboCollections.setVisible(false);
			tableCards.setShowVerticalLines(false);
			cboEdition.setVisible(false);
			
			
//////ADD PANELS	
			
			
			for(String s : new String[]{"W","U","B","R","G","C","1"})
			{
				final JButton btnG = new JButton();
				btnG.setToolTipText(s);
				if(s.equals("1"))
					btnG.setToolTipText("[0-9]*");

				btnG.setIcon(new ImageIcon(pan.getManaSymbol(s).getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
				btnG.setForeground(btnG.getBackground());
				btnG.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFilter.setText("\\{" + btnG.getToolTipText()+"}");
						sorterCards.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
						
					}
				});
				panelmana.add(btnG);
					
			}
			scrollEditions.setViewportView(listEdition);							
			scrollPanePrices.setViewportView(tablePrice);
			scrollCards.setViewportView(tableCards);
			scrollPaneRules.setViewportView(txtRulesArea);
			scrollThumbnails.setViewportView(thumbnailPanel);
	
			
			panneauHaut.add(cboQuereableItems);
			panneauHaut.add(cboCollections);
			panneauHaut.add(txtMagicSearch);
			panneauHaut.add(cboEdition);
			panneauHaut.add(btnSearch);
			panneauHaut.add(btnFilter);
			panneauHaut.add(btnExport);
			panneauHaut.add(lblLoading);

			
			panneauCard.add(cboLanguages, BorderLayout.NORTH);
			panneauCard.add(scrollEditions, BorderLayout.SOUTH);
			panneauCard.add(cardsPicPanel, BorderLayout.CENTER);
	
			boosterPanel.add(btnGenerateBooster, BorderLayout.NORTH);
			boosterPanel.add(lblBoosterPic);
			
			panelResultsCards.add(panelFilters, BorderLayout.NORTH);
			panelResultsCards.add(scrollCards);
		
			
			editionDetailPanel.add(magicEditionDetailPanel, BorderLayout.CENTER);
			editionDetailPanel.add(boosterPanel, BorderLayout.EAST);
		
			
			panelFilters.add(lblFilter);
			panelFilters.add(txtFilter);
			panelFilters.add(btnClear);
			panelFilters.add(panelmana);
	

			tabbedCardsInfo.addTab("Details", null, detailCardPanel, null);
			tabbedCardsInfo.addTab("Edition", null, editionDetailPanel, null);
			tabbedCardsInfo.addTab("Prices", null, scrollPanePrices, null);
			tabbedCardsInfo.addTab("Rules", null, scrollPaneRules, null);
			tabbedCardsInfo.addTab("Variation", null, historyChartPanel, null);

			
			panneauStat.add(cmcChart);
			panneauStat.add(manaRepartitionPanel);
			panneauStat.add(typeRepartitionPanel);
			panneauStat.add(rarityRepartitionPanel);
			
			tabbedCardsView.addTab("Results", null, panelResultsCards, null);
			tabbedCardsView.addTab("Thumbnail", null, scrollThumbnails, null);
			tabbedCardsView.addTab("Stats", null, panneauStat , null);

			
			add(panneauHaut, BorderLayout.NORTH);
			add(panneauCard, BorderLayout.EAST);
			add(panneauCentral, BorderLayout.CENTER);
	
			
			
///////Right click			
			initPopupCollection();

			
			
///////Action listners 
			
			cboEdition.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					txtMagicSearch.setText(((MagicEdition)cboEdition.getSelectedItem()).getId());
				}
			});
			
			
			btnClear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					txtFilter.setText("");
					sorterCards.setRowFilter(null);
				}
			});
			
			btnFilter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(panelFilters.isVisible())
					{
						panelFilters.setVisible(false);
						filterHeader.setVisible(false);
					}
					else
					{
						panelFilters.setVisible(true);
						filterHeader.setVisible(true);
					}
				}
			});
			
			cboQuereableItems.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(cboQuereableItems.getSelectedItem().toString().equalsIgnoreCase("set"))
					{
						txtMagicSearch.setVisible(false);
						cboEdition.setVisible(true);
						cboCollections.setVisible(false);
					}
					else if(cboQuereableItems.getSelectedItem().toString().equalsIgnoreCase("collections"))
					{
						txtMagicSearch.setVisible(false);
						cboEdition.setVisible(false);
						cboCollections.setVisible(true);
					}
					else
					{
						txtMagicSearch.setVisible(true);
						cboEdition.setVisible(false);
						cboCollections.setVisible(false);
					}
				}
			});
		
			btnSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					selectedEdition=null;
					if(txtMagicSearch.getText().equals("") && !cboCollections.isVisible())
						return;
					
					Runnable r = new Runnable() {
						public void run() {
							loading(true,"searching");
							try {
								String searchName=txtMagicSearch.getText();
								
								if(cboCollections.isVisible())
									cards = MTGDesktopCompanionControler.getInstance().getEnabledDAO().getCardsFromCollection((MagicCollection)cboCollections.getSelectedItem());
								else
									cards = MTGDesktopCompanionControler.getInstance().getEnabledProviders().searchCardByCriteria(cboQuereableItems.getSelectedItem().toString(),searchName,null);
								
								
								
								cardsModeltable.init(cards);
								tableCards.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
								
								cardsModeltable.fireTableDataChanged();
								
								thumbnailPanel.initThumbnails(cards,false);
								
								cmcChart.init(cards);
								typeRepartitionPanel.init(cards);
								manaRepartitionPanel.init(cards);
								rarityRepartitionPanel.init(cards);
								tabbedCardsView.setTitleAt(0, "Results ("+cardsModeltable.getRowCount()+")");
								
								btnExport.setEnabled(tableCards.getRowCount()>0);

							} catch (Exception e) {
								e.printStackTrace();
								JOptionPane.showMessageDialog(null, e.getMessage(),"ERREUR",JOptionPane.ERROR_MESSAGE);
							}
							loading(false,"");
						}
					};
					
					ThreadManager.getInstance().execute(r,"SearchCards");
				}
			});

			btnGenerateBooster.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					if(selectedEdition==null)
						selectedEdition = selected.getEditions().get(0);

					try {
						tabbedCardsView.setSelectedIndex(1);
						thumbnailPanel.initThumbnails( MTGDesktopCompanionControler.getInstance().getEnabledProviders().openBooster(selectedEdition),false);

					} catch (Exception e) {
						logger.error(e);
					}
				}
			});

			tableCards.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent evt) {

					if(SwingUtilities.isRightMouseButton(evt))
					{
						Point point = evt.getPoint();
						popupMenu.show(tableCards, (int)point.getX(), (int)point.getY());
					}
					else
					{
						selected = (MagicCard)tableCards.getValueAt(tableCards.getSelectedRow(), 0);
						selectedEdition = selected.getEditions().get(0);
						updateCards();

					}
				}
			});

			listEdition.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent mev) {
						selectedEdition = listEdition.getSelectedValue();
						detailCardPanel.setMagicLogo(selectedEdition.getId(),""+selectedEdition.getRarity());
						
						magicEditionDetailPanel.setMagicEdition(selectedEdition);
						try {
							logger.debug("LOADING ED " + BeanUtils.describe(selectedEdition));
						} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
							logger.error(e1);
						}
						
						ThreadManager.getInstance().execute(new Runnable() {
							public void run() {
								try {
									loading(true,"loading edition");
									
										cardsPicPanel.showPhoto(selected,selectedEdition);//backcard
										lblBoosterPic.setIcon(boosterProvider.getBoosterFor(selectedEdition));
										magicEditionDetailPanel.setMagicEdition(selectedEdition);
										
										historyChartPanel.init(MTGDesktopCompanionControler.getInstance().getEnabledDashBoard().getPriceVariation(selected, selectedEdition),selected.getName());
										
										
										if(tabbedCardsInfo.getSelectedIndex()==INDEX_PRICES)
											updatePrices();
									loading(false,"");
								} catch (IOException e) {
									logger.error(e);
								}
							}
						},"changeEdition");
				}
			});

			tablePrice.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent ev) {
					if(ev.getClickCount()==2 && !ev.isConsumed())
					{
						ev.consume();
						try {
							String url = tablePrice.getValueAt(tablePrice.getSelectedRow(), 6).toString();
							Desktop.getDesktop().browse(new URI(url));
						} catch (Exception e) {
							logger.error(e);
						}

					}

				}
			});

			cboLanguages.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MagicCardNames selLang = (MagicCardNames)cboLanguages.getSelectedItem();
					try {
						if(selLang!=null)
						{
							MagicEdition ed = new MagicEdition();
								ed.setMultiverse_id(""+selLang.getGathererId());
								ed.setId(selectedEdition.getId());
							cardsPicPanel.showPhoto(selected,ed);
						}
					} catch (Exception e1) {}
				}

			});

			btnExport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JPopupMenu menu = new JPopupMenu();
					
					for(final CardExporter exp : MTGDesktopCompanionControler.getInstance().getEnabledDeckExports())
					{
						JMenuItem it = new JMenuItem();
						it.setIcon(exp.getIcon());
						it.setText(exp.getName());
						it.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								JFileChooser jf =new JFileChooser(".");
								jf.setSelectedFile(new File("search"+exp.getFileExtension()));
								int result = jf.showSaveDialog(null);
								final File f=jf.getSelectedFile();
								
								if(result==JFileChooser.APPROVE_OPTION)
									ThreadManager.getInstance().execute(new Runnable() {
										
										@Override
										public void run() {
											try {
											loading(true, "export " + exp);
											
											List<MagicCard> export = ((MagicCardTableModel)tableCards.getRowSorter().getModel()).getListCards();
											exp.export(export, f);
											loading(false, "");
											JOptionPane.showMessageDialog(null, "Export Finished",exp.getName() + " Finished",JOptionPane.INFORMATION_MESSAGE);
											} catch (Exception e) {
												logger.error(e);
												loading(false, "");
												JOptionPane.showMessageDialog(null, e,"Error",JOptionPane.ERROR_MESSAGE);
											}	
										
										}
										}, "export search " + exp);
									
								
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
			
			tabbedCardsInfo.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {

						if(tabbedCardsInfo.getSelectedIndex()==INDEX_PRICES)
							updatePrices();
					
				}
			});

			txtFilter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String text = txtFilter.getText();
			          if (text.length() == 0) {
			        	  sorterCards.setRowFilter(null);
			          } else {
			        	  sorterCards.setRowFilter(RowFilter.regexFilter(text));
			          }
				}
			});
			
			thumbnailPanel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					DisplayableCard lab = (DisplayableCard)thumbnailPanel.getComponentAt(new Point(e.getX(), e.getY()));
					selected = lab.getMagicCard();
					cardsPicPanel.showPhoto(selected, null);
					updateCards();
				}
				
			});
			
			txtMagicSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnSearch.doClick();

				}
			});
		
		}

		public void setSelectedCard(MagicCard mc)
		{
			this.selected=mc;
			updateCards();
		}

		public CardSearchGUI() {
		
			try {
				
				
				priceModel=new CardsPriceTableModel();
				cardsModeltable = new MagicCardTableModel();
				boosterProvider = new BoosterPicturesProvider();

				initGUI();
			} 
			catch(Exception e)
			{
				logger.error(e);
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
			}

			logger.debug("construction of GUI : done");
		}

		public void updatePrices() {
			ThreadManager.getInstance().execute(new Runnable() {

				@Override
				public void run() {

					loading(true,"loading prices");
					priceModel.init(selected, selectedEdition);
					priceModel.fireTableDataChanged();
					loading(false,"");

				}
			},"updatePrices");
			
		}
		
		public void updateCards() {
			try {
				cboLanguages.removeAllItems();
				txtRulesArea.setText("");
				
				((DefaultListModel<MagicEdition>)listEdition.getModel()).removeAllElements();

				for(MagicCardNames mcn : selected.getForeignNames())
					cboLanguages.addItem(mcn);
				
				for(MagicEdition me : selected.getEditions())
					((DefaultListModel<MagicEdition>)listEdition.getModel()).addElement(me);

				detailCardPanel.setMagicCard(selected,true);
				magicEditionDetailPanel.setMagicEdition(selected.getEditions().get(0));
				
				
				ThreadManager.getInstance().execute(new Runnable() {
					public void run() {
						lblBoosterPic.setIcon(boosterProvider.getBoosterFor(selectedEdition));
					}
				}, "load booster pic for " + selectedEdition);
				
				
				
				
				for(MagicRuling mr : selected.getRulings())
				{
					txtRulesArea.append(mr.toString());
					txtRulesArea.append("\n");
				}
			
				
				if(tabbedCardsInfo.getSelectedIndex()==INDEX_PRICES)
					updatePrices();
				
				
				historyChartPanel.init(MTGDesktopCompanionControler.getInstance().getEnabledDashBoard().getPriceVariation(selected, selectedEdition),selected.getName());
				
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error(e1);
			}

		}


		

	}


