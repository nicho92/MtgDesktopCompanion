package org.magic.gui.components;

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
import java.net.URI;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
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
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.CardExporter;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.HandPanel;
import org.magic.gui.MagicGUI;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.MagicEditionListRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.tools.MTGLogger;
import org.magic.tools.MagicCardComparator;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class CardSearchPanel extends JPanel {

	Logger logger = MTGLogger.getLogger(this.getClass());

		public static final int INDEX_PRICES = 2;
		public static final int INDEX_THUMB = 1;
		
		
		private MagicCard selectedCard;
		private MagicEdition selectedEdition;
		
		private CardsPriceTableModel priceModel;
		private MagicCardTableModel cardsModeltable;

		private JTabbedPane tabbedCardsView;
		private JTabbedPane tabbedCardsInfo ;
		
		public static CardSearchPanel inst;
		
		private HandPanel thumbnailPanel;
		private ManaRepartitionPanel manaRepartitionPanel;
		private TypeRepartitionPanel typeRepartitionPanel;
		private RarityRepartitionPanel rarityRepartitionPanel;
		private CmcChartPanel cmcChart;
		private CardsPicPanel cardsPicPanel;
		private HistoryPricesPanel historyChartPanel;
		private MagicEditionDetailPanel magicEditionDetailPanel;
		private MagicCardDetailPanel detailCardPanel;
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
		private JSONPanel panelJson;
		
		
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
		private JButton btnSearch;
		private JButton btnExport;
		private JButton btnFilter;
		
		private List<MagicCard> cards;
		private JList<MagicEdition> listEdition;
		
		private JLabel lblLoading;
		
		
		public void loading(boolean show,String text)
		{
			lblLoading.setText(text);
			lblLoading.setVisible(show);
		}

		public MagicCard getSelected() {
			return selectedCard;
		}

		public void setLookAndFeel(String lookAndFeel)
		{
			try {
				UIManager.put("Table.alternateRowColor", Color.decode("#E1E4F2"));
				UIManager.setLookAndFeel(lookAndFeel);
				MTGControler.getInstance().setProperty("lookAndFeel", lookAndFeel);
				SwingUtilities.updateComponentTreeUI(this);
				
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			
		}

		public void initPopupCollection() throws Exception
		{
			JMenu menuItemAdd = new JMenu("Add");

			for(MagicCollection mc : MTGControler.getInstance().getEnabledDAO().getCollections())
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
								MTGControler.getInstance().getEnabledDAO().saveCard(mc, MTGControler.getInstance().getEnabledDAO().getCollection(collec));
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
			logger.info("init search GUI");
			inst=this;
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

			List<MagicEdition> li = MTGControler.getInstance().getEnabledProviders().loadEditions();
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
			editionDetailPanel = new JPanel();
			panelResultsCards = new JPanel();
			cmcChart = new CmcChartPanel();
			manaRepartitionPanel = new ManaRepartitionPanel();
			typeRepartitionPanel = new TypeRepartitionPanel();
			historyChartPanel = new HistoryPricesPanel();
			cardsPicPanel = new CardsPicPanel();
			rarityRepartitionPanel = new RarityRepartitionPanel();
			detailCardPanel = new MagicCardDetailPanel(new MagicCard());
			panelmana = new JPanel();
			panelFilters = new JPanel();
			ManaPanel pan = new ManaPanel();
			panelJson = new JSONPanel();
			
			tabbedCardsView = new JTabbedPane(JTabbedPane.TOP);
			tabbedCardsInfo = new JTabbedPane(JTabbedPane.TOP);
			thumbnailPanel = new HandPanel();
			thumbnailPanel.setBackground(MTGConstants.THUMBNAIL_BACKGROUND_COLOR);
			
			
			
			btnSearch = new JButton(new ImageIcon(MagicGUI.class.getResource("/res/search.png")));
			btnExport = new JButton(new ImageIcon(MagicGUI.class.getResource("/res/export.png")));
			btnFilter = new JButton(new ImageIcon(MagicGUI.class.getResource("/res/filter.png")));
			btnClear = new JButton(new ImageIcon(MagicGUI.class.getResource("/res/09_clear_location.png")));
			
			cboQuereableItems = new JComboBox<String>(new DefaultComboBoxModel(MTGControler.getInstance().getEnabledProviders().getQueryableAttributs()));
			cboCollections= new JComboBox<MagicCollection>(new DefaultComboBoxModel(MTGControler.getInstance().getEnabledDAO().getCollections().toArray(new MagicCollection[MTGControler.getInstance().getEnabledDAO().getCollections().size()])));
			cboLanguages = new JComboBox<MagicCardNames>();
			
			tablePrice = new JXTable();
			tableCards = new JXTable();
			
			lblLoading = new JLabel(new ImageIcon(MagicGUI.class.getResource("/res/load.gif")));
			JLabel lblFilter = new JLabel();
			
			listEdition = new JList<MagicEdition>();
			listEdition.setCellRenderer(new MagicEditionListRenderer());
			txtMagicSearch = new JTextField();
			txtRulesArea = new JTextArea();
			txtFilter = new JTextField();

			filterHeader = new TableFilterHeader(tableCards, AutoChoices.ENABLED);
			
			cboEdition = new JComboBox<MagicEdition>(new DefaultComboBoxModel(li.toArray(new MagicEdition[li.size()])));
			cboEdition.setRenderer(new MagicEditionListRenderer());
////////MODELS
				listEdition.setModel(new DefaultListModel<MagicEdition>());
				tablePrice.setModel(priceModel);
				tableCards.setModel(cardsModeltable);
			
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
			panelResultsCards.setLayout(new BorderLayout(0, 0));
			panelmana.setLayout(new GridLayout(1, 0, 2, 2));
			
			FlowLayout fl_panelFilters = (FlowLayout) panelFilters.getLayout();
			fl_panelFilters.setAlignment(FlowLayout.LEFT);
			
			FlowLayout flowLayout = (FlowLayout) panneauHaut.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);

	
			
		

///////DIMENSION	
			thumbnailPanel.setThumbnailSize(new Dimension(179, 240));
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
	
			
			panelResultsCards.add(panelFilters, BorderLayout.NORTH);
			panelResultsCards.add(scrollCards);
			magicEditionDetailPanel = new MagicEditionDetailPanel();
			
				
			editionDetailPanel.add(magicEditionDetailPanel, BorderLayout.CENTER);
		
			
			panelFilters.add(lblFilter);
			panelFilters.add(txtFilter);
			panelFilters.add(btnClear);
			panelFilters.add(panelmana);
	

			tabbedCardsInfo.addTab("Details", null, detailCardPanel, null);
			tabbedCardsInfo.addTab("Edition", null, editionDetailPanel, null);
			tabbedCardsInfo.addTab("Prices", null, scrollPanePrices, null);
			tabbedCardsInfo.addTab("Rules", null, scrollPaneRules, null);
			tabbedCardsInfo.addTab("Variation", null, historyChartPanel, null);
			
			if(MTGControler.getInstance().get("debug-json-panel").equalsIgnoreCase("true"))
				tabbedCardsInfo.addTab("Json", null, panelJson, null);
			
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
					
					
					new SwingWorker(){
						@Override
						protected Object doInBackground() throws Exception {
							loading(true,"searching");
							String searchName=txtMagicSearch.getText();
							if(cboCollections.isVisible())
								cards = MTGControler.getInstance().getEnabledDAO().getCardsFromCollection((MagicCollection)cboCollections.getSelectedItem());
							else
								cards = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria(cboQuereableItems.getSelectedItem().toString(),searchName,null);
							
							Collections.sort(cards,new MagicCardComparator());
							
							cardsModeltable.init(cards);
							tableCards.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
							
							
							thumbnailPanel.initThumbnails(cards,false);
							
							cmcChart.init(cards);
							typeRepartitionPanel.init(cards);
							manaRepartitionPanel.init(cards);
							rarityRepartitionPanel.init(cards);
							tabbedCardsView.setTitleAt(0, "Results ("+cardsModeltable.getRowCount()+")");
							
							
							
							return null;
						}
						
						@Override
						protected void done() {
							super.done();
							loading(false,"");
							cardsModeltable.fireTableDataChanged();
							btnExport.setEnabled(tableCards.getRowCount()>0);
							
						}
					}.execute();
					
					
					
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
						try{ 
							selectedCard = (MagicCard)tableCards.getValueAt(tableCards.getSelectedRow(), 0);
							selectedEdition = selectedCard.getEditions().get(0);
							updateCards();
						}catch(Exception e)
						{
							
						}

					}
				}
			});

			listEdition.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent mev) {
						selectedEdition = listEdition.getSelectedValue();
						ThreadManager.getInstance().execute(new Runnable() {
							public void run() {
									loading(true,"loading edition");
										try {
											selectedCard = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", selectedCard.getName(), selectedEdition).get(0);
											detailCardPanel.setMagicCard(selectedCard);
											magicEditionDetailPanel.setMagicEdition(selectedEdition);
											
										} catch (Exception e) {
											e.printStackTrace();
										}
										cardsPicPanel.showPhoto(selectedCard);//backcard
									historyChartPanel.init(selectedCard, selectedEdition,selectedCard.getName());
										
										if(tabbedCardsInfo.getSelectedIndex()==INDEX_PRICES)
											updatePrices();
										
									loading(false,"");
								
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
							MagicEdition ed = (MagicEdition)BeanUtils.cloneBean(selectedEdition);
								ed.setMultiverse_id(""+selLang.getGathererId());
								
							cardsPicPanel.showPhoto(selectedCard);
						}
					} catch (Exception e1) {}
				}

			});

			btnExport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JPopupMenu menu = new JPopupMenu();
					
					for(final CardExporter exp : MTGControler.getInstance().getEnabledDeckExports())
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
												e.printStackTrace();
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
					selectedCard = lab.getMagicCard();
					selectedEdition = lab.getMagicCard().getEditions().get(0);
					cardsPicPanel.showPhoto(selectedCard);
					updateCards();
				}
				
			});
			
			txtMagicSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnSearch.doClick();

				}
			});
		
		}
		
		public void thumbnail(List<MagicCard> cards)
		{
			tabbedCardsView.setSelectedIndex(INDEX_THUMB);
			thumbnailPanel.initThumbnails(cards,false);
		}
		

		public void setSelectedCard(MagicCard mc)
		{
			this.selectedCard=mc;
			updateCards();
		}

		public CardSearchPanel() {
		
			try {
				
				
				priceModel=new CardsPriceTableModel();
				cardsModeltable = new MagicCardTableModel();
				

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
					priceModel.init(selectedCard, selectedEdition);
					priceModel.fireTableDataChanged();
					loading(false,"");

				}
			},"updatePrices");
			
		}
		
		public HandPanel getThumbnailPanel()
		{
			return thumbnailPanel;
		}
		
		
		public void updateCards() {
			try {
				cboLanguages.removeAllItems();
				txtRulesArea.setText("");
				
				((DefaultListModel<MagicEdition>)listEdition.getModel()).removeAllElements();

				for(MagicCardNames mcn : selectedCard.getForeignNames())
					cboLanguages.addItem(mcn);
				
				for(MagicEdition me : selectedCard.getEditions())
					((DefaultListModel<MagicEdition>)listEdition.getModel()).addElement(me);

				detailCardPanel.setMagicCard(selectedCard,true);
				magicEditionDetailPanel.setMagicEdition(selectedCard.getEditions().get(0));
				
				
				
				
				for(MagicRuling mr : selectedCard.getRulings())
				{
					txtRulesArea.append(mr.toString());
					txtRulesArea.append("\n");
				}
			
				
				if(tabbedCardsInfo.getSelectedIndex()==INDEX_PRICES)
					updatePrices();
				
				
				panelJson.show(selectedCard);
				

				ThreadManager.getInstance().execute(new Runnable() {
					public void run() {
						historyChartPanel.init(selectedCard, selectedEdition,selectedCard.getName());
					}
				}, "load history for " + selectedEdition);
				
				
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error(e1);
			}

		}


		

	}


