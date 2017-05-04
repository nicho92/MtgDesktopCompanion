package org.magic.game.gui.components;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicCard;
import org.magic.gui.components.MagicTextPane;
import org.magic.gui.components.ManaPanel;

public class LightDescribeCardPanel extends JPanel {
	private JTextField txtName;
	private JTextField txtType;
	private JTextField txtPower;
	private JTextField txtLoyalty;
	private MagicTextPane magicTextPane;
	private ManaPanel manaPanel;
	
	private BindingGroup m_bindingGroup;
	
	private MagicCard card;
	private JPanel panel;
	private JLabel label;
	private JTextField txtT;
	private JScrollPane scrollPane;
	
	public LightDescribeCardPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{52, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 27, 0, 27, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblName = new JLabel("Name :");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		add(lblName, gbc_lblName);
		
		txtName = new JTextField();
		txtName.setEditable(false);
		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.insets = new Insets(0, 0, 5, 0);
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = 0;
		add(txtName, gbc_txtName);
		txtName.setColumns(10);
		
		JLabel lblCost = new JLabel("Cost :");
		GridBagConstraints gbc_lblCost = new GridBagConstraints();
		gbc_lblCost.anchor = GridBagConstraints.EAST;
		gbc_lblCost.insets = new Insets(0, 0, 5, 5);
		gbc_lblCost.gridx = 0;
		gbc_lblCost.gridy = 1;
		add(lblCost, gbc_lblCost);
		
		manaPanel = new ManaPanel();
		GridBagConstraints gbc_manaPanel = new GridBagConstraints();
		gbc_manaPanel.insets = new Insets(0, 0, 5, 0);
		gbc_manaPanel.fill = GridBagConstraints.BOTH;
		gbc_manaPanel.gridx = 1;
		gbc_manaPanel.gridy = 1;
		add(manaPanel, gbc_manaPanel);
		
		JLabel lblType = new JLabel("Type :");
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.anchor = GridBagConstraints.EAST;
		gbc_lblType.insets = new Insets(0, 0, 5, 5);
		gbc_lblType.gridx = 0;
		gbc_lblType.gridy = 2;
		add(lblType, gbc_lblType);
		
		txtType = new JTextField();
		txtType.setEditable(false);
		GridBagConstraints gbc_txtType = new GridBagConstraints();
		gbc_txtType.insets = new Insets(0, 0, 5, 0);
		gbc_txtType.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtType.gridx = 1;
		gbc_txtType.gridy = 2;
		add(txtType, gbc_txtType);
		txtType.setColumns(10);
		
		JLabel lblSt = new JLabel("P/T :");
		GridBagConstraints gbc_lblSt = new GridBagConstraints();
		gbc_lblSt.anchor = GridBagConstraints.EAST;
		gbc_lblSt.insets = new Insets(0, 0, 5, 5);
		gbc_lblSt.gridx = 0;
		gbc_lblSt.gridy = 3;
		add(lblSt, gbc_lblSt);
		
		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 3;
		add(panel, gbc_panel);
		
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
		
		JLabel lblLoyalty = new JLabel("Loyalty :");
		GridBagConstraints gbc_lblLoyalty = new GridBagConstraints();
		gbc_lblLoyalty.anchor = GridBagConstraints.EAST;
		gbc_lblLoyalty.insets = new Insets(0, 0, 5, 5);
		gbc_lblLoyalty.gridx = 0;
		gbc_lblLoyalty.gridy = 4;
		add(lblLoyalty, gbc_lblLoyalty);
		
		txtLoyalty = new JTextField();
		txtLoyalty.setEditable(false);
		GridBagConstraints gbc_txtLoyalty = new GridBagConstraints();
		gbc_txtLoyalty.insets = new Insets(0, 0, 5, 0);
		gbc_txtLoyalty.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLoyalty.gridx = 1;
		gbc_txtLoyalty.gridy = 4;
		add(txtLoyalty, gbc_txtLoyalty);
		txtLoyalty.setColumns(10);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 5;
		add(scrollPane, gbc_scrollPane);
		
		magicTextPane = new MagicTextPane();
		scrollPane.setViewportView(magicTextPane);
		
		magicTextPane.setMaximumSize(new Dimension(120, 200));
		magicTextPane.setEditable(false);
		
		if (card != null) {
			m_bindingGroup = initDataBindings();
		}
	}
	
	public void setCard(MagicCard newMagicCard) {
			card = newMagicCard;
			
			if (m_bindingGroup != null) {
				m_bindingGroup.unbind();
				m_bindingGroup = null;
			}
			if (card != null) {
				m_bindingGroup = initDataBindings();
			}
	}

	

	protected BindingGroup initDataBindings() 
	{
		
		BeanProperty<MagicCard, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, card, nameProperty, txtName, textProperty);
		autoBinding.bind();
		
		BeanProperty<MagicCard, String> costProperty = BeanProperty.create("cost");
		BeanProperty<ManaPanel, String> textProperty_1 = BeanProperty.create("manaCost");
		AutoBinding<MagicCard, String, ManaPanel, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, card, costProperty, manaPanel, textProperty_1);
		autoBinding_1.bind();
		
		BeanProperty<MagicCard, String> fullTypeProperty = BeanProperty.create("fullType");
		BeanProperty<JTextField, String> textProperty_2 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, card, fullTypeProperty, txtType, textProperty_2);
		autoBinding_2.bind();
		
		BeanProperty<MagicCard, Integer> loyaltyProperty = BeanProperty.create("loyalty");
		BeanProperty<JTextField, String> textProperty_4 = BeanProperty.create("text");
		AutoBinding<MagicCard, Integer, JTextField, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ, card, loyaltyProperty, txtLoyalty, textProperty_4);
		autoBinding_4.bind();
		
		BeanProperty<MagicCard, String> textProperty_8 = BeanProperty.create("text");
		BeanProperty<MagicTextPane, String> textProperty_9 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, MagicTextPane, String> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ, card, textProperty_8, magicTextPane, textProperty_9);
		autoBinding_8.bind();
		
		
		BeanProperty<MagicCard, String> pProperty = BeanProperty.create("power");
		BeanProperty<JTextField, String> textPropertyP = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBindingP = Bindings.createAutoBinding(UpdateStrategy.READ, card, pProperty, txtPower, textPropertyP);
		autoBindingP.bind();
		
		BeanProperty<MagicCard, String> tProperty = BeanProperty.create("toughness");
		BeanProperty<JTextField, String> textPropertyT = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBindingT = Bindings.createAutoBinding(UpdateStrategy.READ, card, tProperty, txtT, textPropertyT);
		autoBindingT.bind();
		
		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding_1);
		bindingGroup.addBinding(autoBinding_2);
		bindingGroup.addBinding(autoBinding_4);
		bindingGroup.addBinding(autoBinding_8);
		bindingGroup.addBinding(autoBindingT);
		bindingGroup.addBinding(autoBindingP);
		
		
		magicTextPane.updateTextWithIcons();
		
	return bindingGroup;
}
	
	
}
