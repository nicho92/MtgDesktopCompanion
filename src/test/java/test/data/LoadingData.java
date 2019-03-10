package test.data;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.exports.impl.JsonExport;

public class LoadingData {

	public List<MagicCard> cardsTest() throws IOException, URISyntaxException
	{
		return new JsonExport().importDeck(new File(LoadingData.class.getResource("/sample.json").toURI())).getAsList();
		
	}
	
}
