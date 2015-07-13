package justweb.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import justweb.http.Result;
import justweb.urlmapper.UrlMappers;

public class DefaultServerHandler extends SimpleChannelInboundHandler<Object> {

    private final UrlMappers urlMappers;
    private HttpRequest request;
    private byte[] body = null;
    private int index = 0;

    public DefaultServerHandler(UrlMappers urlMappers) {
        this.urlMappers = urlMappers;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;
            index = 0;

            String value = request.headers().get(HttpHeaders.Names.CONTENT_LENGTH);
            Integer length = null;
            if (value != null) {
                try {
                    length = Integer.parseInt(value);
                }
                catch (NumberFormatException e) {
                    // log error or?
                }
            }
            else {
                // log error or?
            }

            if (length != null) {
                body = new byte[length.intValue()];
            }
            else {
                // end this shit right here
            }
        } else if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();

            for (int i = 0; i < buf.readableBytes(); i++) {
                body[index + i] = buf.getByte(i);
            }

            index += buf.readableBytes();

            if (content instanceof LastHttpContent) {
                LastHttpContent lastContent = (LastHttpContent) content;

                Result result = null;//urlMappers.handle(new Request(request), body);
                FullHttpResponse response = result.createNettyResponse();
                ctx.write(response);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

//    public void writeResponse(ChannelHandlerContext ctx, HttpObject httpObject) {
//        FullHttpResponse response = new DefaultFullHttpResponse(
//                HTTP_1_1, httpObject.getDecoderResult().isSuccess() ? OK : BAD_REQUEST,
//                Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
//
//        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
//
//        boolean keepAlive = HttpHeaders.isKeepAlive(request);
//        if (keepAlive) {
//            // Add 'Content-Length' header only for a keep-alive connection.
//            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
//            // Add keep alive header as per:
//            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
//            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
//        }
//
//        ctx.write(response);
//    }

}
