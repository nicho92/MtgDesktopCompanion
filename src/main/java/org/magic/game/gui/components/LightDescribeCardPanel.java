package org.magic.game.gui.components;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MTGRuling;
import org.magic.gui.components.MagicTextPane;
import org.magic.gui.components.ManaPanel;

public class LightDescribeCardPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtName;
	private JTextField txtType;
	private JTextField txtPower;
	private JTextField txtLoyalty;
	private MagicTextPane magicTextPane;
	private ManaPanel manaPanel;

	private transient BindingGroup mBindingGroup;

	private MagicCard card;
	private JPanel panel;
	private JLabel label;
	private JTextField txtT;
	private JTabbedPane tabbedPane;
	private JEditorPane rulesTextPane;

	public LightDescribeCardPanel() {
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 52, 152, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 27, 0, 27, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		var lblName = new JLabel("Name :");
		var gbclblName = new GridBagConstraints();
		gbclblName.anchor = GridBagConstraints.EAST;
		gbclblName.insets = new Insets(0, 0, 5, 5);
		gbclblName.gridx = 0;
		gbclblName.gridy = 0;
		add(lblName, gbclblName);

		txtName = new JTextField();
		txtName.setEditable(false);
		var gbctxtName = new GridBagConstraints();
		gbctxtName.insets = new Insets(0, 0, 5, 0);
		gbctxtName.fill = GridBagConstraints.HORIZONTAL;
		gbctxtName.gridx = 1;
		gbctxtName.gridy = 0;
		add(txtName, gbctxtName);
		txtName.setColumns(10);

		var lblCost = new JLabel("Cost :");
		var gbclblCost = new GridBagConstraints();
		gbclblCost.anchor = GridBagConstraints.EAST;
		gbclblCost.insets = new Insets(0, 0, 5, 5);
		gbclblCost.gridx = 0;
		gbclblCost.gridy = 1;
		add(lblCost, gbclblCost);

		manaPanel = new ManaPanel();
		var gbcmanaPanel = new GridBagConstraints();
		gbcmanaPanel.insets = new Insets(0, 0, 5, 0);
		gbcmanaPanel.fill = GridBagConstraints.BOTH;
		gbcmanaPanel.gridx = 1;
		gbcmanaPanel.gridy = 1;
		add(manaPanel, gbcmanaPanel);

		var lblType = new JLabel("Type :");
		var gbclblType = new GridBagConstraints();
		gbclblType.anchor = GridBagConstraints.EAST;
		gbclblType.insets = new Insets(0, 0, 5, 5);
		gbclblType.gridx = 0;
		gbclblType.gridy = 2;
		add(lblType, gbclblType);

		txtType = new JTextField();
		txtType.setEditable(false);
		var gbctxtType = new GridBagConstraints();
		gbctxtType.insets = new Insets(0, 0, 5, 0);
		gbctxtType.fill = GridBagConstraints.HORIZONTAL;
		gbctxtType.gridx = 1;
		gbctxtType.gridy = 2;
		add(txtType, gbctxtType);
		txtType.setColumns(10);

		var lblSt = new JLabel("P/T :");
		var gbclblSt = new GridBagConstraints();
		gbclblSt.anchor = GridBagConstraints.EAST;
		gbclblSt.insets = new Insets(0, 0, 5, 5);
		gbclblSt.gridx = 0;
		gbclblSt.gridy = 3;
		add(lblSt, gbclblSt);

		panel = new JPanel();
		var flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		var gbcpanel = new GridBagConstraints();
		gbcpanel.insets = new Insets(0, 0, 5, 0);
		gbcpanel.fill = GridBagConstraints.BOTH;
		gbcpanel.gridx = 1;
		gbcpanel.gridy = 3;
		add(panel, gbcpanel);

		txtPower = new JTextField();
		panel.add(txtPower);
		txtPower.setEditable(false);
		txtPower.setColumns(3);

		label = new JLabel("/");
		panel.add(label);

		txtT = new JTextField();
		txtT.setEditable(false);
		panel.add(txtT);
		txtT.setColumns(3);

		var lblLoyalty = new JLabel("Loyalty :");
		var gbclblLoyalty = new GridBagConstraints();
		gbclblLoyalty.anchor = GridBagConstraints.EAST;
		gbclblLoyalty.insets = new Insets(0, 0, 5, 5);
		gbclblLoyalty.gridx = 0;
		gbclblLoyalty.gridy = 4;
		add(lblLoyalty, gbclblLoyalty);

		txtLoyalty = new JTextField();
		txtLoyalty.setEditable(false);
		var gbctxtLoyalty = new GridBagConstraints();
		gbctxtLoyalty.insets = new Insets(0, 0, 5, 0);
		gbctxtLoyalty.fill = GridBagConstraints.HORIZONTAL;
		gbctxtLoyalty.gridx = 1;
		gbctxtLoyalty.gridy = 4;
		add(txtLoyalty, gbctxtLoyalty);
		txtLoyalty.setColumns(10);

		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		var gbctabbedPane = new GridBagConstraints();
		gbctabbedPane.gridheight = 2;
		gbctabbedPane.gridwidth = 2;
		gbctabbedPane.insets = new Insets(0, 0, 5, 5);
		gbctabbedPane.fill = GridBagConstraints.BOTH;
		gbctabbedPane.gridx = 0;
		gbctabbedPane.gridy = 5;
		add(tabbedPane, gbctabbedPane);

		magicTextPane = new MagicTextPane();
		tabbedPane.addTab("Text", null, new JScrollPane(magicTextPane), null);
		magicTextPane.setMaximumSize(new Dimension(120, 200));
		magicTextPane.setEditable(false);

		rulesTextPane = new JEditorPane();
		tabbedPane.addTab("Rules", null, new JScrollPane(rulesTextPane), null);



		if (card != null) {
			mBindingGroup = initDataBindings();
		}
	}

	public void setCard(MagicCard newMagicCard) {
		card = newMagicCard;

		if (mBindingGroup != null) {
			mBindingGroup.unbind();
			mBindingGroup = null;
		}
		if (card != null) {
			mBindingGroup = initDataBindings();
		}
	}

	protected BindingGroup initDataBindings() {

		BeanProperty<MagicCard, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ,
				card, nameProperty, txtName, textProperty);
		autoBinding.bind();

		BeanProperty<MagicCard, String> costProperty = BeanProperty.create("cost");
		BeanProperty<ManaPanel, String> textProperty1 = BeanProperty.create("manaCost");
		AutoBinding<MagicCard, String, ManaPanel, String> autoBinding1 = Bindings
				.createAutoBinding(UpdateStrategy.READ_WRITE, card, costProperty, manaPanel, textProperty1);
		autoBinding1.bind();

		BeanProperty<MagicCard, String> fullTypeProperty = BeanProperty.create("fullType");
		BeanProperty<JTextField, String> textProperty2 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding2 = Bindings
				.createAutoBinding(UpdateStrategy.READ, card, fullTypeProperty, txtType, textProperty2);
		autoBinding2.bind();

		BeanProperty<MagicCard, Integer> loyaltyProperty = BeanProperty.create("loyalty");
		BeanProperty<JTextField, String> textProperty4 = BeanProperty.create("text");
		AutoBinding<MagicCard, Integer, JTextField, String> autoBinding4 = Bindings
				.createAutoBinding(UpdateStrategy.READ, card, loyaltyProperty, txtLoyalty, textProperty4);
		autoBinding4.bind();

		BeanProperty<MagicCard, String> textProperty8 = BeanProperty.create("text");
		BeanProperty<MagicTextPane, String> textProperty9 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, MagicTextPane, String> autoBinding8 = Bindings
				.createAutoBinding(UpdateStrategy.READ, card, textProperty8, magicTextPane, textProperty9);
		autoBinding8.bind();

		BeanProperty<MagicCard, String> pProperty = BeanProperty.create("power");
		BeanProperty<JTextField, String> textPropertyP = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBindingP = Bindings
				.createAutoBinding(UpdateStrategy.READ, card, pProperty, txtPower, textPropertyP);
		autoBindingP.bind();

		BeanProperty<MagicCard, String> tProperty = BeanProperty.create("toughness");
		BeanProperty<JTextField, String> textPropertyT = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBindingT = Bindings
				.createAutoBinding(UpdateStrategy.READ, card, tProperty, txtT, textPropertyT);
		autoBindingT.bind();

		var rules = new StringBuilder();
		for (MTGRuling rul : card.getRulings())
			rules.append("-").append(rul.getText()).append("\n");

		rulesTextPane.setText(rules.toString());

		//
		var bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding1);
		bindingGroup.addBinding(autoBinding2);
		bindingGroup.addBinding(autoBinding4);
		bindingGroup.addBinding(autoBinding8);
		bindingGroup.addBinding(autoBindingT);
		bindingGroup.addBinding(autoBindingP);

		magicTextPane.updateTextWithIcons();

		return bindingGroup;
	}

}
