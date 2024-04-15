if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	dao.executeQuery("ALTER TABLE stocks ADD idMe VARCHAR(5) DEFAULT NULL");
	dao.executeQuery("ALTER TABLE stocks ADD dateUpdate TIMESTAMP");
	dao.executeQuery("CREATE INDEX idx_stk_idMe ON stocks (idMe)");	
	
	
	
	//mysql & Maria
	query="UPDATE stocks SET idMe = JSON_UNQUOTE(JSON_EXTRACT(mcard,'\$.edition.id'))";

	if(dao.getName().equals("postgresql"))
		query="UPDATE stocks SET idMe = mcard->>'edition.id'";

	if(dao.getName().equals("SQLite"))
		query="UPDATE stocks SET idMe = json_extract(mcard,'\$.edition.id')";

	dao.executeQuery(query);
	printf("filling stocks idMe columns--done");
	

	
	
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}