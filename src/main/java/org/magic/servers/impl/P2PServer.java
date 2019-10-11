package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.hive2hive.core.H2HSession;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.configs.FileConfiguration;
import org.hive2hive.core.api.configs.NetworkConfiguration;
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
import org.hive2hive.core.network.data.DataManager;
import org.hive2hive.core.processes.files.list.GetFileListStep;
import org.hive2hive.core.processes.login.SessionParameters;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.tools.POMReader;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.GetBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerMapChangeListener;
import net.tomp2p.peers.PeerStatistic;

public class P2PServer extends AbstractMTGServer {

	private IH2HNode serverNode;
	private String version="";
	
	@Override
	public void start() throws IOException {
		
		try 
		{
			serverNode = createAgent(NetworkConfiguration.createInitial(), MTGConstants.MTG_DECK_DIRECTORY,SystemUtils.USER_NAME+"-server","password","pin");
		
			createAgent(NetworkConfiguration.create(InetAddress.getByName("localhost")), MTGConstants.MTG_DECK_DIRECTORY, "bob", "password", "pin");
			createAgent(NetworkConfiguration.create(InetAddress.getByName("localhost")), MTGConstants.MTG_DECK_DIRECTORY, "clara", "password", "pin");
			
			serverNode.getPeer().peerBean().peerMap().all().forEach(pa->{
				System.out.println("Peer connected :" + pa.peerId());
			});
			
			
			
		
			
		} catch (Exception e) {
			throw new IOException(e);
		} 
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
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
		
		
		FileUtils.forceMkdir(new File(root,client));
		
		
		IH2HNode node = H2HNode.createNode(FileConfiguration.createDefault());
		INetworkConfiguration node2Conf = config;
		node.connect(node2Conf);
		IUserManager userManager = node.getUserManager();

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
					return new File(root,client);
				}
			}).execute();
			
			node.getFileManager().subscribeFileEvents(new IFileEventListener() {

				@Override
				public void onFileAdd(IFileAddEvent fileEvent) {
					logger.info(node +" onFileAdd " + fileEvent);
				}

				@Override
				public void onFileDelete(IFileDeleteEvent fileEvent) {
					logger.info(node +" onFileDelete " + fileEvent);
					
				}

				@Override
				public void onFileMove(IFileMoveEvent fileEvent) {
					logger.info(node +" onFileMove " + fileEvent);
					
				}

				@Override
				public void onFileShare(IFileShareEvent fileEvent) {
					logger.info(node +" onFileShare " + fileEvent);
					
				}

				@Override
				public void onFileUpdate(IFileUpdateEvent fileEvent) {
					logger.info(node +" onFileUpdate " + fileEvent);
					
				}
				
			});
			
			
			for(File f : new File(root,client).listFiles())
			{
				logger.info("Adding " + f + " to share");
				node.getFileManager().createAddProcess(f).executeAsync();
			}
			
	
			
		} catch (NoPeerConnectionException | InvalidProcessStateException | ProcessExecutionException e) {
			logger.error("error client",e);
		}
		return node;
		
		
		
	}

}
