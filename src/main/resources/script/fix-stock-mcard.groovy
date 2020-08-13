if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	dao.executeQuery("ALTER TABLE stocks CHANGE mcard mcard LONGTEXT DEFAULT NULL");
	printf("--done");
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}
script-2.16.groovy