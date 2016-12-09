package qclient.memcached.network;

import com.google.common.util.concurrent.ListenableFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import qclient.memcached.network.protocol.MemcachedRequest;
import qclient.memcached.Client;
import qclient.memcached.network.protocol.OpCode;

import java.net.InetSocketAddress;

public class Session implements Client {

    private final String host;
    private final int port;
    private final InetSocketAddress address;

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;

    private final Connector connector;
    private final Dispatcher handler;

    public Session(String host, int port) {
        this.host = host;
        this.port = port;
        this.address = new InetSocketAddress(host, port);

        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap()
                .group(group)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class);

        this.connector = new Connector(bootstrap, address);
        this.handler = new Dispatcher();

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new Connector(bootstrap, address))
                        .addLast(new MemcachedResponseDecoder())
                        .addLast(new MemcachedRequestEncoder())
                        .addLast(handler);
            }
        });

        connector.connect();
    }


    public void close() throws InterruptedException {
        connector.stop();
        group.shutdownGracefully().await();
    }

    @Override
    public ListenableFuture set(String key, String value) {
        return handler.send(new ResultValue(), new Command(new MemcachedRequest(
                OpCode.SET,
                key,
                value
        )));
    }

    @Override
    public ListenableFuture get(String key) {
        return handler.send(new ResultValue(), new Command(new MemcachedRequest(
                OpCode.GET,
                key,
                ""
        )));
    }
}
