package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SystemColor;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
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
import org.magic.gui.components.dialog.CardSearchImportDialog;
import org.magic.gui.components.dialog.CollectionChooserDialog;
import org.magic.gui.components.dialog.DeckSnifferDialog;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.EnumConditionEditor;
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
	private JButton btnDelete = new JButton();
	private JButton btnSave = new JButton();
    private boolean multiselection=false;

	
	private MagicCardDetailPanel magicCardDetailPanel;
	private JSplitPane splitPane;
	private JButton btnReload;
    
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
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
	private JComboBox<String> cboSelections;
	private String[] selections=new String[] {"", MTGControler.getInstance().getLangService().get("NEW"), MTGControler.getInstance().getLangService().get("UPDATED")};
	
	public StockPanelGUI() {
		logger.info("init StockManagment GUI");
		
		initGUI();
		
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
		
		table.getSelectionModel().addListSelectionListener(event->{
	        	
	        	if(!multiselection && !event.getValueIsAdjusting())
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
	    });
		
		btnDelete.addActionListener(event-> {
				int res = JOptionPane.showConfirmDialog(null, 
											MTGControler.getInstance().getLangService().getCapitalize("CONFIRM_DELETE",table.getSelectedRows().length + " item(s)"),
											MTGControler.getInstance().getLangService().getCapitalize("DELETE") +" ?",
											JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION)
					{
					ThreadManager.getInstance().execute(()->{
							try {
								int[] selected  = table.getSelectedRows();
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
						
					}, "delete stock");
					
					}
		});
		
		btnReload.addActionListener(event-> {
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
			
		});
	
		btnshowMassPanel.addActionListener(event->rightPanel.setVisible(!rightPanel.isVisible()));
			
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JPopupMenu menu = new JPopupMenu();

				JMenuItem mnuImportSearch = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_FROM",MTGControler.getInstance().getLangService().get("SEARCH_MODULE")));
				mnuImportSearch.setIcon(MTGConstants.ICON_SEARCH);
				
				mnuImportSearch.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent ae) {
						CardSearchImportDialog cdSearch = new CardSearchImportDialog();
						cdSearch.setVisible(true);
						if(cdSearch.getSelection()!=null)
						{
							for(MagicCard mc : cdSearch.getSelection())
								addCard(mc);
						}
					}
				});
				menu.add(mnuImportSearch);
				
				JMenuItem mnuCol = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_FROM",MTGControler.getInstance().getLangService().get("COLLECTION_MODULE")));
				mnuCol.setIcon(MTGConstants.ICON_COLLECTION);
				mnuCol.addActionListener(collEvent->{
						CollectionChooserDialog diag = new CollectionChooserDialog();
						diag.setVisible(true);
						final MagicCollection col = diag.getSelectedCollection();
						
						if(col!=null)
						{	ThreadManager.getInstance().execute(()->{
									lblLoading.setVisible(true);
									try {
										importFromCollection(col);
									} catch (SQLException e) {
										JOptionPane.showMessageDialog(null, e, MTGControler.getInstance().getLangService().getCapitalize("ERROR"), JOptionPane.ERROR_MESSAGE);
										
									}
									lblLoading.setVisible(false);
									updateCount();
							}, "Import stock from collection");
						}
	
				});
				menu.add(mnuCol);
				
				JMenuItem webSite = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_FROM",MTGControler.getInstance().getLangService().get("WEBSITE")));
				webSite.setIcon(MTGConstants.ICON_WEBSITE);
				webSite.addActionListener(siteEvent-> {
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
				});
				menu.add(webSite);

				
				
				
				for (final CardExporter exp : MTGControler.getInstance().getEnabledDeckExports()) {
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(itemEvent-> {
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
								ThreadManager.getInstance().execute(()->{
										try {
											lblLoading.setVisible(true);
											List<MagicCardStock> list = exp.importStock(f);
											for(MagicCardStock mc : list)
											{
												addStock(mc);
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

								}, "import " + exp);
					});

					menu.add(it);
				}

				Component b = (Component) ae.getSource();
				Point p = b.getLocationOnScreen();
				menu.show(b, 0, 0);
				menu.setLocation(p.x, p.y + b.getHeight());
			}
		});
		
		btnExport.addActionListener(event->{
				JPopupMenu menu = new JPopupMenu();

				for (final CardExporter exp : MTGControler.getInstance().getEnabledDeckExports()) {
					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(itEvent-> {
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
								ThreadManager.getInstance().execute(()->{
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
								}, "export " + exp);
						
					});

					menu.add(it);
				}

				Component b = (Component) event.getSource();
				Point p = b.getLocationOnScreen();
				menu.show(b, 0, 0);
				menu.setLocation(p.x, p.y + b.getHeight());
			
		});
		
		btnGeneratePrice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ThreadManager.getInstance().execute(()-> {
						lblLoading.setVisible(true);
						for(int i : table.getSelectedRows())
						{
							MagicCardStock s = (MagicCardStock)table.getModel().getValueAt(table.convertRowIndexToModel(i), 0);
							Collection<Double> prices;
							Double price=0.0;
							try {
								prices = MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(s.getMagicCard(),null).values();
								if(!prices.isEmpty())
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
					
				}, "generate prices for stock");
				
				
				
			}
		});
		
		cboSelections.addItemListener(ie-> {
				multiselection=true;
				if(String.valueOf(cboSelections.getSelectedItem()).equals(selections[1]))
				{
					table.clearSelection();
					
					for(int i=0;i<table.getRowCount();i++)
					{
						
						if(table.getValueAt(i, 0).toString().equals("-1"))
						{
							table.addRowSelectionInterval(i,i);
						}
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
				multiselection=false;
		});
		
		btnApplyModification.addActionListener(event-> {
				int res = JOptionPane.showConfirmDialog(null,  MTGControler.getInstance().getLangService().getCapitalize("CHANGE_X_ITEMS",table.getSelectedRowCount()),  MTGControler.getInstance().getLangService().getCapitalize("CONFIRMATION"), JOptionPane.YES_NO_CANCEL_OPTION);
				if(res==JOptionPane.YES_OPTION)
				{
					for(int i : table.getSelectedRows())
					{
						MagicCardStock s = (MagicCardStock)table.getModel().getValueAt(table.convertRowIndexToModel(i), 0);
						s.setUpdate(true);
						if(((Integer)spinner.getValue()).intValue()>0)
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
		});
		
	}
	public void addStock(MagicCardStock mcs) {
		mcs.setIdstock(-1);
		mcs.setUpdate(true);
		model.add(mcs);
	}
	
	
	public void addCard(MagicCard mc) {
		MagicCardStock ms = new MagicCardStock();
		ms.setIdstock(-1);
		ms.setUpdate(true);
		ms.setMagicCard(mc);
		model.add(ms);
		
	}


	private List<MagicCardStock> extract(int[] ids)
	{
		List<MagicCardStock> select = new ArrayList<>();
		
		for(int l : ids)
		{
			select.add(((MagicCardStock)table.getValueAt(l, 0)));
		}
		return select;
		
	}
	
	private void importFromCollection(MagicCollection col) throws SQLException
	{
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
		
		model = new CardStockTableModel();
		magicCardDetailPanel = new MagicCardDetailPanel();
		
		
		JPanel centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		JPanel actionPanel = new JPanel();
		centerPanel.add(actionPanel, BorderLayout.NORTH);
				
				
				btnImport = new JButton();
				btnImport.setIcon(MTGConstants.ICON_IMPORT);
				btnImport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
				actionPanel.add(btnImport);
		
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
		new TableFilterHeader(table, AutoChoices.ENABLED);
		scrollTable.setViewportView(table);
		
		magicCardDetailPanel.enableThumbnail(true);
		
		splitPane = new JSplitPane();
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
	
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerPanel.add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(scrollTable);
		splitPane.setRightComponent(magicCardDetailPanel);
		
		rightPanel = new JPanel();
		rightPanel.setBackground(SystemColor.inactiveCaption);
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
		
		cboSelections = new JComboBox<>();
		
		cboSelections.setModel(new DefaultComboBoxModel<String>(selections));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.anchor = GridBagConstraints.NORTH;
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 1;
		rightPanel.add(cboSelections, gbc_comboBox);
		
		lblQte = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("QTY")+ " :");
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
		
		lblLanguage = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_LANGUAGE")+ " :");
		GridBagConstraints gbc_lblLanguage = new GridBagConstraints();
		gbc_lblLanguage.anchor = GridBagConstraints.EAST;
		gbc_lblLanguage.insets = new Insets(0, 0, 5, 5);
		gbc_lblLanguage.gridx = 0;
		gbc_lblLanguage.gridy = 3;
		rightPanel.add(lblLanguage, gbc_lblLanguage);
		
		DefaultComboBoxModel<String> lModel = new DefaultComboBoxModel<>();
		lModel.addElement(null);
		for(Locale l : Locale.getAvailableLocales())
			 lModel.addElement(l.getDisplayLanguage(Locale.US));
		
		cboLanguages = new JComboBox<>(lModel);
		GridBagConstraints gbc_cboLanguages = new GridBagConstraints();
		gbc_cboLanguages.insets = new Insets(0, 0, 5, 0);
		gbc_cboLanguages.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboLanguages.gridx = 1;
		gbc_cboLanguages.gridy = 3;
		rightPanel.add(cboLanguages, gbc_cboLanguages);
		
		lblFoil = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("FOIL")+ " :");
		GridBagConstraints gbc_lblFoil = new GridBagConstraints();
		gbc_lblFoil.anchor = GridBagConstraints.EAST;
		gbc_lblFoil.insets = new Insets(0, 0, 5, 5);
		gbc_lblFoil.gridx = 0;
		gbc_lblFoil.gridy = 4;
		rightPanel.add(lblFoil, gbc_lblFoil);
		
		cboFoil = new JComboBox<>(new DefaultComboBoxModel<Boolean>(values));
		GridBagConstraints gbc_cboFoil = new GridBagConstraints();
		gbc_cboFoil.insets = new Insets(0, 0, 5, 0);
		gbc_cboFoil.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboFoil.gridx = 1;
		gbc_cboFoil.gridy = 4;
		rightPanel.add(cboFoil, gbc_cboFoil);
		
		lblSigned = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("SIGNED")+ " :");
		GridBagConstraints gbc_lblSigned = new GridBagConstraints();
		gbc_lblSigned.anchor = GridBagConstraints.EAST;
		gbc_lblSigned.insets = new Insets(0, 0, 5, 5);
		gbc_lblSigned.gridx = 0;
		gbc_lblSigned.gridy = 5;
		rightPanel.add(lblSigned, gbc_lblSigned);
		
		cboSigned = new JComboBox<>(new DefaultComboBoxModel<Boolean>(values));
		GridBagConstraints gbc_cboSigned = new GridBagConstraints();
		gbc_cboSigned.insets = new Insets(0, 0, 5, 0);
		gbc_cboSigned.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboSigned.gridx = 1;
		gbc_cboSigned.gridy = 5;
		rightPanel.add(cboSigned, gbc_cboSigned);
		
		lblAltered = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("ALTERED")+ " :");
		GridBagConstraints gbc_lblAltered = new GridBagConstraints();
		gbc_lblAltered.anchor = GridBagConstraints.EAST;
		gbc_lblAltered.insets = new Insets(0, 0, 5, 5);
		gbc_lblAltered.gridx = 0;
		gbc_lblAltered.gridy = 6;
		rightPanel.add(lblAltered, gbc_lblAltered);
		
		cboAltered = new JComboBox<>(new DefaultComboBoxModel<Boolean>(values));
		GridBagConstraints gbc_cboAltered = new GridBagConstraints();
		gbc_cboAltered.insets = new Insets(0, 0, 5, 0);
		gbc_cboAltered.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboAltered.gridx = 1;
		gbc_cboAltered.gridy = 6;
		rightPanel.add(cboAltered, gbc_cboAltered);
		
		lblQuality = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("QUALITY")+ " :");
		GridBagConstraints gbc_lblQuality = new GridBagConstraints();
		gbc_lblQuality.anchor = GridBagConstraints.EAST;
		gbc_lblQuality.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuality.gridx = 0;
		gbc_lblQuality.gridy = 7;
		rightPanel.add(lblQuality, gbc_lblQuality);
		
		DefaultComboBoxModel<EnumCondition> qModel = new DefaultComboBoxModel<>();
		qModel.addElement(null);
		for(EnumCondition l : EnumCondition.values())
			 qModel.addElement(l);
		
		
		cboQuality = new JComboBox<>(qModel);
		
		
		
		GridBagConstraints gbc_cboQuality = new GridBagConstraints();
		gbc_cboQuality.insets = new Insets(0, 0, 5, 0);
		gbc_cboQuality.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboQuality.gridx = 1;
		gbc_cboQuality.gridy = 7;
		rightPanel.add(cboQuality, gbc_cboQuality);
		
		lblCollection = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION")+ " :");
		GridBagConstraints gbc_lblCollection = new GridBagConstraints();
		gbc_lblCollection.anchor = GridBagConstraints.EAST;
		gbc_lblCollection.insets = new Insets(0, 0, 5, 5);
		gbc_lblCollection.gridx = 0;
		gbc_lblCollection.gridy = 8;
		rightPanel.add(lblCollection, gbc_lblCollection);
		
		

		DefaultComboBoxModel<MagicCollection> cModel = new DefaultComboBoxModel<>();
		cModel.addElement(null);
		try {
			for(MagicCollection l : MTGControler.getInstance().getEnabledDAO().getCollections())
				 cModel.addElement(l);
		} catch (SQLException e1) {
			MTGLogger.printStackTrace(e1);
		}
		
		cboCollection = new JComboBox<>(cModel);
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
		
		btnApplyModification = new JButton(MTGControler.getInstance().getLangService().getCapitalize("APPLY"));
		
		
		GridBagConstraints gbc_btnApplyModification = new GridBagConstraints();
		gbc_btnApplyModification.gridwidth = 2;
		gbc_btnApplyModification.gridx = 0;
		gbc_btnApplyModification.gridy = 13;
		rightPanel.add(btnApplyModification, gbc_btnApplyModification);
		
		bottomPanel = new JPanel();
		add(bottomPanel, BorderLayout.SOUTH);
		
		lblCount = new JLabel();
		bottomPanel.add(lblCount);
		
		
		ThreadManager.getInstance().execute(()->{
				try {
					lblLoading.setVisible(true);
					model.init();
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),MTGControler.getInstance().getLangService().getCapitalize("ERROR"),JOptionPane.ERROR_MESSAGE);
				}
				lblLoading.setVisible(false);
				updateCount();
			
		}, "init stock");
		
	}
	
	public void updateCount()
	{
		lblCount.setText(MTGControler.getInstance().getLangService().getCapitalize("ITEMS_IN_STOCK")+": " + table.getRowCount());
	}

	
}
