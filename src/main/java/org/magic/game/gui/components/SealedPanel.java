package org.magic.game.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.game.model.PositionEnum;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.models.SealedPackTableModel;
import org.magic.gui.renderer.IntegerCellEditor;
import org.magic.gui.renderer.MagicEditionListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.sorters.CmcSorter;
import org.magic.sorters.ColorSorter;
import org.magic.sorters.MTGComparator;
import org.magic.sorters.TypesSorter;
import javax.swing.JCheckBox;

public class SealedPanel extends JPanel {
	
	private JPanel panelWest;
	private JButton btnSaveDeck;
	private JSplitPane panelCenter;
	
	private JButton btnAddBoosters;
	private JScrollPane scrollTablePack;
	private JScrollPane scrollBooster;
	private JTable table;
	private SealedPackTableModel model;
	private BoosterPanel panelOpenedBooster;
	private JComboBox<MagicEdition> cboEditions;
	private JButton btnOpen;
	private JPanel panelControl;
	private GraveyardPanel panelDeck;
	private JPanel panelAnalyse;
	private CmcChartPanel cmcChartPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private TypeRepartitionPanel typeRepartitionPanel;
	private JPanel panelSorters;
	private JRadioButton rdioCmcSortButton;
	private JRadioButton rdiocolorSort;
	private JRadioButton rdiotypeSort;
	private JPanel panel;
	private List<MagicCard> list;
	private MagicDeck deck;
	public SealedPanel() {
		initGUI();
	}
	
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		panelOpenedBooster=new BoosterPanel();
		scrollBooster=new JScrollPane();
		model = new SealedPackTableModel();
		
		List<MagicEdition> li;
		try {
			li = MTGControler.getInstance().getEnabledProviders().loadEditions();
		} catch (IOException e1) {
			li=new ArrayList<>();
			MTGLogger.printStackTrace(e1);
		}
		panelWest = new JPanel();
		panelWest.setPreferredSize(new Dimension(300, 10));
		
		add(panelWest, BorderLayout.WEST);
		panelWest.setLayout(new BorderLayout(0, 0));
		
		panelControl = new JPanel();
		panelWest.add(panelControl, BorderLayout.NORTH);
				table = new JTable(model);
				table.getColumnModel().getColumn(1).setCellEditor(new IntegerCellEditor());
				panelControl.setLayout(new BorderLayout(0, 0));
				
				panel = new JPanel();
				panelControl.add(panel, BorderLayout.NORTH);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[]{105, 65, 0, 0};
				gbl_panel.rowHeights = new int[]{41, 0, 0};
				gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
				gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
						cboEditions = new JComboBox<>();
						GridBagConstraints gbc_cboEditions = new GridBagConstraints();
						gbc_cboEditions.fill = GridBagConstraints.HORIZONTAL;
						gbc_cboEditions.gridwidth = 3;
						gbc_cboEditions.insets = new Insets(0, 0, 5, 5);
						gbc_cboEditions.gridx = 0;
						gbc_cboEditions.gridy = 0;
						panel.add(cboEditions, gbc_cboEditions);
						cboEditions.setRenderer(new MagicEditionListRenderer());
						cboEditions.setModel(new DefaultComboBoxModel<MagicEdition>(li.toArray(new MagicEdition[li.size()])));
						
									
								btnAddBoosters = new JButton(MTGConstants.ICON_NEW);
								GridBagConstraints gbc_btnAddBoosters = new GridBagConstraints();
								gbc_btnAddBoosters.anchor = GridBagConstraints.NORTH;
								gbc_btnAddBoosters.insets = new Insets(0, 0, 0, 5);
								gbc_btnAddBoosters.gridx = 0;
								gbc_btnAddBoosters.gridy = 1;
								panel.add(btnAddBoosters, gbc_btnAddBoosters);
								
								btnOpen = new JButton(MTGConstants.ICON_OPEN);
								GridBagConstraints gbc_btnOpen = new GridBagConstraints();
								gbc_btnOpen.insets = new Insets(0, 0, 0, 5);
								gbc_btnOpen.anchor = GridBagConstraints.NORTH;
								gbc_btnOpen.gridx = 1;
								gbc_btnOpen.gridy = 1;
								panel.add(btnOpen, gbc_btnOpen);
								btnOpen.setEnabled(false);
								
								btnSaveDeck = new JButton(MTGConstants.ICON_SAVE);
								GridBagConstraints gbc_btnSaveDeck = new GridBagConstraints();
								gbc_btnSaveDeck.gridx = 2;
								gbc_btnSaveDeck.gridy = 1;
								panel.add(btnSaveDeck, gbc_btnSaveDeck);
								btnSaveDeck.addActionListener(e->save());
								btnOpen.addActionListener(ae->open());
								btnAddBoosters.addActionListener(ae->addBooster());
				
				scrollTablePack = new JScrollPane();
				scrollTablePack.setPreferredSize(new Dimension(2, 100));
				panelControl.add(scrollTablePack);
				scrollTablePack.setViewportView(table);
				
				panelAnalyse = new JPanel();
				panelWest.add(panelAnalyse, BorderLayout.CENTER);
				panelAnalyse.setLayout(new GridLayout(4, 1, 0, 0));
				
				panelSorters = new JPanel();
				panelAnalyse.add(panelSorters);
				panelSorters.setLayout(new GridLayout(0, 1, 0, 0));
				
				rdioCmcSortButton = new JRadioButton("Sort by CMC");
				rdioCmcSortButton.addActionListener(ae->sort(new CmcSorter()));
				
				panelSorters.add(rdioCmcSortButton);
				
				rdiocolorSort = new JRadioButton("Sort by Color");
				rdiocolorSort.addActionListener(ae->sort(new ColorSorter()));
				
				panelSorters.add(rdiocolorSort);
				
				rdiotypeSort = new JRadioButton("Sort by Type");
				rdiotypeSort.addActionListener(ae->sort(new TypesSorter()));
				
				panelSorters.add(rdiotypeSort);
				
				
				 ButtonGroup group = new ButtonGroup();
				    group.add(rdioCmcSortButton);
				    group.add(rdiocolorSort);
				    group.add(rdiotypeSort);
				
				
				cmcChartPanel = new CmcChartPanel();
				panelAnalyse.add(cmcChartPanel);
				
				manaRepartitionPanel = new ManaRepartitionPanel();
				panelAnalyse.add(manaRepartitionPanel);
				
				typeRepartitionPanel = new TypeRepartitionPanel();
				panelAnalyse.add(typeRepartitionPanel);
		panelCenter=new JSplitPane();
		panelCenter.setResizeWeight(0.5);
		panelCenter.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelCenter.setLeftComponent(panelOpenedBooster);
		panelCenter.addComponentListener(new ComponentAdapter() {
		      @Override
		      public void componentShown(ComponentEvent componentEvent) {
		    	panelCenter.setDividerLocation(.5);
		        removeComponentListener(this);
		      }
		    });
		add(panelCenter, BorderLayout.CENTER);
		panelDeck = new GraveyardPanel() {
			@Override
			public PositionEnum getOrigine() {
				return PositionEnum.DECK;
				
			};
			
			@Override
			public void moveCard(DisplayableCard mc, PositionEnum to) {
				if(to==PositionEnum.BOOSTER)
				{
					deck.remove(mc.getMagicCard());
					list.add(mc.getMagicCard());
				}
			}
			
			@Override
			public void addComponent(DisplayableCard i) {
				super.addComponent(i);
				deck.add(i.getMagicCard());
			}
			
		};
		panelDeck.setPreferredSize(new Dimension((int)MTGControler.getInstance().getCardsDimension().getWidth()+5, (int) (MTGControler.getInstance().getCardsDimension().getHeight()*30)));
		
		add(panelDeck, BorderLayout.EAST);
		
		
	}
	
	private void addBooster() {
		model.add((MagicEdition)cboEditions.getSelectedItem(), 6);
		btnOpen.setEnabled(model.getSealedPack().size()>0);
	}

	protected void open() 
	{
		deck = new MagicDeck();
		deck.setDateCreation(new Date());
		deck.setDescription("Sealed from " + model.getSealedPack());
		deck.setName("sealed from "+model.getSealedPack().toList().size() +" boosters");
		
		
		panelOpenedBooster.clear();
		panelDeck.removeAll();
		panelDeck.revalidate();
		panelDeck.repaint();
		ThreadManager.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				int column=0;
				list = new ArrayList<>();
				for(Entry<MagicEdition, Integer> ed : model.getSealedPack().getEntries())
				{
				
					try {
						for(int i=0;i<ed.getValue();i++)
						{ 
							
							Booster b = MTGControler.getInstance().getEnabledProviders().generateBooster(ed.getKey());
							column++;
							for(MagicCard mc : b.getCards())
							{
								list.add(mc);
								DisplayableCard c = new DisplayableCard(mc, MTGControler.getInstance().getCardsDimension(), true,false);
								panelOpenedBooster.addComponent(c,column);
							}
							
						}	
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				panelOpenedBooster.setList(list);
				
				
				refreshStats(list);
				
			}
		});
		
		
	}
	

	public void sort(MTGComparator<MagicCard> sorter)
	{
		Collections.sort(list,sorter);
		panelOpenedBooster.clear();
		for(MagicCard mc : list)
		{
			DisplayableCard c = new DisplayableCard(mc, MTGControler.getInstance().getCardsDimension(), true,false);
			panelOpenedBooster.addComponent(c,sorter.getWeight(mc));
		}
		
	}
	
	
	private void refreshStats(List<MagicCard> list) 
	{
		cmcChartPanel.init(list);
		typeRepartitionPanel.init(list);
		manaRepartitionPanel.init(list);
	}

	protected void save() {
		
		try {
			MTGControler.getInstance().saveDeck(deck);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex, MTGControler.getInstance().getLangService().getCapitalize("ERROR"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	public static void main(String[] args) {
		
		MTGControler.getInstance().getEnabledProviders().init();
		
		JFrame f = new JFrame();
			   f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			   f.getContentPane().setLayout(new BorderLayout());
			   f.getContentPane().add(new SealedPanel(),BorderLayout.CENTER);
			   f.pack();
			   f.setVisible(true);
		
		
	}


}
