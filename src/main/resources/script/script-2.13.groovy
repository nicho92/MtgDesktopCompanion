if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	dao.executeQuery("ALTER TABLE stocks ADD graded BOOLEAN;");
	dao.executeQuery("ALTER TABLE stocks ADD gradeName VARCHAR(50);");
	dao.executeQuery("ALTER TABLE stocks ADD gradeNote DECIMAL(5,2);");
	dao.executeQuery("CREATE INDEX idx_stk_gradeName ON stocks (gradeName);");
	printf("--done");
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}
