package org.magic.servers.impl;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;

import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

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
			peer = new PeerMaker(new Number160(SecureRandom.getInstanceStrong()))
					.setPorts(getInt("PORT"))
					.makeAndListen();
		} catch (Exception e) {
			throw new IOException(e);
		} 
		logger.info("Starting peer with id="+peer.getPeerID().intValue()+":"+getInt("PORT"));
		
		if(!getBoolean("IS_MASTER")) {
			connectMaster();
		}
	}

	private void connectMaster() throws NumberFormatException, UnknownHostException {
		PeerAddress p = new PeerAddress(new Number160(1),getString("PEER_NODE_MASTER").split(":")[0],Integer.parseInt(getString("PEER_NODE_MASTER").split(":")[1]),9091);
		
		logger.info("Connecting to master "+ p);
		
		FutureDiscover future = peer.discover().setPeerAddress(p).start();
		var fd = future.awaitUninterruptibly();
		
		if(fd.isSuccess())
		{
			logger.info("Found my outside address as "  +future.getPeerAddress() );
		}
		else
		{
			logger.warn("Failed to connect to " + future.getPeerAddress());
		}
		
		var fb = peer.bootstrap().setPeerAddress(future.getPeerAddress()).start();
		var bf = fb.awaitUninterruptibly();
		
		if(bf.isSuccess())
		{
			logger.info("Bootstrap complete");
		}
		
		
		
	}
	
	@Override
	public String getVersion() {
		return "4.4";
	}


	@Override
	public void stop() throws IOException {
		
		if(peer!=null)
			peer.shutdown();
		
	}

	@Override
	public boolean isAlive() {
		return peer!=null && peer.isRunning();
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
