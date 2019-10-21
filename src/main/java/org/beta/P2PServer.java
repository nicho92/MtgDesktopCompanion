package org.beta;
import java.io.IOException;

import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.runtime.BtClient;

public class P2PServer extends AbstractMTGServer {

	private BtClient client;
	
	public static void main(String[] args) throws IOException {
		new P2PServer().start();
	}

	@Override
	public void start() throws IOException {
		
		Storage store = new FileSystemStorage(MTGConstants.MTG_DECK_DIRECTORY.toPath());

		DHTModule mod = new DHTModule(new DHTConfig() {
			@Override
			public boolean shouldUseRouterBootstrap() {
				return true;
			}
		});
		
		client = Bt.client()
					.storage(store)
					.autoLoadModules()
					.module(mod)
					.stopWhenDownloaded()
					.magnet("magnet:?xt=urn:btih:5dbde2ccce0bcdd9d9ca30b2db3c1bb51b9b7410")
					.build();
		
		 client.startAsync(state -> {
	            logger.debug("Peers: " + state.getConnectedPeers().size() + "; Downloaded: " + (((double)state.getPiecesComplete()) / state.getPiecesTotal()) * 100 + "%");
	        }, 1000).join();
	}

	@Override
	public void stop() throws IOException {
		
		if(client!=null)
			client.stop();
		
	}

	@Override
	public boolean isAlive() {
		if(client!=null)
			return client.isStarted();
		
		return false;

	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTO_START");
	}

	@Override
	public String description() {
		return "Share your decks around the world";
	}

	@Override
	public String getName() {
		return "P2P Sharing";
	}
	
	@Override
	public void initDefault() {
		setProperty("AUTO_START", "false");
	}
	
}
