if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	dao.executeQuery("ALTER TABLE cards ADD scryfallId VARCHAR(50) DEFAULT NULL");
	printf("Adding scryfallId column--done");
	
	dao.executeQuery("CREATE INDEX idx_scryfallId ON cards (scryfallId)");
	printf("Adding scryfallId index--done");

	//MySQL-MariaDB
	var query="UPDATE cards SET scryfallId = JSON_UNQUOTE(JSON_EXTRACT(mcard,'\$.scryfallId'))";

	if(dao.getName().equals("postgresql"))
		query="UPDATE cards SET scryfallId = mcard->>'scryfallId'";

	if(dao.getName().equals("SQLite"))
		query="UPDATE cards SET id = json_extract(mcard,'\$.scryfallId')";

	dao.executeQuery(query);
	printf("filling cards scryfallId columns--done");

	//MySQL-MariaDB
	query="UPDATE stocks SET idmc = JSON_UNQUOTE(JSON_EXTRACT(mcard,'\$.scryfallId'))";

	if(dao.getName().equals("postgresql"))
		query="UPDATE stocks SET idmc = mcard->>'scryfallId'";

	if(dao.getName().equals("SQLite"))
		query="UPDATE stocks SET idmc = json_extract(mcard,'\$.scryfallId')";

	dao.executeQuery(query);
	printf("filling stocks scryfallId columns--done");
	
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}