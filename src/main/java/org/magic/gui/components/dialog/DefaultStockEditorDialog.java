package org.magic.gui.components.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class DefaultStockEditorDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final String CHECKBOX_PROPERTIES = "selected";
	private transient BindingGroup mbindingGroup;
	private JPanel mcontentPane;
	private MTGCardStock magicCardStock = new MTGCardStock();
	private JCheckBox alteredJCheckBox;
	private JCheckBox foilJCheckBox;
	private JTextField languageJTextField;
	private JCheckBox oversizeJCheckBox;
	private JSlider qteJSlider;
	private JCheckBox signedJCheckBox;
	private JPanel panel;
	private JButton btnSave;
	private JButton btnCancel;
	private JLabel lblQtyValue;
	private JPanel panel1;
	private JLabel lblCondition;
	private JComboBox<EnumCondition> cboConditions;


	/**
	 * Create the dialog.
	 */
	public DefaultStockEditorDialog() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setIconImage(MTGConstants.ICON_STOCK.getImage());
		setLocationRelativeTo(null);
		mcontentPane = new JPanel();
		setContentPane(mcontentPane);
		//
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 109, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4 };
		mcontentPane.setLayout(gridBagLayout);

		var alteredLabel = new JLabel("Altered:");
		var labelGbc0 = new GridBagConstraints();
		labelGbc0.insets = new Insets(5, 5, 5, 5);
		labelGbc0.gridx = 0;
		labelGbc0.gridy = 0;
		mcontentPane.add(alteredLabel, labelGbc0);

		alteredJCheckBox = new JCheckBox();
		var componentGbc0 = new GridBagConstraints();
		componentGbc0.insets = new Insets(5, 0, 5, 5);
		componentGbc0.fill = GridBagConstraints.HORIZONTAL;
		componentGbc0.gridx = 1;
		componentGbc0.gridy = 0;
		mcontentPane.add(alteredJCheckBox, componentGbc0);

		var signedLabel = new JLabel("Signed:");
				var labelGbc5 = new GridBagConstraints();
				labelGbc5.insets = new Insets(5, 5, 5, 5);
				labelGbc5.gridx = 2;
				labelGbc5.gridy = 0;
				mcontentPane.add(signedLabel, labelGbc5);

				signedJCheckBox = new JCheckBox();
				var componentGbc5 = new GridBagConstraints();
				componentGbc5.insets = new Insets(5, 0, 5, 0);
				componentGbc5.fill = GridBagConstraints.HORIZONTAL;
				componentGbc5.gridx = 3;
				componentGbc5.gridy = 0;
				mcontentPane.add(signedJCheckBox, componentGbc5);

				var foilLabel = new JLabel("Foil:");
				var labelGbc1 = new GridBagConstraints();
		labelGbc1.insets = new Insets(5, 5, 5, 5);
		labelGbc1.gridx = 0;
		labelGbc1.gridy = 1;
		mcontentPane.add(foilLabel, labelGbc1);

		foilJCheckBox = new JCheckBox();
		var componentGbc1 = new GridBagConstraints();
		componentGbc1.insets = new Insets(5, 0, 5, 5);
		componentGbc1.fill = GridBagConstraints.HORIZONTAL;
		componentGbc1.gridx = 1;
		componentGbc1.gridy = 1;
		mcontentPane.add(foilJCheckBox, componentGbc1);

		var oversizeLabel = new JLabel("Oversize:");
				var labelGbc3 = new GridBagConstraints();
				labelGbc3.insets = new Insets(5, 5, 5, 5);
				labelGbc3.gridx = 2;
				labelGbc3.gridy = 1;
				mcontentPane.add(oversizeLabel, labelGbc3);

				oversizeJCheckBox = new JCheckBox();
				var componentGbc3 = new GridBagConstraints();
				componentGbc3.insets = new Insets(5, 0, 5, 0);
				componentGbc3.fill = GridBagConstraints.HORIZONTAL;
				componentGbc3.gridx = 3;
				componentGbc3.gridy = 1;
				mcontentPane.add(oversizeJCheckBox, componentGbc3);

				var qteLabel = new JLabel("Qte:");
						var labelGbc4 = new GridBagConstraints();
						labelGbc4.insets = new Insets(5, 5, 5, 5);
						labelGbc4.gridx = 0;
						labelGbc4.gridy = 2;
						mcontentPane.add(qteLabel, labelGbc4);

				panel1 = new JPanel();
				var gbcpanel1 = new GridBagConstraints();
				gbcpanel1.gridwidth = 3;
				gbcpanel1.insets = new Insets(0, 0, 5, 0);
				gbcpanel1.fill = GridBagConstraints.BOTH;
				gbcpanel1.gridx = 1;
				gbcpanel1.gridy = 2;
				lblQtyValue = new JLabel();
				lblQtyValue.setPreferredSize(new Dimension(50, 25));
				mcontentPane.add(panel1, gbcpanel1);

						qteJSlider = new JSlider();
						panel1.add(qteJSlider);
						qteJSlider.addChangeListener(event->lblQtyValue.setText("" + qteJSlider.getValue()));
						qteJSlider.setPaintLabels(true);


						panel1.add(lblQtyValue);

						var languageLabel = new JLabel("Language:");
						var labelGbc2 = new GridBagConstraints();
						labelGbc2.insets = new Insets(5, 5, 5, 5);
						labelGbc2.gridx = 0;
						labelGbc2.gridy = 3;
						mcontentPane.add(languageLabel, labelGbc2);

						languageJTextField = new JTextField();
						var componentGbc2 = new GridBagConstraints();
						componentGbc2.gridwidth = 3;
						componentGbc2.insets = new Insets(5, 0, 5, 0);
						componentGbc2.fill = GridBagConstraints.HORIZONTAL;
						componentGbc2.gridx = 1;
						componentGbc2.gridy = 3;
						mcontentPane.add(languageJTextField, componentGbc2);

						lblCondition = new JLabel("Condition :");
						var gbclblCondition = new GridBagConstraints();
						gbclblCondition.anchor = GridBagConstraints.EAST;
						gbclblCondition.fill = GridBagConstraints.VERTICAL;
						gbclblCondition.insets = new Insets(0, 0, 5, 5);
						gbclblCondition.gridx = 0;
						gbclblCondition.gridy = 4;
						mcontentPane.add(lblCondition, gbclblCondition);


						DefaultComboBoxModel<EnumCondition> modCondition = new DefaultComboBoxModel<>(EnumCondition.values());
						cboConditions = new JComboBox<>(modCondition);

						cboConditions.addItemListener(ie->
						{
							if(ie.getStateChange()==ItemEvent.SELECTED)
								magicCardStock.setCondition((EnumCondition)cboConditions.getSelectedItem());

						});

						var gbccboConditions = new GridBagConstraints();
						gbccboConditions.gridwidth = 3;
						gbccboConditions.insets = new Insets(0, 0, 5, 5);
						gbccboConditions.fill = GridBagConstraints.HORIZONTAL;
						gbccboConditions.gridx = 1;
						gbccboConditions.gridy = 4;
						mcontentPane.add(cboConditions, gbccboConditions);

						panel = new JPanel();
						var gbcpanel = new GridBagConstraints();
						gbcpanel.gridwidth = 4;
						gbcpanel.fill = GridBagConstraints.BOTH;
						gbcpanel.gridx = 0;
						gbcpanel.gridy = 5;
						mcontentPane.add(panel, gbcpanel);

						btnSave = new JButton(MTGConstants.ICON_SAVE);
						btnSave.addActionListener(e->{
							MTGControler.getInstance().setDefaultStock(getMagicCardStock());
							dispose();
						});
						panel.add(btnSave);

						btnCancel = new JButton(MTGConstants.ICON_DELETE);
						btnCancel.addActionListener(e->dispose());
						panel.add(btnCancel);

		if (magicCardStock != null) {
			mbindingGroup = initDataBindings();
		}
		pack();

	}

	protected BindingGroup initDataBindings() {
		BeanProperty<MTGCardStock, Boolean> alteredProperty = BeanProperty
				.create("altered");
		BeanProperty<JCheckBox, Boolean> selectedProperty = BeanProperty.create(CHECKBOX_PROPERTIES);
		AutoBinding<MTGCardStock, Boolean, JCheckBox, Boolean> autoBinding = Bindings
				.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, magicCardStock, alteredProperty, alteredJCheckBox,
						selectedProperty);
		autoBinding.bind();
		//
		BeanProperty<MTGCardStock, Boolean> foilProperty = BeanProperty.create("foil");
		BeanProperty<JCheckBox, Boolean> selectedProperty1 = BeanProperty.create(CHECKBOX_PROPERTIES);
		AutoBinding<MTGCardStock, Boolean, JCheckBox, Boolean> autoBinding1 = Bindings
				.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, magicCardStock, foilProperty, foilJCheckBox,
						selectedProperty1);
		autoBinding1.bind();
		//
		BeanProperty<MTGCardStock, String> languageProperty = BeanProperty
				.create("language");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<MTGCardStock, String, JTextField, String> autoBinding2 = Bindings
				.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, magicCardStock, languageProperty,
						languageJTextField, textProperty);
		autoBinding2.bind();
		//
		BeanProperty<MTGCardStock, Boolean> oversizeProperty = BeanProperty
				.create("oversize");
		BeanProperty<JCheckBox, Boolean> selectedProperty2 = BeanProperty.create(CHECKBOX_PROPERTIES);
		AutoBinding<MTGCardStock, Boolean, JCheckBox, Boolean> autoBinding3 = Bindings
				.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, magicCardStock, oversizeProperty,
						oversizeJCheckBox, selectedProperty2);
		autoBinding3.bind();
		//
		BeanProperty<MTGCardStock, Integer> qteProperty = BeanProperty.create("qte");
		BeanProperty<JSlider, Integer> valueProperty = BeanProperty.create("value");
		AutoBinding<MTGCardStock, Integer, JSlider, Integer> autoBinding4 = Bindings
				.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, magicCardStock, qteProperty, qteJSlider,
						valueProperty);
		autoBinding4.bind();
		//
		BeanProperty<MTGCardStock, Boolean> signedProperty = BeanProperty
				.create("signed");
		BeanProperty<JCheckBox, Boolean> selectedProperty3 = BeanProperty.create(CHECKBOX_PROPERTIES);
		AutoBinding<MTGCardStock, Boolean, JCheckBox, Boolean> autoBinding5 = Bindings
				.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, magicCardStock, signedProperty,
						signedJCheckBox, selectedProperty3);
		autoBinding5.bind();
		//
		var bindingGroup = new BindingGroup();
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding1);
		bindingGroup.addBinding(autoBinding2);
		bindingGroup.addBinding(autoBinding3);
		bindingGroup.addBinding(autoBinding4);
		bindingGroup.addBinding(autoBinding5);
		//
		return bindingGroup;
	}

	public MTGCardStock getMagicCardStock() {
		return magicCardStock;
	}

	public void setMagicCardStock(MTGCardStock newMagicCardStock) {
		setMagicCardStock(newMagicCardStock, true);
	}

	public void setMagicCardStock(MTGCardStock newMagicCardStock, boolean update) {
		magicCardStock = newMagicCardStock;
		if (update) {
			if (mbindingGroup != null) {
				mbindingGroup.unbind();
				mbindingGroup = null;
			}
			if (magicCardStock != null) {
				mbindingGroup = initDataBindings();
			}
		}
		if(magicCardStock!=null)
		{
			lblQtyValue.setText(String.valueOf(magicCardStock.getQte()));
			cboConditions.setSelectedItem(magicCardStock.getCondition());
		}

	}

}
