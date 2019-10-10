package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.configs.FileConfiguration;
import org.hive2hive.core.api.configs.NetworkConfiguration;
import org.hive2hive.core.api.interfaces.IFileConfiguration;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.api.interfaces.INetworkConfiguration;
import org.hive2hive.core.api.interfaces.IUserManager;
import org.hive2hive.core.events.framework.interfaces.IFileEventListener;
import org.hive2hive.core.events.framework.interfaces.file.IFileAddEvent;
import org.hive2hive.core.events.framework.interfaces.file.IFileDeleteEvent;
import org.hive2hive.core.events.framework.interfaces.file.IFileMoveEvent;
import org.hive2hive.core.events.framework.interfaces.file.IFileShareEvent;
import org.hive2hive.core.events.framework.interfaces.file.IFileUpdateEvent;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.file.IFileAgent;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;

public class P2PServer extends AbstractMTGServer {

	private IH2HNode serverNode ;
	
	@Override
	public void start() throws IOException {
		INetworkConfiguration netConfig = NetworkConfiguration.createInitial();
		IFileConfiguration fileConfig = FileConfiguration.createDefault();
		serverNode = H2HNode.createNode(fileConfig);
		serverNode.connect(netConfig);
		
		
		serverNode.getFileManager().subscribeFileEvents(new IFileEventListener() {
			
			@Override
			public void onFileUpdate(IFileUpdateEvent fileEvent) {
				System.out.println(fileEvent);
				
			}
			
			@Override
			public void onFileShare(IFileShareEvent fileEvent) {
				System.out.println(fileEvent);
				
			}
			
			@Override
			public void onFileMove(IFileMoveEvent fileEvent) {
				System.out.println(fileEvent);
				
			}
			
			@Override
			public void onFileDelete(IFileDeleteEvent fileEvent) {
				System.out.println(fileEvent);
				
			}
			
			@Override
			public void onFileAdd(IFileAddEvent fileEvent) {
				System.out.println(fileEvent);
				
			}
		});
		
		
		try {
		IUserManager userManager = serverNode.getUserManager();

			UserCredentials credentials = new UserCredentials(getString("USERNAME")+"-server", "password", "pin");
			
			if (!userManager.isRegistered(credentials.getUserId()))
				userManager.createRegisterProcess(credentials).execute();
			
			userManager.createLoginProcess(credentials, new IFileAgent() {
				File cache = new File("d:/","p2p-cache");
				@Override
				public void writeCache(String key, byte[] data) throws IOException {
					logger.info("write cache"  + key);
					FileUtils.writeByteArrayToFile(new File(cache, key), data);
				}
				
				@Override
				public byte[] readCache(String key) throws IOException {
					logger.info("read cache"  + key);
					return FileUtils.readFileToByteArray(new File(cache, key));
				}
				
				@Override
				public File getRoot() {
					return MTGConstants.MTG_DECK_DIRECTORY;
				}
			}).execute();
		} catch (Exception  e) {
			logger.error(e);
			throw new IOException(e);
		}
		
		
		for(File f : MTGConstants.MTG_DECK_DIRECTORY.listFiles())
			try {
					serverNode.getFileManager().createAddProcess(f).executeAsync();
					logger.info("sharing " + f);
			} catch (Exception e) {
				logger.debug("error with " + f,e);
			}
	
	}
	
	@Override
	public String getVersion() {
		return "1.2.2";
	}

	@Override
	public void stop() throws IOException {
		if(serverNode!=null)
			serverNode.disconnect();

	}

	@Override
	public boolean isAlive() {
		if(serverNode!=null)
			return serverNode.isConnected();
		
		return false;
	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTO_START");
	}

	@Override
	public String description() {
		return "Enable this server to exchange decks with other players around the world";
	}

	@Override
	public String getName() {
		return "P2P Server";
	}

	@Override
	public void initDefault() {
		setProperty("AUTO_START", "false");
		setProperty("USERNAME",SystemUtils.USER_NAME);
	}
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/hive2hive.png"));
	}
	
	public static void main(String[] args) throws IOException {
		new P2PServer().start();

	}

}
