package test.providers;

import static org.junit.Assert.fail;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGCommand;
import org.magic.api.interfaces.MTGDao;
import org.magic.console.AbstractResponse;
import org.magic.console.MTGConsoleHandler;

public class CommandsTests {

	
	@Before
	public void initProviders()
	{
		getEnabledPlugin(MTGCardsProvider.class).init();
		try {
			getEnabledPlugin(MTGDao.class).init();
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}
	
	@Test
	public void testCommands()
	{
		String line = "search -c cmc=12";
		String[] commandeLine = MTGConsoleHandler.translateCommandline(line);
		MTGCommand c = MTGConsoleHandler.commandFactory(commandeLine[0]);
		try {
			AbstractResponse resp = c.run(commandeLine);
			System.out.println(resp.show());
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}
}
