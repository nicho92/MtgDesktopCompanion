package org.magic.gui.components;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MTGFormat.FORMATS;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.gui.components.editor.JTagsPanel;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;
public class DeckDetailsPanel extends JComponent {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
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
	private JProgressBar nbSideProgress;
	private JPanel panel;
	private JTagsPanel tagsPanel;
	private JLabel lblDate;
	private JLabel lblDateInformation;

	public DeckDetailsPanel(MagicDeck newMagicDeck) {
		this();
		init(newMagicDeck);
	}

	public DeckDetailsPanel() {
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 140, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 28, 30, 35, 0, 132, 31, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);

		add(new JLabel(capitalize("DECK_NAME") + " :"), UITools.createGridBagConstraints(null, null, 1, 0));
		nameJTextField = new JTextField();
		add(nameJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 0));

		add(new JLabel(capitalize("CARD_LEGALITIES") + " :"), UITools.createGridBagConstraints(null, null, 1, 1));
		panelLegalities = new JPanel();
		var flowLayout = (FlowLayout) panelLegalities.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panelLegalities, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 2, 1));

		add(new JLabel(capitalize("CARD_COLOR") + " :"), UITools.createGridBagConstraints(null, null, 1, 2));
		manaPanel = new ManaPanel();
		add(manaPanel, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 2, 2));

		lblDate = new JLabel(capitalize("DATE") + " :");
		add(lblDate, UITools.createGridBagConstraints(null, null, 1, 3));

		lblDateInformation = new JLabel("");
		add(lblDateInformation, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 2, 3));

		add(new JLabel(capitalize("DESCRIPTION") + " :"), UITools.createGridBagConstraints(null, null, 1, 4));

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		add(new JLabel(capitalize("QTY") + " :"), UITools.createGridBagConstraints(null, null, 1, 5));
		nbCardsProgress = new JProgressBar();
		nbCardsProgress.setStringPainted(true);
		add(nbCardsProgress, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 5));

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

		add(new JLabel(capitalize("SIDEBOARD") + " :"), UITools.createGridBagConstraints(null, null, 1, 6));

		nbSideProgress = new JProgressBar();
		nbSideProgress.setMaximum(15);
		nbSideProgress.setStringPainted(true);
		add(nbSideProgress, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 2, 6));

		add(new JScrollPane(textArea), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 2, 4));

		add(new JLabel(capitalize("TAGS") + " :"), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 7));

		tagsPanel = new JTagsPanel();
		add(tagsPanel, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.VERTICAL, 2, 7));

		panel = new JPanel();
		add(panel, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 2, 8));

		if (magicDeck != null) {
			mBindingGroup = initDataBindings();
		}
	}

	public MagicDeck getMagicDeck() {
		return magicDeck;
	}

	public void init(MagicDeck newMagicDeck) {
		setMagicDeck(newMagicDeck, true);
	}

	public void setLegalities() {

		if (!MTGDeckManager.isLegal(magicDeck,FORMATS.STANDARD))
			lbstd.setBackground(Color.RED);
		else
			lbstd.setBackground(Color.GREEN);

		if (!MTGDeckManager.isLegal(magicDeck,FORMATS.MODERN))
			lbmnd.setBackground(Color.RED);
		else
			lbmnd.setBackground(Color.GREEN);

		if (!MTGDeckManager.isLegal(magicDeck,FORMATS.LEGACY))
			lbLeg.setBackground(Color.RED);
		else
			lbLeg.setBackground(Color.GREEN);

		if (!MTGDeckManager.isLegal(magicDeck,FORMATS.VINTAGE))
			lbvin.setBackground(Color.RED);
		else
			lbvin.setBackground(Color.GREEN);

		if (!MTGDeckManager.isLegal(magicDeck,FORMATS.COMMANDER))
			lbcmd.setBackground(Color.RED);
		else
			lbcmd.setBackground(Color.GREEN);
	}

	public void setMagicDeck(MagicDeck newMagicDeck, boolean update) {
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

		if(magicDeck==null || magicDeck.getMain().isEmpty())
				return;


		panel.removeAll();
		SwingWorker<Void, BufferedImage> sw = new SwingWorker<>()
		{

			@Override
			protected Void doInBackground() throws Exception {
				for (var i = 0; i < 4; i++) {
					BufferedImage im = getEnabledPlugin(MTGPictureProvider.class).extractPicture((MagicCard) magicDeck.getMain().keySet().toArray()[i]);
					publish(im);
				}
				return null;
			}

			@Override
			protected void process(List<BufferedImage> chunks) {

				panel.add(new JLabel(new ImageIcon(ImageTools.resize(chunks.get(0), 150, 220))));
			}


			@Override
			protected void done() {
				panel.revalidate();
				panel.repaint();
			}

		};

		ThreadManager.getInstance().runInEdt(sw,"extract deck pictures");

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

		var bindingGroup = new BindingGroup();
		bindingGroup.addBinding(autoBinding1);
		bindingGroup.addBinding(autoBinding2);
		bindingGroup.addBinding(autoBinding3);
		bindingGroup.addBinding(autoBinding4);

		if (magicDeck != null && magicDeck.getDateCreation() != null) {
			var df = DateFormat.getDateInstance(DateFormat.SHORT, MTGControler.getInstance().getLocale());
			lblDateInformation.setText(df.format(magicDeck.getDateCreation()));
		}

		tagsPanel.clean();

		if (magicDeck != null)
			tagsPanel.bind(magicDeck.getTags());

		return bindingGroup;
	}
}
