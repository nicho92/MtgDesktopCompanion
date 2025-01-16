package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import org.magic.api.beans.MTGAnnounce;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumTransactionDirection;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;

import nl.basjes.parse.useragent.yauaa.shaded.org.apache.commons.lang3.ArrayUtils;

public class AnnouncesExport extends AbstractCardExport {


	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.MTGCOMPANION;
	}

	@Override
	public boolean needFile() {
		return false;
	}

	@Override
	public String getFileExtension() {
		return null;
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		try {
			var a = new MTGAnnounce();
			a.setCategorie(EnumItems.DECK);
			a.setTitle(deck.getName());
			a.setCondition(EnumCondition.OPENED);
			a.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
			a.setTotalPrice(deck.getAveragePrice());
			a.setContact(MTGControler.getInstance().getWebshopService().getWebConfig().getContact());
			var sb = new StringBuilder("//MAIN<br/>");

			deck.getMain().entrySet().forEach(e->{
				var p = MTGControler.getInstance().getDefaultStock();
				p.setProduct(e.getKey());
				a.getItems().add(p);
				sb.append(e.getValue()).append(" ").append(e.getKey()).append("<br/>");
			});

			sb.append("//SIDEBOARD<br/>");
			deck.getSideBoard().entrySet().forEach(e->sb.append(e.getValue()).append(" ").append(e.getKey()).append("<br/>"));
			a.setDescription(sb.toString());
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateAnnounce(a);
			
		} catch (Exception e) {
			logger.error(e);
		}

	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		for(var mcs : stock)
		{
			try {
				var a = new MTGAnnounce();
				a.setCategorie(mcs.getProduct().getTypeProduct());
				a.setTitle(mcs.getProduct().getName());
				a.setCondition(mcs.getCondition());
				a.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
				a.setTotalPrice(mcs.getValue().doubleValue());
				a.setContact(MTGControler.getInstance().getWebshopService().getWebConfig().getContact());

				MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateAnnounce(a);

				notify(mcs.getProduct());
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		
		var m = super.getDefaultAttributes();
			m.put("MODE", new MTGProperty("SELL", "Choose announce mode. to Buy or to Sell",ArrayUtils.toStringArray(EnumTransactionDirection.values())));
		
			return m;
	}

	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		throw new IOException("Not Implemented");
	}

	@Override
	public String getName() {
		return "MTGCompanion Announces";
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_ANNOUNCES;
	}
}
