package unit.providers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.dao.impl.FileDAO;
import org.magic.api.dao.impl.HsqlDAO;
import org.magic.api.dao.impl.MongoDbDAO;
import org.magic.api.dao.impl.MysqlDAO;
import org.magic.api.dao.impl.PostgresqlDAO;
import org.magic.api.interfaces.MagicDAO;
import org.magic.services.MTGLogger;

public class DAOProviderTests {

	
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
		
		MTGLogger.changeLevel(Level.ERROR);
		
	}
	
	@Test
	public void test()
	{
		//testProviders(new FileDAO());
		testProviders(new MongoDbDAO());
	}
	
	
	
	public void testProviders(MagicDAO p)
	{
		
		try {
			p.init();
			System.out.println("******************TESTING " + p);
			System.out.println("STATUT " + p.getStatut());
			System.out.println("LOCATION " + p.getDBLocation());
			System.out.println("SIZE " + p.getDBSize()/1024/1024);
			System.out.println("TYPE " + p.getType());
			
			System.out.println("******************SAVING");
			p.saveCollection(col);
			p.saveCard(mc, col);
			
			System.out.println("******************LISTING");
			System.out.println("list  " + p.listCards());
			System.out.println("count " + p.getCardsCount(col, ed));
			System.out.println(p.getCardsFromCollection(col));
			System.out.println(p.getCardsFromCollection(col, ed));
			System.out.println(p.getCardsFromCollection(col, null));
			System.out.println(p.getEditionsIDFromCollection(col));
			System.out.println("global" + p.getCardsCountGlobal(col));
			System.out.println("cols: " + p.getCollections());
			System.out.println("test: " + p.getCollection("TEST"));
			System.out.println("cols: " + p.getCollectionFromCards(mc));
			
			System.out.println("******************ALERTS");
			MagicCardAlert alert=new MagicCardAlert();
						   alert.setCard(mc);
						   alert.setPrice(10.0);

			p.saveAlert(alert);
			alert.setPrice(15.0);
			p.updateAlert(alert);
			
			System.out.println(p.getAlerts());
			System.out.println(p.hasAlert(mc));
			
			
			System.out.println("******************STOCKS");
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
			
			for(MagicCardStock st : p.getStocks())
			{
				System.out.println(st.getIdstock() +" " + st.getMagicCard() + " " + st.getMagicCollection() + " " + st.isFoil());
			}
			
			
			System.out.println("Get stocks " + p.getStocks(mc, col));
			
			List<MagicCardStock> stocks = new ArrayList<>();
			stocks.add(stock);
			
			
			System.out.println("******************BACKUP");
			try{
				p.backup(new File("d:/"));
			}
			catch(Exception e)
			{
				System.err.println(e);
			}
			
			
			System.out.println("******************DELETE");
			p.deleteStock(stocks);
		//	p.removeEdition(ed, col);
			p.removeCard(mc, col);
			p.removeCollection(col);
			p.deleteAlert(alert);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	
		
	}
	
	
}
