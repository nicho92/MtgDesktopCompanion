/**
* Backup your SQL File database like H2, SQLITE and HSQLDB to another directory
*/


import org.magic.services.*;
import org.magic.services.tools.*;
import org.magic.api.interfaces.*;
import org.magic.api.exports.impl.*;
import org.magic.api.beans.*;
import java.io.File;
import org.magic.services.PluginRegistry;



//////////////////////////////////MANUAL CONFIGURATION 

var daoPluginName = "SQLite";
var externFileDirectory= "E:\\Mon Drive\\MTG";

//////////////////////////////////////////////////////



File externalDirectory=new File(externFileDirectory);
var dao = PluginRegistry.inst().getPlugin(daoPluginName,MTGDao.class);
var dbFile = new File(dao.getString("SERVERNAME"));

var filename = dbFile.getName()+"_"+UITools.formatDate(new Date(),"yyyy-MM-dd_HH-mm");

var dbFileDest = new File(externalDirectory,filename);

FileTools.copyFile(dbFile,dbFileDest);

println(dbFile.getAbsolutePath() + " as been saved in "+dbFileDest.getAbsolutePath());
