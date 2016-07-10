package org.magic.gui.game;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.magic.game.Player;
import org.magic.gui.components.ManaPanel;

public class ManaPoolPanel extends JPanel{
	private JSpinner spinW;
	private JSpinner spinU;
	private JSpinner spinB;
	private JSpinner spinR;
	private JSpinner spinG;
	private JSpinner spinC;
	private Player player;
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	private Map<String,Integer> manapool;
	
	
	
	
	public ManaPoolPanel() {
		
		
		JPanel panel = new JPanel();
		add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{40, 66, 0};
		gbl_panel.rowHeights = new int[]{32, 33, 33, 33, 33, 33, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		ManaPanel panelW = new ManaPanel();
		panelW.setManaCost("{W}");
		GridBagConstraints gbc_panelW = new GridBagConstraints();
		gbc_panelW.insets = new Insets(0, 0, 5, 5);
		gbc_panelW.gridx = 0;
		gbc_panelW.gridy = 0;
		panel.add(panelW, gbc_panelW);
		
		spinW = new JSpinner();
		spinW.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{B}", (int)spinW.getValue());
			}
		});
		spinW.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spinW = new GridBagConstraints();
		gbc_spinW.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinW.insets = new Insets(0, 0, 5, 0);
		gbc_spinW.gridx = 1;
		gbc_spinW.gridy = 0;
		panel.add(spinW, gbc_spinW);
		
		ManaPanel panelU = new ManaPanel();
		panelU.setManaCost("{U}");
		GridBagConstraints gbc_panelU = new GridBagConstraints();
		gbc_panelU.insets = new Insets(0, 0, 5, 5);
		gbc_panelU.gridx = 0;
		gbc_panelU.gridy = 1;
		panel.add(panelU, gbc_panelU);
		
		spinU = new JSpinner();
		spinU.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{U}", (int)spinU.getValue());
			}
		});
		spinU.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spinU = new GridBagConstraints();
		gbc_spinU.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinU.insets = new Insets(0, 0, 5, 0);
		gbc_spinU.gridx = 1;
		gbc_spinU.gridy = 1;
		panel.add(spinU, gbc_spinU);
		
		ManaPanel panelB = new ManaPanel();
		panelB.setManaCost("{B}");
		GridBagConstraints gbc_panelB = new GridBagConstraints();
		gbc_panelB.insets = new Insets(0, 0, 5, 5);
		gbc_panelB.gridx = 0;
		gbc_panelB.gridy = 2;
		panel.add(panelB, gbc_panelB);
		
		spinB = new JSpinner();
		spinB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{B}", (int)spinB.getValue());
			}
		});
		spinB.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spinB = new GridBagConstraints();
		gbc_spinB.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinB.insets = new Insets(0, 0, 5, 0);
		gbc_spinB.gridx = 1;
		gbc_spinB.gridy = 2;
		panel.add(spinB, gbc_spinB);
		
		ManaPanel panelR = new ManaPanel();
		panelR.setManaCost("{R}");
		GridBagConstraints gbc_panelR = new GridBagConstraints();
		gbc_panelR.insets = new Insets(0, 0, 5, 5);
		gbc_panelR.gridx = 0;
		gbc_panelR.gridy = 3;
		panel.add(panelR, gbc_panelR);
		
		spinR = new JSpinner();
		spinR.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{R}", (int)spinR.getValue());
			}
		});
		spinR.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spinR = new GridBagConstraints();
		gbc_spinR.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinR.insets = new Insets(0, 0, 5, 0);
		gbc_spinR.gridx = 1;
		gbc_spinR.gridy = 3;
		panel.add(spinR, gbc_spinR);
		
		ManaPanel panelG = new ManaPanel();
		panelG.setManaCost("{G}");
		GridBagConstraints gbc_panelG = new GridBagConstraints();
		gbc_panelG.insets = new Insets(0, 0, 5, 5);
		gbc_panelG.gridx = 0;
		gbc_panelG.gridy = 4;
		panel.add(panelG, gbc_panelG);
		
		spinG = new JSpinner();
		spinG.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{G}", (int)spinG.getValue());
			}
		});
		spinG.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spinG = new GridBagConstraints();
		gbc_spinG.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinG.insets = new Insets(0, 0, 5, 0);
		gbc_spinG.gridx = 1;
		gbc_spinG.gridy = 4;
		panel.add(spinG, gbc_spinG);
		
		ManaPanel panelC = new ManaPanel();
		panelC.setManaCost("{C}");
		GridBagConstraints gbc_panelC = new GridBagConstraints();
		gbc_panelC.insets = new Insets(0, 0, 5, 5);
		gbc_panelC.gridx = 0;
		gbc_panelC.gridy = 5;
		panel.add(panelC, gbc_panelC);
		
		spinC = new JSpinner();
		spinC.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{C}", (int)spinC.getValue());
			}
		});
		spinC.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spinC = new GridBagConstraints();
		gbc_spinC.insets = new Insets(0, 0, 5, 0);
		gbc_spinC.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinC.gridx = 1;
		gbc_spinC.gridy = 5;
		panel.add(spinC, gbc_spinC);
		
		
	}

	public JSpinner getSpinW() {
		return spinW;
	}
	public JSpinner getSpinU() {
		return spinU;
	}
	public JSpinner getSpinB() {
		return spinB;
	}
	public JSpinner getSpinR() {
		return spinR;
	}
	public JSpinner getSpinG() {
		return spinG;
	}
	public JSpinner getSpinC() {
		return spinC;
	}
	
}
