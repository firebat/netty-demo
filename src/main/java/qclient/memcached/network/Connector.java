package qclient.memcached.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

@ChannelHandler.Sharable
class Connector extends ChannelDuplexHandler {

    private final Logger logger = LoggerFactory.getLogger(Connector.class);

    private final Bootstrap bootstrap;
    private final SocketAddress address;

    private int connectTimeout = 3000;
    private int retries = -1;

    private volatile boolean stop = false;

    public Connector(Bootstrap bootstrap, SocketAddress address) {
        this.bootstrap = bootstrap;
        this.address = address;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        Thread thread = new Thread(new Runnable() {
            public void run() {
                connect();
            }
        }, "connector");
        thread.setDaemon(true);
        thread.start();

        super.channelInactive(ctx);
    }

    public void connect() {
        boolean always = retries < 0;
        int times = retries;

        try {
            while (!stop && (always || times > 0)) {

                ChannelFuture future = bootstrap.connect(address).await();
                if (future.isSuccess()) {
                    logger.info("Connect to {} OK", address);
                    break;
                }

                logger.error("Cannot connect " + address, future.cause());

                times--;
                if (!always && times == 0) {
                    throw new RuntimeException("Cannot connect to " + address, future.cause());
                }

                Thread.sleep(connectTimeout);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        stop = true;
    }
}
