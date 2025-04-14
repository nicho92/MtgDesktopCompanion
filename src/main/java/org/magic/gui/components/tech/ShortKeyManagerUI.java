package org.magic.gui.components.tech;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXTable;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.ShortKeyModel;
import org.magic.gui.renderer.ShortKeysCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.ShortKeyManager;
import org.magic.services.tools.UITools;

public class ShortKeyManagerUI extends MTGUIComponent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable tableKeys;
	private ShortKeyModel model;
	private JTextField textField;
	private int currentKeyCode;

	public ShortKeyManagerUI() {
		setLayout(new BorderLayout(0, 0));

		var panneauHaut = new JPanel();
		var btnDelete = UITools.createBindableJButton(null,MTGConstants.ICON_DELETE,KeyEvent.VK_D,"delete shortkey");
		var btnSaveBinding = UITools.createBindableJButton(null,MTGConstants.ICON_SAVE,KeyEvent.VK_S,"save shortkey");
		textField = new JTextField(10);
		var panneauBas = new JPanel();

		model = new ShortKeyModel();
		model.setMainObjectIndex(1);
		tableKeys = UITools.createNewTable(model,false);
		tableKeys.setDefaultRenderer(JButton.class,new ShortKeysCellRenderer());
		tableKeys.setDefaultRenderer(MTGUIComponent.class,new ShortKeysCellRenderer());

		btnDelete.setEnabled(false);
		tableKeys.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(tableKeys),BorderLayout.CENTER);
		add(panneauHaut, BorderLayout.NORTH);
		add(panneauBas, BorderLayout.SOUTH);


		panneauHaut.add(btnDelete);



		var gblpanneauBas = new GridBagLayout();
		gblpanneauBas.columnWidths = new int[]{108, 247, 0, 0};
		gblpanneauBas.rowHeights = new int[]{0, 0};
		gblpanneauBas.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gblpanneauBas.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panneauBas.setLayout(gblpanneauBas);


		panneauBas.add(new JLabel("Binding Key : "), UITools.createGridBagConstraints(null, null, 0, 0));

		panneauBas.add(textField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 0));
		panneauBas.add(btnSaveBinding, UITools.createGridBagConstraints(null, null, 2, 0));

		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				currentKeyCode=e.getKeyCode();
				textField.setText(KeyEvent.getKeyText(e.getKeyCode()));
			}
		});

		btnDelete.addActionListener(_->{
			JButton c = UITools.getTableSelection(tableKeys,model.getMainObjectIndex());
			ShortKeyManager.inst().removeMnemonic(c);
			model.fireTableDataChanged();
		});


		btnSaveBinding.addActionListener(_->{
			JButton b = UITools.getTableSelection(tableKeys, model.getMainObjectIndex());
			if(b!=null)
				ShortKeyManager.inst().setShortCutTo(currentKeyCode, b);

			ShortKeyManager.inst().store();
		});

		tableKeys.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				JButton b = UITools.getTableSelection(tableKeys,model.getMainObjectIndex());

				btnDelete.setEnabled(b!=null);

				currentKeyCode=b.getMnemonic();
				textField.setText(KeyEvent.getKeyText(currentKeyCode));
			}
		});


	}

		@Override
		public String getTitle() {
			return "ShortKey Manager";
		}

		@Override
		public ImageIcon getIcon() {
			return MTGConstants.ICON_SHORTCUT;
		}
}



