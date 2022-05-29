package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.Announce;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.MTGExportCategory;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;
import org.magic.tools.MTG;

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
	public MTGExportCategory getCategory() {
		return MTGExportCategory.APPLICATION;
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
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		try {
			var a = new Announce();
			a.setCategorie(EnumItems.DECK);
			a.setTitle(deck.getName());
			a.setCondition(EnumCondition.OPENED);
			a.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
			a.setTotalPrice(deck.getAveragePrice());
			a.setContact(MTGControler.getInstance().getWebConfig().getContact());
			var sb = new StringBuilder("//MAIN<br/>");
			
			deck.getMain().entrySet().forEach(e->sb.append(e.getValue()).append(" ").append(e.getKey()).append("<br/>"));
			
			sb.append("//SIDEBOARD<br/>");
			deck.getSideBoard().entrySet().forEach(e->sb.append(e.getValue()).append(" ").append(e.getKey()).append("<br/>"));
			a.setDescription(sb.toString());
			
			
			
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateAnnounce(a);
			
			notify(deck);
		} catch (Exception e) {
			logger.error(e);
		}

	}
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		for(var mcs : stock)
		{
			try {
				var a = new Announce();
				a.setCategorie(mcs.getProduct().getTypeProduct());
				a.setTitle(mcs.getProduct().getName());
				a.setCondition(mcs.getCondition());
				a.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
				a.setTotalPrice(mcs.getPrice());
				a.setContact(MTGControler.getInstance().getWebConfig().getContact());
				
				MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateAnnounce(a);
				
				notify(mcs.getProduct());
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("COLLECTIONS_SELL","To Sell",
					  "COLLECTIONS_BUY","Needed",
					  "DEFAULT","SELL");
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		throw new IOException("Not Implemented");
	}

	@Override
	public String getName() {
		return "MTGCompanion Announces";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGDesktopCompanionExport.class.getResource("/icons/logo.png"));
	}
}
