if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	try{
	dao.executeQuery("ALTER TABLE stocks ADD digital BOOLEAN");
	}
	catch(Exception e)
	{
		printf("column digital already present");
	}

	
	dao.executeQuery("UPDATE stocks SET digital = true where conditions ='ONLINE'");
	dao.executeQuery("UPDATE stocks SET conditions = 'MINT' where conditions ='ONLINE'");
	
	
	printf("filling stocks columns--done");
	

	
	
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}