package org.magic.servers.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;

import net.tomp2p.connection.PeerConnection;
import net.tomp2p.connection.PeerException;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerStatusListener;
import net.tomp2p.peers.RTT;
import net.tomp2p.storage.Data;


public class PeerTestServer extends AbstractMTGServer {

	private static final String PEER_NODE_MASTER = "PEER_NODE_MASTER";
	private PeerDHT  peer;
	
	
	public static void main(String[] args) throws IOException {
		
		new PeerTestServer().start();
	}
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("PORT","9090",
					  "USERNAME",SystemUtils.getUserName(),
					  "AUTO_START","false",
					  PEER_NODE_MASTER,"mtgcompanion.me:9090",
					  "IS_MASTER","true",
					  "BEHIND_FIREWALL","true");
	}
	

	@Override
	public void start() throws IOException {
		try {
			peer = new PeerBuilderDHT(new PeerBuilder(new Number160(SecureRandom.getInstanceStrong()))
					.ports(getInt("PORT"))
					.behindFirewall(getBoolean("BEHIND_FIREWALL"))
					.start()).start();
		} catch (Exception e) {
			throw new IOException(e);
		} 
		logger.info("Starting peer with id="+peer.peerID().intValue()+":"+getInt("PORT"));
		
		if(!getBoolean("IS_MASTER")) {
			connectMaster();
		}
		
		
		
		putData("Nicho", "Nicolas Pihen / MTGCompanion");
		
	}

	
	private Data readData(String k)
	{
		
		try {
			var fa = peer.get(Number160.createHash(k)).all().start().awaitUninterruptibly();
			if(fa.isCompleted())
				return fa.data();
			
		}
		catch(Exception e)
		{
			logger.error(e);
		}

		
		return null;
		
	}
	
	private boolean putData(String k,Object data)
	{
		try {
			var d = new Data(data);
			var fa = peer.put(Number160.createHash(k)).data(d).start().awaitUninterruptibly();
			if(fa.isCompleted())
				return fa.isSuccess();
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return false;
	}	
	

	private void connectMaster() throws NumberFormatException, UnknownHostException {
		var fb = peer.peer().bootstrap().inetAddress(InetAddress.getByName(getString(PEER_NODE_MASTER).split(":")[0])).ports(Integer.parseInt(getString(PEER_NODE_MASTER).split(":")[1])).start();
		fb.awaitUninterruptibly();
		
		if(fb.isSuccess())
		{
			logger.info("Bootstrap done. Discovering...");
			var fd = peer.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();

			if(fd.isSuccess())
				logger.info("Discover done");
			else
				logger.error("Discover error : " + fd.failedReason());
        }
		else
        {
        	logger.error("Bootstrap error for "+getString(PEER_NODE_MASTER) +" : " + fb.failedReason());
        }
	}
	
	@Override
	public String getVersion() {
		return "5.0-Beta8.1";
	}

	@Override
	public void stop() throws IOException {
		    peer.peer().announceShutdown().start().awaitUninterruptibly();
		    var isDisconnected = peer.shutdown().awaitUninterruptibly();
		    if (isDisconnected.isSuccess()) {
		      logger.info("Peer successfully disconnected.");
		    } else {
		      logger.warn("Peer disconnection failed : "+isDisconnected.failedReason());
		    }
		    
		   
		    
		    
	}

	@Override
	public boolean isAlive() {
		return peer!=null && !peer.peer().isShutdown();
	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTO_START");
	}

	@Override
	public String description() {
		return "P2P sharing system";
	}

	@Override
	public String getName() {
		return "Peer";
	}

}