package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.commons.codec.digest.DigestUtils;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGIA;
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.api.interfaces.MTGPictureEditor.MOD;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.pictures.impl.PersonalSetPicturesProvider;
import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.api.sorters.CardsEditionSorter;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.card.MagicEditionDetailPanel;
import org.magic.gui.components.dialog.importer.CardImporterDialog;
import org.magic.gui.components.editor.MagicCardEditorPanel;
import org.magic.gui.components.tech.ObjectViewerPanel;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;
public class CardBuilder2GUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JXTable editionsTable;
	private MagicEditionDetailPanel magicEditionDetailPanel;
	private MagicCardEditorPanel magicCardEditorPanel;
	private MagicEditionsTableModel editionModel;
	private JComboBox<MTGEdition> cboSets;
	private transient Image cardImage;
	private JPanel panelPictures;
	private JXTable cardsTable;
	private MagicCardTableModel cardsModel;
	private ObjectViewerPanel jsonPanel;
	private JTabbedPane tabbedPane;

	private transient PersonalSetPicturesProvider picturesProvider;
	private transient PrivateMTGSetProvider provider;
	private JButton btnRefresh;
	private AbstractBuzyIndicatorComponent buzyCard;
	private AbstractBuzyIndicatorComponent buzySet;

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_BUILDER;
	}

	@Override
	public String getTitle() {
		return capitalize("BUILDER_MODULE");
	}


	@Override
	public void onFirstShowing() {
			var sw = new AbstractObservableWorker<List<MTGEdition>, Void,MTGCardsProvider>(buzySet,provider){
				@Override
				protected List<MTGEdition> doInBackground() throws Exception {
					return plug.listEditions();
				}
				
				@Override
				protected void notifyEnd() {
					editionModel.bind(getResult());
					editionModel.fireTableDataChanged();
					cboSets.setModel(new DefaultComboBoxModel<>(getResult().toArray(new MTGEdition[getResult().size()])));
				}
			};
			ThreadManager.getInstance().runInEdt(sw, "loading personal sets");
	}

	public CardBuilder2GUI() {

			//////////////////////////////////////////////////// INIT LOCAL COMPONENTS
			var panelEditionHaut = new JPanel();
			var panelSets = new JPanel();
			var splitcardEdPanel = new JSplitPane();
			var panelCards = new JPanel();
			var panelCardsHaut = new JPanel();
			var tabbedCards = new JTabbedPane(SwingConstants.TOP);
			
			var btnSaveEdition = new JButton(MTGConstants.ICON_SAVE);
			var btnNewSet = new JButton(MTGConstants.ICON_NEW);
			var btnRemoveEdition = new JButton(MTGConstants.ICON_DELETE);
			var btnImport = new JButton(MTGConstants.ICON_IMPORT);
			var btnRebuildSet = new JButton(MTGConstants.ICON_MASS_IMPORT);
			var btnGenerateCard = new JButton(MTGConstants.ICON_IA);
			var btnGenerateSet= new JButton(MTGConstants.ICON_IA);
			var btnSaveCard = new JButton(MTGConstants.ICON_SAVE);
			var tabbedResult = new JTabbedPane(SwingConstants.TOP);
			var btnRemoveCard = new JButton(MTGConstants.ICON_DELETE);
			var btnNewCard = new JButton(MTGConstants.ICON_NEW);
			var btnReloadSets = new JButton(MTGConstants.ICON_REFRESH);
			
			//////////////////////////////////////////////////// INIT GLOBAL COMPONENTS
			buzyCard = AbstractBuzyIndicatorComponent.createLabelComponent();
			buzySet = AbstractBuzyIndicatorComponent.createLabelComponent();
			editionModel = new MagicEditionsTableModel();
			provider = new PrivateMTGSetProvider();
			provider.init();
			btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
			picturesProvider = new PersonalSetPicturesProvider();
			cardsModel = new MagicCardTableModel();
			jsonPanel = new ObjectViewerPanel();
			jsonPanel.setMaximumSize(new Dimension(400, 10));
			editionsTable = UITools.createNewTable(editionModel,false);
			cardsTable = UITools.createNewTable(cardsModel,false);
			tabbedPane = new JTabbedPane(SwingConstants.TOP);
			cboSets = new JComboBox<>();
			magicCardEditorPanel = new MagicCardEditorPanel();
			magicEditionDetailPanel = new MagicEditionDetailPanel();

			
			panelPictures = new JPanel() {
				private static final long serialVersionUID = 1L;
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(cardImage, 0, 0, null);

					if (magicCardEditorPanel.getImagePanel().getCroppedImage() != null && getEnabledPlugin(MTGPictureEditor.class).getMode()==MOD.LOCAL)
						g.drawImage(magicCardEditorPanel.getImagePanel().getCroppedImage(), 35, 68, 329, 242, null);

					revalidate();
				}
			};

			//////////////////////////////////////////////////// LAYOUT CONFIGURATION
			setLayout(new BorderLayout(0, 0));
			panelSets.setLayout(new BorderLayout(0, 0));
			panelCards.setLayout(new BorderLayout(0, 0));
	
			var gridBagLayout = (GridBagLayout) magicCardEditorPanel.getLayout();
			gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 0, 0};
			gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
			gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };

		
			splitcardEdPanel.setDividerLocation(0.5);
			splitcardEdPanel.setResizeWeight(0.5);


			//////////////////////////////////////////////////// PANEL ADDS
			add(tabbedPane);
			panelCards.add(panelCardsHaut, BorderLayout.NORTH);
			panelSets.add(panelEditionHaut, BorderLayout.NORTH);
			panelEditionHaut.add(btnNewSet);
			panelEditionHaut.add(btnGenerateSet);
			panelEditionHaut.add(btnReloadSets);
			panelEditionHaut.add(btnSaveEdition);
			panelEditionHaut.add(btnRemoveEdition);
			panelEditionHaut.add(btnRebuildSet);
			panelEditionHaut.add(buzySet);
			
			panelSets.add(splitcardEdPanel, BorderLayout.CENTER);
			
			
			panelCardsHaut.add(cboSets);
			panelCardsHaut.add(btnNewCard);
			panelCardsHaut.add(btnGenerateCard);
			panelCardsHaut.add(btnImport);
			panelCardsHaut.add(btnSaveCard);
			panelCardsHaut.add(btnRefresh);
			panelCardsHaut.add(btnRemoveCard);
			panelCardsHaut.add(buzyCard);
			
			panelCards.add(tabbedResult, BorderLayout.EAST);
			tabbedPane.addTab("Set", MTGConstants.ICON_TAB_BACK, panelSets, null);
			tabbedPane.addTab("Cards", MTGConstants.ICON_TAB_DECK, panelCards, null);
			tabbedResult.addTab("Pictures", MTGConstants.ICON_TAB_PICTURE, panelPictures, null);
			tabbedResult.addTab("Object",MTGConstants.ICON_TAB_JSON, jsonPanel,null);
		
			panelCards.add(tabbedCards, BorderLayout.CENTER);
			tabbedCards.addTab("Details", MTGConstants.ICON_TAB_DETAILS, magicCardEditorPanel, null);
			splitcardEdPanel.setLeftComponent(new JScrollPane(editionsTable));

			splitcardEdPanel.setRightComponent( new JScrollPane(cardsTable));
			panelSets.add(magicEditionDetailPanel, BorderLayout.EAST);


			//////////////////////////////////////////////////// COMPONENT CONFIG

			splitcardEdPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
			btnSaveEdition.setToolTipText("Save the set");
			btnNewSet.setToolTipText("New set");
			btnRemoveEdition.setToolTipText("Delete Set");
			btnRebuildSet.setToolTipText("Rebuild Set");
			btnReloadSets.setToolTipText("Reload");
			
			btnImport.setToolTipText("Import existing card");
			btnSaveCard.setToolTipText("Save the card");
			btnRefresh.setToolTipText("Refresh");
			btnNewCard.setToolTipText("New Card");
			btnRemoveCard.setToolTipText("Delete the card");

			magicEditionDetailPanel.setEditable(true);
			
		
			cardsTable.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
			
			panelPictures.setBackground(Color.WHITE);
			panelPictures.setPreferredSize(new Dimension(400, 10));

			cardsModel.setDefaultHiddenComlumns(1,7,8,9,10,11,12,13,14,15,16);
			UITools.initTableVisibility(cardsTable, cardsModel);
			
			editionModel.setDefaultHiddenComlumns(4,5,8,9);
			UITools.initTableVisibility(editionsTable, editionModel);
			
			
			//////////////////////////////////////////////////// ACTION LISTENER

			btnRebuildSet.addActionListener(_->{
				MTGEdition ed = UITools.getTableSelection(editionsTable, 1);
				
				if(ed!=null)
					ThreadManager.getInstance().runInEdt(new AbstractObservableWorker<Void, Void, MTGCardsProvider>(buzySet,provider) {
	
						@Override
						protected Void doInBackground() throws Exception {
							
								List<MTGCard> cards = provider.searchCardByEdition(ed);
								ed.setCardCount(cards.size());
								ed.setCardCountOfficial(cards.size());

								cards.forEach(mc->{
									mc.getEditions().clear();
									try {
										mc.getEditions().add(BeanTools.cloneBean(ed));
										mc.setNumber(null);
									} catch (Exception e) {
										logger.error(e);
									} 
								});
								Collections.sort(cards,new CardsEditionSorter());

								for(var i=0;i<cards.size();i++){
									cards.get(i).setNumber(String.valueOf((i+1)));
								}
										
										
								provider.saveEdition(ed,cards);
								return null;
						}
					}, "calculate Set " +ed);
			});

			btnRemoveCard.addActionListener(_ -> {
				try {
					int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().get("CONFIRM_DELETE", magicCardEditorPanel.getMagicCard()));
					if (res == JOptionPane.YES_OPTION) {
						provider.removeCard((MTGEdition) cboSets.getSelectedItem(), magicCardEditorPanel.getMagicCard());
						picturesProvider.removePicture((MTGEdition) cboSets.getSelectedItem(), magicCardEditorPanel.getMagicCard());
						initCard(new MTGCard());
					}
				} catch (IOException ex) {
					MTGControler.getInstance().notify(ex);
				}
			});

			btnSaveEdition.addActionListener(_ -> {
				try {
					var ed = magicEditionDetailPanel.getMagicEdition();
					provider.saveEdition(ed);
					cboSets.removeAllItems();
					cboSets.setModel(new DefaultComboBoxModel<>(provider.listEditions().toArray(new MTGEdition[provider.listEditions().size()])));
					editionModel.fireTableDataChanged();
				} catch (Exception ex) {
					MTGControler.getInstance().notify(ex);
				}
			});

			btnImport.addActionListener(_ -> {
				var l = new CardImporterDialog();
				l.setVisible(true);
				if (l.hasSelected())
					initCard(l.getSelectedItem());

			});
			
			btnNewCard.addActionListener(_ -> {
				var mc = new MTGCard();
				var ed = (MTGEdition)cboSets.getSelectedItem();
				
				mc.setEdition(ed);
				mc.getEditions().add(ed);
				try {
					mc.setNumber(String.valueOf(provider.searchCardByEdition((MTGEdition) cboSets.getSelectedItem()).size() + 1));
				} catch (IOException e1) {
					logger.error(e1);
				}
				initCard(mc);
			});
			
			btnReloadSets.addActionListener(_->{
				onFirstShowing();
			});
			
			
			btnGenerateSet.addActionListener(_->{
				MTGEdition set = UITools.getTableSelection(editionsTable, 1);
				
				if(set==null)
					return;
				
				buzySet.start();
				buzySet.setText("generating set from IA");
				var text = JOptionPane.showInputDialog("Little description ?");
				
				
				
				ThreadManager.getInstance().runInEdt(new SwingWorker<List<MTGCard>, Void>() {
						@Override
						protected List<MTGCard> doInBackground() throws Exception {
							return MTG.getEnabledPlugin(MTGIA.class).generateSet(text,set,5);
						}
						
						@Override
						protected void done() {
							try {
								cardsModel.addItems(get());
								provider.saveEdition(set, get());
								
							} catch (InterruptedException _) {
								Thread.currentThread().interrupt();
							} catch (ExecutionException e) {
								logger.error(e);
							}
							finally {
								buzySet.end();
							}
						}
					}, "generating set from IA");
				
			
				
				
			});
			

			btnNewSet.addActionListener(_ -> {
				var id = JOptionPane.showInputDialog("ID");
				
				if(id.isEmpty())
					return;
				
				try {
					if(provider.listEditions().stream().anyMatch(ed->ed.getId().equals(id)))
					{
						MTGControler.getInstance().notify(new Exception("Set already present"));
						return;
					}
					
				var ed = new MTGEdition(id,id);
						provider.saveEdition(ed);
						editionModel.addItem(ed);
						magicEditionDetailPanel.setMagicEdition(ed, true);
						cboSets.addItem(ed);
						
				} catch (IOException e1) {
					MTGControler.getInstance().notify(e1);
				}
				
			});

			btnRemoveEdition.addActionListener(_ -> {

				MTGEdition ed = UITools.getTableSelection(editionsTable, 1);
				int res = JOptionPane.showConfirmDialog(null,MTGControler.getInstance().getLangService().get("CONFIRM_DELETE", ed),MTGControler.getInstance().getLangService().get("DELETE"), JOptionPane.YES_NO_OPTION);
				
				if (res == JOptionPane.YES_OPTION) {
					provider.removeEdition(ed);
					cboSets.removeItem(ed);
					editionModel.removeItem(ed);
				}
			});

			cardsTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {

					if(UITools.getTableSelections(cardsTable, 1).isEmpty())
						return;

					MTGCard mc = UITools.getTableSelection(cardsTable, 0);
					if (me.getClickCount() == 2) {
						initCard(mc);
						tabbedPane.setSelectedIndex(1);
					}
					
				}
			});
			
			
			editionsTable.getSelectionModel().addListSelectionListener(_->{

					if(UITools.getTableSelections(editionsTable, 1).isEmpty())
						return;

					MTGEdition ed =UITools.getTableSelection(editionsTable, 1);
					try {
						initEdition(ed);
						cardsModel.bind(provider.searchCardByEdition(ed));
						cardsModel.fireTableDataChanged();
						cardsTable.packAll();
					} catch (IOException e) {
						MTGControler.getInstance().notify(e);
					}
				
			});
			
			btnGenerateCard.addActionListener(_->{
				buzyCard.start();
				buzyCard.setText("generating card from IA");
				var text = JOptionPane.showInputDialog("Little description ?");
				
				ThreadManager.getInstance().runInEdt(new SwingWorker<MTGCard, Void>() {
						@Override
						protected MTGCard doInBackground() throws Exception {
							return MTG.getEnabledPlugin(MTGIA.class).generateRandomCard(text,(MTGEdition)cboSets.getSelectedItem(),String.valueOf(provider.searchCardByEdition((MTGEdition) cboSets.getSelectedItem()).size() + 1));
						}
						
						@Override
						protected void done() {
							try {
								initCard(get());
								
							} catch (InterruptedException _) {
								Thread.currentThread().interrupt();
							} catch (ExecutionException e) {
								logger.error(e);
							}
							finally {
								buzyCard.end();
								renderingCard();
							}
						}
					}, "generating card from IA");
			});

			btnSaveCard.addActionListener(_ -> {
				var me = (MTGEdition) cboSets.getSelectedItem();
				var mc = magicCardEditorPanel.getMagicCard();
					 

				if (mc.getId() == null)
					mc.setId(DigestUtils.sha256Hex(me.getSet() + mc.getId() + mc.getName()));
					
				try {
					provider.addCard(me, mc);
					var bi = new BufferedImage(panelPictures.getSize().width, 560,BufferedImage.TYPE_INT_ARGB);
					var g = bi.createGraphics();
					panelPictures.paint(g);
					g.dispose();
					picturesProvider.savePicture(bi, mc, me);
				} catch (IOException ex) {
					MTGControler.getInstance().notify(ex);
				}
			});

			btnRefresh.addActionListener(_ -> renderingCard());
	}

	private void renderingCard() {
		buzyCard.start();
		buzyCard.setText("Rendering");
		ThreadManager.getInstance().runInEdt(new SwingWorker<BufferedImage, Void>(){

			@Override
			protected BufferedImage doInBackground() throws Exception {
				return getEnabledPlugin(MTGPictureEditor.class).getPicture(magicCardEditorPanel.getMagicCard(),(MTGEdition) cboSets.getSelectedItem());
			}

			@Override
			protected void done() {
				
				BufferedImage img;
				try {
					img = get();
					if(img!=null){
						cardImage = ImageTools.scaleResize(img,panelPictures.getWidth());
					}
					panelPictures.revalidate();
					panelPictures.repaint();
					jsonPanel.init(magicCardEditorPanel.getMagicCard());
					
				} catch (InterruptedException _) {
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					MTGControler.getInstance().notify(e);
				}
				finally {
					buzyCard.end();
				}
			}
		}, "refresh generated card");
		
	}

	protected void initCard(MTGCard mc) {
		magicCardEditorPanel.setMagicCard(mc);
		cboSets.setSelectedItem(mc.getEdition());
		jsonPanel.init(mc);
		
		ThreadManager.getInstance().runInEdt(new AbstractObservableWorker<BufferedImage, Void,MTGPictureProvider>(buzyCard,picturesProvider) {
			@Override
			protected BufferedImage doInBackground() throws Exception {
				return plug.getPicture(mc);
			}
		
			@Override
			protected void notifyEnd () {
					cardImage = getResult();
					panelPictures.repaint();
			}
			
		}, "loading picture");
		
	}

	protected void initEdition(MTGEdition ed) {
		magicEditionDetailPanel.init(ed);
	}

}
