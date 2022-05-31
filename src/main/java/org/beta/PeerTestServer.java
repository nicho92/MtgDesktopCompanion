package org.beta;

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
	public Map<String, String> getDefaultAttributes() {
		return Map.of("PORT","9090",
					  "USERNAME",SystemUtils.getUserName(),
					  "AUTO_START","false",
					  "PEER_NODE_MASTER","mtgcompanion.me:9090");
	}
	

	@Override
	public void start() throws IOException {
		try {
			peer = new PeerMaker(new Number160(SecureRandom.getInstanceStrong().nextInt()))
					.setPorts(getInt("PORT"))
					.makeAndListen();
		} catch (Exception e) {
			throw new IOException(e);
		} 
		logger.info("Starting peer with id="+peer.getPeerID().intValue());
		
		detect();
		
		
	}

	private void detect() throws NumberFormatException, UnknownHostException {
		PeerAddress p = new PeerAddress(new Number160(1),getString("PEER_NODE_MASTER").split(":")[0],Integer.parseInt(getString("PEER_NODE_MASTER").split(":")[1]),9191);
		FutureDiscover future = peer.discover().setPeerAddress(p).start();
		var fd = future.awaitUninterruptibly();
		
		
		
		
		
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
