package org.magic.gui.models;

import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.models.DeckStockComparisonModel.Line;


public class DeckStockComparisonModel extends GenericTableModel<Line> {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DeckStockComparisonModel() {

		columns = new String[] {
				"CARD",
				"QTY",
				"STOCK_MODULE",
				"COLLECTION",
				"NEEDED_QTY"
				};

	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}


	public void addItem(MagicCard mc, Integer qty, boolean has, List<MagicCardStock> stocks)
	{
		var l = new Line(mc, qty, has, stocks);
		l.setResult(calculate(l));

		addItem(l);

	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex==0)
			return MagicCard.class;

		return Integer.class;
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:return items.get(row).getMc();
		case 1:return items.get(row).getNeeded();
		case 2:return items.get(row).getStocks().size();
		case 3: return items.get(row).getHas().booleanValue() ? 1: 0;
		case 4:return items.get(row).getResult();

		default:return "";
		}
	}

	private Integer calculate(Line line) {
		if(line.getHas().booleanValue() && line.getStocks().isEmpty())
		{
			return line.getNeeded()-1;
		}
		else if (!line.getStocks().isEmpty())
		{
			var count =0;
			for(MagicCardStock st : line.getStocks())
				count +=st.getQte();

			count =  line.getNeeded()-count;

			if(count<0)
				count=0;
			return count;
		}
		else
		{
			return line.getNeeded();
		}
	}

	public class Line
	{
		MagicCard mc;
		Integer needed;
		Boolean has;
		List<MagicCardStock> stocks;
		Integer result;

		public Line(MagicCard mc,Integer needed,Boolean has,List<MagicCardStock> stocks) {
			this.mc=mc;
			this.needed=needed;
			this.has=has;
			this.stocks=stocks;
		}

		public void setResult(Integer result) {
			this.result = result;
		}

		public Integer getResult() {
			return result;
		}

		public MagicCard getMc() {
			return mc;
		}


		public Integer getNeeded() {
			return needed;
		}


		public Boolean getHas() {
			return has;
		}


		public List<MagicCardStock> getStocks() {
			return stocks;
		}
	}


}



