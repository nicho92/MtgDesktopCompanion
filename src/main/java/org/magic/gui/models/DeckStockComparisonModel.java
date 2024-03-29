package org.magic.gui.models;

import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
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


	public void addItem(MTGCard mc, Integer qty, List<MTGCollection> has, List<MTGCardStock> stocks)
	{
		var l = new Line(mc, qty, has, stocks);
		l.setResult(calculate(l));

		addItem(l);

	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex==0)
			return MTGCard.class;

		return Integer.class;
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:return items.get(row).getMc();
		case 1:return items.get(row).getNeeded();
		case 2:return items.get(row).getStocks().size();
		case 3: return items.get(row).getHas().size();
		case 4:return items.get(row).getResult();

		default:return "";
		}
	}

	private Integer calculate(Line line) {
		if(!line.getHas().isEmpty() && line.getStocks().isEmpty())
		{
			return line.getNeeded()-line.getHas().size();
		}
		else if (!line.getStocks().isEmpty())
		{
			var count =0;
			for(MTGCardStock st : line.getStocks())
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
		MTGCard mc;
		Integer needed;
		List<MTGCollection> has;
		List<MTGCardStock> stocks;
		Integer result;

		public Line(MTGCard mc,Integer needed,List<MTGCollection> has,List<MTGCardStock> stocks) {
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

		public MTGCard getMc() {
			return mc;
		}


		public Integer getNeeded() {
			return needed;
		}


		public List<MTGCollection> getHas() {
			return has;
		}


		public List<MTGCardStock> getStocks() {
			return stocks;
		}
	}


}



