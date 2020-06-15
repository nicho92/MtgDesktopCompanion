package org.magic.gui.components.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.beans.enums.MTGColor;
import org.magic.gui.components.ManaPanel;
import org.magic.tools.ImageTools;
import org.magic.tools.UITools;

import java.awt.Insets;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JButton;

public class DeckGeneratorComponent extends JPanel {
	
	private ManaPanel panelMana;
	
	
	public DeckGeneratorComponent() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{453, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		panelMana = new ManaPanel();
		
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		
		JComboBox<FORMATS> cboFormats = UITools.createCombobox(MagicFormat.FORMATS.values());
		GridBagConstraints gbcCboFormats = new GridBagConstraints();
		gbcCboFormats.insets = new Insets(0, 0, 5, 0);
		gbcCboFormats.fill = GridBagConstraints.HORIZONTAL;
		gbcCboFormats.gridx = 0;
		gbcCboFormats.gridy = 2;
		add(cboFormats, gbcCboFormats);
		
		JComboBox<Integer> cboNumCards = UITools.createCombobox(Arrays.asList(60,40,100));
		GridBagConstraints gbc_cboNumCards = new GridBagConstraints();
		gbc_cboNumCards.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboNumCards.gridx = 0;
		gbc_cboNumCards.gridy = 3;
		add(cboNumCards, gbc_cboNumCards);
		
		JPanel panelDetails = new JPanel();
		GridBagConstraints gbc_panelDetails = new GridBagConstraints();
		gbc_panelDetails.insets = new Insets(0, 0, 5, 0);
		gbc_panelDetails.fill = GridBagConstraints.BOTH;
		gbc_panelDetails.gridx = 0;
		gbc_panelDetails.gridy = 4;
		add(panelDetails, gbc_panelDetails);
		
		JCheckBox chkSingleton = new JCheckBox("Singleton");
		panelDetails.add(chkSingleton);
		
		JPanel panelSliders = new JPanel();
		panelSliders.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_panelSliders = new GridBagConstraints();
		gbc_panelSliders.insets = new Insets(0, 0, 5, 0);
		gbc_panelSliders.fill = GridBagConstraints.BOTH;
		gbc_panelSliders.gridx = 0;
		gbc_panelSliders.gridy = 5;
		add(panelSliders, gbc_panelSliders);
		GridBagLayout gbl_panelSliders = new GridBagLayout();
		gbl_panelSliders.columnWidths = new int[]{109, 224, 0, 0};
		gbl_panelSliders.rowHeights = new int[]{69, 73, 78, 0};
		gbl_panelSliders.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panelSliders.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelSliders.setLayout(gbl_panelSliders);
		
		JLabel lblCreatures = new JLabel("Creatures :");
		GridBagConstraints gbc_lblCreatures = new GridBagConstraints();
		gbc_lblCreatures.fill = GridBagConstraints.BOTH;
		gbc_lblCreatures.insets = new Insets(0, 0, 5, 5);
		gbc_lblCreatures.gridx = 0;
		gbc_lblCreatures.gridy = 0;
		panelSliders.add(lblCreatures, gbc_lblCreatures);
		
		JSlider sldCreatures = new JSlider();
		sldCreatures.setValue(38);
		sldCreatures.setPaintTicks(true);
		sldCreatures.setMajorTickSpacing(10);
		sldCreatures.setMinorTickSpacing(5);
		sldCreatures.setSnapToTicks(true);
		sldCreatures.setPaintLabels(true);
		GridBagConstraints gbc_sldCreatures = new GridBagConstraints();
		gbc_sldCreatures.fill = GridBagConstraints.BOTH;
		gbc_sldCreatures.insets = new Insets(0, 0, 5, 5);
		gbc_sldCreatures.gridx = 1;
		gbc_sldCreatures.gridy = 0;
		panelSliders.add(sldCreatures, gbc_sldCreatures);
		
		JLabel lblPercentCreature = new JLabel(sldCreatures.getValue()+"%");
		GridBagConstraints gbc_lblPercentCreature = new GridBagConstraints();
		gbc_lblPercentCreature.insets = new Insets(0, 0, 5, 0);
		gbc_lblPercentCreature.gridx = 2;
		gbc_lblPercentCreature.gridy = 0;
		panelSliders.add(lblPercentCreature, gbc_lblPercentCreature);
		
		JLabel lblSpells = new JLabel("Spells :");
		GridBagConstraints gbc_lblSpells = new GridBagConstraints();
		gbc_lblSpells.fill = GridBagConstraints.BOTH;
		gbc_lblSpells.insets = new Insets(0, 0, 5, 5);
		gbc_lblSpells.gridx = 0;
		gbc_lblSpells.gridy = 1;
		panelSliders.add(lblSpells, gbc_lblSpells);
		
		JSlider sldSpells = new JSlider();
		sldSpells.setValue(21);
		sldSpells.setPaintTicks(true);
		sldSpells.setMajorTickSpacing(10);
		sldSpells.setMinorTickSpacing(5);
		sldSpells.setSnapToTicks(true);
		sldSpells.setPaintLabels(true);
		
		GridBagConstraints gbc_sldSpells = new GridBagConstraints();
		gbc_sldSpells.fill = GridBagConstraints.BOTH;
		gbc_sldSpells.insets = new Insets(0, 0, 5, 5);
		gbc_sldSpells.gridx = 1;
		gbc_sldSpells.gridy = 1;
		panelSliders.add(sldSpells, gbc_sldSpells);
		
		JLabel lblPercentSpell = new JLabel(sldSpells.getValue()+"%");
		GridBagConstraints gbc_lblPercentSpell = new GridBagConstraints();
		gbc_lblPercentSpell.insets = new Insets(0, 0, 5, 0);
		gbc_lblPercentSpell.gridx = 2;
		gbc_lblPercentSpell.gridy = 1;
		panelSliders.add(lblPercentSpell, gbc_lblPercentSpell);
		
		JLabel lblLands = new JLabel("Lands :");
		GridBagConstraints gbc_lblLands = new GridBagConstraints();
		gbc_lblLands.fill = GridBagConstraints.BOTH;
		gbc_lblLands.insets = new Insets(0, 0, 0, 5);
		gbc_lblLands.gridx = 0;
		gbc_lblLands.gridy = 2;
		panelSliders.add(lblLands, gbc_lblLands);
		
		JSlider sldLands = new JSlider();
		sldLands.setValue(41);
		sldLands.setPaintTicks(true);
		sldLands.setMajorTickSpacing(10);
		sldLands.setMinorTickSpacing(5);
		sldLands.setSnapToTicks(true);
		sldLands.setPaintLabels(true);
		
		GridBagConstraints gbc_sldLands = new GridBagConstraints();
		gbc_sldLands.insets = new Insets(0, 0, 0, 5);
		gbc_sldLands.fill = GridBagConstraints.BOTH;
		gbc_sldLands.gridx = 1;
		gbc_sldLands.gridy = 2;
		panelSliders.add(sldLands, gbc_sldLands);
		
		JLabel lblPercentLands = new JLabel(sldLands.getValue()+"%");
		GridBagConstraints gbc_lblPercentLands = new GridBagConstraints();
		gbc_lblPercentLands.gridx = 2;
		gbc_lblPercentLands.gridy = 2;
		panelSliders.add(lblPercentLands, gbc_lblPercentLands);
		
		
		for(MTGColor color : MTGColor.getColors())
		{
			JToggleButton tglButton = new JToggleButton(color.toManaCode());
				tglButton.addActionListener(al->{
					
				});
			
			
			panel.add(tglButton);
		}
		
		bind(sldCreatures,lblPercentCreature,sldSpells,sldLands);
		bind(sldSpells,lblPercentSpell,sldCreatures,sldLands);
		bind(sldLands,lblPercentLands,sldCreatures,sldSpells);
		
		JButton btnGenerate = new JButton("Generate");
		GridBagConstraints gbc_btnGenerate = new GridBagConstraints();
		gbc_btnGenerate.gridx = 0;
		gbc_btnGenerate.gridy = 6;
		add(btnGenerate, gbc_btnGenerate);
		
	}
	
	
	private void bind(JSlider slider , JLabel label, JSlider slide2, JSlider slide3)
	{
		
		slider.addChangeListener(cl->{
			label.setText(slider.getValue()+"%");
			
			slide2.setValue(100-slide3.getValue()-slider.getValue());		
			slide3.setValue(100-slide2.getValue()-slider.getValue());	
		});
	}
	
	

	public static void main(String[] args) {
		JFrame f = new JFrame();
		
		f.getContentPane().add(new DeckGeneratorComponent());
		
		f.pack();
		
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
