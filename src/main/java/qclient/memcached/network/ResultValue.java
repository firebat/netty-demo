package qclient.memcached.network;

import com.google.common.util.concurrent.AbstractFuture;

class ResultValue<T> extends AbstractFuture<T> {

    Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void success(T value) {
        set(value);
    }

    public void fail(Throwable t) {
        setException(t);
    }
}
