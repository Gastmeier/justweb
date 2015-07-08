package justweb.netty.server;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;

public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelInboundHandler handler;

    public DefaultChannelInitializer(ChannelInboundHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("logger", new LoggingHandler());
        pipeline.addLast("http", new HttpServerCodec());
        pipeline.addLast("handler", handler);
    }
}
