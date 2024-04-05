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
	printf("filling scryfallId columns--done");

	
	//dao.executeQuery("ALTER TABLE cards DROP PRIMARY KEY");
	//dao.executeQuery("ALTER TABLE cards ADD PRIMARY KEY (scryfallId,edition,collection)");

	
	printf("--done");
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}