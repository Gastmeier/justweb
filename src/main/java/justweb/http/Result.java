package justweb.http;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class Result<BodyType> {

//    public static final DateTimeFormatter RFC1123_DATE_TIME_FORMATTER =
//            DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
//                    .withZoneUTC().withLocale(Locale.US);

    private final HttpResponseStatus status;
    private final BodyType body;

    public Result(HttpResponseStatus status, BodyType body) {
        this.status = status;
        this.body = body;
    }

    public HttpResponseStatus getStatus() { return status; }
    public BodyType getBody() { return body; }

    public abstract FullHttpResponse createNettyResponse();

}
