package org.beta;

import java.io.IOException;
import java.net.SocketAddress;

import org.magic.api.interfaces.abstracts.AbstractMTGServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class NettyServer extends AbstractMTGServer {

	private static final String SERVER_PORT = "SERVER-PORT";
	private static final String PLAYER = "PLAYER";
	private static final String MAX_CLIENT = "MAX_CLIENT";
	private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	@Override
	public void start() throws IOException {
		
		ServerBootstrap b = new ServerBootstrap();
               b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                  @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new ChannelHandlerAdapter() {
                        	
                        		  @Override
                        	       public void channelRead(ChannelHandlerContext ctx, Object msg) {
                        	           ctx.write(msg);
                        	       }
                        	   
                        	       @Override
                        	       public void channelReadComplete(ChannelHandlerContext ctx) {
                        	           ctx.flush();
                        	       }
                        	   
                        	       @Override
                        	       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                        	           logger.error(cause);
                        	           ctx.close();
                        	       }
                        	       
                        	       @Override
	                        	   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
	                        	    	 logger.debug("connection from " + remoteAddress + " "+ ctx);  
	                        	   }
                        });
                    }
                });
   
           ChannelFuture f;
			try {
				f = b.bind(getInt(SERVER_PORT)).sync();
				f.channel().closeFuture().sync();
	           
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IOException(e);
			}
   
		             
	}

	@Override
	public void stop() throws IOException {
		 bossGroup.shutdownGracefully();
		 workerGroup.shutdownGracefully();

	}

	@Override
	public boolean isAlive() {
		return bossGroup.isTerminated();
	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}


	@Override
	public String description() {
		return "Enable local player room server";
	}


	@Override
	public String getName() {
		return "MTG Game Server";
	}

	@Override
	public void initDefault() {
		setProperty(SERVER_PORT, "18567");
		setProperty("IDLE-TIME", "10");
		setProperty("BUFFER-SIZE", "2048");
		setProperty("AUTOSTART", "false");
		setProperty("WELCOME_MESSAGE", "Welcome to my MTG Desktop Gaming Room");
		setProperty(MAX_CLIENT, "0");
	}
	
	public static void main(String[] args) throws IOException {
		new NettyServer().start();
	}
	
	
}
