package qclient.memcached.network;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import qclient.memcached.network.protocol.MemcachedResponse;

import java.util.List;

public class MemcachedResponseDecoder extends ByteToMessageDecoder {

    private enum State {
        Header,
        Body
    }

    private State state = State.Header;
    private int size;
    private byte magic;
    private byte opCode;
    private short keyLength;
    private byte extraLength;
    private short status;
    private int id;
    private long cas;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        switch (state) {
            case Header:
                if (in.readableBytes() < 24) {
                    return; // need more
                }
                magic = in.readByte();
                opCode = in.readByte();
                keyLength = in.readShort();
                extraLength = in.readByte();
                in.skipBytes(1);
                status = in.readShort();
                size = in.readInt();
                id = in.readInt();
                cas = in.readLong();
                state = State.Body;

            case Body:
                if (in.readableBytes() < size) {
                    return;
                }

                int flags = 0, expires = 0;
                int left = size;
                if (extraLength > 0) {
                    flags = in.readInt();
                    left -= 4;
                }

                if (extraLength > 4) {
                    expires = in.readInt();
                    left -= 4;
                }

                String key = "";
                if (keyLength > 0) {
                    ByteBuf keyBuf = in.readBytes(keyLength);
                    key = keyBuf.toString(CharsetUtil.UTF_8);
                    left -= keyLength;
                }

                ByteBuf body = in.readBytes(left);
                String data = body.toString(CharsetUtil.UTF_8);
                out.add(new MemcachedResponse(
                        magic,
                        opCode,
                        status,
                        id,
                        cas,
                        flags,
                        expires,
                        key,
                        data
                ));

                state = State.Header;
        }
    }
}
