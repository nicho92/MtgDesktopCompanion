if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	dao.executeQuery("ALTER TABLE alerts ADD foil boolean");
	printf("--done");
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}
