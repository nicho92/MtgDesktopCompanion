if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	try{
	dao.executeQuery("ALTER TABLE stocks ADD name VARCHAR(50)");
	}
	catch(Exception e)
	{
		printf("column name already present");
	}
	//mysql & Maria
	query="UPDATE stocks SET name = JSON_UNQUOTE(JSON_EXTRACT(mcard,'\$.name'))";

	if(dao.getName().equals("postgresql"))
		query="UPDATE stocks SET name = mcard->>'name'";

	if(dao.getName().equals("SQLite"))
		query="UPDATE stocks SET name = json_extract(mcard,'\$.name')";

	dao.executeQuery(query);
	
	dao.executeQuery("CREATE INDEX idx_stockname ON stocks (name)");
	printf("filling stocks name columns--done");
	

	
	
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}