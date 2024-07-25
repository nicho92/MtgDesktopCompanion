if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	
	
	//alter name column due to some Token's card name
	var query="ALTER TABLE stocks CHANGE name name VARCHAR(150)";

	if(dao.getName().equals("postgresql"))
		query="UPDATE cards SET scryfallId = mcard->>'scryfallId'";

	if(dao.getName().equals("SQLite"))
		query="UPDATE cards SET id = json_extract(mcard,'\$.scryfallId')";

	dao.executeQuery(query);

	
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}