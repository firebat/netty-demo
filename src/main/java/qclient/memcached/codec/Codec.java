package qclient.memcached.codec;

import io.netty.buffer.ByteBuf;

public interface Codec {

    void encode(ByteBuf buf, Object value);

    <T> T decode(ByteBuf buf);
}

