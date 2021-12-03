if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	dao.executeQuery("UPDATE sealed set conditionProduct='SEALED' where conditionProduct='SELEAD'");
	printf("--done");
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}
