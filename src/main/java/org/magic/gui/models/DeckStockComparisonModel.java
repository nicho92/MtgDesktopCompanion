package org.magic.gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.services.MTGControler;

public class DeckStockComparisonModel extends DefaultTableModel {

	private transient String[] columnsName = new String[] { 
			"CARD",
			"NEEDED",
			"STOCK_MODULE",
			"COLLECTION",
			"NEEDED_QTY",
			};
	
	private transient List<Line> list;
	
	public DeckStockComparisonModel() {
		list=new ArrayList<>();
		
	}
	
	@Override
	public String getColumnName(int column) {
		return MTGControler.getInstance().getLangService().getCapitalize(columnsName[column]);
	}
	
	@Override
	public int getColumnCount() {
		return columnsName.length;
	}
	
	@Override
	public int getRowCount() {
		if(list==null)
			return 0;
		
		return list.size();
	}
	
	public void addRow(MagicCard mc, Integer qty, boolean has, List<MagicCardStock> stocks)
	{
		Line l = new Line(mc, qty, has, stocks);
		l.setResult(calculate(l));
		
		list.add(l);
		fireTableDataChanged();
	}

	public void removeAll() {
		list.clear();
		fireTableDataChanged();
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
		case 0:return list.get(row).getMc();
		case 1:return list.get(row).getNeeded();
		case 2:return list.get(row).getStocks().size();
		case 3: return list.get(row).getHas() ? 1: 0;
		case 4:return list.get(row).getResult();
		
		default:return "";
		}
	}

	private Integer calculate(Line line) {
		if(line.getHas() && line.getStocks().isEmpty())
		{
			return line.getNeeded()-1;
		}
		else if (!line.getStocks().isEmpty())
		{
			int count =0;
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

	
}


class Line
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
	};

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

