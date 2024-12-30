package org.magic.gui.models;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;

import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGControler;
import org.magic.services.providers.IconsProvider;

public class MagicEditionsTableModel extends GenericTableModel<MTGEdition> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Map<MTGEdition, Integer> mapCount;

	int countTotal = 0;
	int countDefaultLibrary = 0;
	private MTGCollection collection;

	public MagicEditionsTableModel() {
		initColumns();
	}

	private void initColumns()
	{
		columns=new String[] { "EDITION_CODE",
				"EDITION",
				"EDITION_SIZE",
				"DATE_RELEASE",
				"PC_COMPLETE",
				"QTY",
				"EDITION_TYPE",
				"EDITION_BLOCK",
				"EDITION_ONLINE",
				"PREVIEW"};

	}

	@Override
	public void init(List<MTGEdition> editions) {

		this.items = editions;
		mapCount = new TreeMap<>();

		try {
			calculate();
		} catch (Exception e) {
			logger.error("error calculate", e);
		}
		fireTableDataChanged();
	}


	public void calculate() {

		try {
			collection = new MTGCollection(MTGControler.getInstance().get("default-library"));
			mapCount = CollectionEvaluator.analyse(collection);
		} catch (IOException e) {
			logger.error("can't evaluate for {} : {}",collection,e.getMessage());
		}
	}

	public Map<MTGEdition, Integer> getMapCount() {
		return mapCount;
	}


	public Integer  getCountTotal() {
		return items.stream().mapToInt(MTGEdition::getCardCount).sum();
	}


	public int getCountDefaultLibrary() {
		return mapCount.values().stream().mapToInt(Integer::intValue).sum();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex)
		{
			case 0 : return ImageIcon.class;
			case 1 : return MTGEdition.class;
			case 2 : return Integer.class;
			case 4 : return Double.class;
			case 5 : return Integer.class;
			case 8 | 9 : return Boolean.class;
			default : return super.getColumnClass(columnIndex);
		}
	}


	@Override
	public Object getValueAt(int row, int column) {
		MTGEdition e = items.get(row);
		if (column == 0)
			return IconsProvider.getInstance().get24(e.getId());

		if (column == 1)
			return e;

		if (column == 2)
			return e.getCardCountPhysical();

		if (column == 3)
			return e.getReleaseDate();

		if (column == 4) {

			if(mapCount.get(e)==null)
				return 0.0;

			if (e.getCardCountPhysical() > 0)
				return (double) mapCount.get(e) / e.getCardCountPhysical();
			else
				return (double) mapCount.get(e) / 1;
		}

		if (column == 5)
			return mapCount.get(e)==null?0:mapCount.get(e);

		if (column == 6)
			return e.getType();

		if (column == 7)
			return e.getBlock();

		if (column == 8)
			return e.isOnlineOnly();

		if (column == 9)
			return e.isPreview();

		return "";

	}


}
