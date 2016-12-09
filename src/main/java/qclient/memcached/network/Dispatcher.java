package qclient.memcached.network;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@ChannelHandler.Sharable
class Dispatcher extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    private volatile ChannelHandlerContext ctx;

    private final BlockingQueue<ResultValue> sendQueue;
    private final Deque<ResultValue> waitQueue;

    public Dispatcher() {
        this.sendQueue = new LinkedBlockingDeque();
        this.waitQueue = new LinkedList<ResultValue>();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        // recover
        ctx.channel().eventLoop().submit(flush);

        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        waitQueue.removeFirst().success(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("", cause);
        ctx.close();
    }

    public ResultValue send(ResultValue value, Command command) {

        value.setCommand(command);
        sendQueue.offer(value);

        if (ctx != null)
            ctx.channel().eventLoop().submit(flush);

        return value;
    }

    private final Runnable flush = new Runnable() {

        @Override
        public void run() {

            while (!sendQueue.isEmpty()) {

                if (ctx == null) {
                    return;
                }

                List<ResultValue> list = new ArrayList<ResultValue>(sendQueue.size());
                sendQueue.drainTo(list);

                for (ResultValue item : list) {
                    waitQueue.add(item);
                    item.command.write(ctx);
                }

                ctx.flush();
            }
        }
    };
}
