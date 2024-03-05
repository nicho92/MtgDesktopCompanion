import org.magic.api.beans.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import org.magic.api.providers.impl.PrivateMTGSetProvider;
	



var provider = new PrivateMTGSetProvider();

provider.loadEditions().each(ed->{

	var cards = provider.searchCardByEdition(ed);

	cards.each(c->{
		c.setEdition(ed);
	});

	
	provider.saveEdition(ed,cards);
	
});


