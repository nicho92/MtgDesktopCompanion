package org.magic.gui.components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.gui.components.editor.JTagsPanel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.tools.ImageUtils;

public class DeckDetailsPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private transient BindingGroup mBindingGroup;
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
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);

		JLabel nameLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DECK_NAME") + " :");
		GridBagConstraints labelgbc1 = new GridBagConstraints();
		labelgbc1.insets = new Insets(5, 5, 5, 5);
		labelgbc1.gridx = 1;
		labelgbc1.gridy = 0;
		add(nameLabel, labelgbc1);

		nameJTextField = new JTextField();
		GridBagConstraints componentgbc1 = new GridBagConstraints();
		componentgbc1.insets = new Insets(5, 0, 5, 5);
		componentgbc1.fill = GridBagConstraints.HORIZONTAL;
		componentgbc1.gridx = 2;
		componentgbc1.gridy = 0;
		add(nameJTextField, componentgbc1);

		JLabel lblLegalities = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("CARD_LEGALITIES") + " :");
		GridBagConstraints gbclblLegalities = new GridBagConstraints();
		gbclblLegalities.insets = new Insets(0, 0, 5, 5);
		gbclblLegalities.gridx = 1;
		gbclblLegalities.gridy = 1;
		add(lblLegalities, gbclblLegalities);

		panelLegalities = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelLegalities.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbcpanelLegalities = new GridBagConstraints();
		gbcpanelLegalities.insets = new Insets(0, 0, 5, 5);
		gbcpanelLegalities.fill = GridBagConstraints.BOTH;
		gbcpanelLegalities.gridx = 2;
		gbcpanelLegalities.gridy = 1;
		add(panelLegalities, gbcpanelLegalities);

		JLabel lblColor = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_COLOR") + " :");
		GridBagConstraints gbclblColor = new GridBagConstraints();
		gbclblColor.insets = new Insets(0, 0, 5, 5);
		gbclblColor.gridx = 1;
		gbclblColor.gridy = 2;
		add(lblColor, gbclblColor);

		manaPanel = new ManaPanel();
		GridBagConstraints gbcmanaPanel = new GridBagConstraints();
		gbcmanaPanel.insets = new Insets(0, 0, 5, 5);
		gbcmanaPanel.fill = GridBagConstraints.BOTH;
		gbcmanaPanel.gridx = 2;
		gbcmanaPanel.gridy = 2;
		add(manaPanel, gbcmanaPanel);

		lblDate = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DATE") + " :");
		GridBagConstraints gbclblDate = new GridBagConstraints();
		gbclblDate.insets = new Insets(0, 0, 5, 5);
		gbclblDate.gridx = 1;
		gbclblDate.gridy = 3;
		add(lblDate, gbclblDate);

		lblDateInformation = new JLabel("");
		GridBagConstraints gbclblDateInformation = new GridBagConstraints();
		gbclblDateInformation.anchor = GridBagConstraints.WEST;
		gbclblDateInformation.insets = new Insets(0, 0, 5, 5);
		gbclblDateInformation.gridx = 2;
		gbclblDateInformation.gridy = 3;
		add(lblDateInformation, gbclblDateInformation);

		JLabel descriptionLabel = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("DESCRIPTION") + " :");
		GridBagConstraints labelgbc0 = new GridBagConstraints();
		labelgbc0.insets = new Insets(5, 5, 5, 5);
		labelgbc0.gridx = 1;
		labelgbc0.gridy = 4;
		add(descriptionLabel, labelgbc0);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		JLabel nbCardsLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("QTY") + " :");
		GridBagConstraints labelgbc2 = new GridBagConstraints();
		labelgbc2.insets = new Insets(5, 5, 5, 5);
		labelgbc2.gridx = 1;
		labelgbc2.gridy = 5;
		add(nbCardsLabel, labelgbc2);

		nbCardsProgress = new JProgressBar();
		nbCardsProgress.setStringPainted(true);
		GridBagConstraints gbcnbCardsProgress = new GridBagConstraints();
		gbcnbCardsProgress.fill = GridBagConstraints.HORIZONTAL;
		gbcnbCardsProgress.insets = new Insets(0, 0, 5, 5);
		gbcnbCardsProgress.gridx = 2;
		gbcnbCardsProgress.gridy = 5;
		add(nbCardsProgress, gbcnbCardsProgress);

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

		lblSideboard = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("SIDEBOARD") + " :");
		GridBagConstraints gbclblSideboard = new GridBagConstraints();
		gbclblSideboard.insets = new Insets(0, 0, 5, 5);
		gbclblSideboard.gridx = 1;
		gbclblSideboard.gridy = 6;
		add(lblSideboard, gbclblSideboard);

		nbSideProgress = new JProgressBar();
		nbSideProgress.setMaximum(15);
		nbSideProgress.setStringPainted(true);
		GridBagConstraints gbcnbSideProgress = new GridBagConstraints();
		gbcnbSideProgress.fill = GridBagConstraints.HORIZONTAL;
		gbcnbSideProgress.insets = new Insets(0, 0, 5, 5);
		gbcnbSideProgress.gridx = 2;
		gbcnbSideProgress.gridy = 6;
		add(nbSideProgress, gbcnbSideProgress);

		scrollPane = new JScrollPane();
		scrollPane.setViewportView(textArea);
		GridBagConstraints gbcscrollPane = new GridBagConstraints();
		gbcscrollPane.insets = new Insets(0, 0, 5, 5);
		gbcscrollPane.fill = GridBagConstraints.BOTH;
		gbcscrollPane.gridx = 2;
		gbcscrollPane.gridy = 4;
		add(scrollPane, gbcscrollPane);

		lblTags = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("TAGS") + " :");
		GridBagConstraints gbclblTags = new GridBagConstraints();
		gbclblTags.insets = new Insets(0, 0, 5, 5);
		gbclblTags.gridx = 1;
		gbclblTags.gridy = 7;
		add(lblTags, gbclblTags);

		tagsPanel = new JTagsPanel();
		GridBagConstraints gbctagsPanel = new GridBagConstraints();
		gbctagsPanel.anchor = GridBagConstraints.WEST;
		gbctagsPanel.insets = new Insets(0, 0, 5, 5);
		gbctagsPanel.fill = GridBagConstraints.VERTICAL;
		gbctagsPanel.gridx = 2;
		gbctagsPanel.gridy = 7;
		add(tagsPanel, gbctagsPanel);

		panel = new JPanel();
		GridBagConstraints gbcpanel = new GridBagConstraints();
		gbcpanel.insets = new Insets(0, 0, 0, 5);
		gbcpanel.fill = GridBagConstraints.BOTH;
		gbcpanel.gridx = 2;
		gbcpanel.gridy = 8;
		add(panel, gbcpanel);

		if (magicDeck != null) {
			mBindingGroup = initDataBindings();
		}
	}

	public MagicDeck getMagicDeck() {
		return magicDeck;
	}

	public void setMagicDeck(MagicDeck newMagicDeck) {
		setMagicDeck(newMagicDeck, true);
	}

	public void setLegalities() {

		MagicFormat mf = new MagicFormat();

		mf.setFormat("Standard");
		if (!magicDeck.isCompatibleFormat(mf))
			lbstd.setBackground(Color.RED);
		else
			lbstd.setBackground(Color.GREEN);

		mf.setFormat("Modern");
		if (!magicDeck.isCompatibleFormat(mf))
			lbmnd.setBackground(Color.RED);
		else
			lbmnd.setBackground(Color.GREEN);

		mf.setFormat("Legacy");
		if (!magicDeck.isCompatibleFormat(mf))
			lbLeg.setBackground(Color.RED);
		else
			lbLeg.setBackground(Color.GREEN);

		mf.setFormat("Vintage");
		if (!magicDeck.isCompatibleFormat(mf))
			lbvin.setBackground(Color.RED);
		else
			lbvin.setBackground(Color.GREEN);

		mf.setFormat("Commander");
		if (!magicDeck.isCompatibleFormat(mf))
			lbcmd.setBackground(Color.RED);
		else
			lbcmd.setBackground(Color.GREEN);

		if (magicDeck.getNbCards() != 100)
			lbcmd.setBackground(Color.RED);

	}

	public void setMagicDeck(org.magic.api.beans.MagicDeck newMagicDeck, boolean update) {
		magicDeck = newMagicDeck;
		if (update) {
			if (mBindingGroup != null) {
				mBindingGroup.unbind();
				mBindingGroup = null;
			}
			if (magicDeck != null) {
				mBindingGroup = initDataBindings();

			}
		}
	}

	public void updatePicture() {
		
		
		if(magicDeck.getMap().isEmpty())
			return;
		
		ThreadManager.getInstance().execute(() -> {
			try {
				panel.removeAll();
				for (int i = 0; i < 4; i++) {
					JLabel lab = new JLabel();
					MagicCard mc = (MagicCard) magicDeck.getMap().keySet().toArray()[i];
					lab.setIcon(new ImageIcon(ImageUtils.resize(MTGControler.getInstance().getEnabled(MTGPictureProvider.class).extractPicture(mc), 150, 220)));
					lab.setToolTipText(mc.getName());
					panel.add(lab);
				}
				panel.revalidate();
				panel.repaint();

			} catch (Exception e) {
				logger.error("error in updatePicture " + e);
			}
		}, "extract deck pictures");
	}

	protected BindingGroup initDataBindings() {
		BeanProperty<MagicDeck, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty1 = BeanProperty.create("text");
		AutoBinding<MagicDeck, String, JTextField, String> autoBinding1 = Bindings
				.createAutoBinding(UpdateStrategy.READ_WRITE, magicDeck, nameProperty, nameJTextField, textProperty1);
		autoBinding1.bind();
		//

		BeanProperty<MagicDeck, Integer> nbCardsProperty = BeanProperty.create("nbCards");
		BeanProperty<JProgressBar, Integer> textProperty4 = BeanProperty.create("value");
		AutoBinding<MagicDeck, Integer, JProgressBar, Integer> autoBinding2 = Bindings
				.createAutoBinding(UpdateStrategy.READ, magicDeck, nbCardsProperty, nbCardsProgress, textProperty4);
		autoBinding2.bind();
		nbCardsProgress.setString("" + magicDeck.getNbCards());
		nbSideProgress.setValue(magicDeck.getSideAsList().size());

		setLegalities();
		//
		BeanProperty<MagicDeck, String> descriptionProperty = BeanProperty.create("description");
		BeanProperty<JTextArea, String> textProperty2 = BeanProperty.create("text");
		AutoBinding<MagicDeck, String, JTextArea, String> autoBinding3 = Bindings
				.createAutoBinding(UpdateStrategy.READ_WRITE, magicDeck, descriptionProperty, textArea, textProperty2);
		autoBinding3.bind();
		//
		BeanProperty<MagicDeck, String> colorIdentityProperty = BeanProperty.create("colors");
		BeanProperty<ManaPanel, String> manaCostProperty3 = BeanProperty.create("manaCost");
		AutoBinding<MagicDeck, String, ManaPanel, String> autoBinding4 = Bindings.createAutoBinding(UpdateStrategy.READ,
				magicDeck, colorIdentityProperty, manaPanel, manaCostProperty3);
		autoBinding4.bind();
		//

		BindingGroup bindingGroup = new BindingGroup();
		bindingGroup.addBinding(autoBinding1);
		bindingGroup.addBinding(autoBinding2);
		bindingGroup.addBinding(autoBinding3);
		bindingGroup.addBinding(autoBinding4);

		if (magicDeck != null && magicDeck.getDateCreation() != null) {
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, MTGControler.getInstance().getLocale());
			lblDateInformation.setText(df.format(magicDeck.getDateCreation()));
		}

		tagsPanel.clean();

		if (magicDeck != null)
			tagsPanel.bind(magicDeck.getTags());

		return bindingGroup;
	}
}
