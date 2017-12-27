package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.CardExporter;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.dialog.CollectionChooserDialog;
import org.magic.gui.components.dialog.DeckSnifferDialog;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.EnumConditionEditor;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.gui.renderer.MagicDeckQtyEditor;
import org.magic.gui.renderer.MagicEditionEditor;
import org.magic.gui.renderer.MagicEditionRenderer;
import org.magic.gui.renderer.StockTableRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class StockPanelGUI extends JPanel {
	private JXTable table;
	private CardStockTableModel model;
	private JTextField txtSearch;
	private DefaultListModel<MagicCard> resultListModel = new DefaultListModel<MagicCard>();
	private JList<MagicCard> listResult ;
	private JComboBox<String> cboAttributs ;
	private JButton btnSearch;

	private JButton btnAdd = new JButton();
	private JButton btnDelete = new JButton();
	private JButton btnSave = new JButton();
	
	private MagicCardDetailPanel magicCardDetailPanel;
	private JSplitPane splitPane;
    private TableFilterHeader filterHeader;

	private List<MagicCard> selectedCard;
	private JButton btnReload;
    
	Logger logger = MTGLogger.getLogger(this.getClass());
	private JLabel lblLoading;
	private JPanel rightPanel;
	private JLabel lblQte;
	private JLabel lblLanguage;
	private JLabel lblComment;
	private JSpinner spinner;
	private JComboBox<String> cboLanguages;
	private JTextPane textPane;
	private JComboBox<Boolean> cboFoil;
	private JLabel lblFoil;
	private JLabel lblSigned;
	private JLabel lblAltered;
	private JComboBox<Boolean> cboSigned;
	private JComboBox<Boolean> cboAltered;
	private JButton btnshowMassPanel;
	private JButton btnApplyModification;
	
	private static Boolean[] values = {null,true,false};
	private JLabel lblQuality;
	private JComboBox<EnumCondition> cboQuality;
	private JButton btnImport;
	private JLabel lblCollection;
	private JComboBox<MagicCollection> cboCollection;
	private JButton btnExport;
	private JButton btnGeneratePrice;
	private JPanel bottomPanel;
	private JLabel lblCount;
	private JLabel lblSelect;
	private JComboBox cboSelections;
	private String[] selections=new String[] {"", "New", "Updated"};
	
	public StockPanelGUI() {
		logger.info("init StockManagment GUI");
		
		initGUI();
		
		listResult.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				
				selectedCard=listResult.getSelectedValuesList();
				if(selectedCard!=null)
				{
					btnAdd.setEnabled(true);
					magicCardDetailPanel.setMagicCard(selectedCard.get(0));
				}
			}
		});
		
		txtSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				btnSearch.doClick();
			}
		});
		
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (txtSearch.getText().equals(""))
					return;

				resultListModel.removeAllElements();

				ThreadManager.getInstance().execute(new Runnable() {
					public void run() {
						try {
							lblLoading.setVisible(true);
							//String searchName = URLEncoder.encode(txtSearch.getText(), "UTF-8");
							String searchName = txtSearch.getText();
							List<MagicCard> cards = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria(cboAttributs.getSelectedItem().toString(), searchName, null);
							for (MagicCard m : cards) 
									resultListModel.addElement(m);
							
							listResult.updateUI();
							lblLoading.setVisible(false);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, e.getMessage(), MTGControler.getInstance().getLangService().getCapitalize("ERROR"), JOptionPane.ERROR_MESSAGE);
						}
					}
				},"Search stock");
			}
		});
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ThreadManager.getInstance().execute(new Runnable() {
					
					@Override
					public void run() {
						for(MagicCardStock ms : model.getList())
							if(ms.isUpdate())
								try {
									lblLoading.setVisible(true);
									MTGControler.getInstance().getEnabledDAO().saveOrUpdateStock(ms);
									ms.setUpdate(false);
									lblLoading.setVisible(false);
								} catch (SQLException e1) {
									JOptionPane.showMessageDialog(null, e1.getMessage(),MTGControler.getInstance().getLangService().getCapitalize("ERROR") + " : " + String.valueOf(ms),JOptionPane.ERROR_MESSAGE);
									lblLoading.setVisible(false);
								}
						
						model.fireTableDataChanged();
						
					}
				}, "Batch stock save");
			}
			
		});
		
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				for(MagicCard mc : selectedCard)
				{
					MagicCardStock ms = new MagicCardStock();
					ms.setIdstock(-1);
					ms.setUpdate(true);
					ms.setMagicCard(mc);
					model.add(ms);
					updateCount();
				}
			}
		});
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	
	        	if(!event.getValueIsAdjusting())
	        	{
	        			int viewRow = table.getSelectedRow();
			        	if(viewRow>-1)
			        	{
			        		int modelRow = table.convertRowIndexToModel(viewRow);
							MagicCardStock selectedStock = (MagicCardStock)table.getModel().getValueAt(modelRow, 0);
							btnDelete.setEnabled(true);
							magicCardDetailPanel.setMagicCard(selectedStock.getMagicCard());
			        	}
	        	}
	        }
	    });
		
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(null, 
											MTGControler.getInstance().getLangService().getCapitalize("CONFIRM_DELETE",table.getSelectedRows().length + " item(s)"),
											MTGControler.getInstance().getLangService().getCapitalize("DELETE") +" ?",
											JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION)
					{
					logger.debug("delete stocks");
					ThreadManager.getInstance().execute(new Runnable() {
						
						@Override
						public void run() {
							try {
								int selected [] = table.getSelectedRows();
								lblLoading.setVisible(true);
								List<MagicCardStock> stocks = extract(selected);
								model.removeRows(stocks);
								updateCount();
							}
							catch(Exception e)
							{
								JOptionPane.showMessageDialog(null, e.getMessage(),MTGControler.getInstance().getLangService().getCapitalize("ERROR"),JOptionPane.ERROR_MESSAGE);
								lblLoading.setVisible(false);
							}
							lblLoading.setVisible(false);
							updateCount();
						}
					}, "delete stock");
					
					
					}
				}
		});
		
	
		
		btnReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().getCapitalize("CANCEL_CHANGES"),MTGControler.getInstance().getLangService().getCapitalize("CONFIRM_UNDO"),JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION)
				{
					logger.debug("reload collection");
					ThreadManager.getInstance().execute(new Runnable() {
						
						@Override
						public void run() {
							try {
								lblLoading.setVisible(true);
								model.init();
							} catch (SQLException e1) {
								JOptionPane.showMessageDialog(null, e1.getMessage(),MTGControler.getInstance().getLangService().getCapitalize("ERROR"),JOptionPane.ERROR_MESSAGE);
							}
							lblLoading.setVisible(false);
							updateCount();
						}
					}, "reload stock");
					
				}
			}
		});
	
		btnshowMassPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightPanel.setVisible(!rightPanel.isVisible());
			}
		});
			
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JPopupMenu menu = new JPopupMenu();

				JMenuItem mnuCol = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_FROM","collection"));
				mnuCol.setIcon(MTGConstants.ICON_COLLECTION);
				
				mnuCol.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						CollectionChooserDialog diag = new CollectionChooserDialog();
						diag.setVisible(true);
						final MagicCollection col = diag.getSelectedCollection();
						
						if(col!=null)
						{	ThreadManager.getInstance().execute(new Runnable() {
								
								@Override
								public void run() {
									lblLoading.setVisible(true);
									importFromCollection(col);
									lblLoading.setVisible(false);
									updateCount();
								}
							}, "Import stock from collection");
						}
						
						
						
					}
				});
				
				menu.add(mnuCol);
				
				JMenuItem webSite = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_FROM",MTGControler.getInstance().getLangService().get("WEBSITE")));
				webSite.setIcon(MTGConstants.ICON_WEBSITE);
				webSite.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						DeckSnifferDialog diag = new DeckSnifferDialog();
						diag.setModal(true);
						diag.setVisible(true);

						if (diag.getSelectedDeck() != null) {
							lblLoading.setVisible(true);
							MagicDeck deck = diag.getSelectedDeck();
							importFromWebSite(deck);
							updateCount();
							lblLoading.setVisible(false);
						}
					}
				});
				menu.add(webSite);

				for (final CardExporter exp : MTGControler.getInstance().getEnabledDeckExports()) {
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							JFileChooser jf = new JFileChooser(".");
							jf.setFileFilter(new FileFilter() {

								@Override
								public String getDescription() {
									return exp.getName();
								}

								@Override
								public boolean accept(File f) {
									if (f.isDirectory())
										return true;

									if (f.getName().endsWith(exp.getFileExtension()))
										return true;

									return false;
								}
							});
							int res = jf.showOpenDialog(null);
							final File f = jf.getSelectedFile();

							if (res == JFileChooser.APPROVE_OPTION)
								ThreadManager.getInstance().execute(new Runnable() {

									@Override
									public void run() {
										try {
											lblLoading.setVisible(true);
											List<MagicCardStock> list = exp.importStock(f);
											for(MagicCardStock mc : list)
											{
													mc.setIdstock(-1);
													mc.setUpdate(true);
													model.add(mc);
											}
											model.fireTableDataChanged();
											updateCount();
											lblLoading.setVisible(false);
											JOptionPane.showMessageDialog(null, MTGControler.getInstance().getLangService().combine("IMPORT","FINISHED"),exp.getName() + " "+MTGControler.getInstance().getLangService().getCapitalize("FINISHED"), JOptionPane.INFORMATION_MESSAGE);

										} catch (Exception e) {
											logger.error(e);
											lblLoading.setVisible(false);
											JOptionPane.showMessageDialog(null, e, MTGControler.getInstance().getLangService().getCapitalize("ERROR"), JOptionPane.ERROR_MESSAGE);
										}

									}
								}, "import " + exp);
						}
					});

					menu.add(it);
				}

				Component b = (Component) ae.getSource();
				Point p = b.getLocationOnScreen();
				menu.show(b, 0, 0);
				menu.setLocation(p.x, p.y + b.getHeight());
			}
		});
		
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JPopupMenu menu = new JPopupMenu();

				for (final CardExporter exp : MTGControler.getInstance().getEnabledDeckExports()) {
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							JFileChooser jf = new JFileChooser(".");
							jf.setFileFilter(new FileFilter() {

								@Override
								public String getDescription() {
									return exp.getName();
								}

								@Override
								public boolean accept(File f) {
									if (f.isDirectory())
										return true;

									if (f.getName().endsWith(exp.getFileExtension()))
										return true;

									return false;
								}
							});
							int res = jf.showSaveDialog(null);
							final File f = jf.getSelectedFile();

							if (res == JFileChooser.APPROVE_OPTION)
								ThreadManager.getInstance().execute(new Runnable() {

									@Override
									public void run() {
										try {
											lblLoading.setVisible(true);
											
											exp.exportStock(model.getList(), f);
											
											lblLoading.setVisible(false);
											JOptionPane.showMessageDialog(null,  MTGControler.getInstance().getLangService().combine("EXPORT","FINISHED"), exp.getName() + " " +MTGControler.getInstance().getLangService().getCapitalize("FINISHED"), JOptionPane.INFORMATION_MESSAGE);

										} catch (Exception e) {
											logger.error(e);
											lblLoading.setVisible(false);
											JOptionPane.showMessageDialog(null, e, MTGControler.getInstance().getLangService().getCapitalize("ERROR"), JOptionPane.ERROR_MESSAGE);
										}

									}
								}, "export " + exp);
						}
					});

					menu.add(it);
				}

				Component b = (Component) ae.getSource();
				Point p = b.getLocationOnScreen();
				menu.show(b, 0, 0);
				menu.setLocation(p.x, p.y + b.getHeight());
			}
		});
		
		btnGeneratePrice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				ThreadManager.getInstance().execute(new Runnable() {
					
					@Override
					public void run() {
						lblLoading.setVisible(true);
						for(int i : table.getSelectedRows())
						{
							MagicCardStock s = (MagicCardStock)table.getModel().getValueAt(table.convertRowIndexToModel(i), 0);
							Collection<Double> prices;
							Double price=0.0;
							try {
								prices = MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(s.getMagicCard(),null).values();
								if(prices.size()>0)
									price = (Double)prices.toArray()[prices.size()-1];
								else
									price=0.0;
							} catch (IOException e) {
								price=0.0;
							}
							double old = s.getPrice();
							s.setPrice(price);
							if(old!=s.getPrice())
								s.setUpdate(true);
							
							model.fireTableDataChanged();
						}
						lblLoading.setVisible(false);
					}
				}, "generate prices for stock");
				
				
				
			}
		});
		
		cboSelections.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				
				if(String.valueOf(cboSelections.getSelectedItem()).equals(selections[1]))
				{
					table.clearSelection();
					for(int i=0;i<table.getRowCount();i++)
					{
						if(table.getValueAt(i, 0).toString().equals("-1"))
							table.addRowSelectionInterval(i,i);
					}
				}
				else if(String.valueOf(cboSelections.getSelectedItem()).equals(selections[2]))
				{
					table.clearSelection();
					for(int i=0;i<table.getRowCount();i++)
					{
						if(((MagicCardStock)table.getValueAt(i, 0)).isUpdate())
							table.addRowSelectionInterval(i,i);
					}
				}
				
				
				
				
			}
		});
		
		btnApplyModification.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(null,  MTGControler.getInstance().getLangService().getCapitalize("CHANGE_X_ITEMS",table.getSelectedRowCount()),  MTGControler.getInstance().getLangService().getCapitalize("CONFIRMATION"), JOptionPane.YES_NO_CANCEL_OPTION);
				if(res==JOptionPane.YES_OPTION)
				{
					for(int i : table.getSelectedRows())
					{
						MagicCardStock s = (MagicCardStock)table.getModel().getValueAt(table.convertRowIndexToModel(i), 0);
						s.setUpdate(true);
						if(((Integer)spinner.getValue()).intValue()>0);
							s.setQte((Integer)spinner.getValue());
						if(!textPane.getText().equals(""))
							s.setComment(textPane.getText());
						if(cboAltered.getSelectedItem()!=null)
							s.setAltered((Boolean)cboAltered.getSelectedItem());
						if(cboSigned.getSelectedItem()!=null)
							s.setSigned((Boolean)cboSigned.getSelectedItem());
						if(cboFoil.getSelectedItem()!=null)
							s.setFoil((Boolean)cboFoil.getSelectedItem());
						if(cboLanguages!=null)
							s.setLanguage(String.valueOf(cboLanguages.getSelectedItem()));
						if(cboQuality.getSelectedItem()!=null)
							s.setCondition((EnumCondition)cboQuality.getSelectedItem());
						if(cboCollection.getSelectedItem()!=null)
							s.setMagicCollection((MagicCollection)cboCollection.getSelectedItem());
						
					}
					model.fireTableDataChanged();
				}
				
				
			}
		});
		
	}
	
	
	private List<MagicCardStock> extract(int[] ids)
	{
		List<MagicCardStock> select = new ArrayList<MagicCardStock>();
		
		for(int l : ids)
		{
			select.add(((MagicCardStock)table.getValueAt(l, 0)));
		}
		return select;
		
	}
	
	private void importFromCollection(MagicCollection col)
	{
		try {
			for(MagicCard mc : MTGControler.getInstance().getEnabledDAO().getCardsFromCollection(col))
			{
				MagicCardStock stock = new MagicCardStock();
				stock.setMagicCard(mc);
				stock.setMagicCollection(col);
				stock.setQte(1);
				stock.setComment(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_FROM",col.getName()));
				stock.setIdstock(-1);
				stock.setUpdate(true);
				model.add(stock);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.fireTableDataChanged();
	}
	
	private void importFromWebSite(MagicDeck deck)
	{
		for(MagicCard mc : deck.getMap().keySet())
		{
			MagicCardStock stock = new MagicCardStock();
				stock.setMagicCard(mc);
				stock.setQte(deck.getMap().get(mc));
				stock.setComment(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_FROM",deck.getName()));
				stock.setIdstock(-1);
				stock.setUpdate(true);
				model.add(stock);
		}
		model.fireTableDataChanged();
	}

	private void initGUI()
	{
		setLayout(new BorderLayout(0, 0));
		txtSearch = new JTextField();
		JPanel leftPanel = new JPanel();
		JScrollPane scrollList = new JScrollPane();
		JPanel searchPanel = new JPanel();
		
		model = new CardStockTableModel();
		
		listResult = new JList<MagicCard>(resultListModel);
		
		
		add(leftPanel, BorderLayout.WEST);
		leftPanel.setLayout(new BorderLayout(0, 0));
		leftPanel.add(scrollList, BorderLayout.CENTER);
		
	
		listResult.setCellRenderer(new MagicCardListRenderer());
		
		scrollList.setViewportView(listResult);
		
		leftPanel.add(searchPanel, BorderLayout.NORTH);
		
		String[] q = MTGControler.getInstance().getEnabledProviders().getQueryableAttributs();
		cboAttributs = new JComboBox<String>(new DefaultComboBoxModel<String>(q));
		searchPanel.add(cboAttributs);
	
		searchPanel.add(txtSearch);
		txtSearch.setColumns(10);
		
		btnSearch = new JButton("");
		
		btnSearch.setIcon(MTGConstants.ICON_SEARCH_2);
		searchPanel.add(btnSearch);
		
		JPanel centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		JPanel actionPanel = new JPanel();
		centerPanel.add(actionPanel, BorderLayout.NORTH);
				btnAdd.setEnabled(false);
				btnAdd.setIcon(MTGConstants.ICON_NEW);
				actionPanel.add(btnAdd);
		
				btnDelete.setEnabled(false);
				btnDelete.setIcon(MTGConstants.ICON_DELETE);
				actionPanel.add(btnDelete);
				
				
				btnSave.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("BATCH_SAVE"));
				btnSave.setIcon(MTGConstants.ICON_SAVE);
				actionPanel.add(btnSave);
				
				btnReload = new JButton("");
				btnReload.setIcon(MTGConstants.ICON_REFRESH);
				btnReload.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("RELOAD"));
				actionPanel.add(btnReload);
				
				lblLoading = new JLabel();
				lblLoading.setVisible(false);
				
				btnshowMassPanel = new JButton("");
				
				
				btnImport = new JButton();
				btnImport.setIcon(MTGConstants.ICON_IMPORT);
				btnImport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
				actionPanel.add(btnImport);
				
				btnExport = new JButton("");
				
				btnExport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("EXPORT"));
				btnExport.setIcon(MTGConstants.ICON_EXPORT);
				actionPanel.add(btnExport);
				
				btnGeneratePrice = new JButton();
				
				btnGeneratePrice.setIcon(MTGConstants.ICON_EURO);
				btnGeneratePrice.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("GENERATE_PRICE"));
				actionPanel.add(btnGeneratePrice);
				btnshowMassPanel.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("MASS_MODIFICATION"));
				btnshowMassPanel.setIcon(MTGConstants.ICON_MANUAL);
				actionPanel.add(btnshowMassPanel);
				lblLoading.setIcon(MTGConstants.ICON_LOADING);
				actionPanel.add(lblLoading);
				
		JScrollPane scrollTable = new JScrollPane();
		
		table = new JXTable(model);
		StockTableRenderer render = new StockTableRenderer();
		
		table.setDefaultRenderer(Object.class,render);
		table.setDefaultEditor(EnumCondition.class, new EnumConditionEditor());
		table.setDefaultEditor(Integer.class, new MagicDeckQtyEditor());
		
		table.getColumnModel().getColumn(2).setCellEditor(new MagicEditionEditor());
		table.getColumnModel().getColumn(2).setCellRenderer(new MagicEditionRenderer());
		
		table.packAll();
		filterHeader = new TableFilterHeader(table, AutoChoices.ENABLED);
		scrollTable.setViewportView(table);
		
		magicCardDetailPanel = new MagicCardDetailPanel();
		magicCardDetailPanel.enableThumbnail(true);
		
		splitPane = new JSplitPane();
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
	
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerPanel.add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(scrollTable);
		splitPane.setRightComponent(magicCardDetailPanel);
		
		rightPanel = new JPanel();
		rightPanel.setVisible(false);
		add(rightPanel, BorderLayout.EAST);
		GridBagLayout gbl_rightPanel = new GridBagLayout();
		gbl_rightPanel.columnWidths = new int[]{84, 103, 0};
		gbl_rightPanel.rowHeights = new int[]{83, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_rightPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_rightPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		rightPanel.setLayout(gbl_rightPanel);
		
		lblSelect = new JLabel("Select :");
		GridBagConstraints gbc_lblSelect = new GridBagConstraints();
		gbc_lblSelect.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblSelect.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelect.gridx = 0;
		gbc_lblSelect.gridy = 1;
		rightPanel.add(lblSelect, gbc_lblSelect);
		
		cboSelections = new JComboBox();
		
		cboSelections.setModel(new DefaultComboBoxModel(selections));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.anchor = GridBagConstraints.NORTH;
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 1;
		rightPanel.add(cboSelections, gbc_comboBox);
		
		lblQte = new JLabel("Qte : ");
		GridBagConstraints gbc_lblQte = new GridBagConstraints();
		gbc_lblQte.anchor = GridBagConstraints.EAST;
		gbc_lblQte.insets = new Insets(0, 0, 5, 5);
		gbc_lblQte.gridx = 0;
		gbc_lblQte.gridy = 2;
		rightPanel.add(lblQte, gbc_lblQte);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(0,0, null, 1));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.insets = new Insets(0, 0, 5, 0);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 2;
		rightPanel.add(spinner, gbc_spinner);
		
		lblLanguage = new JLabel("Language :");
		GridBagConstraints gbc_lblLanguage = new GridBagConstraints();
		gbc_lblLanguage.anchor = GridBagConstraints.EAST;
		gbc_lblLanguage.insets = new Insets(0, 0, 5, 5);
		gbc_lblLanguage.gridx = 0;
		gbc_lblLanguage.gridy = 3;
		rightPanel.add(lblLanguage, gbc_lblLanguage);
		
		DefaultComboBoxModel lModel = new DefaultComboBoxModel();
		lModel.addElement(null);
		for(Locale l : Locale.getAvailableLocales())
			 lModel.addElement(l.getDisplayLanguage(Locale.US));
		
		cboLanguages = new JComboBox(lModel);
		GridBagConstraints gbc_cboLanguages = new GridBagConstraints();
		gbc_cboLanguages.insets = new Insets(0, 0, 5, 0);
		gbc_cboLanguages.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboLanguages.gridx = 1;
		gbc_cboLanguages.gridy = 3;
		rightPanel.add(cboLanguages, gbc_cboLanguages);
		
		lblFoil = new JLabel("Foil :");
		GridBagConstraints gbc_lblFoil = new GridBagConstraints();
		gbc_lblFoil.anchor = GridBagConstraints.EAST;
		gbc_lblFoil.insets = new Insets(0, 0, 5, 5);
		gbc_lblFoil.gridx = 0;
		gbc_lblFoil.gridy = 4;
		rightPanel.add(lblFoil, gbc_lblFoil);
		
		cboFoil = new JComboBox(new DefaultComboBoxModel<Boolean>(values));
		GridBagConstraints gbc_cboFoil = new GridBagConstraints();
		gbc_cboFoil.insets = new Insets(0, 0, 5, 0);
		gbc_cboFoil.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboFoil.gridx = 1;
		gbc_cboFoil.gridy = 4;
		rightPanel.add(cboFoil, gbc_cboFoil);
		
		lblSigned = new JLabel("Signed :");
		GridBagConstraints gbc_lblSigned = new GridBagConstraints();
		gbc_lblSigned.anchor = GridBagConstraints.EAST;
		gbc_lblSigned.insets = new Insets(0, 0, 5, 5);
		gbc_lblSigned.gridx = 0;
		gbc_lblSigned.gridy = 5;
		rightPanel.add(lblSigned, gbc_lblSigned);
		
		cboSigned = new JComboBox(new DefaultComboBoxModel<Boolean>(values));
		GridBagConstraints gbc_cboSigned = new GridBagConstraints();
		gbc_cboSigned.insets = new Insets(0, 0, 5, 0);
		gbc_cboSigned.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboSigned.gridx = 1;
		gbc_cboSigned.gridy = 5;
		rightPanel.add(cboSigned, gbc_cboSigned);
		
		lblAltered = new JLabel("Altered :");
		GridBagConstraints gbc_lblAltered = new GridBagConstraints();
		gbc_lblAltered.anchor = GridBagConstraints.EAST;
		gbc_lblAltered.insets = new Insets(0, 0, 5, 5);
		gbc_lblAltered.gridx = 0;
		gbc_lblAltered.gridy = 6;
		rightPanel.add(lblAltered, gbc_lblAltered);
		
		cboAltered = new JComboBox(new DefaultComboBoxModel<Boolean>(values));
		GridBagConstraints gbc_cboAltered = new GridBagConstraints();
		gbc_cboAltered.insets = new Insets(0, 0, 5, 0);
		gbc_cboAltered.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboAltered.gridx = 1;
		gbc_cboAltered.gridy = 6;
		rightPanel.add(cboAltered, gbc_cboAltered);
		
		lblQuality = new JLabel("Quality :");
		GridBagConstraints gbc_lblQuality = new GridBagConstraints();
		gbc_lblQuality.anchor = GridBagConstraints.EAST;
		gbc_lblQuality.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuality.gridx = 0;
		gbc_lblQuality.gridy = 7;
		rightPanel.add(lblQuality, gbc_lblQuality);
		
		DefaultComboBoxModel qModel = new DefaultComboBoxModel();
		qModel.addElement(null);
		for(EnumCondition l : EnumCondition.values())
			 qModel.addElement(l);
		
		
		cboQuality = new JComboBox<EnumCondition>(qModel);
		
		
		
		GridBagConstraints gbc_cboQuality = new GridBagConstraints();
		gbc_cboQuality.insets = new Insets(0, 0, 5, 0);
		gbc_cboQuality.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboQuality.gridx = 1;
		gbc_cboQuality.gridy = 7;
		rightPanel.add(cboQuality, gbc_cboQuality);
		
		lblCollection = new JLabel("Collection :");
		GridBagConstraints gbc_lblCollection = new GridBagConstraints();
		gbc_lblCollection.anchor = GridBagConstraints.EAST;
		gbc_lblCollection.insets = new Insets(0, 0, 5, 5);
		gbc_lblCollection.gridx = 0;
		gbc_lblCollection.gridy = 8;
		rightPanel.add(lblCollection, gbc_lblCollection);
		
		

		DefaultComboBoxModel<MagicCollection> cModel = new DefaultComboBoxModel<MagicCollection>();
		cModel.addElement(null);
		try {
			for(MagicCollection l : MTGControler.getInstance().getEnabledDAO().getCollections())
				 cModel.addElement(l);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		cboCollection = new JComboBox<MagicCollection>(cModel);
		GridBagConstraints gbc_cboCollection = new GridBagConstraints();
		gbc_cboCollection.insets = new Insets(0, 0, 5, 0);
		gbc_cboCollection.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboCollection.gridx = 1;
		gbc_cboCollection.gridy = 8;
		rightPanel.add(cboCollection, gbc_cboCollection);
		
		lblComment = new JLabel("Comment :");
		GridBagConstraints gbc_lblComment = new GridBagConstraints();
		gbc_lblComment.insets = new Insets(0, 0, 5, 5);
		gbc_lblComment.gridx = 0;
		gbc_lblComment.gridy = 9;
		rightPanel.add(lblComment, gbc_lblComment);
		
		textPane = new JTextPane();
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.insets = new Insets(0, 0, 5, 0);
		gbc_textPane.gridwidth = 2;
		gbc_textPane.gridheight = 3;
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 0;
		gbc_textPane.gridy = 10;
		rightPanel.add(textPane, gbc_textPane);
		
		btnApplyModification = new JButton("Apply");
		
		
		GridBagConstraints gbc_btnApplyModification = new GridBagConstraints();
		gbc_btnApplyModification.gridwidth = 2;
		gbc_btnApplyModification.gridx = 0;
		gbc_btnApplyModification.gridy = 13;
		rightPanel.add(btnApplyModification, gbc_btnApplyModification);
		
		bottomPanel = new JPanel();
		add(bottomPanel, BorderLayout.SOUTH);
		
		lblCount = new JLabel();
		bottomPanel.add(lblCount);
		
		
		ThreadManager.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					lblLoading.setVisible(true);
					model.init();
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),MTGControler.getInstance().getLangService().getCapitalize("ERROR"),JOptionPane.ERROR_MESSAGE);
				}
				lblLoading.setVisible(false);
				updateCount();
			}
		}, "init stock");
		
	}
	
	public void updateCount()
	{
		lblCount.setText(MTGControler.getInstance().getLangService().getCapitalize("ITEMS_IN_STOCK")+": " + table.getRowCount());
	}

	
}
