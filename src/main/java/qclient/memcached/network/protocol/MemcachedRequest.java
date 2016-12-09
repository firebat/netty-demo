package qclient.memcached.network.protocol;

import java.util.Random;

public class MemcachedRequest {
    private static final Random random = new Random();
    private final int magic = 0x80;
    private final byte opCode;
    private final String key;
    private final int flags = 0xdeadbeef; // random
    private final int expires; //
    private final String body;
    private final int id = random.nextInt(); // opaque
    private final long cas = 0; // data version check
    private final boolean hasExtras;

    public MemcachedRequest(byte opCode, String key, String value) {
        this.opCode = opCode;
        this.key = key;
        this.body = value == null ? "" : value;
        this.expires = 0;
        this.hasExtras = opCode == OpCode.SET;
    }

    public int magic() {
        return magic;
    }

    public int opCode() {
        return opCode;
    }

    public String key() {
        return key;
    }

    public int flags() {
        return flags;
    }

    public int expires() {
        return expires;
    }

    public String body() {
        return body;
    }

    public int id() {
        return id;
    }

    public long cas() {
        return cas;
    }

    public boolean hasExtras() {
        return hasExtras;
    }
}
