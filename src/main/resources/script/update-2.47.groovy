if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	//dao.executeQuery("ALTER TABLE cards ADD scryfallId VARCHAR(50) DEFAULT NULL");
	//dao.executeQuery("CREATE INDEX idx_scryfallId ON cards (scryfallId)");

	//MySQL-MariaDB
	//var query="UPDATE cards SET scryfallId = JSON_UNQUOTE(JSON_EXTRACT(mcard,'$.scryfallId'))";

	//Postgres
	//var query="UPDATE cards SET scryfallId = mcard->>'scryfallId'";

	var query="UPDATE cards SET scryfallId = JSON_EnumExtraCT(mcard,'\$.scryfallId')";


	dao.executeQuery(query);
	printf("--done");
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}