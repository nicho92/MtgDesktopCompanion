package org.magic.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableRowSorter;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.models.EditionsShakerTableModel;
import org.magic.services.ThreadManager;

public class MagicEditionDetailPanel extends JSplitPane {

	private BindingGroup m_bindingGroup;
	private org.magic.api.beans.MagicEdition magicEdition = new org.magic.api.beans.MagicEdition();
	private JTextField borderJTextField;
	private JTextField cardCountTextField;
	private JTextField releaseDateJTextField;
	private JTextField setJTextField;
	private JTextField typeJTextField;
	private JLabel lblBlock;
	private JTextField blockJTextField;
	private JLabel lblId;
	private JTextField idJtextField;
	private JScrollPane scrollPane;
	private JTable table;
	private EditionsShakerTableModel mod;
	private JPanel panneauHaut;
	
	
	public MagicEditionDetailPanel(MagicEdition newMagicEdition) {
		setMagicEdition(newMagicEdition);
	}

	public MagicEditionDetailPanel() {
		setOrientation(JSplitPane.VERTICAL_SPLIT);
		panneauHaut = new JPanel();
		
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 104, 333, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
				1.0E-4 };
		panneauHaut.setLayout(gridBagLayout);
				
						JLabel setLabel = new JLabel("Set:");
						GridBagConstraints labelGbc_8 = new GridBagConstraints();
						labelGbc_8.insets = new Insets(5, 5, 5, 5);
						labelGbc_8.gridx = 0;
						labelGbc_8.gridy = 0;
						panneauHaut.add(setLabel, labelGbc_8);
				
						setJTextField = new JTextField();
						
						GridBagConstraints componentGbc_8 = new GridBagConstraints();
						componentGbc_8.insets = new Insets(5, 0, 5, 5);
						componentGbc_8.fill = GridBagConstraints.HORIZONTAL;
						componentGbc_8.gridx = 1;
						componentGbc_8.gridy = 0;
						panneauHaut.add(setJTextField, componentGbc_8);
				
						JLabel typeLabel = new JLabel("Type:");
						GridBagConstraints labelGbc_11 = new GridBagConstraints();
						labelGbc_11.insets = new Insets(5, 5, 5, 5);
						labelGbc_11.gridx = 0;
						labelGbc_11.gridy = 1;
						panneauHaut.add(typeLabel, labelGbc_11);
				
						typeJTextField = new JTextField();
						GridBagConstraints componentGbc_11 = new GridBagConstraints();
						componentGbc_11.insets = new Insets(5, 0, 5, 5);
						componentGbc_11.fill = GridBagConstraints.HORIZONTAL;
						componentGbc_11.gridx = 1;
						componentGbc_11.gridy = 1;
						panneauHaut.add(typeJTextField, componentGbc_11);

		JLabel releaseDateLabel = new JLabel("ReleaseDate:");
		GridBagConstraints labelGbc_7 = new GridBagConstraints();
		labelGbc_7.insets = new Insets(5, 5, 5, 5);
		labelGbc_7.gridx = 0;
		labelGbc_7.gridy = 2;
		panneauHaut.add(releaseDateLabel, labelGbc_7);

		releaseDateJTextField = new JTextField();

		GridBagConstraints componentGbc_7 = new GridBagConstraints();
		componentGbc_7.insets = new Insets(5, 0, 5, 5);
		componentGbc_7.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_7.gridx = 1;
		componentGbc_7.gridy = 2;
		panneauHaut.add(releaseDateJTextField, componentGbc_7);
		
				JLabel borderLabel = new JLabel("Border:");
				GridBagConstraints labelGbc_2 = new GridBagConstraints();
				labelGbc_2.insets = new Insets(5, 5, 5, 5);
				labelGbc_2.gridx = 0;
				labelGbc_2.gridy = 3;
				panneauHaut.add(borderLabel, labelGbc_2);
								
				borderJTextField = new JTextField();
				GridBagConstraints componentGbc_2 = new GridBagConstraints();
				componentGbc_2.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_2.insets = new Insets(5, 0, 5, 5);
				componentGbc_2.gridx = 1;
				componentGbc_2.gridy = 3;
				panneauHaut.add(borderJTextField, componentGbc_2);

				JLabel cardCountLabel = new JLabel("CardCount:");
				GridBagConstraints labelGbc_3 = new GridBagConstraints();
				labelGbc_3.insets = new Insets(5, 5, 5, 5);
				labelGbc_3.gridx = 0;
				labelGbc_3.gridy = 4;
				panneauHaut.add(cardCountLabel, labelGbc_3);
						
				cardCountTextField = new JTextField();

				GridBagConstraints componentGbc_3 = new GridBagConstraints();
				componentGbc_3.insets = new Insets(5, 0, 5, 5);
				componentGbc_3.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_3.gridx = 1;
				componentGbc_3.gridy = 4;
				panneauHaut.add(cardCountTextField, componentGbc_3);
				
				lblBlock = new JLabel("Block : ");
				GridBagConstraints gbc_lblBlock = new GridBagConstraints();
				gbc_lblBlock.insets = new Insets(0, 0, 5, 5);
				gbc_lblBlock.gridx = 0;
				gbc_lblBlock.gridy = 5;
				panneauHaut.add(lblBlock, gbc_lblBlock);
				
				blockJTextField = new JTextField();
				GridBagConstraints gbc_blockJTextField = new GridBagConstraints();
				gbc_blockJTextField.insets = new Insets(0, 0, 5, 5);
				gbc_blockJTextField.fill = GridBagConstraints.HORIZONTAL;
				gbc_blockJTextField.gridx = 1;
				gbc_blockJTextField.gridy = 5;
				panneauHaut.add(blockJTextField, gbc_blockJTextField);
				blockJTextField.setColumns(10);
				
				lblId = new JLabel("ID :");
				GridBagConstraints gbc_lblId = new GridBagConstraints();
				gbc_lblId.insets = new Insets(0, 0, 5, 5);
				gbc_lblId.gridx = 0;
				gbc_lblId.gridy = 6;
				panneauHaut.add(lblId, gbc_lblId);
				
				idJtextField = new JTextField();

				GridBagConstraints gbc_txtID = new GridBagConstraints();
				gbc_txtID.insets = new Insets(0, 0, 5, 5);
				gbc_txtID.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtID.gridx = 1;
				gbc_txtID.gridy = 6;
				panneauHaut.add(idJtextField, gbc_txtID);
				idJtextField.setColumns(10);
				
				scrollPane = new JScrollPane();
//				GridBagConstraints gbc_scrollPane = new GridBagConstraints();
//				gbc_scrollPane.gridwidth = 2;
//				gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
//				gbc_scrollPane.fill = GridBagConstraints.BOTH;
//				gbc_scrollPane.gridx = 0;
//				gbc_scrollPane.gridy = 7;
//				panneauHaut.add(scrollPane, gbc_scrollPane);
//				
				mod= new EditionsShakerTableModel();
				table = new JTable(mod);
				table.setRowSorter(new TableRowSorter(mod));
				scrollPane.setViewportView(table);
				
				setLeftComponent(panneauHaut);
				setRightComponent(scrollPane);

		if (magicEdition != null) {
			m_bindingGroup = initDataBindings();
		}
		
		setEditable(false);
	}
	
	public void setEditable(boolean b)
	{
		idJtextField.setEditable(b);
		blockJTextField.setEditable(b);
		borderJTextField.setEditable(b);
		cardCountTextField.setEditable(b);
		releaseDateJTextField.setEditable(b);
		typeJTextField.setEditable(b);
		setJTextField.setEditable(b);
	}
	

	public org.magic.api.beans.MagicEdition getMagicEdition() {
		return magicEdition;
	}

	public void setMagicEdition(MagicEdition newMagicEdition) {
		setMagicEdition(newMagicEdition, true);
	}

	public void setMagicEdition(MagicEdition newMagicEdition, boolean update) {
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
		
		
		ThreadManager.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				mod.init(magicEdition);
				mod.fireTableDataChanged();
				
			}
		}, "load prices for" + magicEdition);
		
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
		
		BeanProperty<MagicEdition, String> blockProperty = BeanProperty.create("block");
		BeanProperty<JTextField, String> textProperty_11 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding_12 = Bindings.createAutoBinding(UpdateStrategy.READ, magicEdition, blockProperty, blockJTextField, textProperty_11);
		autoBinding_12.bind();
		
		BeanProperty<MagicEdition, String> idProperty = BeanProperty.create("id");
		BeanProperty<JTextField, String> textProperty_12 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding_13 = Bindings.createAutoBinding(UpdateStrategy.READ, magicEdition, idProperty, idJtextField, textProperty_12);
		autoBinding_13.bind();

		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding_2);
		bindingGroup.addBinding(autoBinding_3);
		bindingGroup.addBinding(autoBinding_7);
		bindingGroup.addBinding(autoBinding_8);
		bindingGroup.addBinding(autoBinding_11);
		bindingGroup.addBinding(autoBinding_12);
		return bindingGroup;
	}
}
