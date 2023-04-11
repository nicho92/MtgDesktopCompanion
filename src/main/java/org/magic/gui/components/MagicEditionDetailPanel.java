package org.magic.gui.components;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;
public class MagicEditionDetailPanel extends MTGUIComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient BindingGroup mBindingGroup;
	private MagicEdition magicEdition;
	private JTextField cardCountTextField;
	private JTextField releaseDateJTextField;
	private JTextField setJTextField;
	private JTextField typeJTextField;

	private JTextField blockJTextField;
	private JTextField idJtextField;
	private JCheckBox chkOnline;
	private boolean openBooster;


	public MagicEditionDetailPanel(boolean openBooster) {
		this.openBooster = openBooster;
		initGUI();
	}

	public MagicEditionDetailPanel() {
		openBooster = true;
		initGUI();
	}

	public void initGUI() {
		JPanel panneauBooster;
		JButton btnOpenBooster;
		JPanel panneauHaut;

		panneauHaut = new JPanel();

		setLayout(new BorderLayout(0, 0));
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 104, 333, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4 };
		panneauHaut.setLayout(gridBagLayout);

		panneauHaut.add(new JLangLabel("EDITION",true), UITools.createGridBagConstraints(null, null, 0, 0));
		setJTextField = new JTextField();
		panneauHaut.add(setJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 0));

		panneauHaut.add(new JLangLabel("EDITION_TYPE",true), UITools.createGridBagConstraints(null, null, 0, 1));
		typeJTextField = new JTextField();
		panneauHaut.add(typeJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 1));

		panneauHaut.add(new JLangLabel("DATE_RELEASE",true), UITools.createGridBagConstraints(null, null, 0, 2));
		releaseDateJTextField = new JTextField();
		panneauHaut.add(releaseDateJTextField,  UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 2));

		panneauHaut.add(new JLangLabel("EDITION_CARD_COUNT",true), UITools.createGridBagConstraints(null, null, 0, 4));
		cardCountTextField = new JTextField();
		panneauHaut.add(cardCountTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 4));

		panneauHaut.add(new JLangLabel("EDITION_BLOCK",true), UITools.createGridBagConstraints(null, null, 0, 5));
		blockJTextField = new JTextField(10);
		panneauHaut.add(blockJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 5));

		panneauHaut.add(new JLangLabel("ID",true), UITools.createGridBagConstraints(null, null, 0, 6));
		idJtextField = new JTextField(10);
		panneauHaut.add(idJtextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 6));

		panneauHaut.add(new JLangLabel("EDITION_ONLINE",true), UITools.createGridBagConstraints(null, null, 0, 7));
		chkOnline = new JCheckBox("");
		panneauHaut.add(chkOnline, UITools.createGridBagConstraints(GridBagConstraints.WEST,null, 1, 7));


		add(panneauHaut,BorderLayout.CENTER);



		panneauBooster = new JPanel();
		add(panneauBooster, BorderLayout.EAST);
		panneauBooster.setLayout(new BorderLayout(0, 0));

		if (openBooster) {
			btnOpenBooster = new JButton(
					capitalize("OPEN_BOOSTER") + " :");
			panneauBooster.add(btnOpenBooster, BorderLayout.NORTH);
			btnOpenBooster.addActionListener(ae -> {
				try {
					CardSearchPanel.getInstance().thumbnail(
							getEnabledPlugin(MTGCardsProvider.class).generateBooster(magicEdition).getCards());
				} catch (Exception e) {
					logger.error("Error loading booster for {}",magicEdition, e);
				}
			});

		}

		if (magicEdition != null) {
			mBindingGroup = initDataBindings();
		}

		setEditable(false);
	}

	public void setEditable(boolean b) {
		idJtextField.setEditable(b);
		blockJTextField.setEditable(b);
		cardCountTextField.setEditable(b);
		releaseDateJTextField.setEditable(b);
		typeJTextField.setEditable(b);
		setJTextField.setEditable(b);
		chkOnline.setEnabled(b);
	}

	public MagicEdition getMagicEdition() {
		return magicEdition;
	}

	public void init(MagicEdition newMagicEdition) {
		setMagicEdition(newMagicEdition, true);
	}

	public void setMagicEdition(MagicEdition newMagicEdition, boolean update) {
		magicEdition = newMagicEdition;


		if(isVisible() && update) {

				if (mBindingGroup != null) {
					mBindingGroup.unbind();
					mBindingGroup = null;
				}
				if (magicEdition != null) {
					mBindingGroup = initDataBindings();
				}
			}



	}

	@Override
	public void onVisible() {
		if (mBindingGroup != null) {
			mBindingGroup.unbind();
			mBindingGroup = null;
		}
		if (magicEdition != null) {
			mBindingGroup = initDataBindings();
		}
	}


	protected BindingGroup initDataBindings() {
		BeanProperty<MagicEdition, Integer> cardCountProperty = BeanProperty.create("cardCount");
		BeanProperty<JTextField, String> valueProperty = BeanProperty.create("text");
		AutoBinding<MagicEdition, Integer, JTextField, String> autoBinding3 = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, magicEdition, cardCountProperty, cardCountTextField, valueProperty);
		autoBinding3.bind();
		//
		BeanProperty<MagicEdition, String> releaseDateProperty = BeanProperty.create("releaseDate");
		BeanProperty<JTextField, String> textProperty6 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding7 = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, magicEdition, releaseDateProperty, releaseDateJTextField, textProperty6);
		autoBinding7.bind();
		//
		BeanProperty<MagicEdition, String> setProperty = BeanProperty.create("set");
		BeanProperty<JTextField, String> textProperty7 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding8 = Bindings
				.createAutoBinding(UpdateStrategy.READ_WRITE, magicEdition, setProperty, setJTextField, textProperty7);
		autoBinding8.bind();
		//
		BeanProperty<MagicEdition, String> typeProperty = BeanProperty.create("type");
		BeanProperty<JTextField, String> textProperty10 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding11 = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, magicEdition, typeProperty, typeJTextField, textProperty10);
		autoBinding11.bind();

		BeanProperty<MagicEdition, String> blockProperty = BeanProperty.create("block");
		BeanProperty<JTextField, String> textProperty11 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding12 = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, magicEdition, blockProperty, blockJTextField, textProperty11);
		autoBinding12.bind();

		BeanProperty<MagicEdition, String> idProperty = BeanProperty.create("id");
		BeanProperty<JTextField, String> textProperty12 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding13 = Bindings
				.createAutoBinding(UpdateStrategy.READ_WRITE, magicEdition, idProperty, idJtextField, textProperty12);
		autoBinding13.bind();

		BeanProperty<MagicEdition, Boolean> onlineProperty = BeanProperty.create("onlineOnly");
		BeanProperty<JCheckBox, Boolean> chkProperty13 = BeanProperty.create("selected");
		AutoBinding<MagicEdition, Boolean, JCheckBox, Boolean> autoBinding14 = Bindings
				.createAutoBinding(UpdateStrategy.READ_WRITE, magicEdition, onlineProperty, chkOnline, chkProperty13);
		autoBinding14.bind();

		//
		var bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding3);
		bindingGroup.addBinding(autoBinding7);
		bindingGroup.addBinding(autoBinding8);
		bindingGroup.addBinding(autoBinding11);
		bindingGroup.addBinding(autoBinding12);
		bindingGroup.addBinding(autoBinding13);
		bindingGroup.addBinding(autoBinding14);
		return bindingGroup;
	}

	@Override
	public String getTitle() {
		return "Edition";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_BACK;
	}




}
