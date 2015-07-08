package justweb.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

public class NettyHttpClientInboundHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private FullHttpResponse response;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        response = msg;
    }

    public FullHttpResponse response() {
        return response;
    }
}
