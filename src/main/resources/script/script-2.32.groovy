import org.magic.services.MTGConstants;
import java.io.File;



if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	dao.executeQuery("UPDATE sealed set typeProduct='SET' WHERE typeProduct='BANNER'");
	printf("--done");
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}