package org.magic.gui.models;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.MTGControler;

public class CardAlertTableModel extends GenericTableModel<MagicCardAlert> {
	
	private static final long serialVersionUID = 1L;

	public CardAlertTableModel() {
		columns = new String[] { "CARD","EDITION","FOIL","MAX_BID","OFFERS","DAILY","WEEKLY","PC_DAILY" };
	}

	@Override
	public int getRowCount() {
		try {
			if (getEnabledPlugin(MTGDao.class).listAlerts() != null)
				return getEnabledPlugin(MTGDao.class).listAlerts().size();

		} catch (Exception e) {
			logger.error(e);
		}

		return 0;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) { 
		case 0:
			return MagicCardAlert.class;
		case 1:
			return List.class;
		case 2:
			return Boolean.class;
		case 3:
			return Double.class;
		case 4:
			return Integer.class;
		case 5:
			return Double.class;
		case 6:
			return Double.class;
		case 7:
			return Double.class;
		default:
			return super.getColumnClass(columnIndex);
		}

	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return (column == 2 || column==1 || column==3);
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:
			return getEnabledPlugin(MTGDao.class).listAlerts().get(row);
		case 1:
			return getEnabledPlugin(MTGDao.class).listAlerts().get(row).getCard().getEditions();
		case 2:
			return getEnabledPlugin(MTGDao.class).listAlerts().get(row).isFoil();
		case 3:
			return getEnabledPlugin(MTGDao.class).listAlerts().get(row).getPrice();
		case 4:
			return getEnabledPlugin(MTGDao.class).listAlerts().get(row).getOffers().size();
		case 5:
			return getEnabledPlugin(MTGDao.class).listAlerts().get(row).getShake().getPriceDayChange();
		case 6:
			return getEnabledPlugin(MTGDao.class).listAlerts().get(row).getShake().getPriceWeekChange();
		case 7:
			return getEnabledPlugin(MTGDao.class).listAlerts().get(row).getShake().getPercentDayChange();
		default:
			return "";
		}

	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		MagicCardAlert alert = getEnabledPlugin(MTGDao.class).listAlerts().get(row);
		if (column == 1) 
		{
			MagicEdition ed = (MagicEdition) aValue;
			try {
				if(!ed.equals(alert.getCard().getCurrentSet())) 
				{
					getEnabledPlugin(MTGDao.class).deleteAlert(alert);
					MagicCard mc = MTGControler.getInstance().switchEditions(alert.getCard(), ed);
					MagicCardAlert alert2 = new MagicCardAlert();
					alert2.setCard(mc);
					alert2.setPrice(alert.getPrice());
					getEnabledPlugin(MTGDao.class).saveAlert(alert2);
					
				}
			} catch (Exception e) {
				logger.error("error set value " + aValue, e);
			}
		}
		
		if(column==2) 
		{
			alert.setFoil(Boolean.parseBoolean(aValue.toString()));
			try {
				getEnabledPlugin(MTGDao.class).updateAlert(alert);
			} catch (Exception e) {
				logger.error("error set value " + aValue, e);
			}
		}
		
		if(column==3) 
		{
			alert.setPrice(Double.parseDouble(aValue.toString()));
			try {
				getEnabledPlugin(MTGDao.class).updateAlert(alert);
			} catch (Exception e) {
				logger.error("error set value " + aValue, e);
			}
		}
		
		fireTableDataChanged();
		
	}


}
