package org.magic.gui.components.charts;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.standard.DoubleCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.tools.UITools;
public class DrawProbabilityPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JXTable table;
	private transient MTGDeckManager calc;
	private AbstractTableModel model;
	private int maxTurn = 10;
	private MTGDeck d;

	public DrawProbabilityPanel() {
		initGUI();
	}

	public void setMaxTurn(int maxTurn) {
		this.maxTurn = maxTurn;
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		calc = new MTGDeckManager();
		table = UITools.createNewTable(null,false);
		table.setDefaultRenderer(Double.class, new DoubleCellEditorRenderer(true,false));


		add(new JScrollPane(table), BorderLayout.CENTER);
		var panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.NORTH);

		var lblDrawProbability = new JLabel(capitalize("DRAW_PROBABILITIES"));
		lblDrawProbability.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 14));
		panel.add(lblDrawProbability);
	}

	public void init(MTGDeck d) {
		this.d=d.getMergedDeck();
		initDeck();
	}

	public void init(MTGDeck d, MTGCard c) {
		this.d=d;

		if (c != null)
			initCard(c);
	}

	private void initCard(MTGCard card) {
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
			public Class<?> getColumnClass(int columnIndex) {
				if(columnIndex>0)
					return Double.class;

				return super.getColumnClass(columnIndex);
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
			public Class<?> getColumnClass(int columnIndex) {
				if(columnIndex>0)
					return Double.class;

				return super.getColumnClass(columnIndex);
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

	@Override
	public String getTitle() {
		return "DRAWING";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_DECK;
	}
	
	
}
