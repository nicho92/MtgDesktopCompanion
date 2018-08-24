package unit.providers;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.interfaces.MTGCommand;
import org.magic.console.CommandResponse;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGControler;

public class CommandsTests {

	
	@Before
	public void initProviders()
	{
		MTGControler.getInstance().getEnabledCardsProviders().init();
		try {
			MTGControler.getInstance().getEnabledDAO().init();
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
			CommandResponse<?> resp = c.run(commandeLine);
			System.out.println(resp.show());
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}
}
