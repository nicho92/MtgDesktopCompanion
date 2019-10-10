package org.beta;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.configs.FileConfiguration;
import org.hive2hive.core.api.configs.NetworkConfiguration;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.api.interfaces.INetworkConfiguration;
import org.hive2hive.core.api.interfaces.IUserManager;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.file.IFileAgent;
import org.hive2hive.core.processes.files.list.FileNode;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

public class P2PClient {

	private static Logger logger = MTGLogger.getLogger(P2PClient.class);

	
	public static void main(String[] args) throws NoPeerConnectionException, InvalidProcessStateException, ProcessExecutionException, UnknownHostException, NoSessionException, IllegalArgumentException {
		
		
		IH2HNode nodeClient = H2HNode.createNode(FileConfiguration.createDefault());
		INetworkConfiguration node2Conf = NetworkConfiguration.create(InetAddress.getByName("localhost"));
		nodeClient.connect(node2Conf);
		IUserManager userManager = nodeClient.getUserManager();

		try {
			UserCredentials user = new UserCredentials("Nicho2", "password", "pin");
			
			if (!userManager.isRegistered(user.getUserId()))
				userManager.createRegisterProcess(user).execute();
		
			
			if(!userManager.isLoggedIn())
				userManager.createLoginProcess(user, new IFileAgent() {
				
				File cache = new File(MTGConstants.DATA_DIR,"p2p-cache");
				
				@Override
				public void writeCache(String key, byte[] data) throws IOException {
					logger.debug("write cache"  + key);
					FileUtils.writeByteArrayToFile(new File(cache, key), data);
				}
				
				@Override
				public byte[] readCache(String key) throws IOException {
					logger.debug("read cache"  + key);
					return FileUtils.readFileToByteArray(new File(cache, key));
				}
				
				@Override
				public File getRoot() {
					return MTGConstants.MTG_DECK_DIRECTORY;
				}
			}).execute();
			
			
			System.out.println("list files");
			
			FileNode fNode = nodeClient.getFileManager().createFileListProcess().execute();
			System.out.println(fNode);
			
		} catch (NoPeerConnectionException | InvalidProcessStateException | ProcessExecutionException e) {
			logger.error("error client",e);
		}
		
		
		
	}
}
