package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.SystemUtils;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.tools.POMReader;

import net.tomp2p.connection.DiscoverNetworks;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;



public class P2PServer extends AbstractMTGServer {

	private Peer serverNode;
	private String version="";

	
	@Override
	public void start() throws IOException {
		
		try 
		{
			serverNode = createAgent(MTGConstants.MTG_DECK_DIRECTORY,"Nicolas",7700,null);
		
			createAgent(MTGConstants.MTG_DECK_DIRECTORY, "bob",7701,serverNode);
			createAgent(MTGConstants.MTG_DECK_DIRECTORY, "clara",7702,serverNode);
			
			logger.info( "Server started Listening to: " + DiscoverNetworks.discoverInterfaces(serverNode.connectionBean().resourceConfiguration().bindings()).existingAddresses());
	   
			
			
		 
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
			version = POMReader.readVersionFromPom(Peer.class, "/META-INF/maven/net.tomp2p/tomp2p-all/pom.properties");
		
		return version;
	}

	@Override
	public void stop() throws IOException {
		if(serverNode!=null)
			serverNode.shutdown();
	}

	@Override
	public boolean isAlive() {
		if(serverNode!=null)
			return serverNode.isShutdown();
		
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
	
	
	private Peer createAgent(File root,String id,int port, Peer masterPeer) throws IOException {
		PeerBuilder b=  new PeerBuilder(new Number160(id.getBytes())).ports(port);
		
		if(masterPeer!=null)
			b.masterPeer(masterPeer);
		
		logger.info("init peer " + id);						
		return b.start();
		
		
		
		
	}

}
