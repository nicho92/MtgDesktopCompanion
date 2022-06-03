package org.magic.servers.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;

import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;

public class PeerTestServer extends AbstractMTGServer {

	private Peer peer;
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
					  "IS_MASTER","true");
	}
	

	@Override
	public void start() throws IOException {
		try {
			peer = new PeerBuilder(new Number160(SecureRandom.getInstanceStrong()))
					.ports(getInt("PORT"))
					.start();
					
		} catch (Exception e) {
			throw new IOException(e);
		} 
		logger.info("Starting peer with id="+peer.peerID().intValue()+":"+getInt("PORT"));
		
		if(!getBoolean("IS_MASTER")) {
			connectMaster();
		}
	}

	private void connectMaster() throws NumberFormatException, UnknownHostException {
		FutureBootstrap fb = peer.bootstrap().inetAddress(InetAddress.getByName(getString("PEER_NODE_MASTER").split(":")[0])).ports(Integer.parseInt(getString("PEER_NODE_MASTER").split(":")[1])).start();
		
		fb.awaitUninterruptibly();
		if(fb.isSuccess()) {
           var fd= peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
           logger.info(fd);
        }
		
	}
	
	@Override
	public String getVersion() {
		return "5.0-Beta8";
	}


	@Override
	public void stop() throws IOException {
		
		if(peer!=null)
			peer.shutdown();
		
	}

	@Override
	public boolean isAlive() {
		return peer!=null && !peer.isShutdown();
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