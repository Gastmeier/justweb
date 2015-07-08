package justweb.netty.client;

import co.paralleluniverse.fibers.FiberAsync;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LoggingHandler;

public class NettyHttpClient {

    private Bootstrap bootstrap;

    public NettyHttpClient(EventLoopGroup workerGroup) {
        bootstrap(workerGroup);
    }

    private void bootstrap(EventLoopGroup workerGroup) {
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("logger", new LoggingHandler());
                pipeline.addLast("http", new HttpClientCodec());
                pipeline.addLast("decompressor", new HttpContentDecompressor());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast("handler", new NettyHttpClientInboundHandler());
            }
        });
    }

    public NettyHttpConnection connect(String url, int port) throws Throwable {
        return new ConnectAsync(url, port).run();
    }

    private class ConnectAsync extends FiberAsync<NettyHttpConnection, Throwable> {

        private final String url;
        private final int port;

        public ConnectAsync(String url, int port) {
            this.url = url;
            this.port = port;
        }

        @Override
        protected void requestAsync() {
            ChannelFuture future = bootstrap.connect(url, port);

            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        asyncCompleted(new NettyHttpConnection(future.channel()));
                    }
                    else {
                        asyncFailed(future.cause());
                    }
                }
            });
        }
    }

}
