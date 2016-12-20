package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.gui.components.CropImagePanel;
import org.magic.gui.components.JSONPanel;
import org.magic.gui.components.MagicCardEditorPanel;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGCardMakerPicturesProvider;
import org.magic.services.MTGDesktopCompanionControler;

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
		JButton btnOpen = new JButton("");
		JButton btnImage = new JButton("Image");
		JScrollPane scrollTableCards = new JScrollPane();
		JButton btnSaveCard = new JButton("");
		JButton btnRefresh = new JButton("");
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		JButton btnRemoveCard = new JButton("");
		JButton btnNewCard = new JButton("");
		
////////////////////////////////////////////////////INIT GLOBAL COMPONENTS		
		editionModel = new MagicEditionsTableModel();
		provider=new PrivateMTGSetProvider();
		picProvider = new MTGCardMakerPicturesProvider();
		cardsModel = new MagicCardTableModel();
		jsonPanel = new JSONPanel();
		jsonPanel.setMaximumSize(new Dimension(400, 10));
		magicCardEditorPanel = new MagicCardEditorPanel();
		magicEditionDetailPanel = new MagicEditionDetailPanel(false);
		editionsTable = new JXTable();
		cardsTable = new JXTable();
		panelImage = new CropImagePanel();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		cboSets = new JComboBox<MagicEdition>();
		panelPictures = new JPanel(){
			protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					 g.drawImage(cardImage, 0, 0, null);
					 if(panelImage.getCroppedImage()!=null)
						g.drawImage(panelImage.getCroppedImage(), 35, 68, 329, 242, null);
			}
		};

			
////////////////////////////////////////////////////LAYOUT CONFIGURATION			
		GridBagLayout gridBagLayout = (GridBagLayout) magicCardEditorPanel.getLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0};
		GridBagConstraints gbc_btnImage = new GridBagConstraints();
		gbc_btnImage.insets = new Insets(0, 0, 0, 5);
		gbc_btnImage.gridx = 0;
		gbc_btnImage.gridy = 13;
		GridBagConstraints gbc_cropImagePanel = new GridBagConstraints();
		gbc_cropImagePanel.gridwidth = 4;
		gbc_cropImagePanel.fill = GridBagConstraints.BOTH;
		gbc_cropImagePanel.gridx = 1;
		gbc_cropImagePanel.gridy = 13;
		setLayout(new BorderLayout(0, 0));
		panelSets.setLayout(new BorderLayout(0, 0));
		panelCards.setLayout(new BorderLayout(0, 0));


////////////////////////////////////////////////////MODELS INIT		
		editionsTable.setModel(editionModel);
		cardsTable.setModel(cardsModel);
		cboSets.setModel(new DefaultComboBoxModel<MagicEdition>(provider.loadEditions().toArray(new MagicEdition[provider.loadEditions().size()])));
		

////////////////////////////////////////////////////PANEL ADDS		
		add(tabbedPane);
		panelCards.add(panelCardsHaut, BorderLayout.NORTH);
		panelSets.add(panelEditionHaut, BorderLayout.NORTH);
		panelEditionHaut.add(btnNewSet);
		panelEditionHaut.add(btnSaveEdition);
		panelEditionHaut.add(btnRemoveEdition);
		panelSets.add(magicEditionDetailPanel, BorderLayout.EAST);
		panelSets.add(splitcardEdPanel, BorderLayout.CENTER);
		panelCardsHaut.add(cboSets);
		panelCardsHaut.add(btnNewCard);
		panelCardsHaut.add(btnOpen);
		panelCardsHaut.add(btnSaveCard);
		panelCardsHaut.add(btnRefresh);
		panelCards.add(magicCardEditorPanel, BorderLayout.CENTER);
		magicCardEditorPanel.add(btnImage, gbc_btnImage);
		magicCardEditorPanel.add(panelImage, gbc_cropImagePanel);
		panelCards.add(tabbedPane_1, BorderLayout.EAST);
		panelCardsHaut.add(btnRemoveCard);
		tabbedPane.addTab("Set", null, panelSets, null);
		tabbedPane.addTab("Cards", null, panelCards, null);
		tabbedPane_1.addTab("Pictures", null, panelPictures, null);
		tabbedPane_1.addTab("JSON", jsonPanel);
		
////////////////////////////////////////////////////COMPONENT CONFIG
		editionModel.init(provider.loadEditions());
		editionModel.fireTableDataChanged();
		magicEditionDetailPanel.setEditable(true);
		splitcardEdPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitcardEdPanel.setLeftComponent(scrollTableEdition);
		scrollTableEdition.setViewportView(editionsTable);
		splitcardEdPanel.setRightComponent(scrollTableCards);
		scrollTableCards.setViewportView(cardsTable);
		btnOpen.setToolTipText("Import existing card");
		panelImage.setBorder(new LineBorder(new Color(0, 0, 0)));
		btnSaveEdition.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/save.png")));
		btnNewSet.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/new.png")));
		btnRemoveEdition.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/delete.png")));
		btnOpen.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/import.png")));
		btnSaveCard.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/save.png")));
		btnRefresh.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/refresh.png")));
		btnRemoveCard.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/delete.png")));
		btnNewCard.setIcon(new ImageIcon(CardBuilder2GUI.class.getResource("/res/new.png")));
		
		
		cardsTable.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());	
		
		panelPictures.setBackground(Color.WHITE);
		panelPictures.setPreferredSize(new Dimension(400, 10));
		
////////////////////////////////////////////////////ACTION LISTENER		
		
		btnNewCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MagicCard mc = new MagicCard();
					try 
					{
						mc.setNumber(String.valueOf(provider.getCards((MagicEdition)cboSets.getSelectedItem()).size()+1));
						logger.debug("create new card for " + cboSets.getSelectedItem()  + " num = " + mc.getNumber() );
					} catch (IOException e1) {
					}
				magicCardEditorPanel.setMagicCard(mc);
			}
		});
		btnRemoveCard.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					provider.removeCard((MagicEdition)cboSets.getSelectedItem(), magicCardEditorPanel.getMagicCard());
					magicCardEditorPanel.setMagicCard(new MagicCard());
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		btnSaveEdition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					provider.saveEdition(magicEditionDetailPanel.getMagicEdition());
					cboSets.addItem(magicEditionDetailPanel.getMagicEdition());
					editionModel.init(provider.loadEditions());
					editionModel.fireTableDataChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JDialog l = new JDialog();
				final CardSearchGUI searchPane = new CardSearchGUI();
				JButton selectCard = new JButton(new ImageIcon(CardBuilder2GUI.class.getResource("/res/import.png")));
				selectCard.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MagicCard mc = searchPane.getSelected();
						magicCardEditorPanel.setMagicCard(mc);
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
						e.printStackTrace();
					}
				}				
			}
		});
		
		cardsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				MagicCard ed = (MagicCard)cardsTable.getValueAt(cardsTable.getSelectedRow(), 0);
				
				if(arg0.getClickCount()==2){
				magicCardEditorPanel.setMagicCard(ed);
				tabbedPane.setSelectedIndex(1);
				}
				
			}
		});
		editionsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				MagicEdition ed = (MagicEdition)editionsTable.getValueAt(editionsTable.getSelectedRow(), 1);
				try {
					magicEditionDetailPanel.setMagicEdition(ed);
					cardsModel.init(provider.getCards(ed));
					cardsModel.fireTableDataChanged();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
					
					mc.getEditions().add(me);
				try {
					provider.addCard(me, mc);
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
				} 
			}
		});
		
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
