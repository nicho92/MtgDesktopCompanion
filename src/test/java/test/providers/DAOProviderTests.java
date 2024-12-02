package test.providers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

import test.TestTools;

public class DAOProviderTests {

	MTGCollection col = new MTGCollection("TEST");
	MTGCard mc;
	MTGEdition ed;
	
	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		MTGControler.getInstance();
		TestTools.initTest();
		mc = TestTools.loadData().get(0);
		ed = mc.getEdition();
		
	}
	
	@Test
	public void launch()
	{
		PluginRegistry.inst().listPlugins(MTGDao.class).forEach(this::testPlugin);
	}
	
	
	
	public void testPlugin(MTGDao p)
	{
		
		try {
			p.init();
			System.out.println("*****************************"+p.getName());
			System.out.println("STAT "+p.getStatut());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());
			System.out.println("ICON "+p.getIcon());
			System.out.println("VERS "+p.getVersion());
			System.out.println("JMX NAME "+p.getObjectName());
			System.out.println("CONF FILE " + p.getConfFile());
			System.out.println("LOCATION " + p.getDBLocation());
			System.out.println("SIZE " + p.getDBSize());
			System.out.println("TYPE " + p.getType());
			System.out.println("VERS "+p.getVersion());
			
			System.out.println("SAVING CARDS");
			p.saveCollection(col);
			p.saveCard(mc, col);
			
			System.out.println("LISTING COLLECTION");
			System.out.println(p.listCardsFromCollection(col));
			System.out.println(p.listCardsFromCollection(col, ed));
			System.out.println(p.listCardsFromCollection(col, null));
			System.out.println(p.listEditionsIDFromCollection(col));
			System.out.println("global" + p.getCardsCountGlobal(col));
			System.out.println("cols: " + p.listCollections());
			System.out.println("test: " + p.getCollection("TEST"));
			System.out.println("cols: " + p.listCollectionFromCards(mc));
			
			System.out.println("ALERTS");
			MTGAlert alert=new MTGAlert();
						   alert.setCard(mc);
						   alert.setPrice(10.0);

			p.saveAlert(alert);
			alert.setPrice(15.0);
			p.updateAlert(alert);
			
			System.out.println(p.listAlerts());
			System.out.println(p.hasAlert(mc));
				
			System.out.println("STOCKS");
			var stock = new MTGCardStock();
							stock.setProduct(mc);
							stock.setComment("TEST");
							stock.setQte(1);
							stock.setCondition(EnumCondition.MINT);
							stock.setLanguage("French");
							stock.setMagicCollection(col);
			
			p.saveOrUpdateCardStock(stock);
			stock.setFoil(true);
			p.saveOrUpdateCardStock(stock);
			
			
			
			System.out.println("Total stock :" + p.listStocks().size());
			System.out.println("Get stocks strict " + p.listStocks(mc, col,true));
			System.out.println("Get stocks nstrict " + p.listStocks(mc, col,false));
			
			
			List<MTGCardStock> stocks = new ArrayList<>();
			stocks.add(stock);
			
			
		
			System.out.println("DELETE");
			p.deleteStock(stocks);
			p.removeEdition(ed, col);
			p.removeCard(mc, col);
			p.removeCollection(col);
			p.deleteAlert(alert);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
		
	}
	
	
}
