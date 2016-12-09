package qclient.memcached.network;


import io.netty.channel.embedded.EmbeddedChannel;
import qclient.memcached.network.protocol.MemcachedRequest;
import qclient.memcached.network.protocol.OpCode;

public class MemcachedRequestEncoderTest {


    public void testMemcachedRequestEncoder() {
        MemcachedRequest request = new MemcachedRequest(OpCode.SET, "key1", "value1");

        EmbeddedChannel channel = new EmbeddedChannel(new MemcachedRequestEncoder());
        channel.writeOutbound(request);

    }
}
