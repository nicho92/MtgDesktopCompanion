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
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

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
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new GridLayout(3, 4, 0, 0));
		
		ManaPanel panelW = new ManaPanel();
		panelW.setManaCost("{W}");
		panel.add(panelW);
		
		spinW = new JSpinner();
		spinW.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{B}", (int)spinW.getValue());
			}
		});
		spinW.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		panel.add(spinW);
		
		ManaPanel panelU = new ManaPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panelU.getLayout();
		flowLayout_2.setAlignment(FlowLayout.CENTER);
		panelU.setManaCost("{U}");
		panel.add(panelU);
		
		spinU = new JSpinner();
		spinU.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{U}", (int)spinU.getValue());
			}
		});
		spinU.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		panel.add(spinU);
		
		ManaPanel panelB = new ManaPanel();
		panelB.setManaCost("{B}");
		panel.add(panelB);
		
		spinB = new JSpinner();
		spinB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{B}", (int)spinB.getValue());
			}
		});
		spinB.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		panel.add(spinB);
		
		ManaPanel panelR = new ManaPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelR.getLayout();
		flowLayout_1.setAlignment(FlowLayout.CENTER);
		panelR.setManaCost("{R}");
		panel.add(panelR);
		
		spinR = new JSpinner();
		spinR.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{R}", (int)spinR.getValue());
			}
		});
		spinR.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		panel.add(spinR);
		
		ManaPanel panelG = new ManaPanel();
		panelG.setManaCost("{G}");
		panel.add(panelG);
		
		spinG = new JSpinner();
		spinG.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{G}", (int)spinG.getValue());
			}
		});
		spinG.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		panel.add(spinG);
		
		ManaPanel panelC = new ManaPanel();
		FlowLayout flowLayout = (FlowLayout) panelC.getLayout();
		flowLayout.setAlignment(FlowLayout.CENTER);
		panelC.setManaCost("{C}");
		panel.add(panelC);
		
		spinC = new JSpinner();
		spinC.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setMana("{C}", (int)spinC.getValue());
			}
		});
		spinC.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		panel.add(spinC);
		
		
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
