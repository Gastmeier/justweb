package justweb.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;

public class Request {

    private final HttpRequest request;
    private final QueryStringDecoder uriDecoder;

    public Request(HttpRequest request) {
        this.request = request;
        uriDecoder = new QueryStringDecoder(request.getUri());
    }

    public HttpMethod method() {
        return request.getMethod();
    }
    public String uri() {
        return request.getUri();
    }
    public String path() { return uriDecoder.path(); }
    public String lang() { return request.headers().get(HttpHeaders.Names.ACCEPT_LANGUAGE); }

    public String parameter(String name) {
        List<String> values = uriDecoder.parameters().get(name);
        if (values == null || values.isEmpty())
            return null;
        return values.get(0);
    }

}
