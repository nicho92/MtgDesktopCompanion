package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

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
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.file.IFileAgent;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.tools.POMReader;

public class P2PServer extends AbstractMTGServer {

	private IH2HNode serverNode ;
	private String version="";
	
	@Override
	public void start() throws IOException {
		
		try {
			serverNode = createAgent(NetworkConfiguration.createInitial(), MTGConstants.MTG_DECK_DIRECTORY,SystemUtils.USER_NAME+"-server","password","pin");
			
			
			
			IH2HNode node2 = createAgent(NetworkConfiguration.create(InetAddress.getByName("localhost")), new File("D:/"), UUID.randomUUID().toString(), "password", "pin");
			IH2HNode node3 = createAgent(NetworkConfiguration.create(InetAddress.getByName("localhost")), new File("C:\\Users\\Nicolas\\.jmc\\6.0.0\\.metadata"), UUID.randomUUID().toString(), "password", "pin");
			
			
			System.out.println("Node Server is connected: " + serverNode.isConnected());
			System.out.println("Node 2 is connected: " + node2.isConnected() + " " + node2.getPeer().peerBean().localMap());
			System.out.println("Node 3 is connected: " + node3.isConnected() + " " + node3.getPeer().peerBean().localMap());
			
			
			
			
			
			
		} catch (Exception e) {
			throw new IOException(e);
		} 
	}
	
	
	
	@Override
	public String getVersion() {
		
		if(version.isEmpty())
			version = POMReader.readVersionFromPom(H2HNode.class, "/META-INF/maven/org.hive2hive/org.hive2hive.core/pom.properties");
		
		return version;
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
	
	
	private IH2HNode createAgent(INetworkConfiguration config, File root, String client, String pass, String pin) throws NoPeerConnectionException, InvalidProcessStateException, ProcessExecutionException, NoSessionException, IOException {
		
		
		IH2HNode nodeClient = H2HNode.createNode(FileConfiguration.createDefault());
		INetworkConfiguration node2Conf = config;
		nodeClient.connect(node2Conf);
		IUserManager userManager = nodeClient.getUserManager();

		try {
			UserCredentials user = new UserCredentials(client, pass, pin);
			
			if (!userManager.isRegistered(user.getUserId()))
				userManager.createRegisterProcess(user).execute();
		
			
			File cache = new File(MTGConstants.DATA_DIR,"p2p-cache");

			if(!userManager.isLoggedIn())
				userManager.createLoginProcess(user, new IFileAgent() {
				
				
				@Override
				public void writeCache(String key, byte[] data) throws IOException {
					logger.debug("write cache"  + key);
					FileUtils.writeByteArrayToFile(new File(cache, key), data);
				}
				
				@Override
				public byte[] readCache(String key) throws IOException {
					logger.debug("read cache "  + key);
		
					if(!new File(cache, key).exists())
						FileUtils.touch(new File(cache, key));
		
					return FileUtils.readFileToByteArray(new File(cache, key));
				}
				
				@Override
				public File getRoot() {
					return root;
				}
			}).execute();
		} catch (NoPeerConnectionException | InvalidProcessStateException | ProcessExecutionException e) {
			logger.error("error client",e);
		}
		return nodeClient;
		
		
		
	}

}
