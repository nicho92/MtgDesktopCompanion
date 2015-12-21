package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.providers.impl.DeckbrewProvider;
import org.magic.api.providers.impl.MtgapiProvider;
import org.magic.api.providers.impl.MtgjsonProvider;

public class ProviderSelector extends JDialog {

	private final JPanel contentPanel = new JPanel();
	JComboBox<MagicCardsProvider> cboProviders;
	JComboBox<String> cboLanguages;
	/**
	 * Create the dialog.
	 */
	
	public MagicCardsProvider getProvider()
	{
		return (MagicCardsProvider)cboProviders.getSelectedItem();
	}
	
	
	public ProviderSelector() {
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 127);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{191, 160, 0};
		gbl_contentPanel.rowHeights = new int[]{20, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
				JLabel lblSelectYourProvider = new JLabel("Select your provider : ");
				GridBagConstraints gbc_lblSelectYourProvider = new GridBagConstraints();
				gbc_lblSelectYourProvider.anchor = GridBagConstraints.WEST;
				gbc_lblSelectYourProvider.insets = new Insets(0, 0, 5, 5);
				gbc_lblSelectYourProvider.gridx = 0;
				gbc_lblSelectYourProvider.gridy = 0;
				contentPanel.add(lblSelectYourProvider, gbc_lblSelectYourProvider);
		cboProviders = new JComboBox<MagicCardsProvider>();
		cboProviders.addItem(new MtgjsonProvider());
		cboProviders.addItem(new DeckbrewProvider());
		cboProviders.addItem(new MtgapiProvider());
		
		GridBagConstraints gbc_cboProviders = new GridBagConstraints();
		gbc_cboProviders.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboProviders.anchor = GridBagConstraints.NORTH;
		gbc_cboProviders.insets = new Insets(0, 0, 5, 0);
		gbc_cboProviders.gridx = 1;
		gbc_cboProviders.gridy = 0;
		contentPanel.add(cboProviders, gbc_cboProviders);
		cboProviders.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MagicCardsProvider prov = (MagicCardsProvider)cboProviders.getSelectedItem();
				if(prov.getLanguages()!=null)
				{
					cboLanguages.setModel(new DefaultComboBoxModel<String>(prov.getLanguages()));
				}
				
			}
		});
			JLabel lblDefaultCardLanguage = new JLabel("Default Card Language :");
			GridBagConstraints gbc_lblDefaultCardLanguage = new GridBagConstraints();
			gbc_lblDefaultCardLanguage.anchor = GridBagConstraints.WEST;
			gbc_lblDefaultCardLanguage.insets = new Insets(0, 0, 0, 5);
			gbc_lblDefaultCardLanguage.gridx = 0;
			gbc_lblDefaultCardLanguage.gridy = 1;
			contentPanel.add(lblDefaultCardLanguage, gbc_lblDefaultCardLanguage);
			cboLanguages= new JComboBox<String>();
			GridBagConstraints gbc_cboLanguages = new GridBagConstraints();
			gbc_cboLanguages.fill = GridBagConstraints.HORIZONTAL;
			gbc_cboLanguages.anchor = GridBagConstraints.NORTH;
			gbc_cboLanguages.gridx = 1;
			gbc_cboLanguages.gridy = 1;
			contentPanel.add(cboLanguages, gbc_cboLanguages);
		
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

			JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						fermeture();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				this.setVisible(false);
			cboProviders.getActionListeners()[0].actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		
	}


	protected void fermeture() {
		this.dispose();
		
	}


	public String getLanguage() {
		if(cboLanguages.getSelectedItem()!=null)
			return cboLanguages.getSelectedItem().toString();
		else
			return "English";
		
	}

}
