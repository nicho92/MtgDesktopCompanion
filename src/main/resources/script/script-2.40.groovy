if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	
	dao.executeQuery("ALTER TABLE contacts RENAME COLUMN id TO contact_id;");
	
	printf("--done");
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}
