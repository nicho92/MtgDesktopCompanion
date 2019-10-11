package org.magic.game.network;

import java.awt.Color;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicDeck;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATE;
import org.magic.game.network.actions.ReponseAction.CHOICE;
import org.magic.game.network.actions.RequestPlayAction;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient implements NetworkClient {

	private Player p;
	private Bootstrap clientBootstrap ;
	
	Logger logger = MTGLogger.getLogger(getClass());

	public NettyClient(String server, int port) {
		p = new Player();
		clientBootstrap = new Bootstrap();
		clientBootstrap.group(new NioEventLoopGroup());
		clientBootstrap.channel(NioSocketChannel.class);
		clientBootstrap.remoteAddress(new InetSocketAddress(server, port));
		clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
		    protected void initChannel(SocketChannel socketChannel) throws Exception 
		    {
		        socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
				
		            @Override
		            public void channelActive(ChannelHandlerContext channelHandlerContext){
		                channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("Netty Rocks!", MTGConstants.DEFAULT_ENCODING));
		            }

		            @Override
		            public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
		                logger.error(cause);
		                channelHandlerContext.close();
		            }

					@Override
					protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
						 logger.debug("Client received: " + in.toString(MTGConstants.DEFAULT_ENCODING));
						
					}
		        });
		    }
		});
	}
	
	@Override
	public Player getPlayer() {
		return p;
	}

	@Override
	public void join() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDeck(MagicDeck d) {
		

	}

	@Override
	public void sendMessage(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendDeck(MagicDeck d, Player to) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessage(String text, Color c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestPlay(Player otherplayer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reponse(RequestPlayAction pa, CHOICE c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeStatus(STATE selectedItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addObserver(Observer obs) {
		// TODO Auto-generated method stub

	}

}
