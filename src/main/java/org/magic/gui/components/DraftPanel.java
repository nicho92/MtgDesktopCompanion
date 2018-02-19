package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.game.gui.components.BoosterPanel;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.HandPanel;
import org.magic.gui.models.SealedPackTableModel;
import org.magic.gui.renderer.IntegerCellEditor;
import org.magic.gui.renderer.MagicEditionListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class DraftPanel extends JPanel {
	
	private JPanel panelHaut;
	private JPanel panelBottom;
	private JButton btnSaveDeck;
	private JSplitPane panelCenter;
	
	private JButton btnAddBoosters;
	private JScrollPane scrollWest;
	private JScrollPane scrollBooster;
	private JTable table;
	private SealedPackTableModel model;
	private BoosterPanel panelOpenedBooster;
	private JComboBox<MagicEdition> cboEditions;
	private JButton btnOpen;
	private JPanel panel;
	private HandPanel panelDeck;
	
	public DraftPanel() {
		initGUI();
	}
	
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		panelOpenedBooster=new BoosterPanel();
		panelDeck = new HandPanel();
		scrollBooster=new JScrollPane();
		model = new SealedPackTableModel();
		
		List<MagicEdition> li;
		try {
			li = MTGControler.getInstance().getEnabledProviders().loadEditions();
		} catch (IOException e1) {
			li=new ArrayList<>();
			MTGLogger.printStackTrace(e1);
		}
		panelHaut = new JPanel();
		
		add(panelHaut, BorderLayout.WEST);
		table = new JTable(model);
		table.getColumnModel().getColumn(1).setCellEditor(new IntegerCellEditor());
		panelHaut.setLayout(new BorderLayout(0, 0));
		
		scrollWest = new JScrollPane();
		panelHaut.add(scrollWest, BorderLayout.CENTER);
		scrollWest.setViewportView(table);
		
		panel = new JPanel();
		panelHaut.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		cboEditions = new JComboBox<>();
		panel.add(cboEditions, BorderLayout.CENTER);
		cboEditions.setRenderer(new MagicEditionListRenderer());
		cboEditions.setModel(new DefaultComboBoxModel<MagicEdition>(li.toArray(new MagicEdition[li.size()])));
		
					
				btnAddBoosters = new JButton(MTGConstants.ICON_NEW);
				panel.add(btnAddBoosters, BorderLayout.EAST);
				btnAddBoosters.addActionListener(ae->addBooster());
		
		panelBottom = new JPanel();
		add(panelBottom, BorderLayout.SOUTH);
		
		btnSaveDeck = new JButton(MTGConstants.ICON_SAVE);
		btnSaveDeck.addActionListener(e->save());
		
		btnOpen = new JButton(MTGConstants.ICON_OPEN);
		btnOpen.setEnabled(false);
		panelBottom.add(btnOpen);
		btnOpen.addActionListener(ae->open());
		panelBottom.add(btnSaveDeck);
		panelCenter=new JSplitPane();
		panelCenter.setResizeWeight(0.5);
		panelCenter.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelCenter.setLeftComponent(panelOpenedBooster);
		panelCenter.setRightComponent(panelDeck);
		panelCenter.addComponentListener(new ComponentAdapter() {
		      @Override
		      public void componentShown(ComponentEvent componentEvent) {
		    	panelCenter.setDividerLocation(.5);
		        removeComponentListener(this);
		      }
		    });
		add(panelCenter, BorderLayout.CENTER);
		
		
	}
	
	private void addBooster() {
		model.add((MagicEdition)cboEditions.getSelectedItem(), 6);
		btnOpen.setEnabled(model.getSealedPack().size()>0);
	}

	protected void open() 
	{
		for(Entry<MagicEdition, Integer> ed : model.getSealedPack().getEntries())
		{
			try {
				for(int i=0;i<ed.getValue();i++)
				{ 
					Booster b = MTGControler.getInstance().getEnabledProviders().generateBooster(ed.getKey());
					for(MagicCard mc : b.getCards())
					{
						DisplayableCard c = new DisplayableCard(mc, MTGControler.getInstance().getCardsDimension(), true);
						panelOpenedBooster.addComponent(c);
					}
				}	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	

	protected void save() {
		// TODO Auto-generated method stub
		
	}
	
	
	public static void main(String[] args) {
		
		MTGControler.getInstance().getEnabledProviders().init();
		
		JFrame f = new JFrame();
			   f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			   f.getContentPane().setLayout(new BorderLayout());
			   f.getContentPane().add(new DraftPanel(),BorderLayout.CENTER);
			   f.pack();
			   f.setVisible(true);
		
		
	}


}
