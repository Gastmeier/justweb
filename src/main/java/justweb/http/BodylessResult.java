package justweb.http;

import io.netty.handler.codec.http.*;
import org.joda.time.DateTime;

public class BodylessResult extends Result<Object> {

    public BodylessResult(HttpResponseStatus status) {
        super(status, null);
    }

    @Override
    public FullHttpResponse createNettyResponse() {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, getStatus());
        response.headers().add(HttpHeaders.Names.DATE, RFC1123_DATE_TIME_FORMATTER.print(new DateTime()));
        return response;
    }

}
