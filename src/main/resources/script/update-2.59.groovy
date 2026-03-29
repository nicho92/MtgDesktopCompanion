import org.magic.services.MTGControler;



MTGControler.getInstance().setProperty("technical-log/enable",MTGControler.getInstance().get("technical-log"));

MTGControler.getInstance().setProperty("technical-log/conf/jsonqueryinfo","true");
MTGControler.getInstance().setProperty("technical-log/conf/daoinfo","true");
MTGControler.getInstance().setProperty("technical-log/conf/taskinfo","true");
MTGControler.getInstance().setProperty("technical-log/conf/networkinfo","true");
MTGControler.getInstance().setProperty("technical-log/conf/discordinfo","true");
MTGControler.getInstance().setProperty("technical-log/conf/fileaccessinfo","true");
MTGControler.getInstance().setProperty("technical-log/conf/talkmessage","true");