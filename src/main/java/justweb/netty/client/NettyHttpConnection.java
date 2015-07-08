package justweb.netty.client;

import co.paralleluniverse.fibers.FiberAsync;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;

public class NettyHttpConnection implements AutoCloseable {

    private final Channel channel;

    public NettyHttpConnection(Channel channel) {
        this.channel = channel;
    }

    public void basicAuthentication() {

    }

    public FullHttpResponse send(FullHttpRequest request) throws Throwable {
        return new SendAsync(request).run();
    }

    public FullHttpResponse get(String uri) throws Throwable {
        return send(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri));
    }

    private class SendAsync extends FiberAsync<FullHttpResponse, Throwable> {

        private final FullHttpRequest request;

        public SendAsync(FullHttpRequest request) {
            this.request = request;
        }

        @Override
        protected void requestAsync() {
            ChannelFuture future = channel.writeAndFlush(request);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        NettyHttpClientInboundHandler handler =
                                (NettyHttpClientInboundHandler) future.channel().pipeline().get("handler");
                        asyncCompleted(handler.response());
                    }
                    else {
                        asyncFailed(future.cause());
                    }
                }
            });
        }
    }


    @Override
    public void close() throws Exception {
        channel.close();
    }
}
