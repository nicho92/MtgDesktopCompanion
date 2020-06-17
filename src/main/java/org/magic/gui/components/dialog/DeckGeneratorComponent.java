package org.magic.gui.components.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.beans.enums.MTGColor;
import org.magic.gui.components.ManaPanel;
import org.magic.tools.UITools;

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
		GridBagConstraints gbcpanel = new GridBagConstraints();
		gbcpanel.insets = new Insets(0, 0, 5, 0);
		gbcpanel.fill = GridBagConstraints.BOTH;
		gbcpanel.gridx = 0;
		gbcpanel.gridy = 1;
		add(panel, gbcpanel);
		
		JComboBox<FORMATS> cboFormats = UITools.createCombobox(MagicFormat.FORMATS.values());
		GridBagConstraints gbcCboFormats = new GridBagConstraints();
		gbcCboFormats.insets = new Insets(0, 0, 5, 0);
		gbcCboFormats.fill = GridBagConstraints.HORIZONTAL;
		gbcCboFormats.gridx = 0;
		gbcCboFormats.gridy = 2;
		add(cboFormats, gbcCboFormats);
		
		JComboBox<Integer> cboNumCards = UITools.createCombobox(Arrays.asList(60,40,100));
		GridBagConstraints gbccboNumCards = new GridBagConstraints();
		gbccboNumCards.fill = GridBagConstraints.HORIZONTAL;
		gbccboNumCards.gridx = 0;
		gbccboNumCards.gridy = 3;
		add(cboNumCards, gbccboNumCards);
		
		JPanel panelDetails = new JPanel();
		GridBagConstraints gbcpanelDetails = new GridBagConstraints();
		gbcpanelDetails.insets = new Insets(0, 0, 5, 0);
		gbcpanelDetails.fill = GridBagConstraints.BOTH;
		gbcpanelDetails.gridx = 0;
		gbcpanelDetails.gridy = 4;
		add(panelDetails, gbcpanelDetails);
		
		JCheckBox chkSingleton = new JCheckBox("Singleton");
		panelDetails.add(chkSingleton);
		
		JPanel panelSliders = new JPanel();
		panelSliders.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbcpanelSliders = new GridBagConstraints();
		gbcpanelSliders.insets = new Insets(0, 0, 5, 0);
		gbcpanelSliders.fill = GridBagConstraints.BOTH;
		gbcpanelSliders.gridx = 0;
		gbcpanelSliders.gridy = 5;
		add(panelSliders, gbcpanelSliders);
		GridBagLayout gblpanelSliders = new GridBagLayout();
		gblpanelSliders.columnWidths = new int[]{109, 224, 0, 0};
		gblpanelSliders.rowHeights = new int[]{69, 73, 78, 0};
		gblpanelSliders.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gblpanelSliders.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelSliders.setLayout(gblpanelSliders);
		
		JLabel lblCreatures = new JLabel("Creatures :");
		GridBagConstraints gbclblCreatures = new GridBagConstraints();
		gbclblCreatures.fill = GridBagConstraints.BOTH;
		gbclblCreatures.insets = new Insets(0, 0, 5, 5);
		gbclblCreatures.gridx = 0;
		gbclblCreatures.gridy = 0;
		panelSliders.add(lblCreatures, gbclblCreatures);
		
		JSlider sldCreatures = new JSlider();
		sldCreatures.setValue(38);
		sldCreatures.setPaintTicks(true);
		sldCreatures.setMajorTickSpacing(10);
		sldCreatures.setMinorTickSpacing(5);
		sldCreatures.setSnapToTicks(true);
		sldCreatures.setPaintLabels(true);
		GridBagConstraints gbcsldCreatures = new GridBagConstraints();
		gbcsldCreatures.fill = GridBagConstraints.BOTH;
		gbcsldCreatures.insets = new Insets(0, 0, 5, 5);
		gbcsldCreatures.gridx = 1;
		gbcsldCreatures.gridy = 0;
		panelSliders.add(sldCreatures, gbcsldCreatures);
		
		JLabel lblPercentCreature = new JLabel(sldCreatures.getValue()+"%");
		GridBagConstraints gbclblPercentCreature = new GridBagConstraints();
		gbclblPercentCreature.insets = new Insets(0, 0, 5, 0);
		gbclblPercentCreature.gridx = 2;
		gbclblPercentCreature.gridy = 0;
		panelSliders.add(lblPercentCreature, gbclblPercentCreature);
		
		JLabel lblSpells = new JLabel("Spells :");
		GridBagConstraints gbclblSpells = new GridBagConstraints();
		gbclblSpells.fill = GridBagConstraints.BOTH;
		gbclblSpells.insets = new Insets(0, 0, 5, 5);
		gbclblSpells.gridx = 0;
		gbclblSpells.gridy = 1;
		panelSliders.add(lblSpells, gbclblSpells);
		
		JSlider sldSpells = new JSlider();
		sldSpells.setValue(21);
		sldSpells.setPaintTicks(true);
		sldSpells.setMajorTickSpacing(10);
		sldSpells.setMinorTickSpacing(5);
		sldSpells.setSnapToTicks(true);
		sldSpells.setPaintLabels(true);
		
		GridBagConstraints gbcsldSpells = new GridBagConstraints();
		gbcsldSpells.fill = GridBagConstraints.BOTH;
		gbcsldSpells.insets = new Insets(0, 0, 5, 5);
		gbcsldSpells.gridx = 1;
		gbcsldSpells.gridy = 1;
		panelSliders.add(sldSpells, gbcsldSpells);
		
		JLabel lblPercentSpell = new JLabel(sldSpells.getValue()+"%");
		GridBagConstraints gbclblPercentSpell = new GridBagConstraints();
		gbclblPercentSpell.insets = new Insets(0, 0, 5, 0);
		gbclblPercentSpell.gridx = 2;
		gbclblPercentSpell.gridy = 1;
		panelSliders.add(lblPercentSpell, gbclblPercentSpell);
		
		JLabel lblLands = new JLabel("Lands :");
		GridBagConstraints gbclblLands = new GridBagConstraints();
		gbclblLands.fill = GridBagConstraints.BOTH;
		gbclblLands.insets = new Insets(0, 0, 0, 5);
		gbclblLands.gridx = 0;
		gbclblLands.gridy = 2;
		panelSliders.add(lblLands, gbclblLands);
		
		JSlider sldLands = new JSlider();
		sldLands.setValue(41);
		sldLands.setPaintTicks(true);
		sldLands.setMajorTickSpacing(10);
		sldLands.setMinorTickSpacing(5);
		sldLands.setSnapToTicks(true);
		sldLands.setPaintLabels(true);
		
		GridBagConstraints gbcsldLands = new GridBagConstraints();
		gbcsldLands.insets = new Insets(0, 0, 0, 5);
		gbcsldLands.fill = GridBagConstraints.BOTH;
		gbcsldLands.gridx = 1;
		gbcsldLands.gridy = 2;
		panelSliders.add(sldLands, gbcsldLands);
		
		JLabel lblPercentLands = new JLabel(sldLands.getValue()+"%");
		GridBagConstraints gbclblPercentLands = new GridBagConstraints();
		gbclblPercentLands.gridx = 2;
		gbclblPercentLands.gridy = 2;
		panelSliders.add(lblPercentLands, gbclblPercentLands);
		
		
		for(MTGColor color : MTGColor.getColors())
		{
			
			JToggleButton tglButton = new JToggleButton(new ImageIcon(panelMana.getManaSymbol(color.toManaCode()).getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
				tglButton.addActionListener(al->{
					
				});
			
			
			panel.add(tglButton);
		}
		
		bind(sldCreatures,lblPercentCreature,sldSpells,sldLands);
		bind(sldSpells,lblPercentSpell,sldCreatures,sldLands);
		bind(sldLands,lblPercentLands,sldCreatures,sldSpells);
		
		JButton btnGenerate = new JButton("Generate");
		GridBagConstraints gbcbtnGenerate = new GridBagConstraints();
		gbcbtnGenerate.gridx = 0;
		gbcbtnGenerate.gridy = 6;
		add(btnGenerate, gbcbtnGenerate);
		
	}
	
	
	private void bind(JSlider slider , JLabel label, JSlider slide2, JSlider slide3)
	{
		
		slider.addChangeListener(cl->{
			
			
			if(!slider.getValueIsAdjusting()) {
					
				label.setText(slider.getValue()+"%");
				slide2.setValue(100-slide3.getValue()-slider.getValue());		
				slide3.setValue(100-slide2.getValue()-slider.getValue());
			}
		});
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		
		f.getContentPane().add(new DeckGeneratorComponent());
		f.pack();
		
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
	}
	

}
