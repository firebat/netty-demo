package qclient.memcached.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import qclient.memcached.command.Charsets;
import qclient.memcached.network.protocol.MemcachedRequest;

@ChannelHandler.Sharable
public class MemcachedRequestEncoder extends MessageToByteEncoder<MemcachedRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MemcachedRequest msg, ByteBuf out) throws Exception {


        byte[] key = Charsets.encode(msg.key(), Charsets.UTF_8);
        byte[] body = Charsets.encode(msg.body(), Charsets.UTF_8);

        int size = key.length + body.length + (msg.hasExtras() ? 8 : 0);

        // 24 byte header
        out.writeByte(msg.magic());  // 1: magic
        out.writeByte(msg.opCode()); // 1: op code
        out.writeShort(key.length);  // 2: key length
        out.writeByte(msg.hasExtras() ? 0x08 : 0x0); // 1: extra length
        out.writeByte(0);  // 1: data type
        out.writeShort(0); // 2: reserved
        out.writeInt(size); // 4: length
        out.writeInt(msg.id()); // 4: opaque
        out.writeLong(msg.cas()); // 8: case

        // extra
        if (msg.hasExtras()) {
            out.writeInt(msg.flags());   // 4: flags
            out.writeInt(msg.expires()); // 4: expires
        }

        out.writeBytes(key);
        out.writeBytes(body);
    }
}
