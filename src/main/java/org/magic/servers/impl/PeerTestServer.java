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
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerStatusListener;
import net.tomp2p.peers.RTT;
import net.tomp2p.storage.Data;

public class PeerTestServer extends AbstractMTGServer {

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
					  "PEER_NODE_MASTER","mtgcompanion.me:9090",
					  "IS_MASTER","true",
					  "BEHIND_FIREWALL","true");
	}
	

	@Override
	public void start() throws IOException {
		try {
			peer = new PeerBuilderDHT(new PeerBuilder(new Number160(SecureRandom.getInstanceStrong()))
					.ports(9898)
					.behindFirewall(getBoolean("BEHIND_FIREWALL"))
					.start()).start();
		} catch (Exception e) {
			throw new IOException(e);
		} 
		logger.info("Starting peer with id="+peer.peerID().intValue()+":"+getInt("PORT"));
		
		if(!getBoolean("IS_MASTER")) {
			connectMaster();
		}
		
		listening();
		
		
	}

	private void listening() {
		
		try {
			Data data = new Data("test");
			var fp = peer.put(Number160.createHash("TEST")).data(data).start();
			
			fp.awaitListenersUninterruptibly();
			
			if(fp.isSuccess())
			{
				logger.info("Message done");
			}
			else
			{
				logger.warn("Error put " + fp.failedReason());
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		peer.peerBean().addPeerStatusListener(new PeerStatusListener() {
			
			@Override
			public boolean peerFound(PeerAddress remotePeer, PeerAddress referrer, PeerConnection peerConnection, RTT roundTripTime) {
				logger.info("Peer Found " + remotePeer.inetAddress()+":"+remotePeer.tcpPort());
				return false;
			}
			
			@Override
			public boolean peerFailed(PeerAddress remotePeer, PeerException exception) {
				logger.warn("Peer Failed " + remotePeer.inetAddress()+":"+remotePeer.tcpPort());
				return false;
			}
		});

	}


	private void connectMaster() throws NumberFormatException, UnknownHostException {
		FutureBootstrap fb = peer.peer().bootstrap().inetAddress(InetAddress.getByName(getString("PEER_NODE_MASTER").split(":")[0])).ports(Integer.parseInt(getString("PEER_NODE_MASTER").split(":")[1])).start();
		
		fb.awaitUninterruptibly();
		if(fb.isSuccess()) {
			logger.info("Bootstrap done ");
			
			var fd = peer.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
			if(fd.isSuccess())
				logger.info("Discover done");
			else
				logger.error(fd.failedReason());
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