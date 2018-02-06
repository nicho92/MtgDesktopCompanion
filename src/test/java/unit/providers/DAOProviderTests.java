package unit.providers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.dao.impl.HsqlDAO;
import org.magic.api.interfaces.MagicDAO;

public class DAOProviderTests {

	MagicDAO p;
	MagicCard mc;
	MagicEdition ed;
	MagicCollection col;
	
	@Before
	public void createCards()
	{
		mc = new MagicCard();
		mc.setName("Black Lotus");
		mc.setLayout("normal");
		mc.setCost("{0}");
		mc.setCmc(0);
		mc.getTypes().add("Artifact");
		mc.setReserved(true);
		mc.setText("{T}, Sacrifice Black Lotus: Add three mana of any one color to your mana pool.");
		mc.setRarity("Rare");
		mc.setArtist("Christopher Rush");
		mc.setId("c944c7dc960c4832604973844edee2a1fdc82d98");
		mc.setMciNumber("232");
		MagicEdition ed = new MagicEdition();
					 ed.setId("lea");
					 ed.setSet("Limited Edition Alpha");
					 ed.setBorder("Black");
					 ed.setRarity("Rare");
					 ed.setArtist("Christopher Rush");
					 ed.setMultiverse_id("3");
					 ed.setNumber("232");
		
		mc.getEditions().add(ed);
		
		col = new MagicCollection("TEST");
	}
	
	@Test
	public void testProviders()
	{
		
		try {
			p = new HsqlDAO();
			p.init();
			System.out.println(p.getStatut());
			System.out.println(p.getDBLocation());
			System.out.println(p.getDBSize()/1024/1024);
			
			p.saveCollection(col);
			p.saveCard(mc, col);
			
			System.out.println(p.listCards());
			System.out.println(p.getCardsCount(col, ed));
			System.out.println(p.getCardsFromCollection(col));
			System.out.println(p.getCardsFromCollection(col, ed));
			System.out.println(p.getCardsFromCollection(col, null));
			System.out.println(p.getEditionsIDFromCollection(col));
			System.out.println(p.getCardsCountGlobal(col));
			System.out.println(p.getCollections());
			System.out.println(p.getCollection("TEST"));
			System.out.println(p.getCollectionFromCards(mc));
			
			
			MagicCardAlert alert=new MagicCardAlert();
						   alert.setCard(mc);
						   alert.setPrice(10.0);

			p.saveAlert(alert);
			alert.setPrice(15.0);
			p.updateAlert(alert);
			
			System.out.println(p.getAlerts());
			System.out.println(p.hasAlert(mc));
			
			
			
			MagicCardStock stock = new MagicCardStock();
			stock.setMagicCard(mc);
			stock.setComment("TEST");
			stock.setQte(1);
			stock.setCondition(EnumCondition.MINT);
			stock.setLanguage("French");
			stock.setMagicCollection(col);
			
			p.saveOrUpdateStock(stock);
			
			stock.setFoil(true);
			
			p.saveOrUpdateStock(stock);
			
			System.out.println(p.getStocks());
			System.out.println(p.getStocks(mc, col));
			
			
			
			
			
			List<MagicCardStock> stocks = new ArrayList<>();
			stocks.add(stock);
			p.deleteStock(stocks);
			
			
			p.backup(new File("d:/"));
			
			
			p.removeCard(mc, col);
			p.removeCollection(col);
			
			p.deleteAlert(alert);
			
			
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	
		
	}
	
	
}
