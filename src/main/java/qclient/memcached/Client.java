package qclient.memcached;

import com.google.common.util.concurrent.ListenableFuture;

public interface Client {

    ListenableFuture set(String key, String value);

    ListenableFuture<String> get(String key);
}
