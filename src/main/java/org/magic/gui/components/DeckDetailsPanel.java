package org.magic.gui.components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicFormat;
import org.magic.gui.components.editor.JTagsPanel;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

public class DeckDetailsPanel extends JPanel {

	private BindingGroup m_bindingGroup;
	private MagicDeck magicDeck = new MagicDeck();
	private JTextField nameJTextField;
	private JTextArea textArea;
	private ManaPanel manaPanel;
	private JPanel panelLegalities;
	private JProgressBar nbCardsProgress;
	private JLabel lbstd;
	private JLabel lbmnd;
	private JLabel lbvin;
	private JLabel lbcmd;
	private JLabel lbLeg;
	private JLabel lblSideboard;
	private JProgressBar nbSideProgress;
	private JScrollPane scrollPane;
	private JButton btnUpdateLegalities;
	private JPanel panel;
	private JLabel lblTags;
	private JTagsPanel tagsPanel;
	private JLabel lblDate;
	private JLabel lblDateInformation;

	
	public DeckDetailsPanel(org.magic.api.beans.MagicDeck newMagicDeck) {
		this();
		setMagicDeck(newMagicDeck);
	}

	public DeckDetailsPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 140, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 28, 30, 35, 0, 132, 31, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0E-4 };
		setLayout(gridBagLayout);
		
				JLabel nameLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DECK_NAME")+ " :");
				GridBagConstraints labelGbc_1 = new GridBagConstraints();
				labelGbc_1.insets = new Insets(5, 5, 5, 5);
				labelGbc_1.gridx = 1;
				labelGbc_1.gridy = 0;
				add(nameLabel, labelGbc_1);
		
				nameJTextField = new JTextField();
				GridBagConstraints componentGbc_1 = new GridBagConstraints();
				componentGbc_1.insets = new Insets(5, 0, 5, 5);
				componentGbc_1.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_1.gridx = 2;
				componentGbc_1.gridy = 0;
				add(nameJTextField, componentGbc_1);
		
		JLabel lblLegalities = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_LEGALITIES")+ " :");
		GridBagConstraints gbc_lblLegalities = new GridBagConstraints();
		gbc_lblLegalities.insets = new Insets(0, 0, 5, 5);
		gbc_lblLegalities.gridx = 1;
		gbc_lblLegalities.gridy = 1;
		add(lblLegalities, gbc_lblLegalities);
		
		panelLegalities = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelLegalities.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panelLegalities = new GridBagConstraints();
		gbc_panelLegalities.insets = new Insets(0, 0, 5, 5);
		gbc_panelLegalities.fill = GridBagConstraints.BOTH;
		gbc_panelLegalities.gridx = 2;
		gbc_panelLegalities.gridy = 1;
		add(panelLegalities, gbc_panelLegalities);
		
		JLabel lblColor = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_COLOR")+ " :");
		GridBagConstraints gbc_lblColor = new GridBagConstraints();
		gbc_lblColor.insets = new Insets(0, 0, 5, 5);
		gbc_lblColor.gridx = 1;
		gbc_lblColor.gridy = 2;
		add(lblColor, gbc_lblColor);
				
				manaPanel = new ManaPanel();
				GridBagConstraints gbc_manaPanel = new GridBagConstraints();
				gbc_manaPanel.insets = new Insets(0, 0, 5, 5);
				gbc_manaPanel.fill = GridBagConstraints.BOTH;
				gbc_manaPanel.gridx = 2;
				gbc_manaPanel.gridy = 2;
				add(manaPanel, gbc_manaPanel);
				
				lblDate = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DATE")+ " :");
				GridBagConstraints gbc_lblDate = new GridBagConstraints();
				gbc_lblDate.insets = new Insets(0, 0, 5, 5);
				gbc_lblDate.gridx = 1;
				gbc_lblDate.gridy = 3;
				add(lblDate, gbc_lblDate);
				
				lblDateInformation = new JLabel("");
				GridBagConstraints gbc_lblDateInformation = new GridBagConstraints();
				gbc_lblDateInformation.anchor = GridBagConstraints.WEST;
				gbc_lblDateInformation.insets = new Insets(0, 0, 5, 5);
				gbc_lblDateInformation.gridx = 2;
				gbc_lblDateInformation.gridy = 3;
				add(lblDateInformation, gbc_lblDateInformation);
		
				JLabel descriptionLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DESCRIPTION")+ " :");
				GridBagConstraints labelGbc_0 = new GridBagConstraints();
				labelGbc_0.insets = new Insets(5, 5, 5, 5);
				labelGbc_0.gridx = 1;
				labelGbc_0.gridy = 4;
				add(descriptionLabel, labelGbc_0);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		

		JLabel nbCardsLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("QTY")+ " :");
		GridBagConstraints labelGbc_2 = new GridBagConstraints();
		labelGbc_2.insets = new Insets(5, 5, 5, 5);
		labelGbc_2.gridx = 1;
		labelGbc_2.gridy = 5;
		add(nbCardsLabel, labelGbc_2);
		
		nbCardsProgress = new JProgressBar();
		nbCardsProgress.setStringPainted(true);
		GridBagConstraints gbc_nbCardsProgress = new GridBagConstraints();
		gbc_nbCardsProgress.fill = GridBagConstraints.HORIZONTAL;
		gbc_nbCardsProgress.insets = new Insets(0, 0, 5, 5);
		gbc_nbCardsProgress.gridx = 2;
		gbc_nbCardsProgress.gridy = 5;
		add(nbCardsProgress, gbc_nbCardsProgress);

		
		lbstd = new JLabel(" STD ");
		lbstd.setOpaque(true);
		lbstd.setBackground(Color.GREEN);
		lbmnd = new JLabel(" MDN ");
		lbmnd.setOpaque(true);
		lbmnd.setBackground(Color.GREEN);
		lbvin = new JLabel(" VIN ");
		lbvin.setOpaque(true);
		lbvin.setBackground(Color.GREEN);
		lbcmd = new JLabel(" CMD ");
		lbcmd.setOpaque(true);
		lbcmd.setBackground(Color.GREEN);
		lbLeg = new JLabel(" LEG ");
		lbLeg.setOpaque(true);
		lbLeg.setBackground(Color.GREEN);

		
		panelLegalities.add(lbvin);
		panelLegalities.add(lbLeg);
		panelLegalities.add(lbstd);
		panelLegalities.add(lbmnd);
		panelLegalities.add(lbcmd);
		
		
		
		lblSideboard = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("SIDEBOARD")+ " :");
		GridBagConstraints gbc_lblSideboard = new GridBagConstraints();
		gbc_lblSideboard.insets = new Insets(0, 0, 5, 5);
		gbc_lblSideboard.gridx = 1;
		gbc_lblSideboard.gridy = 6;
		add(lblSideboard, gbc_lblSideboard);
		
		nbSideProgress = new JProgressBar();
		nbSideProgress.setMaximum(15);
		nbSideProgress.setStringPainted(true);
		GridBagConstraints gbc_nbSideProgress = new GridBagConstraints();
		gbc_nbSideProgress.fill = GridBagConstraints.HORIZONTAL;
		gbc_nbSideProgress.insets = new Insets(0, 0, 5, 5);
		gbc_nbSideProgress.gridx = 2;
		gbc_nbSideProgress.gridy = 6;
		add(nbSideProgress, gbc_nbSideProgress);
		
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(textArea);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 4;
		add(scrollPane, gbc_scrollPane);
		
		lblTags = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("TAGS")+ " :");
		GridBagConstraints gbc_lblTags = new GridBagConstraints();
		gbc_lblTags.insets = new Insets(0, 0, 5, 5);
		gbc_lblTags.gridx = 1;
		gbc_lblTags.gridy = 7;
		add(lblTags, gbc_lblTags);
		
		tagsPanel = new JTagsPanel();
		GridBagConstraints gbc_tagsPanel = new GridBagConstraints();
		gbc_tagsPanel.anchor = GridBagConstraints.WEST;
		gbc_tagsPanel.insets = new Insets(0, 0, 5, 5);
		gbc_tagsPanel.fill = GridBagConstraints.VERTICAL;
		gbc_tagsPanel.gridx = 2;
		gbc_tagsPanel.gridy = 7;
		add(tagsPanel, gbc_tagsPanel);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 8;
		add(panel, gbc_panel);
		
	
		
		
		if (magicDeck != null) {
			m_bindingGroup = initDataBindings();
		}
	}

	public MagicDeck getMagicDeck() {
		return magicDeck;
	}

	public void setMagicDeck(MagicDeck newMagicDeck) {
		setMagicDeck(newMagicDeck, true);
	}

	public void setLegalities()
	{
		
		MagicFormat mf = new MagicFormat();
				
		mf.setFormat("Standard");
		if(!magicDeck.isCompatibleFormat(mf))
			lbstd.setBackground(Color.RED);
		else
			lbstd.setBackground(Color.GREEN);
		
		mf.setFormat("Modern");
		if(!magicDeck.isCompatibleFormat(mf))
			lbmnd.setBackground(Color.RED);
		else
			lbmnd.setBackground(Color.GREEN);
		
		mf.setFormat("Legacy");
		if(!magicDeck.isCompatibleFormat(mf))
			lbLeg.setBackground(Color.RED);
		else
			lbLeg.setBackground(Color.GREEN);
		
		mf.setFormat("Vintage");
		if(!magicDeck.isCompatibleFormat(mf))
			lbvin.setBackground(Color.RED);
		else
			lbvin.setBackground(Color.GREEN);
		
		mf.setFormat("Commander");
		if(!magicDeck.isCompatibleFormat(mf))
			lbcmd.setBackground(Color.RED);
		else
			lbcmd.setBackground(Color.GREEN);
		
		if(magicDeck.getNbCards()!=100)
			lbcmd.setBackground(Color.RED);
		
	}
	
	
	public void setMagicDeck(org.magic.api.beans.MagicDeck newMagicDeck, boolean update) {
		magicDeck = newMagicDeck;
		if (update) {
			if (m_bindingGroup != null) {
				m_bindingGroup.unbind();
				m_bindingGroup = null;
			}
			if (magicDeck != null) {
				m_bindingGroup = initDataBindings();
				
				
			}
		}
	}
	
	public void updatePicture()
	{
			ThreadManager.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					panel.removeAll();
					for(int i=0;i<4;i++)
					{
						JLabel lab = new JLabel();
						MagicCard mc =  (MagicCard)magicDeck.getMap().keySet().toArray()[i];
						lab.setIcon(new ImageIcon(MTGControler.getInstance().getEnabledPicturesProvider().extractPicture(mc)));
						lab.setToolTipText(mc.getName());
						panel.add(lab);
					}
					panel.revalidate();
					panel.repaint();
					
				} catch (Exception e) {
					
				}
			}
		}, "extract deck pictures");
	}
	
	
	protected BindingGroup initDataBindings() {
		BeanProperty<MagicDeck, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty_1 = BeanProperty.create("text");
		AutoBinding<MagicDeck, String, JTextField, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicDeck, nameProperty, nameJTextField, textProperty_1);
		autoBinding_1.bind();
		//
		
		BeanProperty<MagicDeck, Integer> nbCardsProperty = BeanProperty.create("nbCards");
		BeanProperty<JProgressBar, Integer> textProperty_4 = BeanProperty.create("value");
		AutoBinding<MagicDeck, Integer, JProgressBar, Integer> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, magicDeck, nbCardsProperty,nbCardsProgress , textProperty_4);
		autoBinding_2.bind();
		nbCardsProgress.setString(""+magicDeck.getNbCards());
		nbSideProgress.setValue(magicDeck.getSideAsList().size());
		
		
		setLegalities();
		//
		BeanProperty<MagicDeck, String> descriptionProperty = BeanProperty.create("description");
		BeanProperty<JTextArea, String> textProperty_2 = BeanProperty.create("text");
		AutoBinding<MagicDeck, String, JTextArea, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicDeck, descriptionProperty, textArea, textProperty_2);
		autoBinding_3.bind();
		//
		BeanProperty<MagicDeck, String> colorIdentityProperty = BeanProperty.create("colors");
		BeanProperty<ManaPanel, String> manaCostProperty_3 = BeanProperty.create("manaCost");
		AutoBinding<MagicDeck, String, ManaPanel, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ, magicDeck, colorIdentityProperty, manaPanel, manaCostProperty_3);
		autoBinding_4.bind();
		//
		
		BindingGroup bindingGroup = new BindingGroup();
		bindingGroup.addBinding(autoBinding_1);
		bindingGroup.addBinding(autoBinding_2);
		bindingGroup.addBinding(autoBinding_3);
		bindingGroup.addBinding(autoBinding_4);
		
		if(magicDeck!=null&&magicDeck.getDateCreation()!=null)
		{
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, MTGControler.getInstance().getLocale());
			lblDateInformation.setText(df.format(magicDeck.getDateCreation()));
		}
		
		tagsPanel.clean();
		tagsPanel.bind(magicDeck.getTags());
		
		
		
		
		return bindingGroup;
	}
}
