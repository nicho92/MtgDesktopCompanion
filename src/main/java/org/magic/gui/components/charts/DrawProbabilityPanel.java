package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.tools.UITools;

public class DrawProbabilityPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JXTable table;
	private transient MTGDeckManager calc;
	private AbstractTableModel model;
	int maxTurn = 10;
	private MagicDeck d;

	public DrawProbabilityPanel() {
		initGUI();
	}

	public void setMaxTurn(int maxTurn) {
		this.maxTurn = maxTurn;
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		calc = new MTGDeckManager();
		table = new JXTable();
		
		
		add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.NORTH);

		JLabel lblDrawProbability = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DRAW_PROBABILITIES"));
		lblDrawProbability.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 14));
		panel.add(lblDrawProbability);
	}

	public void init(MagicDeck d) {
		this.d=d;
		initDeck();
	}

	public void init(MagicDeck d, MagicCard c) {
		this.d=d;
		
		if (c != null)
			initCard(c);
	}

	private void initCard(MagicCard card) {
		model = new AbstractTableModel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getColumnName(int t) {

				if (t == 0)
					return "Turn";

				return card.getName();
			}

			@Override
			public Object getValueAt(int r, int c) {
				if (c == 0) {
					return "Turn " + (r);
				} else {
					return UITools.roundDouble(calc.getProbability(d,r, card));
				}
			}

			@Override
			public int getRowCount() {
				return maxTurn + 1;
			}

			@Override
			public int getColumnCount() {
				return 2;
			}
		};

		table.setModel(model);
		model.fireTableDataChanged();
		table.getColumnModel().getColumn(1).setCellRenderer((JTable t, Object val, boolean b1, boolean b2, int r,int c)->new DefaultTableCellRenderer().getTableCellRendererComponent(t, ((double)val*100)+"%", b1, b2, r, c));
		table.packAll();
	}

	private void initDeck() {
		model = new AbstractTableModel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getColumnName(int t) {
				if (t == 0)
					return "Card";
				else
					return "Turn " + t;
			}

			@Override
			public Object getValueAt(int r, int c) {
				if (c == 0) {
					return d.getUniqueCards().get(r);
				} else {
					return UITools.roundDouble(calc.getProbability(d,c - 1, d.getUniqueCards().get(r)));
				}
			}

			@Override
			public int getRowCount() {
				return d.getMain().keySet().size();
			}

			@Override
			public int getColumnCount() {
				return maxTurn + 1;
			}
		};

		table.setModel(model);
		model.fireTableDataChanged();
		table.packAll();
	}

}
