package qclient.memcached.network;


import io.netty.channel.ChannelHandlerContext;
import qclient.memcached.network.protocol.MemcachedRequest;
import qclient.memcached.network.protocol.MemcachedResponse;

public class Command {

    private final MemcachedRequest request;
    private MemcachedResponse response;

    public Command(MemcachedRequest request) {
        this.request = request;
    }

    public void write(ChannelHandlerContext ctx) {
        ctx.write(request);
    }
}
