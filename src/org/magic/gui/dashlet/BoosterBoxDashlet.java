package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.models.CardsShakerTableModel;
import org.magic.gui.renderer.MagicEditionListRenderer;
import org.magic.gui.renderer.MagicEditionRenderer;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

import javax.swing.JTextPane;
import javax.swing.JTable;

public class BoosterBoxDashlet extends AbstractJDashlet{
	private CardsShakerTableModel modStandard;
	private JSpinner mythicsSpinner;
	private JSpinner boxSizeSpinner;
	private JComboBox<MagicEdition> cboEditions;
	private JTable table;
	private BoosterLineTableModel boostersModel;
	private JLabel lblTotal;
	
	public BoosterBoxDashlet() {
		super();
		setFrameIcon(new ImageIcon(BoosterBoxDashlet.class.getResource("/res/up.png")));
		//initGUI();
	}
	
	@Override
	public String getName() {
		return "Booster Box";
	}

	

	@Override
	public void init() {
	
		
	}

	@Override
	public void initGUI() {
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
		
		mythicsSpinner = new JSpinner();
		mythicsSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				init();
			}
		});
	
		List<MagicEdition> eds= new ArrayList<>();
		try {
			eds.addAll(MTGControler.getInstance().getEnabledProviders().loadEditions());
			Collections.sort(eds);
			eds.add(0,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cboEditions = new JComboBox(new DefaultComboBoxModel<MagicEdition>(eds.toArray(new MagicEdition[eds.size()])));
		cboEditions.setRenderer(new MagicEditionListRenderer());
		
		
		panneauHaut.add(cboEditions);
		
		JLabel lblBoxSize = new JLabel("Box size: ");
		panneauHaut.add(lblBoxSize);
		
		boxSizeSpinner = new JSpinner();
		boxSizeSpinner.setModel(new SpinnerNumberModel(new Integer(36), new Integer(0), null, new Integer(1)));
		panneauHaut.add(boxSizeSpinner);
		
		JLabel lblMythicsInBox = new JLabel("mythics in box");
		panneauHaut.add(lblMythicsInBox);
		mythicsSpinner.setModel(new SpinnerNumberModel(new Integer(5), new Integer(1), null, new Integer(1)));
		panneauHaut.add(mythicsSpinner);
		
		JButton btnCalculate = new JButton("Calculate");
		
		panneauHaut.add(btnCalculate);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		boostersModel = new BoosterLineTableModel();
		table = new JTable(boostersModel);
		scrollPane.setViewportView(table);
		
		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);
		
		lblTotal = new JLabel("Total");
		panneauBas.add(lblTotal);
		modStandard = new CardsShakerTableModel();
		
		btnCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				ThreadManager.getInstance().execute(new Runnable() {
					
					@Override
					public void run() {
						try {
							List<CardShake> prices = MTGControler.getInstance().getEnabledDashBoard().getShakeForEdition((MagicEdition)cboEditions.getSelectedItem());
							boostersModel.clear();
							double total=0;
							
							for(int i=0;i<(int)boxSizeSpinner.getValue();i++)
							{
								List<MagicCard> booster =MTGControler.getInstance().getEnabledProviders().openBooster((MagicEdition) cboEditions.getSelectedItem());
								BoosterLine line = new BoosterLine();
											line.setBoosterNumber(String.valueOf(i+1));
											line.setCards(booster);
								
								double price = 0;
								for(MagicCard mc : booster)
									for(CardShake cs : prices)
										if(cs.getName().equalsIgnoreCase(mc.getName()))
										{
											price += cs.getPrice();
											line.setPrice(price);
											
										}
								
								boostersModel.addLine(line);
								total = total+line.getPrice();
								lblTotal.setText("Total : $ " + total);
							}
							
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
						
					}
				}, "Open Box");
				
				
				
				
			}
		});
		
		
		
		
		
		
		
		if(props.size()>0) {
			Rectangle r = new Rectangle((int)Double.parseDouble(props.getProperty("x")), 
										(int)Double.parseDouble(props.getProperty("y")),
										(int)Double.parseDouble(props.getProperty("w")),
										(int)Double.parseDouble(props.getProperty("h")));
			
			try {
				mythicsSpinner.setValue(Integer.parseInt(props.getProperty("LIMIT","5")));
			} catch (Exception e) {
				//logger.error("can't get LIMIT value",e);
			}
			setBounds(r);
			}
		
		setVisible(true);
	}


}

class BoosterLineTableModel extends DefaultTableModel
{
	
	List<BoosterLine> boosters;
	private static final String[] COLUMNS = {"Number","Cards","Price"};
	
	
	@Override
	public Object getValueAt(int row, int column) {
		
		switch (column) {
		case 0: return boosters.get(row).getBoosterNumber();
		case 1: return boosters.get(row).getCards();
		case 2: return String.format("$%.,.2f", boosters.get(row).getPrice());
		default : return "";
		}
	}
	
	public void clear() {
		boosters.clear();
		
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}
	
	public void addLine(BoosterLine bl)
	{
		boosters.add(bl);
		fireTableDataChanged();
	}
	
	public BoosterLineTableModel() {
		boosters=new ArrayList<BoosterLine>();
	}
	
	public void init(List<BoosterLine> lines)
	{
		this.boosters=lines;
		fireTableDataChanged();
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}
	
	@Override
	public int getRowCount() {
		if(boosters==null)
			return 0;
		else
			return boosters.size();
	}
	
	
}

class BoosterLine
{
		private String boosterNumber;
		private List<MagicCard> cards;
		private Double price;
		
		public String getBoosterNumber() {
			return boosterNumber;
		}
		public void setBoosterNumber(String boosterNumber) {
			this.boosterNumber = boosterNumber;
		}
		public List<MagicCard> getCards() {
			return cards;
		}
		public void setCards(List<MagicCard> cards) {
			this.cards = cards;
		}
		public Double getPrice() {
			return price;
		}
		public void setPrice(Double price) {
			this.price = price;
		}
		
		@Override
		public String toString() {
			return "Booster " + getBoosterNumber() +": $" + price;
		}
		
		
}
