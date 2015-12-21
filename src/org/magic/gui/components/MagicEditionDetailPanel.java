package org.magic.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicEdition;

public class MagicEditionDetailPanel extends JPanel {

	private BindingGroup m_bindingGroup;
	private org.magic.api.beans.MagicEdition magicEdition = new org.magic.api.beans.MagicEdition();
	private JTextField borderJTextField;
	private JTextField cardCountTextField;
	private JTextField releaseDateJTextField;
	private JTextField setJTextField;
	private JTextField typeJTextField;

	public MagicEditionDetailPanel(org.magic.api.beans.MagicEdition newMagicEdition) {
		this();
		setMagicEdition(newMagicEdition);
	}

	public MagicEditionDetailPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 104, 333, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				1.0E-4 };
		setLayout(gridBagLayout);
				
						JLabel setLabel = new JLabel("Set:");
						GridBagConstraints labelGbc_8 = new GridBagConstraints();
						labelGbc_8.insets = new Insets(5, 5, 5, 5);
						labelGbc_8.gridx = 0;
						labelGbc_8.gridy = 0;
						add(setLabel, labelGbc_8);
				
						setJTextField = new JTextField();
						GridBagConstraints componentGbc_8 = new GridBagConstraints();
						componentGbc_8.insets = new Insets(5, 0, 5, 0);
						componentGbc_8.fill = GridBagConstraints.HORIZONTAL;
						componentGbc_8.gridx = 1;
						componentGbc_8.gridy = 0;
						add(setJTextField, componentGbc_8);
				
						JLabel typeLabel = new JLabel("Type:");
						GridBagConstraints labelGbc_11 = new GridBagConstraints();
						labelGbc_11.insets = new Insets(5, 5, 5, 5);
						labelGbc_11.gridx = 0;
						labelGbc_11.gridy = 1;
						add(typeLabel, labelGbc_11);
				
						typeJTextField = new JTextField();
						GridBagConstraints componentGbc_11 = new GridBagConstraints();
						componentGbc_11.insets = new Insets(5, 0, 5, 0);
						componentGbc_11.fill = GridBagConstraints.HORIZONTAL;
						componentGbc_11.gridx = 1;
						componentGbc_11.gridy = 1;
						add(typeJTextField, componentGbc_11);

		JLabel releaseDateLabel = new JLabel("ReleaseDate:");
		GridBagConstraints labelGbc_7 = new GridBagConstraints();
		labelGbc_7.insets = new Insets(5, 5, 5, 5);
		labelGbc_7.gridx = 0;
		labelGbc_7.gridy = 2;
		add(releaseDateLabel, labelGbc_7);

		releaseDateJTextField = new JTextField();
		GridBagConstraints componentGbc_7 = new GridBagConstraints();
		componentGbc_7.insets = new Insets(5, 0, 5, 0);
		componentGbc_7.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_7.gridx = 1;
		componentGbc_7.gridy = 2;
		add(releaseDateJTextField, componentGbc_7);
		
				JLabel borderLabel = new JLabel("Border:");
				GridBagConstraints labelGbc_2 = new GridBagConstraints();
				labelGbc_2.insets = new Insets(5, 5, 5, 5);
				labelGbc_2.gridx = 0;
				labelGbc_2.gridy = 3;
				add(borderLabel, labelGbc_2);
								
										borderJTextField = new JTextField();
										GridBagConstraints componentGbc_2 = new GridBagConstraints();
										componentGbc_2.fill = GridBagConstraints.HORIZONTAL;
										componentGbc_2.insets = new Insets(5, 0, 5, 0);
										componentGbc_2.gridx = 1;
										componentGbc_2.gridy = 3;
										add(borderJTextField, componentGbc_2);
						
								JLabel cardCountLabel = new JLabel("CardCount:");
								GridBagConstraints labelGbc_3 = new GridBagConstraints();
								labelGbc_3.insets = new Insets(5, 5, 5, 5);
								labelGbc_3.gridx = 0;
								labelGbc_3.gridy = 4;
								add(cardCountLabel, labelGbc_3);
												
														cardCountTextField = new JTextField();
														GridBagConstraints componentGbc_3 = new GridBagConstraints();
														componentGbc_3.insets = new Insets(5, 0, 5, 0);
														componentGbc_3.fill = GridBagConstraints.HORIZONTAL;
														componentGbc_3.gridx = 1;
														componentGbc_3.gridy = 4;
														add(cardCountTextField, componentGbc_3);

		if (magicEdition != null) {
			m_bindingGroup = initDataBindings();
		}
	}

	public org.magic.api.beans.MagicEdition getMagicEdition() {
		return magicEdition;
	}

	public void setMagicEdition(org.magic.api.beans.MagicEdition newMagicEdition) {
		setMagicEdition(newMagicEdition, true);
	}

	public void setMagicEdition(org.magic.api.beans.MagicEdition newMagicEdition, boolean update) {
		magicEdition = newMagicEdition;
		if (update) {
			if (m_bindingGroup != null) {
				m_bindingGroup.unbind();
				m_bindingGroup = null;
			}
			if (magicEdition != null) {
				m_bindingGroup = initDataBindings();
			}
		}
	}
	protected BindingGroup initDataBindings() {
		BeanProperty<MagicEdition, String> borderProperty = BeanProperty.create("border");
		BeanProperty<JTextField, String> textProperty_2 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, magicEdition, borderProperty, borderJTextField, textProperty_2);
		autoBinding_2.bind();
		//
		BeanProperty<MagicEdition, Integer> cardCountProperty = BeanProperty.create("cardCount");
		BeanProperty<JTextField, String> valueProperty = BeanProperty.create("text");
		AutoBinding<MagicEdition, Integer, JTextField, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, magicEdition, cardCountProperty, cardCountTextField, valueProperty);
		autoBinding_3.bind();
		//
		BeanProperty<MagicEdition, String> releaseDateProperty = BeanProperty.create("releaseDate");
		BeanProperty<JTextField, String> textProperty_6 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ, magicEdition, releaseDateProperty, releaseDateJTextField, textProperty_6);
		autoBinding_7.bind();
		//
		BeanProperty<MagicEdition, String> setProperty = BeanProperty.create("set");
		BeanProperty<JTextField, String> textProperty_7 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ, magicEdition, setProperty, setJTextField, textProperty_7);
		autoBinding_8.bind();
		//
		BeanProperty<MagicEdition, String> typeProperty = BeanProperty.create("type");
		BeanProperty<JTextField, String> textProperty_10 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ, magicEdition, typeProperty, typeJTextField, textProperty_10);
		autoBinding_11.bind();
		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding_2);
		bindingGroup.addBinding(autoBinding_3);
		bindingGroup.addBinding(autoBinding_7);
		bindingGroup.addBinding(autoBinding_8);
		bindingGroup.addBinding(autoBinding_11);
		return bindingGroup;
	}
}
