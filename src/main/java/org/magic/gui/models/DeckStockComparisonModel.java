package org.magic.gui.models;

import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGEdition;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.models.DeckStockComparisonModel.Line;


public class DeckStockComparisonModel extends GenericTableModel<Line> {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DeckStockComparisonModel() {

		setColumns(
				"CARD",
				"SET",
				"QTY",
				"STOCK_MODULE",
				"NEEDED_QTY"
				);

	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}


	public void addItem(MTGCard mc, Integer qty, List<MTGCardStock> stocks)
	{
		var l = new Line(mc, qty, stocks);
		l.setResult(calculate(l));

		addItem(l);

	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex==0)
			return MTGCard.class;
		else if (columnIndex==1)
			return MTGEdition.class;

		
		return Integer.class;
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:return items.get(row).getMc();
		case 1:return items.get(row).getMc().getEdition();
		case 2:return items.get(row).getNeeded();
		case 3:return items.get(row).getStocks().stream().mapToInt(mcs->mcs.getQte()).sum();
		case 4:return items.get(row).getResult();

		default:return "";
		}
	}

	private Integer calculate(Line line) {
		var count =0;
		if (!line.getStocks().isEmpty())
		{
			
			for(MTGCardStock st : line.getStocks())
				count +=st.getQte();
		}
		
		count =  line.getNeeded()-count;

		if(count<0)
			count=0;
		
		return count;
		
		
	}

	public class Line
	{
		MTGCard mc;
		Integer needed;
	
		List<MTGCardStock> stocks;
		Integer result;

		public Line(MTGCard mc,Integer needed,List<MTGCardStock> stocks) {
			this.mc=mc;
			this.needed=needed;
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

		public List<MTGCardStock> getStocks() {
			return stocks;
		}
	}


}



