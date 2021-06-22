import org.magic.tools.MTG;
import org.magic.api.interfaces.*;
import org.magic.services.MTGConstants;
import java.io.File;


MTG.getPlugin("Json Http Server",MTGServer.class).setProperty("ENABLE_SSL","false");
MTG.getPlugin("Json Http Server",MTGServer.class).setProperty("KEYSTORE_URI", new File(MTGConstants.DATA_DIR,"jetty.jks").getAbsolutePath());
MTG.getPlugin("Json Http Server",MTGServer.class).setProperty("KEYSTORE_PASS", "changeit");
MTG.getPlugin("Party Web Server",MTGServer.class).initDefault();
MTG.getPlugin("Web UI Server",MTGServer.class).initDefault();


if(dao.isSQL()) {
	printf("Executing db update on " + dao.getName());
	dao.executeQuery("ALTER TABLE stocks ADD etched boolean");
	printf("--done");
}
else
{
	printf("Your DAO is not SQL. Don't need to pass script");
}
