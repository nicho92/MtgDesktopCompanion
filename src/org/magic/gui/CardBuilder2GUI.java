package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.pictures.impl.PersonalSetPicturesProvider;
import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.gui.components.JSONPanel;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.components.editor.CropImagePanel;
import org.magic.gui.components.editor.MagicCardEditorPanel;
import org.magic.gui.models.MagicCardNamesTableModel;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.MagicCardNameEditor;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGCardMakerPicturesProvider;
import org.magic.services.MTGDesktopCompanionControler;
import javax.swing.JToggleButton;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;

public class CardBuilder2GUI extends JPanel{
	
	private JXTable editionsTable;
	private MagicEditionDetailPanel magicEditionDetailPanel;
	private MagicCardEditorPanel magicCardEditorPanel;
	private MagicEditionsTableModel editionModel;
	private PrivateMTGSetProvider provider;
	private JComboBox<MagicEdition> cboSets;
	private CropImagePanel panelImage;
	private MTGCardMakerPicturesProvider picProvider;
	private Image cardImage;
	private JPanel panelPictures;
	private JXTable cardsTable;
	private MagicCardTableModel cardsModel;
	private JSONPanel jsonPanel;
	private JTabbedPane tabbedPane;
	static final Logger logger = LogManager.getLogger(CardBuilder2GUI.class.getName());
	private JSpinner spinCommon;
	private JSpinner spinRare;
	private JSpinner spinUnco;
	private JPanel foreignNamesEditorPanel ;
	private PersonalSetPicturesProvider picturesProvider;
	private JButton btnRefresh;
	private JTable listNames;
	private MagicCardNamesTableModel namesModel;
	public CardBuilder2GUI() {
		try{

		logger.info("init Builder GUI");
				
////////////////////////////////////////////////////INIT LOCAL COMPONENTS			
		JPanel panelEditionHaut = new JPanel();
		JPanel panelSets = new JPanel();
		JButton btnSaveEdition = new JButton("Save");
		JButton btnNewSet = new JButton("New Set");
		JButton btnRemoveEdition = new JButton("Remove");
		JSplitPane splitcardEdPanel = new JSplitPane();
		JScrollPane scrollTableEdition = new JScrollPane();
		JPanel panelCards = new JPanel();
		JPanel panelCardsHaut = new JPanel();
		JButton btnImport = new JButton("");
		JScrollPane scrollTableCards = new JScrollPane();
		JButton btnSaveCard = new JButton("");
		JButton btnAddName = new JButton("add Languages");
		JTabbedPane tabbedResult = new JTabbedPane(JTabbedPane.TOP);
		JButton btnRemoveCard = new JButton("");
		JButton btnNewCard = new JButton("");
		JPanel panelBooster = new JPanel();
		JLabel lblCommon = new JLabel("Common :");
		JLabel lblUncommon = new JLabel("Uncommon :");
		JLabel lblRareMythic = new JLabel("Rare/Mythic :");

		JTabbedPane tabbedCards = new JTabbedPane(JTabbedPane.TOP);
		JButton btnImage = new JButton("Image");
		JPanel panelMisc = new JPanel();
		JPanel panelCardEditions = new JPanel();
		JPanel legalitiesPanel = new JPanel();
		
////////////////////////////////////////////////////INIT GLOBAL COMPONENTS		
		editionModel = new MagicEditionsTableModel();
		provider=new PrivateMTGSetProvider();
		btnRefresh = new JButton("");
		picturesProvider= new PersonalSetPicturesProvider();
		spinCommon = new JSpinner();
		spinRare = new JSpinner();
		spinUnco = new JSpinner();
		picProvider = new MTGCardMakerPicturesProvider();
		cardsModel = new MagicCardTableModel();
		jsonPanel = new JSONPanel();
		jsonPanel.setMaximumSize(new Dimension(400, 10));
		editionsTable = new JXTable();
		cardsTable = new JXTable();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		cboSets = new JComboBox<MagicEdition>();
		namesModel = new MagicCardNamesTableModel();
		panelPictures = new JPanel(){
			protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					 g.drawImage(cardImage, 0, 0, null);
					 if(panelImage.getCroppedImage()!=null)
						g.drawImage(panelImage.getCroppedImage(), 35, 68, 329, 242, null);
			}
		};
		
		foreignNamesEditorPanel = new JPanel();
		listNames = new JTable();
		panelImage = new CropImagePanel();
		magicCardEditorPanel = new MagicCardEditorPanel();
		magicEditionDetailPanel = new MagicEditionDetailPanel(false);
		

////////////////////////////////////////////////////MODELS INIT		
		editionsTable.setModel(editionModel);
		cardsTable.setModel(cardsModel);
		listNames.setModel(namesModel);

		spinCommon.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		spinUnco.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		spinRare.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		try {
			cboSets.setModel(new DefaultComboBoxModel<MagicEdition>(provider.loadEditions().toArray(new MagicEdition[provider.loadEditions().size()])));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
////////////////////////////////////////////////////LAYOUT CONFIGURATION			
		setLayout(new BorderLayout(0, 0));
		panelSets.setLayout(new BorderLayout(0, 0));
		panelCards.setLayout(new BorderLayout(0, 0));
		panelMisc.setLayout(new BorderLayout(0, 0));
		
		
		GridBagLayout gbl_panelBooster = new GridBagLayout();
		gbl_panelBooster.columnWidths = new int[]{218, 218, 0};
		gbl_panelBooster.rowHeights = new int[]{38, 41, 37, 0};
		gbl_panelBooster.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panelBooster.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		GridBagConstraints gbc_lblCommon = new GridBagConstraints();
		gbc_lblCommon.fill = GridBagConstraints.BOTH;
		gbc_lblCommon.insets = new Insets(0, 0, 5, 5);
		gbc_lblCommon.gridx = 0;
		gbc_lblCommon.gridy = 0;


		GridBagConstraints gbc_spinCommon = new GridBagConstraints();
		gbc_spinCommon.fill = GridBagConstraints.BOTH;
		gbc_spinCommon.insets = new Insets(0, 0, 5, 0);
		gbc_spinCommon.gridx = 1;
		gbc_spinCommon.gridy = 0;

		GridBagConstraints gbc_lblUncommon = new GridBagConstraints();
		gbc_lblUncommon.fill = GridBagConstraints.BOTH;
		gbc_lblUncommon.insets = new Insets(0, 0, 5, 5);
		gbc_lblUncommon.gridx = 0;
		gbc_lblUncommon.gridy = 1;

		
		GridBagConstraints gbc_spinUnco = new GridBagConstraints();
		gbc_spinUnco.fill = GridBagConstraints.BOTH;
		gbc_spinUnco.insets = new Insets(0, 0, 5, 0);
		gbc_spinUnco.gridx = 1;
		gbc_spinUnco.gridy = 1;
		
		GridBagConstraints gbc_lblRareMythic = new GridBagConstraints();
		gbc_lblRareMythic.fill = GridBagConstraints.BOTH;
		gbc_lblRareMythic.insets = new Insets(0, 0, 0, 5);
		gbc_lblRareMythic.gridx = 0;
		gbc_lblRareMythic.gridy = 2;
		GridBagConstraints gbc_spinRare = new GridBagConstraints();
		gbc_spinRare.fill = GridBagConstraints.BOTH;
		gbc_spinRare.gridx = 1;
		gbc_spinRare.gridy = 2;
		
		GridBagLayout gridBagLayout = (GridBagLayout) magicCardEditorPanel.getLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0};
		GridBagConstraints gbc_btnImage = new GridBagConstraints();
		gbc_btnImage.insets = new Insets(0, 0, 0, 5);
		gbc_btnImage.gridx = 0;
		gbc_btnImage.gridy = 12;
		GridBagConstraints gbc_cropImagePanel = new GridBagConstraints();
		gbc_cropImagePanel.gridwidth = 4;
		gbc_cropImagePanel.fill = GridBagConstraints.BOTH;
		gbc_cropImagePanel.gridx = 1;
		gbc_cropImagePanel.gridy = 12;
		
		panelBooster.setLayout(gbl_panelBooster);
		

////////////////////////////////////////////////////PANEL ADDS		
		add(tabbedPane);
		panelCards.add(panelCardsHaut, BorderLayout.NORTH);
		panelSets.add(panelEditionHaut, BorderLayout.NORTH);
		panelEditionHaut.add(btnNewSet);
		panelEditionHaut.add(btnSaveEdition);
		panelEditionHaut.add(btnRemoveEdition);
		panelSets.add(splitcardEdPanel, BorderLayout.CENTER);
		panelCardsHaut.add(cboSets);
		panelCardsHaut.add(btnNewCard);
		panelCardsHaut.add(btnImport);
		panelCardsHaut.add(btnSaveCard);
		panelCardsHaut.add(btnRefresh);
		panelCards.add(tabbedResult, BorderLayout.EAST);
		panelCardsHaut.add(btnRemoveCard);
		tabbedPane.addTab("Set", null, panelSets, null);
		tabbedPane.addTab("Cards", null, panelCards, null);
		tabbedResult.addTab("Pictures", null, panelPictures, null);
		tabbedResult.addTab("JSON", jsonPanel);
		panelBooster.add(lblCommon, gbc_lblCommon);
		panelBooster.add(spinCommon, gbc_spinCommon);
		panelBooster.add(lblUncommon, gbc_lblUncommon);
		panelBooster.add(spinUnco, gbc_spinUnco);
		panelBooster.add(lblRareMythic, gbc_lblRareMythic);
		panelBooster.add(spinRare, gbc_spinRare);
		panelCards.add(tabbedCards, BorderLayout.CENTER);
		magicCardEditorPanel.add(btnImage, gbc_btnImage);
		magicCardEditorPanel.add(panelImage, gbc_cropImagePanel);
		tabbedCards.addTab("Details", null, magicCardEditorPanel, null);
		tabbedCards.addTab("Editions", null, panelCardEditions, null);
		tabbedCards.addTab("Misc", null, panelMisc, null);
		panelMisc.add(legalitiesPanel, BorderLayout.SOUTH);
		
////////////////////////////////////////////////////COMPONENT CONFIG
		editionModel.init(provider.loadEditions());
		editionModel.fireTableDataChanged();
		splitcardEdPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitcardEdPanel.setLeftComponent(scrollTableEdition);
		scrollTableEdition.setViewportView(editionsTable);
		splitcardEdPanel.setRightComponent(scrollTableCards);
		scrollTableCards.setViewportView(cardsTable);
		btnImport.setToolTipText("Import existing card");
		btnSaveEdition.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/save.png")));
		btnNewSet.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/new.png")));
		btnRemoveEdition.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/delete.png")));
		panelSets.add(magicEditionDetailPanel, BorderLayout.EAST);
		magicEditionDetailPanel.setEditable(true);
		magicEditionDetailPanel.setRightComponent(panelBooster);
		
	
		btnImport.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/import.png")));
		btnSaveCard.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/save.png")));
		btnRefresh.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/refresh.png")));
		btnRemoveCard.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/delete.png")));
		btnNewCard.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/new.png")));
		cardsTable.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());	
		panelPictures.setBackground(Color.WHITE);
		panelPictures.setPreferredSize(new Dimension(400, 10));
		
		listNames.getColumnModel().getColumn(0).setCellEditor(new MagicCardNameEditor());
			
		
				
				panelMisc.add(foreignNamesEditorPanel);
				foreignNamesEditorPanel.setLayout(new BorderLayout(0, 0));
				
				JScrollPane scrollPane = new JScrollPane();
				foreignNamesEditorPanel.add(scrollPane);
				
				
				scrollPane.setViewportView(listNames);
				
				JPanel buttonsForeignNamesPanel = new JPanel();
				foreignNamesEditorPanel.add(buttonsForeignNamesPanel, BorderLayout.NORTH);
				buttonsForeignNamesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
				
				
			
				buttonsForeignNamesPanel.add(btnAddName);
				
				JButton btnRemoveName = new JButton("Remove");
				buttonsForeignNamesPanel.add(btnRemoveName);
				
				
				panelImage.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		
////////////////////////////////////////////////////ACTION LISTENER
		btnAddName.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					MagicCardNames name = new MagicCardNames();
						name.setLanguage("");
						name.setName("");
					magicCardEditorPanel.getMagicCard().getForeignNames().add(name);
					namesModel.init(magicCardEditorPanel.getMagicCard());
		
				}
		});
				
				
		btnImage.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						JFileChooser choose = new JFileChooser();
						choose.showOpenDialog(null);
						File pics = choose.getSelectedFile();
						
						Image i = new ImageIcon(pics.getAbsolutePath()).getImage();
						panelImage.setImage(i.getScaledInstance(panelImage.getWidth(), panelImage.getHeight(), Image.SCALE_SMOOTH));
						panelImage.revalidate();
						panelImage.repaint();
					}
				});
		btnNewCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MagicCard mc = new MagicCard();
					try 
					{
						mc.setNumber(String.valueOf(provider.getCards((MagicEdition)cboSets.getSelectedItem()).size()+1));
						logger.debug("create new card for " + cboSets.getSelectedItem()  + " num = " + mc.getNumber() );
					} catch (IOException e1) {
					}
				initCard(mc);
			}
		});
		btnRemoveCard.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					provider.removeCard((MagicEdition)cboSets.getSelectedItem(), magicCardEditorPanel.getMagicCard());
					picturesProvider.removePicture((MagicEdition)cboSets.getSelectedItem(), magicCardEditorPanel.getMagicCard());
					initCard(new MagicCard());
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		btnSaveEdition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					MagicEdition ed = magicEditionDetailPanel.getMagicEdition();
					List<Object> boos= new ArrayList<Object>();
					for(int i=0;i<(Integer)spinCommon.getValue();i++)
						boos.add("common");
					for(int i=0;i<(Integer)spinUnco.getValue();i++)
						boos.add("uncommon");
					for(int i=0;i<(Integer)spinRare.getValue();i++)
						boos.add(new String[]{"rare","mythic rare"});
					ed.setBooster(boos);
					provider.saveEdition(ed);
					try {
						cboSets.removeAllItems();
						cboSets.setModel(new DefaultComboBoxModel<MagicEdition>(provider.loadEditions().toArray(new MagicEdition[provider.loadEditions().size()])));
					} catch (Exception e) {
						//e.printStackTrace();
					}
					editionModel.init(provider.loadEditions());
					editionModel.fireTableDataChanged();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JDialog l = new JDialog();
				final CardSearchGUI searchPane = new CardSearchGUI();
				JButton selectCard = new JButton(new ImageIcon(CardBuilder2GUI.class.getResource("/res/import.png")));
				selectCard.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MagicCard mc = searchPane.getSelected();
						initCard(mc);
						l.dispose();
					}

					
				});
				
				l.getContentPane().setLayout(new BorderLayout());
				l.getContentPane().add(searchPane,BorderLayout.CENTER);
				l.getContentPane().add(selectCard,BorderLayout.SOUTH);
				l.setModal(true);
				l.pack();
				l.setVisible(true);
			}
		});
		btnNewSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				magicEditionDetailPanel.setMagicEdition(new MagicEdition(),true);
			}
		});
		btnRemoveEdition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				MagicEdition ed = (MagicEdition)editionsTable.getValueAt(editionsTable.getSelectedRow(), 1);
				
				int res = JOptionPane.showConfirmDialog(null,"Delete", "Delete " + ed + " ?",JOptionPane.YES_NO_OPTION);
				
				if(res==JOptionPane.YES_OPTION)
				{ 
					provider.removeEdition(ed);
					try {
						editionModel.init(provider.loadEditions());
						editionModel.fireTableDataChanged();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
					}
				}				
			}
		});
		
		cardsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				MagicCard ed = (MagicCard)cardsTable.getValueAt(cardsTable.getSelectedRow(), 0);
				
				if(arg0.getClickCount()==2){
					initCard(ed);
					tabbedPane.setSelectedIndex(1);
				}
				
			}
		});
		editionsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				MagicEdition ed = (MagicEdition)editionsTable.getValueAt(editionsTable.getSelectedRow(), 1);
				try {
					initEdition(ed);
					cardsModel.init(provider.getCards(ed));
					cardsModel.fireTableDataChanged();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSaveCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MagicEdition me = (MagicEdition)cboSets.getSelectedItem();
				MagicCard mc = magicCardEditorPanel.getMagicCard();
					me.setNumber(mc.getNumber());
					me.setRarity(mc.getRarity());
					me.setArtist(mc.getArtist());
					me.setFlavor(mc.getFlavor());
					
					if(mc.getId()==null)
						mc.setId(DigestUtils.sha1Hex(me.getSet()+mc.getId()));
					
					
					if(!mc.getEditions().contains(me))
						mc.getEditions().add(0,me);
				try {
					provider.addCard(me, mc);
					BufferedImage bi = new BufferedImage(panelPictures.getSize().width, 560, BufferedImage.TYPE_INT_ARGB); 
					Graphics2D g = bi.createGraphics();
					panelPictures.paint(g);  
					g.dispose();
					picturesProvider.savePicture(bi, mc,me);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
				}
				
				
			}
		});
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					cardImage = ImageIO.read(picProvider.getPictureURL(magicCardEditorPanel.getMagicCard()));
					panelPictures.revalidate();
					panelPictures.repaint();
					jsonPanel.showCard(magicCardEditorPanel.getMagicCard());
					
				} catch (Exception e) {
					e.printStackTrace();
					//JOptionPane.showMessageDialog(null, e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
				} 
			}
		});
		
		
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
		}
	}

	
	protected void initCard(MagicCard mc) {
		magicCardEditorPanel.setMagicCard(mc);
		btnRefresh.doClick();
		namesModel.init(mc);

	}
	
	protected void initEdition(MagicEdition ed) {
		magicEditionDetailPanel.setMagicEdition(ed);
		
		spinCommon.setValue(0);
		spinUnco.setValue(0);
		spinRare.setValue(0);
		
		
		for(Object o : ed.getBooster())
		{
			if(o.equals("common"))
				spinCommon.setValue((int)spinCommon.getValue()+1);
			else if(o.equals("uncommon"))
				spinUnco.setValue((int)spinUnco.getValue()+1);
			else 
				spinRare.setValue((int)spinRare.getValue()+1);
		}
		
		
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		MTGDesktopCompanionControler.getInstance().getEnabledProviders().init();
		MTGDesktopCompanionControler.getInstance().getEnabledDAO().init();
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new CardBuilder2GUI());
		f.pack();
		f.setVisible(true);
	}
	
}
