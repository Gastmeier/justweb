package justweb.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class TextResult extends Result {

    public TextResult(HttpResponseStatus status, String body) {
        super(status, body);
    }

    @Override
    public FullHttpResponse createNettyResponse() {
        ByteBuffer bufferUtf8 = Charset.forName("UTF-8").encode((String) getBody());
        int lengthUtf8 = bufferUtf8.limit();
        byte[] jsonUtf8 = new byte[lengthUtf8];
        bufferUtf8.get(jsonUtf8);

        ByteBuf buf = Unpooled.copiedBuffer(jsonUtf8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, getStatus(), buf);

        response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, lengthUtf8);
        response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
//        response.headers().add(HttpHeaders.Names.DATE, RFC1123_DATE_TIME_FORMATTER.print(new DateTime()));

        return response;
    }
}
