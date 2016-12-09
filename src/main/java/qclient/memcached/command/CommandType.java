package qclient.memcached.command;

public enum CommandType {

    get,
    gets,

    set,
    add,
    replace,
    append,
    prepend,
    cas,

    delete,

    incr,
    decr,

    touch,

    stats,

    flush_all,
    version,
    verbosity,
    quit;

    public final byte[] bytes;

    CommandType() {
        this.bytes = Charsets.encode(name(), Charsets.ASCII);
    }
}
