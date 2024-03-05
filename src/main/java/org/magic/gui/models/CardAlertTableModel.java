package org.magic.gui.models;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.CardsManagerService;

public class CardAlertTableModel extends GenericTableModel<MTGAlert> {

	private static final long serialVersionUID = 1L;

	public CardAlertTableModel() {
		columns = new String[] { "CARD","EDITION","NEEDED","FOIL","MAX_BID","OFFERS","DAILY","WEEKLY","PC_DAILY" };
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return MTGAlert.class;
		case 1:
			return MTGEdition.class;
		case 2:
			return Integer.class;
		case 3:
			return Boolean.class;
		case 4:
			return Double.class;
		case 5:
			return Integer.class;
		case 6:
			return Double.class;
		case 7:
			return Double.class;
		case 8:
			return Double.class;
		default:
			return super.getColumnClass(columnIndex);
		}

	}



	@Override
	public boolean isCellEditable(int row, int column) {
		return (column==2 || column==3 || column==4);
	}

	@Override
	public Object getValueAt(int row, int column) {

		if(getItems().isEmpty())
			return null;


		switch (column) {
		case 0:
			return getItems().get(row);
		case 1:
			return getItems().get(row).getCard().getEdition();
		case 2:
			return getItems().get(row).getQty();
		case 3:
			return getItems().get(row).isFoil();
		case 4:
			return getItems().get(row).getPrice();
		case 5:
			return getItems().get(row).getOffers().size();
		case 6:
			return getItems().get(row).getShake().getPriceDayChange();
		case 7:
			return getItems().get(row).getShake().getPriceWeekChange();
		case 8:
			return getItems().get(row).getShake().getPercentDayChange();
		default:
			return "";
		}

	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		MTGAlert alert = getItems().get(row);
		if (column == 1)
		{
			MTGEdition ed = (MTGEdition) aValue;
			try {
				if(!ed.equals(alert.getCard().getEdition()))
				{
					getEnabledPlugin(MTGDao.class).deleteAlert(alert);
					MTGCard mc = CardsManagerService.switchEditions(alert.getCard(), ed);
					var alert2 = new MTGAlert();
					alert2.setCard(mc);
					alert2.setPrice(alert.getPrice());
					getEnabledPlugin(MTGDao.class).saveAlert(alert2);

				}
			} catch (Exception e) {
				logger.error("error {}" ,aValue, e);
			}
		}

		if(column==2)
		{
			alert.setQty(Integer.parseInt(aValue.toString()));
			try {
				getEnabledPlugin(MTGDao.class).updateAlert(alert);
			} catch (Exception e) {
				logger.error("error set {}",aValue, e);
			}
		}


		if(column==3)
		{
			alert.setFoil(Boolean.parseBoolean(aValue.toString()));
			try {
				getEnabledPlugin(MTGDao.class).updateAlert(alert);
			} catch (Exception e) {
				logger.error("error set {}",aValue, e);
			}
		}

		if(column==4)
		{
			alert.setPrice(Double.parseDouble(aValue.toString()));
			try {
				getEnabledPlugin(MTGDao.class).updateAlert(alert);
			} catch (Exception e) {
				logger.error("error set {}",aValue, e);
			}
		}



		fireTableDataChanged();

	}


}
