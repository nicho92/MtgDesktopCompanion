import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.api.dao.impl.MongoDbDAO;



/////////CUSTOM BACKUP : COPY your customs cards to another DAO

var editor = new PrivateMTGSetProvider();
var targetDAO = new MongoDbDAO();


targetDAO.init();

editor.loadEditions().forEach(edition-> {
	
	targetDAO.saveCustomSet(edition);

	editor.searchCardByEdition(edition).forEach(c->{
		targetDAO.saveCustomCard(c);
	});
	
});