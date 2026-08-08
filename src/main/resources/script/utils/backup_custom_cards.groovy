import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.api.dao.impl.*;



/////////CUSTOM BACKUP : COPY your customs cards to another DAO

var editor = new PrivateMTGSetProvider();
var targetDAO = new SQLLiteDAO();

targetDAO.init(null);

editor.loadEditions().forEach(edition-> {
	System.out.println("Duplate " + edition);
	targetDAO.saveCustomSet(edition);

	editor.searchCardByEdition(edition).forEach(c->{
		targetDAO.saveCustomCard(c);
	});
	
});