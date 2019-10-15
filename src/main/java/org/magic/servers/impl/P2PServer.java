package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.tools.POMReader;

import com.google.common.collect.Lists;

import net.tomp2p.connection.DiscoverNetworks;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;



public class P2PServer extends AbstractMTGServer {

	private PeerDHT serverNode;
	private String version="";

	private static final String DOMAIN = MTGConstants.MTG_APP_NAME;
	
	private Number160 add(PeerDHT node,File f) throws IOException
	{
		Number160 k = Number160.createHash(f.getName());
		byte[] content = FileUtils.readFileToByteArray(f);
		FuturePut fadd = node.add(k).data(new Data(content)).domainKey( Number160.createHash( DOMAIN ) ).start();
		logger.info("Peer "+ node.peerID() + " add [key: " + k + ", value: "+ content + "]");
		fadd.awaitUninterruptibly();
		
		return k;
	}
	
	private FutureGet get(PeerDHT node,Number160 k) throws IOException
	{
		FutureGet fget = node.get(k).all().domainKey( Number160.createHash(DOMAIN) ).start();
		return fget.awaitUninterruptibly();
		
	}
		
	private PeerDHT createAgent(File root,String id,int port, PeerDHT masterPeer) throws IOException {
		
		PeerBuilder b=  new PeerBuilder(new Number160(id.getBytes())).ports(port);
		
		if(masterPeer!=null)
			b.masterPeer(masterPeer.peer());
		
		
		PeerBuilderDHT bdht = new PeerBuilderDHT(b.start());
		PeerDHT peerdht = bdht.start();
	
		if(root!=null)
		{
			for(File f : root.listFiles((File p)->!p.isDirectory()))
				add(peerdht,f);
		}
		
		if(masterPeer!=null)
			peerdht.peer().bootstrap().peerAddress(masterPeer.peerAddress());
		
		
		logger.info("init peer " + id);						
		return peerdht;
		
		
		
		
	}
	
	private void bootstrap(Peer[] peers,Peer master )
    {
        List<FutureBootstrap> futures1 = new ArrayList<>();
        List<FutureDiscover> futures2 = new ArrayList<>();
        
        for ( Peer p : peers )
            futures2.add( p.discover().peerAddress( master.peerAddress() ).start() );
        
        for ( FutureDiscover future : futures2 )
       		future.awaitUninterruptibly();
        
        for (Peer p : peers)
            futures1.add( p.bootstrap().peerAddress( master.peerAddress() ).start() );
       
        for (Peer p : peers)
            futures1.add( master.bootstrap().peerAddress( p.peerAddress() ).start() );
        
        for ( FutureBootstrap future : futures1 )
        	future.awaitUninterruptibly();

    }
	
	
	@Override
	public void start() throws IOException {
		
		try 
		{
			serverNode = createAgent(MTGConstants.MTG_DECK_DIRECTORY,"Nicolas",7700,null);
		
			logger.info( "Server started Listening to: " + DiscoverNetworks.discoverInterfaces(serverNode.peer().connectionBean().resourceConfiguration().bindings()).existingAddresses());
	   
			PeerDHT n1 = createAgent(new File("D:\\programmation"), "bob",7700,serverNode);
			PeerDHT n2 = createAgent(new File("D:\\conf"), "clara",7700,serverNode);

			bootstrap(new Peer[] {n1.peer(),n2.peer()}, serverNode.peer());
			
			Number160 key1Keyword = Number160.createHash("UG.json");
			
			FutureGet fg = get(serverNode,key1Keyword);
			
			while(!fg.isCompleted())
				logger.debug("waiting...");
			
			System.out.println(fg.data());
			
			
			
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
			return serverNode.peer().isShutdown();
		
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
		setProperty("PORT","7700");
	}
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/hive2hive.png"));
	}
	
	public static void main(String[] args) throws IOException {
		new P2PServer().start();

	}
	
	
}
