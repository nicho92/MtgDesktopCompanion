if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	
	dao.executeQuery("DELETE TABLE favorites;");
	
	printf("--done");
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}
